package com.atguigu.gmall.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class FeignInterceptor implements RequestInterceptor {

    public void apply(RequestTemplate requestTemplate) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //RequestContextHolder.setRequestAttributes(attributes, true);
        HttpServletRequest request = attributes.getRequest();

        requestTemplate.header("userTempId", request.getHeader("userTempId"));
        requestTemplate.header("userId", request.getHeader("userId"));
    }
}
