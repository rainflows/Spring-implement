package com.yu;

import com.spring.YuApplicationContext;
import com.yu.service.UserService;

/**
 * 我的测试
 *
 * @author shah
 * @date 2022/04/30
 */
public class MyTest {
    public static void main(String[] args) {
        YuApplicationContext context = new YuApplicationContext(AppConfig.class);
        UserService userServiceImpl = context.getBean("userServiceImpl", UserService.class);
        userServiceImpl.test();
    }
}
