package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.clent.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @Author Kilig Zong
 * @Date 2020/12/2 12:19
 * @Version 1.0
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;
    /***
     * @author Kilig Zong
     * @date 2020/12/2 12:27
     * @description 这里需要分别调用productFeignClient将四种数据查回再封装成一个Map
     *Price 价格数据
     * skuInfo 详情数据
     * image 图片数据
     * spuSaleAttr 销售属性数据
     * 以及分类属性
     * @param skuId
     * @return java.util.Map<java.lang.String, java.lang.Object>
     **/
    @Override
    public Map<String, Object> getItem(Long skuId) {
        Map<String, Object> resultMap = getItemByThread(skuId);
        return resultMap;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/7 11:16
     * @description 在多线程的方式下启动
     * @param skuId
     * @return java.util.Map<java.lang.String, java.lang.Object>
     **/
    private Map<String, Object> getItemByThread(Long skuId) {
        //方法启动开启时间
        long start = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        //开启异步多线程编排，查询skuInfo的详情信息 需要返回信息
        CompletableFuture<SkuInfo> completableFutureSkuInFo = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                //skuInfo 详情数据
                SkuInfo skuInfo=productFeignClient.getSkuInfoById(skuId);
                map.put("skuInfo",skuInfo);
                return skuInfo;
            }
        },threadPoolExecutor);
        //开启异步多线程编排，查询Price 价格数据的详情信息 无需返回信息
        CompletableFuture<Void> completableFuturePrice = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                BigDecimal price = productFeignClient.getPrice(skuId);
                map.put("price",price);
            }
        },threadPoolExecutor);
        //开启异步多线程编排，puSaleAttr销售属性数据,根据spuId来查询销售属性数据这里需要依赖skuInfo的信息 无需返回信息
        CompletableFuture<Void> completableFutureSpuSaleAttrList=completableFutureSkuInFo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                Long spuId = skuInfo.getSpuId();
                //这里没有被选中的属性
                //List<SpuSaleAttr> spuSaleAttrList =productFeignClient.getSpuSaleAttrBySpuId(spuId);
                //修改成有被选中属性的id
                List<SpuSaleAttr> spuSaleAttrList =  productFeignClient.getSpuSaleAttrListCheckBySku(spuId,skuId);
                map.put("spuSaleAttrList",spuSaleAttrList);
            }
        },threadPoolExecutor);
        //开启异步多线程编排，导航栏数据，就是一级二级三级分类数据,根据spuId来查询销售属性数据这里需要依赖skuInfo的信息 无需返回信息
        CompletableFuture<Void> completableFutureCategoryViewByCategory3Id=completableFutureSkuInFo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                Long category3Id = skuInfo.getCategory3Id();
                BaseCategoryView categoryView=productFeignClient.getCategoryViewByCategory3Id(category3Id);
                map.put("categoryView",categoryView);
            }
        },threadPoolExecutor);
        //开启异步多线程编排，我们这里还需要一组json数据发送给前端，是根据spuId查询出来的sku和销售属性值id的对应关系哈希表{"13|14",10},
        // 根据spuId来查询销售属性数据这里需要依赖skuInfo的信息 无需返回信息
        CompletableFuture<Void> completableFutureGetValueIdsMap=completableFutureSkuInFo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                Long spuId = skuInfo.getSpuId();
                Map<String,Long> jsonMap=productFeignClient.getValueIdsMap(spuId);
                String json = JSON.toJSONString(jsonMap);
                //System.out.println(json);
                //前端需要一个json字符串，所以返回一个json字符串回去
                map.put("valuesSkuJson",json);
            }
        },threadPoolExecutor);
        //这里是等待所有的子线程执行完后，主线程再启动
        CompletableFuture.allOf(completableFutureSkuInFo,completableFuturePrice,completableFutureSpuSaleAttrList
        ,completableFutureCategoryViewByCategory3Id,completableFutureGetValueIdsMap).join();
        long end = System.currentTimeMillis();
        System.out.println("消耗时间:"+(end-start));
        return map;
    }
}
