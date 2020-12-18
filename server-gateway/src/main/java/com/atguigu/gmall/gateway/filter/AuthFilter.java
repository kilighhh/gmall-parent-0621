package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.utils.JSONUtils;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/12/15 11:38
 * @Version 1.0
 * 这个类是过滤作用，我们会拦截我们应该拦截的请求，而且会做鉴权处理
 */
@Component
public class AuthFilter implements GlobalFilter {
    @Autowired
    private UserFeignClient userFeignClient;
    //也是过滤器，制定我们的黑白名单
    private AntPathMatcher antPathMatcher=new AntPathMatcher();
    //白名单
    @Value("${authUrls.url}")
    String authUrls;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //获取请求地址，然后把一些不该拦截的请求放行,如果是这些请求就放行
        String uri = request.getURI().toString();
        System.out.println(uri);
        if(uri.contains("passport")||uri.contains(".ico")||uri.contains("login")||uri.contains(".js")||uri.contains(".jpg")||uri.contains(".png")||uri.contains(".css")){
            return chain.filter(exchange);
        }
        //给内部的接口制定黑名单，无论是否登陆都不可以访问这些端口
        boolean match = antPathMatcher.match("**/admin/**", uri);
        //如果是在黑名单的里面就是true
        if(match){
            return out(response,ResultCodeEnum.PERMISSION);
        }
        //制定我们的白名单，就是我们必须需要登录才能有的功能,authUrls就是我们所有白名单的请求地址
        boolean ifWhite=false;
        String[] urls = authUrls.split(",");
        for (String url : urls) {
            if(uri.contains(url)){
                //将这个标记置为true，让他去进行登录
                ifWhite=true;
            }
        }

        //远程调用sso单点登录系统进行鉴权 verify
            //获得token 根据请求获得token
            String token=getToken(request);
        Map<String,Object> userMap=null;
        if(!StringUtils.isEmpty(token)){
            //如果用户登录过的话我们就会获取到我们的token
            userMap=userFeignClient.verify(token);
        }else {
            //未登录的话我们也会有临时的用户id
            String userTempId=getUserTempId(request);
            System.out.println("userTempId = " + userTempId);
        }


            //下面是鉴权操作
        if(null!=userMap&&userMap.size()>0){
            //如果用户登录了的话，我们将他的信息传递下去
            Object o = userMap.get("user");
            //这里担心我们会有类型擦除，只获取我们的object对象，再将他转化成我们的userInfo对象
            UserInfo userInfo = JSONObject.parseObject(JSON.toJSONString(o), UserInfo.class);
            //获取我们的userId
            Long userId = userInfo.getId();
            //将我们的userId放进我们的request中
            //下面的两个api是需要改变我们的request和exchange
            request.mutate().header("userId",userId+"").build();
            exchange.mutate().request(request);
            return chain.filter(exchange);
        }else{
            //设置转发的状态码，且这里如果他是白名单的话就让他去登录
            if(ifWhite){
                response.setStatusCode(HttpStatus.SEE_OTHER);
                //设置重定向的转发地址以及他的动作 如果鉴权失败的话我们就要继续的去请求登录再加上我们原来要请求的地址
                response.getHeaders().set(HttpHeaders.LOCATION,"http://passport.gmall.com/login.html?originUrl="+uri);
                //返回一个Mono给予放行
                Mono<Void> mono = response.setComplete();
                return mono;
            }
        }
        //对那些既不是黑名单也不是白名单的请求地址进行放行
        return chain.filter(exchange);

    }

    /***
     * @author Kilig Zong
     * @date 2020/12/17 14:40
     * @description 如果是我们的一些页面会对这个用户生成我们的临时的用户id
     * @param request
     * @return java.lang.String
     **/
    private String getUserTempId(ServerHttpRequest request) {
        String userTempId="";
        //我们的同步请求的token是放在我们的cookie里面的
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if(null!=cookies){
            List<HttpCookie> tokenCookies = cookies.get("userTempId");
            if(null!=tokenCookies){
                for (HttpCookie tokenCookie : tokenCookies) {
                    if(tokenCookie.getName().equals("userTempId")){
                        userTempId=  tokenCookie.getValue();
                    }
                }
            }

        }
        //如果我们的是异步请求的话，我们的前端会将token存放在我们的请求头上面,我们的token是不存放在我们的cookie里面
        if(StringUtils.isEmpty(userTempId)){
            HttpHeaders headers = request.getHeaders();
            if(null!=headers){
                List<String> strings = headers.get("userTempId");
                if(null!=strings){
                    userTempId=strings.get(0);
                }
            }
        }
        return null;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/16 13:13
     * @description 获取我们的token 这里有一个点需要注意的是我们的异步请求是没有token的，同步请求有token（在cookie里面），
     * 异步请求我们是放在我们的请求头上面的
     * @param request
     * @return java.lang.String
     **/
    private String getToken(ServerHttpRequest request) {
        String token="";
        //我们的同步请求的token是放在我们的cookie里面的
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if(null!=cookies){
            List<HttpCookie> tokenCookies = cookies.get("token");
            if(null!=tokenCookies){
                for (HttpCookie tokenCookie : tokenCookies) {
                    if(tokenCookie.getName().equals("token")){
                         token=  tokenCookie.getValue();
                    }
                }
            }

        }
        //如果我们的是异步请求的话，我们的前端会将token存放在我们的请求头上面,我们的token是不存放在我们的cookie里面
        if(StringUtils.isEmpty(token)){
            HttpHeaders headers = request.getHeaders();
            if(null!=headers){
                List<String> strings = headers.get("token");
                if(null!=strings){
                    token=strings.get(0);
                }
            }
        }
        return token;
    }

    //鉴权，对我们的请求进行一个鉴权处理
    // 接口鉴权失败返回数据
    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum){
        //返回用户没有权限进行登录
        Result<Object> result = Result.build(null, resultCodeEnum);
        byte[] bits = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer wrap = response.bufferFactory().wrap(bits);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        // 输入到页面
        Mono<Void> mono = response.writeWith(Mono.just(wrap));
        return mono;
    }

}
