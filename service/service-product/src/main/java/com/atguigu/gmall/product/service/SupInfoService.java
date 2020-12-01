package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/11/30 11:17
 * @Version 1.0
 */
public interface SupInfoService {
    IPage<SpuInfo> spuInfoList(Long pageNo, Long pageSize, Long category3Id);

    void saveSpuInfo(SpuInfo spuInfo);

    List<SpuImage> spuImageList(Long spuId);

    List<SpuSaleAttr> spuSaleAttrList(Long spuId);
}
