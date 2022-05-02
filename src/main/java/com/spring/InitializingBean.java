package com.spring;

/**
 * 初始化bean
 *
 * @author shah
 * @date 2022/05/01
 */
public interface InitializingBean {
    /**
     * 在属性设置
     *
     * @throws Exception 异常
     */
    void afterPropertiesSet() throws Exception;
}
