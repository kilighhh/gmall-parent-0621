package com.atguigu.gmall.user.client;

import com.atguigu.gmall.model.user.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/12/15 17:41
 * @Version 1.0
 */
@FeignClient(value = "service-user")
public interface UserFeignClient {
    @RequestMapping("/api/user/passport/verify/{token}")
    Map<String, Object> verify(@PathVariable("token") String token);
    @RequestMapping("/api/user/passport/findUserAddressListByUserId/{userId}")
    List<UserAddress> findUserAddressListByUserId(@PathVariable("userId")String userId);
}
