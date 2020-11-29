package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/11/28 18:46
 * @Version 1.0
 */
public interface BaseAttrService {
    List<BaseAttrInfo> attrInfoList(Long cartegory3Id);

    List<BaseAttrValue> getAttrValueList(Long attrId);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);
}
