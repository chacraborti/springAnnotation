package foo.bar;

import foo.bar.annotation.MethodTimeCount;
import org.springframework.stereotype.Component;

@MethodTimeCount
@Component
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello() {
        return "Hello world!";
    }
}
