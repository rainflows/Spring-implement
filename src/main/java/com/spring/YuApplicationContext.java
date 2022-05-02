package com.spring;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 于应用程序上下文
 *
 * @author shah
 * @date 2022/04/30
 */
public class YuApplicationContext {
    /**
     * 配置类
     */
    private Class configClass;
    /**
     * 单例池
     */
    private ConcurrentHashMap<String,Object> singletonObjects = new ConcurrentHashMap<>();
    /**
     * bean定义_map
     */
    private ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    /**
     * bean后置处理程序列表
     */
    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    /**
     * 于应用程序上下文
     * 得到配置类
     * 解析配置类
     * ComponentScan注解-->扫描路径-->扫描
     *
     * @param configClass 配置类
     */
    public YuApplicationContext(Class configClass) {
        /**
         * 得到配置类
         */
        this.configClass = configClass;
        /**
         * 解析配置类:ComponentScan注解-->扫描路径-->扫描
         */
        scan(configClass);

        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            /**
             * 单例bean创建
             */
            if (beanDefinition.getClazz().isAnnotationPresent(Scope.class)) {
                if ("singleton".equals(beanDefinition.getScope())) {
                    Object bean = createBean(beanName,beanDefinition);
                    singletonObjects.put(beanName,bean);
                }
            }
        }

    }

    /**
     * 扫描
     *
     * @param configClass 配置类
     */
    private void scan(Class configClass) {
        /**
         * 扫描路径
         */
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value();
        path = path.replace(".","/");
        /**
         * 扫描
         */
        ClassLoader classLoader = YuApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(path);
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                String fileName = f.getName();
                if (fileName.endsWith(".class")) {
                    String className = path.replace("/",".") + "." + fileName.substring(0,fileName.indexOf((".class")));

                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        if (clazz.isAnnotationPresent(Component.class)) {

                            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                BeanPostProcessor instance = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                                beanPostProcessorList.add(instance);
                            }
                            /**
                             * 解析类-->BeanDefinition
                             */
                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            String beanName = componentAnnotation.value();

                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setClazz(clazz);
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                beanDefinition.setScope(scopeAnnotation.value());
                            }
                            beanDefinitionMap.put(beanName,beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * 创建bean
     *
     * @param beanDefinition bean定义
     * @return {@link Object}
     */
    public Object createBean(String beanName, BeanDefinition beanDefinition){
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            /**
             * 依赖注入
             */
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    Object bean = getBean(declaredField.getName());
                    declaredField.setAccessible(true);
                    declaredField.set(instance,bean);
                }
            }
            /**
             * Aware回调
             */
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }
            /**
             * BeanPostProcessor:postProcessorBeforeInitialization
             */
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessorBeforeInitialization(instance,beanName);
            }
            /**
             * 初始化
             */
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }
            /**
             * BeanPostProcessor:postProcessorAfterInitialization
             */
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessorAfterInitialization(instance,beanName);
            }

            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * getBean
     *
     * @param beanName bean名字
     * @return {@link Object}
     */
    public Object getBean(String beanName){
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getClazz().isAnnotationPresent(Scope.class) && "singleton".equals(beanDefinition.getScope())) {
                /**
                 * singleton:共享bean,每次请求返回同一bean,不加即默认为singleton
                 */
                return singletonObjects.get(beanName);
            } else {
                /**
                 * prototype:每次请求会产生一个新的bean
                 */
                return createBean(beanName,beanDefinition);
            }
        } else {
            /**
             * 不存在对应的Bean
             */
            throw new NullPointerException();
        }
    }

    /**
     * getBean
     *
     * @param beanName     bean名字
     * @param requiredType 所需类型
     * @return {@link T}
     */
    public <T> T getBean(String beanName, Class<T> requiredType){
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getClazz().isAnnotationPresent(Scope.class) && "singleton".equals(beanDefinition.getScope())) {
                /**
                 * singleton:共享bean,每次请求返回同一bean,不加即默认为singleton
                 */
                return (T) singletonObjects.get(beanName);
            } else {
                /**
                 * prototype:每次请求会产生一个新的bean
                 */
                return (T) createBean(beanName,beanDefinition);
            }
        } else {
            /**
             * 不存在对应的Bean
             */
            throw new NullPointerException();
        }
    }
}
