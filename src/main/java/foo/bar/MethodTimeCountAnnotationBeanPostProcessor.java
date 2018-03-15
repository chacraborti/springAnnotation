package foo.bar;

import foo.bar.annotation.MethodTimeCount;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;


public class MethodTimeCountAnnotationBeanPostProcessor implements BeanPostProcessor {
    private Map<String, Class> nameClassMap = new HashMap<>();
    private Map<String, Method> nameMethodMap = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(MethodTimeCount.class)) {
                nameClassMap.put(beanName, clazz);
                nameMethodMap.put(method.getName(), method);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class clazz = nameClassMap.get(beanName);
        if (clazz != null) {
            return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String proxyMethodName = method.getName();
                    if(nameMethodMap.containsKey(proxyMethodName)){
                        long before = System.nanoTime();
                        Object methodInvocation = method.invoke(bean, args);
                        long after = System.nanoTime();
                        System.out.println("Method \""+method.getName()+"\" working time: " + (after - before));
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
