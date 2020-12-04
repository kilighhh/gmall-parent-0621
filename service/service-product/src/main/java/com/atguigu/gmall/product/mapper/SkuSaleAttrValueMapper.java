package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/12/1 17:58
 * @Version 1.0
 */
@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {
    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(@Param("spuId") Long spuId,@Param("skuId") Long skuId);

    List<Map> selectValueIdsMap(@Param("spuId") Long spuId);
}
