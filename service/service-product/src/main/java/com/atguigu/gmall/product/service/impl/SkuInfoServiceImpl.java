package com.atguigu.gmall.product.service.impl;

import com.atguigu.gamll.common.constant.RedisConst;
import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.product.mapper.SkuImageMapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    //注入redisTemplate
    @Autowired
    private RedisTemplate redisTemplate;


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

    /***
     * @author Kilig Zong
     * @date 2020/12/2 18:49
     * @description 查询sku的商品价格
     * @param skuId
     * @return java.math.BigDecimal
     **/
    @Override
    public BigDecimal getPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        BigDecimal price = skuInfo.getPrice();
        return price;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/2 19:04
     * @description 查询skuinfo的具体数据也要查查询它的图片集合set进这个skuinfo里面
     * 在这里我们先查询redis如果redis没有数据则查询数据库,这里需要做防护做防止被缓存穿透的操作
     * @param skuId
     * @return com.atguigu.gmall.model.product.SkuInfo
     **/
    @Override
    public SkuInfo getSkuInfoById(Long skuId) {
        SkuInfo skuInfo = null;
        //访问nosql->redis 先查看nosql是否有这条数据
        //测试查询速度
        long start = System.currentTimeMillis();
        skuInfo  = (SkuInfo)redisTemplate.opsForValue().get(RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX);
        if(null==skuInfo){
            //访问数据库
            skuInfo = getSkuInfoByIdFromDb(skuId);
            //往redis存入数据
            redisTemplate.opsForValue().set(RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX,skuInfo);
        }
        long end = System.currentTimeMillis();
     long count= end-start;
        System.out.println("共花了"+count+"毫秒");
        return skuInfo;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/2 19:04
     * @description 查询skuinfo的具体数据也要查查询它的图片集合set进这个skuinfo里面
     * @param skuId
     * @return com.atguigu.gmall.model.product.SkuInfo
     **/
    private SkuInfo getSkuInfoByIdFromDb(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        //查询它携带的图片url集合
        QueryWrapper<SkuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("sku_id",skuId);
        List<SkuImage> skuImages = skuImageMapper.selectList(wrapper);
        skuInfo.setSkuImageList(skuImages);
        return skuInfo;
    }
}
