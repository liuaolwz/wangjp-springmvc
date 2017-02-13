package com.tumbleweed.handlerAdapter;


import com.tumbleweed.annotation.Service;
import com.tumbleweed.argumentResolver.ArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Service("wangjpHandlerAdapter")
public class WangjpHandlerAdapter implements HandlerAdapter {

    //对参数进行处理
    public Object[] hand(
            HttpServletRequest request,
            HttpServletResponse response, Method method,
            Map<String, Object> beans
    ) {
        Class<?>[] paramClass = method.getParameterTypes();

        Object[] args = new Object[paramClass.length];

        //1、要拿到所有实现了ArgumentResolver这个接口的实现类
        Map<String, Object> argumentResolvers = getBeansOfType(beans, ArgumentResolver.class);

        int paramIndex = 0;
        int i = 0;
        for (Class<?> paramClazz : paramClass) {
            for(Map.Entry<String, Object> entry : argumentResolvers.entrySet()) {
                ArgumentResolver argumentResolver = (ArgumentResolver) entry.getValue();
                if (argumentResolver.support(paramClazz, paramIndex, method)) {
                    args[i++] = argumentResolver.argumentResolver(request, response, paramClazz, paramIndex, method);
                }
            }
            paramIndex++;
        }

        return args;
    }

    private Map<String, Object> getBeansOfType(Map<String, Object> beans, Class<?> intfType) {

        Map<String, Object> resultBeans = new HashMap<String, Object>();

        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Class<?>[] intfs = entry.getValue().getClass().getInterfaces();

            if (intfs != null && intfs.length > 0) {
                for (Class<?> intf : intfs) {
                    if (intf.isAssignableFrom(intfType)) {
                        resultBeans.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return resultBeans;
    }

}
