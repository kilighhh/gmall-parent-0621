package com.atguigu.gmall.item.service;

import java.util.Map;

/**
 * @Author Kilig Zong
 * @Date 2020/12/2 12:18
 * @Version 1.0
 */
public interface ItemService {
    Map<String, Object> getItem(Long skuId);
}
