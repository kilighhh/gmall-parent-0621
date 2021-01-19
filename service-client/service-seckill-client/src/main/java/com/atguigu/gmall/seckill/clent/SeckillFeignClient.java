package com.atguigu.gmall.seckill.clent;

import com.atguigu.gmall.model.activity.OrderRecode;
import com.atguigu.gmall.model.activity.SeckillGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/23 20:18
 * @Version 1.0
 */
@FeignClient("service-activity")
public interface SeckillFeignClient {
    @RequestMapping("api/activity/seckill/findAll")
    List<SeckillGoods> findAll();
    @RequestMapping("api/activity/seckill/getItem/{skuId}")
    SeckillGoods getItem(@PathVariable("skuId")Long skuId);
    @RequestMapping("api/activity/seckill/getOrderRecode/{userId}")
    OrderRecode getOrderRecode(@PathVariable("userId")String userId);
}
