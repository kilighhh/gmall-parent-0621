package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/12/7 17:22
 * @Version 1.0
 */
@Controller
public class ListController {

    @Autowired
    private ListFeignClient listFeignClient;
    /***
     * @author Kilig Zong
     * @date 2020/12/7 17:48
     * @description 首页一级二级三级分类数据
     * @param model
     * @return java.lang.String
     **/
    @RequestMapping("/")
    public  String index(Model model){
     List<JSONObject> list=listFeignClient.getBaseCategoryList();
    model.addAttribute("list",list);
     return "index/index";
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/11 11:10
     * @description 搜索框查询或者根据三级分类来进行搜索结果
     * @param model
     * @return java.lang.String
     **/
    @RequestMapping({"list.html","search.html"})
    public  String list(Model model, SearchParam searchParam, HttpServletRequest request){
        //获得urlParam
        String urlParam = getUrlParam(searchParam,request);
      SearchResponseVo searchResponseVo=listFeignClient.list(searchParam);
        List<Goods> goodsList = searchResponseVo.getGoodsList();
        if(null!=goodsList&&goodsList.size()>0){
            //上传商品的信息集合
            model.addAttribute("goodsList",goodsList);
            //上传品牌以及logo的信息集合
            List<SearchResponseTmVo> trademarkList = searchResponseVo.getTrademarkList();
            model.addAttribute("trademarkList",trademarkList);
            //将平台销售属性设置进去
            List<SearchResponseAttrVo> attrsList = searchResponseVo.getAttrsList();
            model.addAttribute("attrsList",attrsList);
            model.addAttribute("urlParam",urlParam);
        }
        //判断品牌不为空 返回给前台显示
        if(!StringUtils.isEmpty(searchParam.getTrademark())){
            model.addAttribute("trademarkParam",searchParam.getTrademark().split(":")[1]);
        }
        //平台属性返回给前端显示
        if(null!=searchParam.getProps()&&searchParam.getProps().length>0){
            List<SearchAttr> searchAttrs = new ArrayList<>();
            for (String prop : searchParam.getProps()) {//prop 属性id:属性值名称:属性名称
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(Long.parseLong(prop.split(":")[0]));
                searchAttr.setAttrName(prop.split(":")[2]);
                searchAttr.setAttrValue(prop.split(":")[1]);
                searchAttrs.add(searchAttr);
            }
            model.addAttribute("propsParamList",searchAttrs);
        }
        //判断我们的排序是综合排序还是价格排序 再返回给前端
        if(!StringUtils.isEmpty(searchParam.getOrder())){
            String order = searchParam.getOrder();
            String type = order.split(":")[0];
            String sort=  order.split(":")[1];
            Map<String, String> orderMap = new HashMap<>();
            orderMap.put("type",type);
            orderMap.put("sort",sort);
            model.addAttribute("orderMap",orderMap);
        }
        return "list/index";
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/12 14:48
     * @description 处理我们的请求参数并且将参数返回给页面 页面
     * @param searchParam
     * @param request
     * @return java.lang.String
     **/
    private String getUrlParam(SearchParam searchParam, HttpServletRequest request) {
        //获取当前的uri
        String requestURI = request.getRequestURI();
        //用StringBuffer
        StringBuffer urlParam = new StringBuffer(requestURI);
        //将前端可能会点击的平台属性都get出来
        Long category3Id = searchParam.getCategory3Id();
        String keyword = searchParam.getKeyword();
        String[] props = searchParam.getProps();
        String trademark = searchParam.getTrademark();
        //判断如果前端传来的数据不为空的话就传回前端 然后前端再根据参数聚合查询
        if(null!=category3Id&&category3Id>0){
            urlParam.append("?category3Id="+category3Id);
        }
        if(!StringUtils.isEmpty(keyword)){
            urlParam.append("?keyword="+keyword);
        }
        if(null!=props&&props.length>0){
            for (String prop : props) {
                urlParam.append("&props="+prop);
            }
        }
        if(!StringUtils.isEmpty(trademark)){
            urlParam.append("&trademark="+trademark);
        }

        return urlParam.toString();
    }

}
