package com.spring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * bean定义
 *
 * @author shah
 * @date 2022/04/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeanDefinition {
    /**
     * clazz
     */
    private Class clazz;
    /**
     * 作用域
     */
    private String scope;
}
