package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/1 15:09
 * @Version 1.0
 */
public interface SkuInfoService {
    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> list(Long pageNo, Long pageSize);

    boolean cancelSale(Long skuInfoId);

    boolean onSale(Long skuInfoId);

    BigDecimal getPrice(Long skuId);

    SkuInfo getSkuInfoById(Long skuId);


    List<SearchAttr> getSearchAttrList(Long skuInfoId);
}
