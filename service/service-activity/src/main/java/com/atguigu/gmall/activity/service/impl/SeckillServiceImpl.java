package com.atguigu.gmall.activity.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.activity.mapper.SeckillMapper;
import com.atguigu.gmall.activity.service.SeckillService;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.activity.UserRecode;
import com.atguigu.gmall.mq.service.RabbitService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author Kilig Zong
 * @Date 2020/12/23 18:59
 * @Version 1.0
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private SeckillMapper seckillMapper;
    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private RedisTemplate redisTemplate;
    /***
     * @author Kilig Zong
     * @date 2020/12/23 19:08
     * @description 将数据库的的数据全部push到我们的redis上面
     * @param skuId
     * @return void
     **/
    @Override
    public void putGoods(Long skuId) {
        //将我们的数据库的数据push
        QueryWrapper<SeckillGoods> wrapper = new QueryWrapper<>();
        wrapper.eq("sku_id",skuId);
        List<SeckillGoods> seckillGoods = seckillMapper.selectList(wrapper);
        //把这个放进我们的redis里面
        for (SeckillGoods seckillGood : seckillGoods) {
            //放入库存，就是我们外面抢购就是抢这里的，抢一下少一件产品
            for (int i = 0; i < seckillGood.getStockCount(); i++) {
                redisTemplate.opsForList().leftPush("seckill:stock:"+seckillGood.getSkuId(),seckillGood.getSkuId()+"");
            }
            //放入秒杀的商品表
            Map<String, Object> goodsMap = new HashMap<>();
            goodsMap.put(seckillGood.getSkuId()+"",seckillGood);
            //把需要的商品表放入我们的redis上面
            redisTemplate.opsForHash().putAll("seckill:goods",goodsMap);
            //发布入库的信息需要通知所有微服务,我们通知微服务，其他的微服务只要查看我们的缓存map就OK了
            redisTemplate.convertAndSend("seckillpush",seckillGood.getSkuId()+":1");
        }

    }

    /***
     * @author Kilig Zong
     * @date 2020/12/23 20:30
     * @description 查询nosql数据库中的数据
     * @param
     * @return java.util.List<com.atguigu.gmall.model.activity.SeckillGoods>
     **/
    @Override
    public List<SeckillGoods> findAll() {
        List<SeckillGoods> seckillGoods=(List<SeckillGoods>)redisTemplate.opsForHash().values("seckill:goods");
        return seckillGoods;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/23 21:13
     * @description 查询我们缓存中的数据
     * @param skuId
     * @return com.atguigu.gmall.model.activity.SeckillGoods
     **/
    @Override
    public SeckillGoods getItem(Long skuId) {
       SeckillGoods seckillGoods= (SeckillGoods)redisTemplate.opsForHash().get("seckill:goods",skuId+"");
        return seckillGoods;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/23 22:53
     * @description 准备抢单，这里防止抢单要判断我们的用户是否多次重复抢单
     * @param skuId
     * @param userId
     * @return java.util.Map<java.lang.String, java.lang.Object>
     **/
    @Override
    public Map<String, Object> seckillOrder(Long skuId, String userId) {
        Map<String, Object> map = new HashMap<>();
        //在这里加一个分布式的锁，只让一个对象60s只能抢一次
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("seckill.user:" + userId, 1, 60l, TimeUnit.SECONDS);
        if(aBoolean){
            //把我们的两个数据转化成一个对象传递过去
            UserRecode userRecode = new UserRecode();
            userRecode.setSkuId(skuId);
            userRecode.setUserId(userId);
            //发送抢单的消息队列
            rabbitService.sendMassage("exchange.direct.seckill.user","seckill.user", JSON.toJSONString(userRecode));
            map.put("success",true);
            return map;
        }else {
            map.put("success",false);
            return map;
        }
    }
}
