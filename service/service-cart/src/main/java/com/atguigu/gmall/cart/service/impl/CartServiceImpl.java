package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.clent.ProductFeignClient;
import com.atguigu.gmall.service.config.GmallCache;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/12/14 12:20
 * @Version 1.0
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
     private CartInfoMapper cartInfoMapper;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private RedisTemplate redisTemplate;
    /***
     * @author Kilig Zong
     * @date 2020/12/14 13:18
     * @description 1.将我们添加的购物的所有的信息添加到我们的数据库中，打算我们的价格是敏感字段
     * 需要远程调用查询我们的skuInfo的商品价格
     * 2.在我们的的中间确认的页面上我们倒回去再次点击的时候理论是是修改我们数据库的信息
     * @param cartInfo
     * @return void
     **/
    @Override
    public void addCart(CartInfo cartInfo) {
        //获取我们前端传来的数据在数据库查询一下在数据库中是否添加过
        Long skuId = cartInfo.getSkuId();
        String userId = cartInfo.getUserId();
        Integer skuNum = cartInfo.getSkuNum();
        //判断购物车中是否添加过
        QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("sku_id",skuId);
        CartInfo cartInfoForDb = cartInfoMapper.selectOne(queryWrapper);
        //如果数据库中的没有信息则是插入
        if(cartInfoForDb==null){
            //因为还没有写单点登录功能所以先写死我们的userId
            //我们需要sku的信息，所以我们需要远程调用
            // 购物车保存时，没有skuPrice字段，因为一致性差，skuproce字段只能从sku表中查询
            SkuInfo skuInfo = productFeignClient.getSkuInfoById(cartInfo.getSkuId());
            //将sku的信息放进我们的cart里面
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setIsChecked(1);
            cartInfo.setSkuId(skuInfo.getId());
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setUserId(userId);
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setSkuNum(cartInfo.getSkuNum());
            cartInfoMapper.insert(cartInfo);
        }else {
            //否则是修改
            cartInfo=cartInfoForDb;
            cartInfo.setSkuNum(cartInfoForDb.getSkuNum()+skuNum);
            //这里修改的条件是按照两个id组成的唯一的主键
            cartInfoMapper.update(cartInfo,queryWrapper);
        }
        //这里缓存时利用map缓存
        //他这里的缓存分为大key 小key 还有值
        // 大Key：User:userId:cart
        //   小Key：skuId 值：skuInfo
        redisTemplate.opsForHash().put(RedisConst.USER_KEY_PREFIX+cartInfo.getUserId()+RedisConst.USER_CART_KEY_SUFFIX,cartInfo.getSkuId()+"",cartInfo);
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/14 13:25
     * @description 根据用户的id查询我们的购物车信息
     * @param cartInfo
     * @return java.util.List<com.atguigu.gmall.model.cart.CartInfo>
     **/
    //@GmallCache
    @Override
    public List<CartInfo> cartList(CartInfo cartInfo) {
       //在这里查询我们需要现在缓存中查询我们的数据
        List<CartInfo> cartInfos=(List<CartInfo>)redisTemplate.opsForHash().values(RedisConst.USER_KEY_PREFIX+cartInfo.getUserId()+RedisConst.USER_CART_KEY_SUFFIX);
        //如果我们的缓存中没有这个用户的购物车数据，则需要查询数据库
        if(null==cartInfos||cartInfos.size()<=0){
            //创建一个hashMap用于缓存我们的数据
            Map<String, Object> cacheMap = new HashMap<>();
            QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",cartInfo.getUserId());
            cartInfos = cartInfoMapper.selectList(queryWrapper);
            //如果数据库有信息的话就存进我们的缓存里面
            if(null!=cartInfos&&cartInfos.size()>0){
                for (CartInfo info : cartInfos) {
                    //这里存放的是小key
                    cacheMap.put(info.getSkuId()+"",info);
                }
                //同步缓存
                redisTemplate.opsForHash().putAll(RedisConst.USER_KEY_PREFIX+cartInfo.getUserId()+RedisConst.USER_CART_KEY_SUFFIX,cacheMap);
            }
        }
        //只有在页面展示的时候我们才需要将价格设置进去
        if(null!=cartInfos&&cartInfos.size()>0){
            for (CartInfo info : cartInfos) {
                info.setSkuPrice(productFeignClient.getPrice(info.getSkuId()));
                //info.setCartPrice(productFeignClient.getPrice(info.getSkuId()).multiply(new BigDecimal(info.getSkuNum())));
            }
        }
        return cartInfos;


    }
    /***
     * @author Kilig Zong
     * @date 2020/12/14 14:52
     * @description 根据用户id和skuId修改商品的数量
     * @param skuId
     * @param skuNum
     * @param userId
     * @return void
     **/
    @Override
    public void addToCart(Long skuId, Integer skuNum, String userId) {
        QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
        //根据用户id和skuId还有skuNum修改商品的数量
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("sku_id",skuId);
        queryWrapper.eq("sku_num",skuNum);
        CartInfo cartInfo = cartInfoMapper.selectOne(queryWrapper);
        //这里需要商品数量
        if(null!=cartInfo){
            cartInfo.setSkuNum(skuNum+1);
            cartInfo.setSkuId(skuId);
            cartInfo.setUserId(userId);
            cartInfoMapper.updateById(cartInfo);
        }
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/14 18:51
     * @description 修啊给i购物车的选中状态
     * @param cartInfo
     * @return void
     **/
    @Override
    public void checkCart(CartInfo cartInfo) {
        //修改数据库的信息
        QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",cartInfo.getUserId());
        queryWrapper.eq("sku_id",cartInfo.getSkuId());
        cartInfoMapper.update(cartInfo,queryWrapper);
        CartInfo cartInfoForDb = cartInfoMapper.selectOne(queryWrapper);
        //更新缓存的信息
        //这里缓存时利用map缓存
        //他这里的缓存分为大key 小key 还有值
        // 大Key：User:userId:cart
        //   小Key：skuId 值：skuInfo
        redisTemplate.opsForHash().put(RedisConst.USER_KEY_PREFIX+cartInfo.getUserId()+RedisConst.USER_CART_KEY_SUFFIX,cartInfoForDb.getSkuId()+"",cartInfoForDb);
    }
}
