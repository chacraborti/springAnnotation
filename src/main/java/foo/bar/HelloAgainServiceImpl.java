package foo.bar;

import foo.bar.annotation.MethodTimeCount;
import org.springframework.stereotype.Component;

@Component
public class HelloAgainServiceImpl implements HelloAgainService {
    @Override
    public String sayHello() {
        return "Hello again";
    }

    @Override
    @MethodTimeCount
    public String sayGoodBye() {
        return "Goodbye again";
    }
}
