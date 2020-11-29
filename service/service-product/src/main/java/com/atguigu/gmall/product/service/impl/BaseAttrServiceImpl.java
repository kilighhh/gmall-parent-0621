package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/11/28 18:49
 * @Version 1.0
 */
@Service
public class BaseAttrServiceImpl implements BaseAttrService {

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseAttrInfo> attrInfoList(Long cartegory3Id) {
        return baseAttrInfoMapper.selectAttrInfoList(3,cartegory3Id);
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_id",attrId);
        List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.selectList(wrapper);
        return baseAttrValueList;
    }

    /***
     * @author Kilig Zong
     * @date 2020/11/28 20:24
     * @description 这个方法根据传来的参数是否携带id来决定是保存还是修改,注意要保存平台属性表和平台属性值的表。
     * 还有
     * @param baseAttrInfo
     * @return void
     **/
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        Long id = baseAttrInfo.getId();
        //如果id是null就是保存否则就是修改
        if(null==id||id<=0){
            baseAttrInfoMapper.insert(baseAttrInfo);
            Long attrInfoId = baseAttrInfo.getId();
            id=attrInfoId;
        }else {
            //如果id不是null就是修改
            baseAttrInfoMapper.updateById(baseAttrInfo);
            //这删除是因为删除之后添加效率高
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id",id);
            baseAttrValueMapper.delete(wrapper);
        }
        //往平台属性值列表中添加数据，这里记得添加平台属性的id
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            baseAttrValue.setAttrId(id);
            baseAttrValueMapper.insert(baseAttrValue);
        }
    }
}
