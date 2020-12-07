package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Kilig Zong
 * @Date 2020/11/28 16:24
 * @Version 1.0
 */
@RestController
@RequestMapping("admin/product/")
@CrossOrigin
@Slf4j
public class BaseAttrInfoApiController {

    @Autowired
    private BaseAttrService baseAttrService;

    /***
     * @author Kilig Zong
     * @date 2020/11/28 18:48
     * @description 根据三级分类以及级别查询平台属性
     * @param cartegory3Id
     * @return com.atguigu.gamll.common.result.Result
     **/
    @GetMapping("attrInfoList/{cartegory1Id}/{cartegory2Id}/{cartegory3Id}")
    public Result attrInfoList(@PathVariable Long cartegory3Id){
     List<BaseAttrInfo> attrInfoList= baseAttrService.attrInfoList(cartegory3Id);
     return  Result.ok(attrInfoList);
    }
    /***
     * @author Kilig Zong
     * @date 2020/11/28 19:50
     * @description 根据平台属性id来获取平台属性值
     * @param attrId
     * @return com.atguigu.gamll.common.result.Result
     **/
    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable Long attrId){
        List<BaseAttrValue> baseAttrValueList= baseAttrService.getAttrValueList(attrId);
        return Result.ok(baseAttrValueList);
    }

    /***
     * @author Kilig Zong
     * @date 2020/11/28 20:21
     * @description 根据我们前台是否传来的BaseAttrInfo的id来决定是添加还是修改
     * @param baseAttrInfo
     * @return com.atguigu.gamll.common.result.Result
     **/
    @RequestMapping("saveAttrInfo")
    public  Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

}
