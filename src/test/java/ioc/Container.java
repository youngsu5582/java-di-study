package ioc;

import ioc.context.AnnotationConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericGroovyApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 컨테이너 개요
 * 스프링 IoC 컨테이너에 대해 좀 더 알아봅시다.
 * <p>
 * 스프링 애플리케이션은 ApplicationContext 인터페이스를 스프링 IoC 컨테이너로 사용합니다.
 * 스프링 IoC 컨테이너를 줄여서 컨테이너(container)라고 부르기도 합니다.
 * 컨테이너는 스프링이 아닌 다른 것(WAS 등)을 의미할 수 있으니 맥락에 따라 구분 해야 합니다.
 * <p>
 * 애플리케이션 코드에서 ApplicationContext 인터페이스를 직접 사용하는 일은 거의 없습니다.
 * 실제로 애플리케이션 코드는 getBean() 메서드 호출이 전혀 없어야 하며, Spring API에 대한 종속성도 없어야 합니다.
 * <p>
 * ApplicationContext 인터페이스를 사용하는 코드를 보겠습니다.
 *
 * @see <a href="https://github.com/kang-hyungu/di-study/blob/master/src/test/kotlin/ioc/Container.kt">원문 코드</a>
 */
@DisplayName("컨테이너 개요")
public class Container {
    /**
     * """
     * 컨테이너는 빈(Bean)을 등록하기 위해 구성 메타데이터(Configuration Metadata)를 사용합니다.
     * 구성 메타데이터는 어떻게 빈(Bean)을 생성하고 구성할지에 대한 정보를 담고 있습니다.
     * 이 정보는 XML 파일, Annotation, Java 코드로 작성할 수 있습니다.
     * 구성 메타데이터를 통해 스프링 IoC 컨테이너는 객체를 생성하고, 객체 간의 의존성을 관리하며, 객체의 라이프 사이클을 관리합니다.
     * 컨테이너 개요에서는 XML 기반 구성 메타데이터를 사용하는 방법을 살펴보겠습니다.
     * 다른 형태의 Annotation, Java 코드 방식은 다른 챕터에서 다룹니다.
     * """
     */
    @Nested
    @DisplayName("컨테이너를 생성할 수 있는 방법")
    class ContainerRegisterCase {
        /**
         * """
         * XML-based configuration metadata
         * 스프링 초기부터 제공한 XML 구성 메타데이터 방식을 사용해봅시다.
         * XML 파일에 빈 설정을 기술합니다.
         * java-di-study/src/test/resources/ioc/applicationContext.xml 파일을 참고하세요.
         * """
         */
        @Test
        void XML_기반_생성() {
            final ApplicationContext applicationContext = new GenericXmlApplicationContext("classpath:ioc/applicationContext.xml");

            // ApplicationContext에서 SampleObject Bean 객체를 조회합니다.
            final var sampleObject = applicationContext.getBean("sampleObject", SampleObject.class);

            // 조회한 Bean 객체가 SampleObject 타입인지 확인합니다.
            assertThat(sampleObject).isInstanceOf(SampleObject.class);
        }

        /**
         * """
         * Groovy 파일로 구성 메타데이터를 작성할 수도 있습니다.
         * 스프링은 XML 파일 사용을 강제하지 않습니다. XML은 구성 메타데이터를 작성하는 여러 방법 중 하나일 뿐입니다.
         * XML, Groovy, Annotation, Java 코드 등등 구성 메타데이터를 작성할 수 있는 다양한 방법을 제공합니다.
         * 개발자가 원한다면 커스텀한 방법으로 구성 메타데이터를 작성할 수 있습니다.
         * 스프링은 특정 기술에 종속되지 않도록 설계되었습니다.
         * """
         */
        @Test
        void 그루비_기반_생성() {
            final var applicationContext = new GenericGroovyApplicationContext("classpath:ioc/applicationContext.groovy");

            // ApplicationContext에서 SampleObject Bean 객체를 조회합니다.
            final var sampleObject = applicationContext.getBean("sampleObject", SampleObject.class);

            // 조회한 Bean 객체가 SampleObject 타입인지 확인합니다.
            assertThat(sampleObject).isInstanceOf(SampleObject.class);
        }

        /**
         * """
         * Configuration 파일로 구성 메타데이터를 작성할 수도 있습니다.
         * 설정파일임을 드러내는 `@Configuration`과
         * 해당 메소드가 반환하는 값이 빈임을 나타내는 `@Bean` 으로 이루어져 있습니다.
         * """
         */
        @Test
        void 자바_어노테이션_기반_생성() {
            final var applicationContext = new AnnotationConfigApplicationContext(AnnotationConfig.class);

            // ApplicationContext에서 SampleObject Bean 객체를 조회합니다.
            final var sampleObject = applicationContext.getBean("sampleObject", SampleObject.class);

            // 조회한 Bean 객체가 SampleObject 타입인지 확인합니다.
            assertThat(sampleObject).isInstanceOf(SampleObject.class);
        }
    }
}
