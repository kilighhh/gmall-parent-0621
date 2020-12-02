package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.clent.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/12/2 12:19
 * @Version 1.0
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductFeignClient productFeignClient;
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
       Map<String, Object> map = new HashMap<>();
    //Price 价格数据
     BigDecimal price = productFeignClient.getPrice(skuId);
     //skuInfo 详情数据
     SkuInfo skuInfo=productFeignClient.getSkuInfoById(skuId);
     //spuSaleAttr销售属性数据,根据spuId来查询销售属性数据
        Long spuId = skuInfo.getSpuId();
     List<SpuSaleAttr> spuSaleAttrList =productFeignClient.getSpuSaleAttrBySpuId(spuId);
     //导航栏数据，就是一级二级三级分类数据
        Long category3Id = skuInfo.getCategory3Id();
     BaseCategoryView categoryView=productFeignClient.getCategoryViewByCategory3Id(category3Id);
     map.put("categoryView",categoryView);
        map.put("spuSaleAttrList",spuSaleAttrList);
        map.put("skuInfo",skuInfo);
     map.put("price",price);
        return map;
    }
}
