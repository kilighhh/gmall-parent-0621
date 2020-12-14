package com.atguigu.gmall.service.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author Kilig Zong
 * @Date 2020/12/5 14:43
 * @Version 1.0
 */
@Component
@Aspect
public class GmallCacheAspect {
    @Autowired
    private RedisTemplate redisTemplate;

    /***
     * @author Kilig Zong
     * @date 2020/12/5 14:52
     * @description 我们这边主要是环绕通知，方法前和方法后
     * @param point
     * @return java.lang.Object
     **/
    @Around("@annotation(com.atguigu.gmall.service.config.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point){
        //先设置返回值是null
        Object result =null;
        //设置缓存的key
        String cacheKey="";
        //先获取被代理的方法的信息
        MethodSignature methodSignature=(MethodSignature)point.getSignature();
        //获得方法的名字 方便做成缓存key
        String name = methodSignature.getMethod().getName();
        cacheKey=name;
        //返回类型和参数
        Class returnType = methodSignature.getReturnType();
        //获取参数
        Object[] args = point.getArgs();
        //循环遍历参数设置缓存key 这个key是分布式锁的key
        for (Object arg : args) {
            if(Object.class.isInstance(arg)){
                    arg=arg.hashCode();
                System.out.println("arg = " + arg);
            }
            cacheKey=cacheKey+":"+arg;
        }
        //注解信息 虽然没什么用
        GmallCache annotation = methodSignature.getMethod().getAnnotation(GmallCache.class);
        //先查询一波缓存，如果没有缓存再访问被代理对象的方法体
       result=redisTemplate.opsForValue().get(cacheKey);
       //判断我们缓存中是否有数据
        if(null==result){
            try {
                //为了保证安全，布置分布式锁
                String key = UUID.randomUUID().toString();
                Boolean ok = redisTemplate.opsForValue().setIfAbsent(cacheKey + ":lock", key, 2, TimeUnit.SECONDS);
                //通过ok判断是否拿下分布式锁
                if(ok){
                    //执行被代理方法
                   result  = point.proceed();
                   //需要再次判断
                    if(null==result){
                        //同步空缓存，因为数据库实在没有就只能同步空缓存了
                        redisTemplate.opsForValue().set(cacheKey,result,5,TimeUnit.SECONDS);
                    }else {
                        //同步缓存
                        redisTemplate.opsForValue().set(cacheKey,result);
                    }
                    //同步完成之后开始删除分布式锁
                 String openKey=(String) redisTemplate.opsForValue().get(cacheKey+":lock");
                    //判断两个分布式锁是否一样
                    if(key.equals(openKey)){
                        redisTemplate.delete(cacheKey+":lock");
                    }
                }else {
                    //如果没有拿下分布式锁则进行自旋
                    System.out.println("没拿到分布式锁开始自旋");
                    //先沉睡几秒
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //过了几秒后重新开始查询
                    return redisTemplate.opsForValue().get(cacheKey);
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }
            //如果有的话就直接返回数据
        return result;


    }


}
