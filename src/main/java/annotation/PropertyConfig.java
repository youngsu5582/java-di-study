package annotation;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import annotation.sample.SampleValue;

@Configuration
@ComponentScan(basePackageClasses = SampleValue.class)
@PropertySource("classpath:application.properties")
public class PropertyConfig {
}
