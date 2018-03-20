package foo.bar;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class HelloApp {


    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        HelloService helloService = context.getBean(HelloService.class);
        HelloAgainService helloAgainService = context.getBean(HelloAgainService.class);
        System.out.println(helloService.sayHello());
        System.out.println(helloService.sayHello("Ilona"));
        System.out.println(helloService.sayGoodBye());
        System.out.println(helloAgainService.sayHello());
        System.out.println(helloAgainService.sayGoodBye());
        System.out.println(helloService.hey());
    }
}
