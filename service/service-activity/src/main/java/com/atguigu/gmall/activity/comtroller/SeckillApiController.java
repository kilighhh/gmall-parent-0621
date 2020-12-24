package com.atguigu.gmall.activity.comtroller;

import com.atguigu.gmall.activity.service.SeckillService;
import com.atguigu.gmall.activity.util.CacheHelper;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.activity.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity/seckill")
public class SeckillApiController {

    @Autowired
    private SeckillService seckillService;

    /***
     * @author Kilig Zong
     * @date 2020/12/23 22:53
     * @description 准备抢单
     * @param skuId
     * @param skuIdStr
     * @param request
     * @return com.atguigu.gmall.common.result.Result
     **/
    //http://api.gmall.com/api/activity/seckill/auth/seckillOrder/skuId?skuIdStr=null
    @RequestMapping("auth/seckillOrder/{skuId}")
    public Result seckillOrder(@PathVariable("skuId") Long skuId,String skuIdStr,HttpServletRequest request){
        String userId= request.getHeader("userId");
        Map<String,Object> map = seckillService.seckillOrder(skuId,userId);
        return Result.ok();
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/23 21:22
     * @description 我们先验证一下我们的是否还能抢购我们的秒杀商品，生成我们的唯一的抢购码
     * @param skuId
     * @param request
     * @return com.atguigu.gmall.common.result.Result
     **/
    //http://api.gmall.com/api/activity/seckill/auth/getSeckillSkuIdStr/30
    @RequestMapping("auth/getSeckillSkuIdStr/{skuId}")
    public  Result getSeckillSkuIdStr(@PathVariable("skuId") Long skuId, HttpServletRequest request){
        //首先我们需要得到我们商品上架的信息
     String status= (String)CacheHelper.get(skuId+"");
     if(null!=status&&status.equals("1")){
         String userId=request.getHeader("userId");
         String skuIdStr = MD5.encrypt(userId);
         return Result.ok(skuIdStr);
     }else {
         return Result.fail();
     }
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/23 21:11
     * @description 查询我们秒杀商品的详情
     * @param skuId
     * @return com.atguigu.gmall.model.activity.SeckillGoods
     **/
    @RequestMapping("getItem/{skuId}")
   public SeckillGoods getItem(@PathVariable("skuId")Long skuId){
        SeckillGoods seckillGoods=seckillService.getItem(skuId);
        return seckillGoods;
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/23 20:28
     * @description 从我们的缓存中查询我们发布的商品
     * @param
     * @return java.util.List<com.atguigu.gmall.model.activity.SeckillGoods>
     **/
    @RequestMapping("findAll")
   public List<SeckillGoods> findAll(){
        List<SeckillGoods> seckillGoods=seckillService.findAll();
        return seckillGoods;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/23 18:42
     * @description 测试查看我们的缓存中是否有数据
     * @param skuId
     * @return com.atguigu.gmall.common.result.Result
     **/
    @RequestMapping("testCacheHelper/{skuId}")
    public Result testCacheHelper(@PathVariable ("skuId") Long skuId){

        return Result.ok(CacheHelper.get(skuId+""));
    }

    @RequestMapping("putGoods/{skuId}")
    public Result putGoods(@PathVariable ("skuId") Long skuId){
        seckillService.putGoods(skuId);
        return Result.ok();
    }


}