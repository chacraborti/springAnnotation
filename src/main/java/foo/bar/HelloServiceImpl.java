package foo.bar;

import foo.bar.annotation.MethodTimeCount;
import org.springframework.stereotype.Component;

@Component
public class HelloServiceImpl implements HelloService {

    @Override
    @MethodTimeCount
    public String sayHello() {
        return "Hello world!";
    }

    @Override
    public String sayGoodBye() {
        return "Bye!";
    }
}
