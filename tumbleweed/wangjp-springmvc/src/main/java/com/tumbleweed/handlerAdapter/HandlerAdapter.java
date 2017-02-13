package com.tumbleweed.handlerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

//处理适配器
public interface HandlerAdapter {

    Object[] hand(
            HttpServletRequest request,
            HttpServletResponse response, Method method,
            Map<String, Object> beans);
}
