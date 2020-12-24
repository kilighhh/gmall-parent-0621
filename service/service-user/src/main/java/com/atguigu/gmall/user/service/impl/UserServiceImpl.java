package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.mapper.UserMapper;
import com.atguigu.gmall.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author Kilig Zong
 * @Date 2020/12/15 17:56
 * @Version 1.0
 */
@Service
public class UserServiceImpl implements UserService {
  @Autowired
  private RedisTemplate redisTemplate;
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private UserAddressMapper userAddressMapper;
  /***
   * @author Kilig Zong
   * @date 2020/12/15 17:59
   * @description 通过token查看我们的redis是否存有这个token，是否一致
   * @param token
   * @return java.util.Map<java.lang.String, java.lang.Object>
   **/
    @Override
    public UserInfo verify(String token) {
        //我们存在redis的数据是kv键值对
        //我们key的构成是user:login:+token的值
        //我们的值的构成UserInfo
        UserInfo userInfo = (UserInfo)redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_KEY_PREFIX + token);
        return userInfo;
    }

    /***
     * @author Kilig Zong
     * @date 2020/12/16 10:19
     * @description 登录接口需要把token写入我们的缓存
     * @param userInfo
     * @return com.atguigu.gmall.model.user.UserInfo
     **/
    @Override
    public UserInfo login(UserInfo userInfo) {
        //先验证用户账户密码
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_name",userInfo.getLoginName());
        queryWrapper.eq("passwd", MD5.encrypt(userInfo.getPasswd()));
        userInfo=userMapper.selectOne(queryWrapper);
        //如果存在用户则将我们的token写入我们的缓存
        if (null==userInfo){
            return null;
        }else {
            String token = UUID.randomUUID().toString();
            userInfo.setToken(token);
            redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX + token,userInfo);
            //写入缓存
            return userInfo;
        }

    }

    /***
     * @author Kilig Zong
     * @date 2020/12/18 12:21
     * @description 根据我们的userId查询我们的用户的地址
     * @param userId
     * @return java.util.List<com.atguigu.gmall.model.user.UserAddress>
     **/
    @Override
    public List<UserAddress> findUserAddressListByUserId(String userId) {
        QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        List<UserAddress> addresses = userAddressMapper.selectList(wrapper);
        return addresses;
    }
}
