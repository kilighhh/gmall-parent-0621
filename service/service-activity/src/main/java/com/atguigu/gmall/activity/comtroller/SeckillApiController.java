package com.atguigu.gmall.activity.comtroller;

import com.atguigu.gmall.activity.service.SeckillService;
import com.atguigu.gmall.activity.util.CacheHelper;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.activity.OrderRecode;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
    @Autowired
    private OrderFeignClient orderFeignClient;
    /***
     * @author Kilig Zong
     * @date 2020/12/25 20:22
     * @description 提交我们的订单，生成订单，删除我们的预订单
     * @param order
     * @return com.atguigu.gmall.common.result.Result
     **/
    @RequestMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo order,HttpServletRequest request, Model model){
        //保存我们的订单
        String userId = request.getHeader("userId");
        order.setUserId(Long.parseLong(userId));
        String orderId=orderFeignClient.submitOrder(order);
        // 删除预订单
        seckillService.deleteOrderRecode(userId);
        // 生成已提交用户订单
        seckillService.genOrderUsers(userId,orderId);
        return Result.ok(orderId);
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/25 20:10
     * @description 根据我们的用户id获取我们的秒杀商品
     * @param userId
     * @return com.atguigu.gmall.model.activity.OrderRecode
     **/
    @RequestMapping("getOrderRecode/{userId}")
   public OrderRecode getOrderRecode(@PathVariable("userId")String userId){
        OrderRecode orderRecode=seckillService.getOrderRecode(userId);
        return orderRecode;
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/25 20:50
     * @description 检查我们的预订单
     * @param skuId
     * @param request
     * @return com.atguigu.gmall.common.result.Result
     **/
    //http://api.gmall.com/api/activity/seckill/auth/checkOrder/30
    @RequestMapping("auth/checkOrder/{skuId}")
    public Result checkOrder(@PathVariable("skuId") String skuId,HttpServletRequest request){
        //先获取我们的userId，用户id
        String userId = request.getHeader("userId");
        //我们根据检查我们的秒杀的结果返回状态码
        //先判断是已经下单成功
        String orderId=seckillService.checkTrueOrder(userId);
        if(null!=orderId){
            return Result.build(orderId, ResultCodeEnum.SECKILL_ORDER_SUCCESS);
        }
        //判断是否下预订单成功
        OrderRecode orderRecode=seckillService.checkOrderRecode(userId);
        if(null!=orderRecode){
            return Result.build(orderRecode,ResultCodeEnum.SECKILL_SUCCESS);
        }
        //判断是否售罄
        String skuIdForCache=(String)CacheHelper.get(skuId+"");
        if(null==skuIdForCache||"0".equals(skuIdForCache)){
            return Result.build(null,ResultCodeEnum.FAIL);
        }
        //判断是否在排队
        return Result.build(null,ResultCodeEnum.SECKILL_RUN);

    }

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

    /***
     * @author Kilig Zong
     * @date 2020/12/25 18:53
     * @description 我们上架我们需要秒杀的商品，上架到我们的缓存
     * @param skuId
     * @return com.atguigu.gmall.common.result.Result
     **/
    @RequestMapping("putGoods/{skuId}")
    public Result putGoods(@PathVariable ("skuId") Long skuId){
        seckillService.putGoods(skuId);
        return Result.ok();
    }


}