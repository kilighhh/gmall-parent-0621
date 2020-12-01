package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

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
}
