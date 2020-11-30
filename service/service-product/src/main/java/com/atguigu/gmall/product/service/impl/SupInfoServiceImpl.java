package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.product.mapper.SpuSaleAttrValueMapper;
import com.atguigu.gmall.product.mapper.SupImageMapper;
import com.atguigu.gmall.product.mapper.SupInfoMapper;
import com.atguigu.gmall.product.service.SupInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/11/30 11:17
 * @Version 1.0
 */
@Service
public class SupInfoServiceImpl implements SupInfoService {

    @Autowired
    private SupInfoMapper supInfoMapper;
    @Autowired
    private SupImageMapper supImageMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    /***
     * @author Kilig Zong
     * @date 2020/11/30 11:39
     * @description 查询spu具体值
     * @param pageNo
     * @param pageSize
     * @param category3Id
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.atguigu.gmall.model.product.SpuInfo>
     **/
    @Override
    public IPage<SpuInfo> spuInfoList(Long pageNo, Long pageSize, Long category3Id) {
        Page<SpuInfo> spuInfoPage = new Page<>();
        spuInfoPage.setSize(pageSize);
        spuInfoPage.setCurrent(pageNo);
        QueryWrapper<SpuInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id",category3Id);
        IPage<SpuInfo> infoIPage = supInfoMapper.selectPage(spuInfoPage, wrapper);
        return infoIPage;
    }

    /***
     * @author Kilig Zong
     * @date 2020/11/30 12:15
     * @description 保存商家的编辑的spu信息，需要保存四张表的信息
     * @param spuInfo
     * @return void
     **/
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        supInfoMapper.insert(spuInfo);
        //获取id后续保存到表需要使用
        Long spuInfoId = spuInfo.getId();
        //先保存图片表的数据
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        //如果图片不为空就保存
        if(null!=spuImageList){
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuInfoId);
                supImageMapper.insert(spuImage);
            }
        }
        //保存平台属性的数据,也需要判断不为空
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if(null!=spuSaleAttrList){
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuInfoId);
                spuSaleAttrMapper.insert(spuSaleAttr);
                //保存平台销售属性值的数据，也需要判断不为空
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if(null!=spuSaleAttrValueList){
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfoId);
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());//需要从平台属性中获得
                        spuSaleAttrValue.setBaseSaleAttrId(spuSaleAttr.getBaseSaleAttrId());//其实我这里保存的是平台属性规定的id
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    }
                }


            }
        }


    }
}
