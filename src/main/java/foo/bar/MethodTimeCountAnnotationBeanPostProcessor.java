package foo.bar;

import foo.bar.annotation.MethodTimeCount;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MethodTimeCountAnnotationBeanPostProcessor implements BeanPostProcessor {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MethodTimeCountAnnotationBeanPostProcessor.class);
    private Map<String, Class> nameClassMap = new HashMap<>();
     private HashMap<Class, Map<String, Method>> classMethodMapMap = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        for (Method method : clazz.getMethods()) {
           Map nameMethodMap = new HashMap<String, Method>();
            if (method.isAnnotationPresent(MethodTimeCount.class)) {
                nameClassMap.put(beanName, clazz);
                nameMethodMap.put(method.getName(), method);
                classMethodMapMap.put(clazz, nameMethodMap);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class clazz = nameClassMap.get(beanName);
        Map<String, Method> nameMethodMap = classMethodMapMap.get(clazz);
        if (clazz != null) {
            return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String proxyMethodName = method.getName();
                    Method originalMethod = nameMethodMap.get(proxyMethodName);
                    if(nameMethodMap.containsKey(proxyMethodName)
                            && Arrays.equals(method.getParameters(), originalMethod.getParameters())
                            && originalMethod.getDeclaringClass().equals(clazz)) {
                        long before = System.nanoTime();
                        Object methodInvocation = method.invoke(bean, args);
                        long after = System.nanoTime();
                        LOGGER.info("Method \""+method.getName()+"\" of "+clazz.getName()+" working time: " + (after - before));
                        return methodInvocation;
                    } else {
                        return method.invoke(bean, args);
                    }
                }
            });
        }
        return bean;
    }
}
