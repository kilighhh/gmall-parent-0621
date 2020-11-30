package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @Author Kilig Zong
 * @Date 2020/11/30 11:17
 * @Version 1.0
 */
public interface SupInfoService {
    IPage<SpuInfo> spuInfoList(Long pageNo, Long pageSize, Long category3Id);

    void saveSpuInfo(SpuInfo spuInfo);
}
