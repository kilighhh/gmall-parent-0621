package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.config.CookieUtils;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/12/15 16:11
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/user/passport/")
public class UserApiController{
    @Autowired
    private UserService userService;

    /***
     * @author Kilig Zong
     * @date 2020/12/15 17:54
     * @description 我们这是要对token进行验证
     * @param token
     * @return java.util.Map<java.lang.String, java.lang.Object>
     **/
    @RequestMapping("verify/{token}")
   public Map<String, Object> verify(@PathVariable("token") String token){
        UserInfo userInfo = userService.verify(token);
        Map<String, Object> map = new HashMap<>();
        map.put("user",userInfo);
        return map;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/16 10:19
     * @description 登录接口
     * @param userInfo
     * @return com.atguigu.gmall.common.result.Result
     **/
    @RequestMapping("login")
    public Result login(@RequestBody UserInfo userInfo){
        userInfo=  userService.login(userInfo);
        if(null==userInfo){
            return Result.fail();
        }else {
            return Result.ok(userInfo);
        }
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/18 14:18
     * @description 根据用户i查询我们的用户的地址
     * @param userId
     * @return java.util.List<com.atguigu.gmall.model.user.UserAddress>
     **/
    @RequestMapping("findUserAddressListByUserId/{userId}")
    public List<UserAddress> findUserAddressListByUserId(@PathVariable("userId")String userId){
      List<UserAddress> addresses=userService.findUserAddressListByUserId(userId);
      return addresses;

    }

    /***
     * @author Kilig Zong
     * @date 2020/12/18 14:20
     * @description 退出用户登录
     * @param request
     * @return void
     **/
    @RequestMapping("logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){
        CookieUtils.deleteCookie(request,response,"token");
        CookieUtils.deleteCookie(request,response,"userInfo");
    }
}
