package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.SupInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

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

    /***
     * @author Kilig Zong
     * @date 2020/12/1 16:13
     * @description 查询spu的图片集合
     * @param spuId
     * @return java.util.List<com.atguigu.gmall.model.product.SpuImage>
     **/
    @Override
    public List<SpuImage> spuImageList(Long spuId) {
        QueryWrapper<SpuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);
        List<SpuImage> spuImageList = supImageMapper.selectList(wrapper);
        return spuImageList;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/1 16:13
     * @description 查询spu的销售属性以及他的值
     * @param spuId
     * @return java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     **/
    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        QueryWrapper<SpuSaleAttr> spuSaleAttrQueryWrapper = new QueryWrapper<>();
        spuSaleAttrQueryWrapper.eq("spu_id",spuId);
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.selectList(spuSaleAttrQueryWrapper);
        if(null!=spuSaleAttrList){
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                QueryWrapper<SpuSaleAttrValue> spuSaleAttrValueQueryWrapper = new QueryWrapper<>();
                spuSaleAttrValueQueryWrapper.eq("spu_id",spuId);
                spuSaleAttrValueQueryWrapper.eq("base_sale_attr_id",spuSaleAttr.getBaseSaleAttrId());
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttrValueMapper.selectList(spuSaleAttrValueQueryWrapper);
                spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValueList);
            }
        }
        return spuSaleAttrList;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/4 12:04
     * @description 查询销售属性表以及销售属性值表，并且携带被选中的属性设置为1
     * @param spuId
     * @param skuId
     * @return java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     **/
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long spuId, Long skuId) {
        List<SpuSaleAttr> spuSaleAttrList=skuSaleAttrValueMapper.selectSpuSaleAttrListCheckBySku(spuId,skuId);
        return spuSaleAttrList;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/4 15:23
     * @description 我们根据spuId来查询中间表，mybatis的返回值是一个Map
     * @param spuId
     * @return java.util.Map<java.lang.String, java.lang.Long>
     **/
    @Override
    public Map<String, Long> getValueIdsMap(Long spuId) {
      List<Map> saleMaps= skuSaleAttrValueMapper.selectValueIdsMap(spuId);
      //创建一个map返回回去
        Map<String, Long> idsMap = new HashMap<>();
        for (Map saleMap : saleMaps) {
            //根据数据库封装查询出来的map结构获取到值，封装成我们需要返回回去的Key
            String key = (String) saleMap.get("values_Ids");
            //根据数据库封装查询出来的map结构获取到值，封装成我们需要返回回去的Value
          Long value =(Long)saleMap.get("sku_id");
            idsMap.put(key,value);
        }
        return idsMap;
    }
}
