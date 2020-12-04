package com.atguigu.gmall.product.clent;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/12/2 12:23
 * @Version 1.0
 */
@FeignClient(value = "service-product")
public interface ProductFeignClient {
    @RequestMapping("api/product/getPrice/{skuId}")
    BigDecimal getPrice(@PathVariable("skuId") Long skuId);
    @RequestMapping("api/product/getSkuInfoById/{skuId}")
    SkuInfo getSkuInfoById(@PathVariable("skuId") Long skuId);
    @RequestMapping("api/product/getSpuSaleAttrBySpuId/{spuId}")
    List<SpuSaleAttr> getSpuSaleAttrBySpuId(@PathVariable("spuId")Long spuId);
    @RequestMapping("api/product/getCategoryViewByCategory3Id/{category3Id}")
    BaseCategoryView getCategoryViewByCategory3Id( @PathVariable("category3Id")Long category3Id);
    @RequestMapping("api/product/getSpuSaleAttrListCheckBySku/{spuId}/{skuId}")
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("spuId")Long spuId, @PathVariable("skuId")Long skuId);
    @RequestMapping("api/product/getValueIdsMap/{spuId}")
    Map<String, Long> getValueIdsMap(@PathVariable("spuId")Long spuId);
}
