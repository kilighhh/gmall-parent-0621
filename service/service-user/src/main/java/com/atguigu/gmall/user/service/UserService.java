package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/12/15 17:55
 * @Version 1.0
 */
public interface UserService {
    UserInfo verify(String token);

    UserInfo login(UserInfo userInfo);

    List<UserAddress> findUserAddressListByUserId(String userId);
}
