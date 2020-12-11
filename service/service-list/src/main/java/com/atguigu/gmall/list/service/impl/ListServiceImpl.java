package com.atguigu.gmall.list.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.utils.JSONUtils;
import com.atguigu.gmall.list.respository.GoodsElasticsearchRepository;
import com.atguigu.gmall.model.list.*;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.clent.ProductFeignClient;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.ListService;
import org.apache.commons.lang.time.DateUtils;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Kilig Zong
 * @Date 2020/12/7 17:51
 * @Version 1.0
 */
@Service
public class ListServiceImpl implements ListService {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private GoodsElasticsearchRepository goodsElasticsearchRepository;
    @Autowired
    private ElasticsearchRestTemplate restTemplate;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /***
     * @author Kilig Zong
     * @date 2020/12/7 17:59
     * @description 获取首页的一二三级分类数据
     * @param
     * @return java.util.List<com.alibaba.fastjson.JSONObject>
     **/
    @Override
    public List<JSONObject> getBaseCategoryList() {
        List<JSONObject> list=  productFeignClient.getBaseCategoryList();
        return list;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/9 17:23
     * @description 商品下架功能
     * @param skuInfoId
     * @return void
     **/
    @Override
    public void cancelSale(Long skuInfoId) {
        //先判断es搜索引擎有没有这条数据
        Optional<Goods> idRet = goodsElasticsearchRepository.findById(skuInfoId);
        if(null==idRet){
            return;
        }
        goodsElasticsearchRepository.deleteById(skuInfoId);
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/9 17:23
     * @description 商品上架功能 这里需要将数据从数据库查出，在放到es里面,我们这里需要四种数据
     * 1.skuInfo的具体数据
     * 2.平台销售属性以及平台销售属性值
     * 3.一二三级分类的数据
     * 4.品牌名
     * @param skuInfoId
     * @return void
     **/
    @Override
    public void onSale(Long skuInfoId) throws Exception {
        //设置时间格式,但是不建议，太麻烦了
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
       String formatDate = format.format(new Date());
        Date date = format.parse(formatDate);
        //先创建一个goods
        Goods goods = new Goods();
        //1.skuInfo的具体数据
        SkuInfo skuInfo = productFeignClient.getSkuInfoById(skuInfoId);
        // 2.平台销售属性以及平台销售属性值
        List<SearchAttr>  searchAttrs=productFeignClient.getSearchAttrList(skuInfoId);
        //3.一二三级分类的数据
        BaseCategoryView categoryView = productFeignClient.getCategoryViewByCategory3Id(skuInfo.getCategory3Id());
        //4.品牌名
         BaseTrademark baseTrademark=productFeignClient.getTradeMark(skuInfo.getTmId());
         //设置goods的数据 商品Id
         goods.setId(skuInfoId);
        // 默认图片
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        // title = skuName
        goods.setTitle(skuInfo.getSkuName());
        // 商品价格
        goods.setPrice(skuInfo.getPrice().doubleValue());
        // 创建时间
        goods.setCreateTime(date);
        // 品牌Id
        goods.setTmId(baseTrademark.getId());
        // 品牌名称
        goods.setTmName(baseTrademark.getTmName());
        // 品牌的logo
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        // 一级分类Id
        goods.setCategory1Id(categoryView.getCategory1Id());
        // 一级分类name
        goods.setCategory1Name(categoryView.getCategory1Name());
        // 二级分类Id
        goods.setCategory2Id(categoryView.getCategory2Id());
        // 二级分类name
        goods.setCategory2Name(categoryView.getCategory2Name());
        // 三级分类Id
        goods.setCategory3Id(categoryView.getCategory3Id());
        // 三级分类name
        goods.setCategory3Name(categoryView.getCategory3Name());
        // 热度排名
        goods.setHotScore(0l);
        // 平台属性集合对象
        // Nested 支持嵌套查询
        goods.setAttrs(searchAttrs);
        goodsElasticsearchRepository.save(goods);
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/9 18:50
     * @description 创建一个搜索库
     * @param
     * @return void
     **/
    @Override
    public void createGoodsIndex() {
        restTemplate.createIndex(Goods.class);
        restTemplate.putMapping(Goods.class);
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/11 11:22
     * @description 根据搜索条件在es中进行搜索 在这里我们需要进行抽取
     * 1.将查询结果的条件封装
     * 2.将返回结果进行封装
     * @param searchParam
     * @return com.atguigu.gmall.model.list.SearchResponseVo
     **/
    @Override
    public SearchResponseVo list(SearchParam searchParam) {
        //将前端传来的参数转换成我们es规定的查询参数
        SearchRequest searchRequest=getSearchRequest(searchParam);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将我们系统查询结果解析成我们需要显示的页面
        SearchResponseVo searchResponseVo= parseSearchResponse(searchResponse);
//        //获取品牌的id 名字 还有logo的url地址 就是获取品牌的基本信息
//         List<SearchResponseTmVo> searchResponseTmVos = getSearchResponseTmVoS(searchResponse);
//        //设置回searchResponseVo里面
//        searchResponseVo.setTrademarkList(searchResponseTmVos);
        return searchResponseVo;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/11 17:04
     * @description 将结果集中的品牌信息解析然后封装
     * @param searchResponse
     * @return java.util.List<com.atguigu.gmall.model.list.SearchResponseTmVo>
     **/
    private List<SearchResponseTmVo> getSearchResponseTmVoS(SearchResponse searchResponse) {
        //创建一个VO的集合然后返回回去
        List<SearchResponseTmVo> searchResponseTmVos = new ArrayList<>();
        //这个是聚合查询的结果
        Aggregations aggregations = searchResponse.getAggregations();
        //获取第一个聚合查询的结果然后在他的基础上可以再次获取他的子聚合查询
        ParsedLongTerms tmIdAgg = (ParsedLongTerms) aggregations.get("tmIdAgg");
        //获取分组
        List<? extends Terms.Bucket> buckets = tmIdAgg.getBuckets();
        for (Terms.Bucket tmIdAggBucket : buckets) {
            //创建一个品牌的对象
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            //获取品牌id
            long tmIdKey = tmIdAggBucket.getKeyAsNumber().longValue();
            //获取品牌名字
            ParsedStringTerms tmNameAgg = (ParsedStringTerms) tmIdAggBucket.getAggregations().get("tmNameAgg");
            String tmNameKey = tmNameAgg.getBuckets().get(0).getKeyAsString();
            //获取品牌LogoUrl
            ParsedStringTerms tmLogoUrlAgg = (ParsedStringTerms) tmIdAggBucket.getAggregations().get("tmLogoUrlAgg");
            String tmLogoUrlKey = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
            //set进Vo
            searchResponseTmVo.setTmId(tmIdKey);
            searchResponseTmVo.setTmName(tmNameKey);
            searchResponseTmVo.setTmLogoUrl(tmLogoUrlKey);
            //传进集合里面
            searchResponseTmVos.add(searchResponseTmVo);
        }

        return searchResponseTmVos;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/11 11:30
     * @description 将前端传来的查询结果封装成es中我们规定的查询条件
     * @param searchParam
     * @return org.elasticsearch.action.search.SearchRequest
     **/
    private SearchRequest getSearchRequest(SearchParam searchParam) {
        SearchRequest searchRequest = new SearchRequest();
        //如果我们是参数是空的话就是直接将es中所有的数据都查询出来
        searchRequest.indices("goods");
        searchRequest.types("info");
        //如果有参数的话，就把参数set进去
        //es的查询的api是source，他所需要的参数是searchSourceBuilder，所以我们需要自己创建
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //这里需要查询的语句的所需要的条件，这里是条件查询
        Long category3Id = searchParam.getCategory3Id();
        if(null!=category3Id&&category3Id>0){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category3Id",category3Id);
            //嵌套查询要根据原生的sql
            searchSourceBuilder.query(termQueryBuilder);
        }
        //下列是分组聚合查询,要查询品牌的名字，id 还有logo图片 subAggregation代表子聚合查询
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("tmIdAgg").field("tmId")
                        .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                        .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));
        //聚合查询所需要的Builder查询api
        searchSourceBuilder.aggregation(aggregationBuilder);
        //最初所需要的搜索 或者说根据三级分类id来进行查询
        searchRequest.source(searchSourceBuilder);
        System.out.println(searchRequest);
        return searchRequest;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/11 11:29
     * @description 将在es中搜索出来的结果进行封装成我们想要的结果
     * @param searchResponse
     * @return com.atguigu.gmall.model.list.SearchResponseVo
     **/
    private SearchResponseVo parseSearchResponse(SearchResponse searchResponse) {
        //我们需要将在es搜索出来的结果解析成vo 创建vo
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        //我们原生的es搜索出来的结果对比发现有两个hits，所以要解析两次
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hitsResult = hits.getHits();
        if (null!=hitsResult&&hitsResult.length>0){
            //创建我们vo当中的goods的集合
            List<Goods> list = new ArrayList<>();
            for (SearchHit hit : hitsResult) {
                //这里我们会得到一条json数据
                String sourceAsString = hit.getSourceAsString();
                //这里我们需要将json数据转化成我们需要的vo
                Goods goods = JSON.parseObject(sourceAsString, Goods.class);
                //把我们的goods放进集合里面
                list.add(goods);
            }
            searchResponseVo.setGoodsList(list);
            //下面是流式编程 将他抽取成一个方法
            //List<SearchResponseTmVo> searchResponseTmVos = getSearchResponseStreamTmVos(searchResponse);
            //下面不是流式编程 就是利用goods get出我们的品牌信息 然后返回一个list
            List<SearchResponseTmVo> searchResponseTmVos =getSearchResponseSetTmVos(list);
            //下面不是流式编程，将品牌信息抽取出来
            //List<SearchResponseTmVo> searchResponseTmVos =getSearchResponseTmVoS(searchResponse);
            searchResponseVo.setTrademarkList(searchResponseTmVos);
        }
        return searchResponseVo;
    }
    /***
     * @author Kilig Zong
     * @date 2020/12/11 20:20
     * @description 最简单的获取品牌信息的方法
     * @param list
     * @return java.util.List<com.atguigu.gmall.model.list.SearchResponseTmVo>
     **/
    private List<SearchResponseTmVo> getSearchResponseSetTmVos(List<Goods> list){
        //创建一个set
        Set<SearchResponseTmVo> hashSet = new HashSet<>();
        //循环迭代叠加
        for (Goods goods : list) {
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            searchResponseTmVo.setTmName(goods.getTmName());
            searchResponseTmVo.setTmLogoUrl(goods.getTmLogoUrl());
            searchResponseTmVo.setTmId(goods.getTmId());
            hashSet.add(searchResponseTmVo);
        }
        //利用流式编程返回
        List<SearchResponseTmVo> searchResponseTmVos= hashSet.stream().collect(Collectors.toList());
        return  searchResponseTmVos;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/11 18:46
     * @description 利用流式编程查询商标聚合信息
     * @param searchResponse
     * @return java.util.List<com.atguigu.gmall.model.list.SearchResponseTmVo>
     **/
    private List<SearchResponseTmVo> getSearchResponseStreamTmVos(SearchResponse searchResponse) {
        //以下设置商标聚合的信息
        ParsedLongTerms tmIdAgg = (ParsedLongTerms) searchResponse.getAggregations().get("tmIdAgg");
        //这里是利用流式编程
        List<SearchResponseTmVo> searchResponseTmVos= tmIdAgg.getBuckets().stream().map(tmIdAggBucket->{
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            //获取品牌的id
            long tmId =  tmIdAggBucket.getKeyAsNumber().longValue();
            //获取品牌的名字
            ParsedStringTerms tmNameAgg=(ParsedStringTerms)tmIdAggBucket.getAggregations().get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            //获取品牌的LogoUrl地址
            ParsedStringTerms tmLogoUrlAgg=(ParsedStringTerms)tmIdAggBucket.getAggregations().get("tmLogoUrlAgg");
            String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
            //将我们查询的品牌聚合的名字set进去
            searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            searchResponseTmVo.setTmName(tmName);
            searchResponseTmVo.setTmId(tmId);
            return searchResponseTmVo;
        }).collect(Collectors.toList());
        return searchResponseTmVos;
    }

}
