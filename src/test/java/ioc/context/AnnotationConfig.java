package ioc.context;

import ioc.SampleObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnnotationConfig {
    @Bean
    public SampleObject sampleObject() {
        return new SampleObject();
    }
}
