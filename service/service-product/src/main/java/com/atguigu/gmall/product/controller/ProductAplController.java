package com.atguigu.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.BaseCategoryService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SupInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/12/2 12:31
 * @Version 1.0 这个controller是专门写给feign调用的
 */
@RequestMapping("api/product/")
@RestController
@CrossOrigin
public class ProductAplController {

    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SupInfoService supInfoService;
    @Autowired
    private BaseCategoryService baseCategoryService;
    /***
     * @author Kilig Zong
     * @date 2020/12/2 19:01
     * @description 查询sku具体的id
     * @param skuId
     * @return java.math.BigDecimal
     **/
    @RequestMapping("getPrice/{skuId}")
   public BigDecimal getPrice(@PathVariable("skuId")  Long skuId){
        BigDecimal bigDecimal =skuInfoService.getPrice(skuId);
        return bigDecimal;
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/2 19:02
     * @description 查询skuinfo的具体数据，记得还需要查询图片url集合set进去
     * @param skuId
     * @return com.atguigu.gmall.model.product.SkuInfo
     **/
    @RequestMapping("getSkuInfoById/{skuId}")
    public SkuInfo getSkuInfoById(@PathVariable("skuId") Long skuId){
        SkuInfo skuInfo=  skuInfoService.getSkuInfoById(skuId);
        return skuInfo;
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/2 19:45
     * @description 查勋销售属性的数据，以及它值也需要查询  此方法被弃用
     * @param spuId
     * @return java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     **/
    @RequestMapping("getSpuSaleAttrBySpuId/{spuId}")
   public List<SpuSaleAttr> getSpuSaleAttrBySpuId(@PathVariable("spuId")Long spuId){
        List<SpuSaleAttr> spuSaleAttrList = supInfoService.spuSaleAttrList(spuId);
        return spuSaleAttrList;
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/4 12:02
     * @description 查询spu销售属性以及销售属性值，并且携带被默认选中的属性设置为1
     * @param spuId
     * @param skuId
     * @return java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     **/
    @RequestMapping("getSpuSaleAttrListCheckBySku/{spuId}/{skuId}")
  public   List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("spuId") Long spuId, @PathVariable("skuId")Long skuId){
        List<SpuSaleAttr> spuSaleAttrList=  supInfoService.getSpuSaleAttrListCheckBySku(spuId,skuId);
        return spuSaleAttrList;
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/2 19:56
     * @description 查询sku对应导航栏的数据,他是查询base_category_view视图的数据，视图的本质是临时数据表，本质是一句sql语句
     * @param category3Id
     * @return com.atguigu.gmall.model.product.BaseCategoryView
     **/
    @RequestMapping("getCategoryViewByCategory3Id/{category3Id}")
     public  BaseCategoryView getCategoryViewByCategory3Id(@PathVariable("category3Id")Long category3Id){
        BaseCategoryView baseCategoryView= baseCategoryService.getCategoryViewByCategory3Id(category3Id);
        return baseCategoryView;
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/4 15:03
     * @description 我们前端需要一个销售属性与skuId的map集合 并且是通过spuId查询出来的（"sale_attr_value_id|sale_attr_value_id",skuId）
     * @param spuId
     * @return java.util.Map<java.lang.String, java.lang.Long>
     **/
    @RequestMapping("getValueIdsMap/{spuId}")
   public Map<String, Long> getValueIdsMap(@PathVariable("spuId")Long spuId){
        Map<String, Long> idsMap= supInfoService.getValueIdsMap(spuId);
        return idsMap;
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/7 18:01
     * @description 获取首页页面一二三级分类数据
     * @param
     * @return java.util.List<com.alibaba.fastjson.JSONObject>
     **/
    @RequestMapping("getBaseCategoryList")
   public List<JSONObject> getBaseCategoryList(){
        List<JSONObject> list=  baseCategoryService.getBaseCategoryList();
        return list;
    }
}
