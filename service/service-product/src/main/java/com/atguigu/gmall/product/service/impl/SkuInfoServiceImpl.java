package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.product.mapper.SkuImageMapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/12/1 15:09
 * @Version 1.0
 */
@Service
public class SkuInfoServiceImpl implements SkuInfoService {
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;


    /***
     * @author Kilig Zong
     * @date 2020/12/1 18:01
     * @description 保存SkuInfo到数据库需要四张表
     * @param skuInfo
     * @return void
     **/
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //保存skuinfo信息到数据库
        skuInfoMapper.insert(skuInfo);
        Long skuInfoId = skuInfo.getId();
        //保存数据图片到数据库
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if(null!=skuImageList){
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfoId);
                skuImageMapper.insert(skuImage);
            }
        }
        //保存数据到sku_attr_value表中
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if(null!=skuAttrValueList){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfoId);
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
        //保存数据到sku_sale_attr_value中
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if(null!=skuSaleAttrValueList){
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuInfoId);
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/1 18:50
     * @description 分页查询sku的数据
     * @param pageNo
     * @param pageSize
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.atguigu.gmall.model.product.SkuInfo>
     **/
    @Override
    public IPage<SkuInfo> list(Long pageNo, Long pageSize) {
        //Page<SkuInfo> skuInfoPage = new Page<>();
        IPage<SkuInfo> skuInfoPage = new Page<>();
        //skuInfoPage.setPages(pageNo);
        skuInfoPage.setCurrent(pageNo);
        skuInfoPage.setSize(pageSize);
        skuInfoMapper.selectPage(skuInfoPage, null);
        long pages = skuInfoPage.getPages();
        System.out.println(pages);
        System.out.println(skuInfoPage.getSize());
        return skuInfoPage;
    }


    /***
     * @author Kilig Zong
     * @date 2020/12/1 19:53
     * @description 商品下架功能 但是后期得改成用nosql或者是es 1是上架 0是下架
     * @param skuInfoId
     * @return java.lang.Boolean
     **/
    @Override
    public boolean cancelSale(Long skuInfoId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuInfoId);
        skuInfo.setIsSale(0);
        int i = skuInfoMapper.updateById(skuInfo);
        // 清理nosql
        System.out.println("同步搜索引擎");
        if(i<=0){
            return false;
        }else {
            return true;
        }
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/1 19:54
     * @description 商品上架功能 但是后期得改成用nosql或者是es  1是上架 0是下架
     * @param skuInfoId
     * @return java.lang.Boolean
     **/
    @Override
    public boolean onSale(Long skuInfoId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuInfoId);
        skuInfo.setIsSale(1);
        int i = skuInfoMapper.updateById(skuInfo);
        // 清理nosql
        System.out.println("同步搜索引擎");
        if(i<=0){
            return false;
        }else {
            return true;
        }
    }
}
