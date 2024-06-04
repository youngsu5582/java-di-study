package ioc;

import javaconfig.FullModeConfig;
import javaconfig.JavaConfig;
import javaconfig.LiteModeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.servlet.ViewResolver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Java 기반 컨테이너 구성(Java-based Container Configuration)
 * <p>
 * 스프링의 Java 기반 컨테이너 구성은 @Configuration 클래스와 @Bean 메서드로 이루어집니다.
 *
 * @Bean 메서드는 스프링 IoC 컨테이너에서 관리할 새 객체를 인스턴스화하여 구성 및 초기화에 사용됩니다.
 * @Bean은 XML 구성의 <bean/> 요소와 동일한 역할을 합니다.
 * 일반적으로 @Configuration 클래스와 함께 사용됩니다.
 * @Configuration를 클래스에 붙이면 이 클래스의 주요 목적은 Bean 정의를 뜻하게 됩니다.
 * 또한 @Configuration 클래스를 사용하면 동일한 클래스에서 다른 @Bean 메서드를 호출하여 Bean 의존성을 정의할 수 있습니다.
 * <p>
 * 앞선 학습 테스트에서 Java 기반 컨테이너 구성을 사용하여 스프링 IoC 컨테이너를 구성하는 방법을 살펴보았습니다.
 * 여기서는 Java 기반 컨테이너 구성의 두 가지 모드인 전체(Full) 모드와 라이트(Lite) 모드에 대해 알아보겠습니다.
 * 특별한 이유가 없다면 전체 모드를 사용하는 것이 좋으며 라이트 모드를 사용한다면 어떤 점을 주의해야 하는지 알아보겠습니다.
 * @see <a href="https://github.com/kang-hyungu/di-study/blob/master/src/test/kotlin/ioc/JavaBasedConfiguration.kt">원문 코드</a>
 */
@DisplayName("자바 기반 컨테이너 구성(Java-based Container Configuration)")
class JavaBasedConfiguration {
    /**
     * """
     * 전체(Full) @Confriguration Mode와 라이트(Lite) @Bean Mode
     *
     * @Configuration이 붙지 않은 클래스에서 @Bean 메서드를 선언하면 "라이트(Lite)" 모드로 동작합니다.
     * 라이트 모드에서 @Bean 메서드는 해당 클래스의 인스턴스를 생성하는 데 사용됩니다.
     * 주요 목적은 일반적인 Bean과 다르며, @Bean 메서드로 추가적인 기능을 제공하는 용도로 사용합니다.
     * 예를 들어, 특정 서비스 클래스가 다양한 형태의 ViewResolver를 제공해야 하는 경우를 생각해봅시다.
     * 이 때, 서비스 클래스는 @Bean 메서드를 사용하여 ViewResolver 인스턴스를 생성하고 이를 스프링 컨테이너에 제공할 수 있습니다.
     * <p>
     * 라이트 모드에서 선언한 @Bean 메서드는 Bean 간의 의존성을 선언할 수 없습니다.
     * 라이트 모드의 @Bean 메서드는 다른 @Bean 메서드를 호출하면 안 됩니다.
     * 라이트 모드의 장점은 런타임에 CGLIB 서브클래싱을 적용할 필요가 없어 클래스 설계에 제약이 없습니다.
     * <p>
     * 특별한 이유가 없다면 @Configuration 클래스 내에서 @Bean 메서드를 선언하여 항상 "전체(Full) 모드"로 사용합시다.
     * """
     */

    /**
     * """
     *
     * @Service 클래스에서 선언한 @Bean 메서드는 라이트 모드로 동작합니다.
     * 라이트 모드지만 싱글톤 Bean으로 등록되어서 스프링 컨테이너가 동일한 객체를 반환합니다.
     * """
     */
    @Test
    void 싱글톤내_라이트는_동일() {

        final var applicationContext = new AnnotationConfigApplicationContext(JavaConfig.class);
        //TODO: 라이트만 동작을 하는 이유는 무엇인지? + 서비스가 싱글톤으로 등록되었다는건지?
        // BeanDefinition 통해 싱글톤 확인
        final var firstLiteBean = applicationContext.getBean("viewResolver", ViewResolver.class);
        final var secondLiteBean = applicationContext.getBean("viewResolver", ViewResolver.class);

        assertThat(firstLiteBean).isEqualTo(secondLiteBean);
    }

    /**
     * """
     *
     * @Serivce 클래스에서 선언한 @Bean 메서드는 라이트 모드로 동작합니다.
     * LiteModeService 클래스에서 @Bean을 사용한 anotherViewResolver() 메서드는 viewResolver() 메서드를 호출하고 있습니다.
     * 하지만 라이트 모드에서는 Bean 간의 의존성을 선언할 수 없습니다.
     * anotherViewResolver() 메서드 내의 viewResolver() 메서드는 새로운 ViewResolver 인스턴스를 생성합니다.
     * 싱글톤 스코프이지만 동일한 Bean을 반환하지 않습니다.
     * """
     */
    @Test
    void 싱글톤내_함수_호출_한번더해서_주입() {
        final var applicationContext = new AnnotationConfigApplicationContext(JavaConfig.class);

        final var viewResolver = applicationContext.getBean("viewResolver", ViewResolver.class);

        // anotherViewResolver() 메서드 내부에서 viewResolver() 메서드를 호출하더라도 동일한 Bean을 반환하지 않습니다.
        final var anotherViewResolver = applicationContext.getBean("anotherViewResolver", ViewResolver.class);

        assertThat(viewResolver).isNotEqualTo(anotherViewResolver);
    }

    /**
     * """
     *
     * @Configuration 클래스에서 선언한 @Bean 메서드는 전체 모드로 동작합니다.
     * FullModeConfig 클래스의 anotherViewResolver() 메서드는 LiteModeService 클래스의 anotherViewResolver() 메서드와 동일한 로직입니다.
     * 하지만 FullModeConfig 클래스의 anotherViewResolver() 메서드는 싱글톤 Bean으로 등록됩니다.
     * """
     */
    @Test
    void 전체_모드의_빈은_관리_대상() {
        final var applicationContext = new AnnotationConfigApplicationContext(FullModeConfig.class);

        final var viewResolver = applicationContext.getBean("viewResolver", ViewResolver.class);

        // anotherViewResolver() 메서드 내부에서 viewResolver() 메서드를 호출하면 동일한 Bean을 반환합니다.
        final var firstAnotherViewResolver = applicationContext.getBean("anotherViewResolver", ViewResolver.class);

        // FullModeConfig 클래스의 anotherViewResolver() 메서드는 싱글톤 Bean으로 등록됩니다.
        assertThat(viewResolver).isEqualTo(firstAnotherViewResolver);

        final var fullModeConfig = applicationContext.getBean("fullModeConfig", FullModeConfig.class);

        // @Configuration 클래스인 fullModeConfig Bean으로 직접 anotherViewResolver() 메서드를 호출해도 동일한 Bean을 반환합니다.
        final var secondAnotherViewResolver = fullModeConfig.anotherViewResolver();

        assertThat(firstAnotherViewResolver).isEqualTo(secondAnotherViewResolver);
    }

    /**
     * """
     *
     * @Configuration 클래스와 달리 @Service 클래스에서 선언한 @Bean 메서드는 단순 팩터리 메서드로 동작합니다.
     * 라이트 모드에서 선언한 @Bean 메서드는 Bean 간의 의존성을 선언할 수 없습니다.
     * @Configuration 클래스 내에서 선언한 @Bean 메서드는 클래스 내 다른 메서드에서 싱글톤 Bean으로 참조할 수 있습니다.
     * """
     */
    @Test
    void 라이트_모드의_빈은_단순_팩토리_메소드() {

        final var applicationContext = new AnnotationConfigApplicationContext(JavaConfig.class);

        final var liteBean = applicationContext.getBean("viewResolver", ViewResolver.class);
        final var service = applicationContext.getBean(LiteModeService.class);
        final var notBean = service.anotherViewResolver();

        // @Configuration 클래스에서 선언한 @Bean 메서드와 달리 @Service 클래스에서 선언한 @Bean 메서드는 단순 팩터리 메서드로 동작합니다.
        assertThat(liteBean).isNotEqualTo(notBean);

    }
}
