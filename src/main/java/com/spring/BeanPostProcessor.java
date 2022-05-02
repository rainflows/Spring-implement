package com.spring;


/**
 * bean后置处理器
 * 在Bean对象在实例化和依赖注入完毕后，在显示调用初始化方法的前后添加我们自己的逻辑
 * AOP利用BeanPostProcessor来实现
 *
 * @author shah
 * @date 2022/05/01
 */
public interface BeanPostProcessor {
    /**
     * 后置处理程序初始化之前
     *
     * @param bean     bean
     * @param beanName bean名字
     * @return {@link Object}
     */
    Object postProcessorBeforeInitialization(Object bean, String beanName);

    /**
     * 后置处理程序初始化后
     * AOP在此实现
     *
     * @param bean     bean
     * @param beanName bean名字
     * @return {@link Object}
     */
    Object postProcessorAfterInitialization(Object bean, String beanName);
}
