package com.spring;

/**
 * bean名字清楚
 *
 * @author shah
 * @date 2022/05/01
 */
public interface BeanNameAware {
    /**
     * 设置bean名称
     *
     * @param beanName bean名字
     */
    void setBeanName(String beanName);
}
