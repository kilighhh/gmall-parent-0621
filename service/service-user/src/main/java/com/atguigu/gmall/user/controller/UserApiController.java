package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/12/15 16:11
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/user/passport/")
public class UserApiController{
    @Autowired
    private UserService userService;

    /***
     * @author Kilig Zong
     * @date 2020/12/15 17:54
     * @description 我们这是要对token进行验证
     * @param token
     * @return java.util.Map<java.lang.String, java.lang.Object>
     **/
    @RequestMapping("verify/{token}")
   public Map<String, Object> verify(@PathVariable("token") String token){
        UserInfo userInfo = userService.verify(token);
        Map<String, Object> map = new HashMap<>();
        map.put("user",userInfo);
        return map;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/16 10:19
     * @description 登录接口
     * @param userInfo
     * @return com.atguigu.gmall.common.result.Result
     **/
    @RequestMapping("login")
    public Result login(@RequestBody UserInfo userInfo){
        userInfo=  userService.login(userInfo);
        if(null==userInfo){
            return Result.fail();
        }else {
            return Result.ok(userInfo);
        }
    }
}
