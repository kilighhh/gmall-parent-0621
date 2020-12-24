package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.clent.SeckillFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/23 19:47
 * @Version 1.0
 */
@Controller
public class SeckillController{

    @Autowired
    private SeckillFeignClient seckillFeignClient;

    /***
     * @author Kilig Zong
     * @date 2020/12/23 21:04
     * @description 去首页
     * @param model
     * @return java.lang.String
     **/
    @GetMapping("seckill.html")
    public  String index(Model model){
       List<SeckillGoods> seckillGoods= seckillFeignClient.findAll();
       model.addAttribute("list",seckillGoods);
       return "seckill/index";
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/23 22:23
     * @description 秒杀页商品详情
     * @param skuId
     * @param model
     * @return java.lang.String
     **/
    //http://activity.gmall.com/seckill/30.html
    @GetMapping("seckill/{skuId}.html")
    public  String getItem(@PathVariable("skuId") Long skuId,Model model){
        //通过skuId查询商品的具体商品
       SeckillGoods seckillGoods = seckillFeignClient.getItem(skuId);
       model.addAttribute("item",seckillGoods);
       return "seckill/item";
    }

    //http://activity.gmall.com/seckill/queue.html?skuId=30&skuIdStr=eccbc87e4b5ce2fe28308fd9f2a7baf3
    @GetMapping("seckill/queue.html")
    public String queue(Model model,Long skuId, HttpServletRequest request,String skuIdStr){
        //获得用户的id然后对比我们的抢购码
       String userId= request.getHeader("userId");
       String checkCode= MD5.encrypt(userId);
       //检查我们的抢购码
       if(null!=skuIdStr&&checkCode.equals(skuIdStr)){
           model.addAttribute("skuId",skuId);
           model.addAttribute("skuIdStr",skuIdStr);
           return "seckill/queue";
       }else {
           return "seckill/fail";
       }


    }
}
