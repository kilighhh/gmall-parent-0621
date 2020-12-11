package com.atguigu.gmall.list.respository;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author Kilig Zong
 * @Date 2020/12/9 17:38
 * @Version 1.0
 */
public interface GoodsElasticsearchRepository extends ElasticsearchRepository<Goods,Long> {
}
