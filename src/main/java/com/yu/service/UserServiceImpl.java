package com.yu.service;

import com.spring.*;

/**
 * 用户服务
 *
 * @author shah
 * @date 2022/04/30
 */
@Component("userServiceImpl")
@Scope("prototype")
public class UserServiceImpl implements BeanNameAware, InitializingBean,UserService {
    /**
     * 订单服务
     */
    @Autowired
    private OrderService orderService;

    /**
     * bean名字
     */
    private String beanName;

    /**
     * 设置bean名称
     *
     * @param beanName bean名字
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * 在属性设置
     *
     * @throws Exception 异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("喵了个咪的初始化");
    }

    /**
     * 测试
     */
    @Override
    public void test() {
        System.out.println(orderService);
        System.out.println(beanName);
    }
}
