package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.BaseCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author Kilig Zong
 * @Date 2020/11/28 11:39
 * @Version 1.0
 */
@Service
public class BaseCategoryServiceImpl implements BaseCategoryService {
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;
    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;
    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;
    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    @Override
    public List<BaseCategory1> getCategory1List() {
        List<BaseCategory1> baseCategory1List = baseCategory1Mapper.selectList(null);
        return baseCategory1List;
    }

    @Override
    public List<BaseCategory2> getCategory2List(Long category1Id) {
        QueryWrapper<BaseCategory2> wrapper = new QueryWrapper<>();
        wrapper.eq("category1_id", category1Id);
        List<BaseCategory2> baseCategory2List = baseCategory2Mapper.selectList(wrapper);
        return baseCategory2List;
    }

    @Override
    public List<BaseCategory3> getCategory3List(Long category2Id) {
        QueryWrapper<BaseCategory3> wrapper = new QueryWrapper<>();
        wrapper.eq("category2_id", category2Id);
        List<BaseCategory3> baseCategory3List = baseCategory3Mapper.selectList(wrapper);
        return baseCategory3List;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/2 20:02
     * @description 这里查询的是视图，视图的本质是一句sql语句
     * @param category3Id
     * @return com.atguigu.gmall.model.product.BaseCategoryView
     **/
    @Override
    public BaseCategoryView getCategoryViewByCategory3Id(Long category3Id) {
        QueryWrapper<BaseCategoryView> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id",category3Id);
        BaseCategoryView baseCategoryView = baseCategoryViewMapper.selectOne(wrapper);
        return baseCategoryView;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/7 18:03
     * @description 获取前端一二三级分类的数据
     * @param
     * @return java.util.List<com.alibaba.fastjson.JSONObject>
     **/
    @Override
    public List<JSONObject> getBaseCategoryList() {
        //从数据库获得数据
        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);
        //创建一个返回给前端页面的数据
        List<JSONObject> list = new ArrayList<>();
        // 将CategoryList 转化为JSONObject
        Map<Long, List<BaseCategoryView>> category1Map = baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //这个集合category1Map是以一级分类的id作为key的map集合 下面将集合遍历出来将二级分类筛选出来
        for (Map.Entry<Long, List<BaseCategoryView>> category1Object : category1Map.entrySet()) {
            //获得一级分类的id和name属性
            Long category1Id = category1Object.getKey();
            String category1Name = category1Object.getValue().get(0).getCategory1Name();
            //这里要转换成JSONObject格式，因为前端需要这样的数据格式
            JSONObject category1Json = new JSONObject();
            category1Json.put("categoryId",category1Id);
            category1Json.put("categoryName",category1Name);
            //二级分类的集合
            List<JSONObject> category2List = new ArrayList<>();
            List<BaseCategoryView> category2Views = category1Object.getValue();
            //根据分组的方法获得一个以在一级分类下以二级分类id分类的map
            Map<Long, List<BaseCategoryView>> category2Map = category2Views.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            for (Map.Entry<Long, List<BaseCategoryView>> category2Object : category2Map.entrySet()) {
                //获得二级分类的id和name属性
                Long category2Id = category2Object.getKey();
                String category2Name = category2Object.getValue().get(0).getCategory2Name();
                //这里要转换成JSONObject格式，因为前端需要这样的数据格式
                JSONObject category2Json = new JSONObject();
                category2Json.put("categoryId",category2Id);
                category2Json.put("categoryName",category2Name);
                //三级分类的集合
                List<JSONObject> category3List = new ArrayList<>();
                List<BaseCategoryView> category3Views = category2Object.getValue();
                //根据分组的方法获得一个以在二级分类下以三级分类id分类的map
                Map<Long, List<BaseCategoryView>> category3Map = category3Views.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id));
                for (Map.Entry<Long, List<BaseCategoryView>> category3Object : category3Map.entrySet()) {
                    //获得二级分类的id和name属性
                    Long category3Id = category3Object.getKey();
                    String category3Name = category3Object.getValue().get(0).getCategory3Name();
                    //这里要转换成JSONObject格式，因为前端需要这样的数据格式
                    JSONObject category3Json = new JSONObject();
                    category3Json.put("categoryId",category3Id);
                    category3Json.put("categoryName",category3Name);
                    category3List.add(category3Json);
                }
                //将前端的数据添加进集合里面
                category2Json.put("categoryChild",category3List);
                category2List.add(category2Json);
            }
            //将前端的数据添加进集合里面
            category1Json.put("categoryChild",category2List);
            list.add(category1Json);

        }
        return list;
    }
}
