package com.atguigu.gmall.service.config;

/**
 * @Author Kilig Zong
 * @Date 2020/12/5 14:41
 * @Version 1.0
 */
public @interface GmallCache {
    public String SkuPrefix() default "sku:";
    public String SspuPrefix() default "spu:";
}
