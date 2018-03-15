package foo.bar;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("foo.bar")
public class AppConfig {
    @Bean
    public MethodTimeCountAnnotationBeanPostProcessor methodTimeCountAnnotationBeanPostProcessor() {
        return new MethodTimeCountAnnotationBeanPostProcessor();
    }
}