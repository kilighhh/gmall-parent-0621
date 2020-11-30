package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/11/30 11:48
 * @Version 1.0
 */
@Service
public class BaseTrademarkServiceImpl implements BaseTrademarkService {
    @Autowired
    private BaseTrademarkMapper trademarkMapper;
    /***
     * @author Kilig Zong
     * @date 2020/11/30 11:51
     * @description 查询品牌商的属性值
     * @param
     * @return java.util.List<com.atguigu.gmall.model.product.BaseTrademark>
     **/
    @Override
    public List<BaseTrademark> getTrademarkList() {
        QueryWrapper<BaseTrademark> wrapper = new QueryWrapper<>();
        List<BaseTrademark> trademarks = trademarkMapper.selectList(wrapper);
        return trademarks;
    }
}
