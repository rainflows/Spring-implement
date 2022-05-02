package com.yu.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;
import java.lang.reflect.Proxy;

/**
 * Yu Bean后置处理程序
 *
 * @author shah
 * @date 2022/05/01
 */
@Component("yuBeanPostProcessor")
public class YuBeanPostProcessor implements BeanPostProcessor {
    /**
     * 后置处理程序之前初始化
     *
     * @param bean     bean
     * @param beanName bean名字
     * @return {@link Object}
     */
    @Override
    public Object postProcessorBeforeInitialization(Object bean, String beanName) {
        if ("userServiceImpl".equals(beanName)) {
            System.out.println("初始化前");
        }
        return bean;
    }

    /**
     * 后置处理程序初始化后
     *
     * @param bean     bean
     * @param beanName bean名字
     * @return {@link Object}
     */
    @Override
    public Object postProcessorAfterInitialization(Object bean, String beanName) {
        if ("userServiceImpl".equals(beanName)) {
            System.out.println("初始化后");
            Object proxyInstance = Proxy.newProxyInstance(this.getClass().getClassLoader(), bean.getClass().getInterfaces(), (proxy, method, args) -> {
                System.out.println("代理逻辑");
                Object invoke = method.invoke(bean, args);
                System.out.println("代理结束");
                return invoke;
            });
            return proxyInstance;
        }
        return bean;
    }
}
