package ioc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 스프링 IoC 컨테이너와 Bean 소개
 * 제어의 역전(IoC) 원리를 스프링 프레임워크에서 구현하는 방법을 살펴봅시다.
 *
 * @see <a href="https://github.com/kang-hyungu/di-study/blob/master/src/test/kotlin/ioc/Introduction.kt">원문 코드</a>
 */
@DisplayName("스프링 IoC 컨테이너와 Bean 소개")
class Introduction {
    /**
     * """
     * BeanFactory
     * 스프링 프레임워크에서 IoC 원리를 실제 구현하기 위해 사용하는 인터페이스입니다.
     * BeanFactory를 사용하여 Bean을 등록하고, 조회하고, 관리하는 기능을 제공합니다.
     * BeanFactory를 구현한 StaticListableBeanFactory를 사용해봅시다.
     *
     * @see <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/ApplicationContext.html">ApplicationContext Javadoc</a>
     * """
     */
    @Test
    void 빈_팩토리에_객체추가() {
        // BeanFactory 객체를 생성합니다.
        final BeanFactory beanFactory = new StaticListableBeanFactory();

        // BeanFactory에 SampleObject 객체를 Bean으로 등록합니다.
        ((StaticListableBeanFactory) beanFactory).addBean("sampleObject", new SampleObject());

        // BeanFactory에서 SampleObject Bean 객체를 조회합니다.
        final var sampleObject = beanFactory.getBean("sampleObject", SampleObject.class);

        // 조회한 Bean 객체가 SampleObject 타입인지 확인합니다.
        assertThat(beanFactory.containsBean("sampleObject")).isTrue();
        assertThat(sampleObject).isInstanceOf(SampleObject.class);
    }

    /**
     * """
     * ApplicationContext
     * 스프링 IoC 컨테이너로 BeanFactory를 확장한 인터페이스며 BeanFactory의 모든 기능을 포함하고 있습니다.
     * IoC 컨테이너 기능 외에 AOP, i18n, Event publication 같은 엔터프라이즈 전용 기능을 추가로 제공합니다.
     * 스프링 부트의 SpringApplication.run() 메서드는 내부적으로 ApplicationContext를 사용합니다.
     * """
     */
    @Test
    void 애플리케이션_컨텍스트에_객체추가() {
        final ApplicationContext applicationContext = new StaticApplicationContext();

        // ApplicationContext에 SampleObject를 Bean으로 등록합니다.
        ((StaticApplicationContext) applicationContext).registerSingleton("sampleObject", SampleObject.class);

        // ApplicationContext에서 SampleObject Bean 객체를 조회합니다.
        final var sampleObject = applicationContext.getBean("sampleObject", SampleObject.class);

        // 조회한 Bean 객체가 SampleObject 타입인지 확인합니다.
        assertThat(sampleObject).isInstanceOf(SampleObject.class);

        // ApplicationContext가 BeanFactory를 포함하는지 확인합시다.
        assertThat(applicationContext.containsBean("sampleObject")).isTrue();
    }

    /**
     * """
     * Bean
     * 스프링 IoC 컨테이너에 의해 관리되는 객체를 빈(Bean)이라 합니다.
     * Bean은 스프링 IoC 컨테이너에 의해 인스턴스화, 어셈블 및 관리되는 객체입니다.
     * @see <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/Bean.html">Bean Javadoc</a>
     * """
     */
    @Test
    void 빈은_컨텍스트에서_관리() {
        // 직접 객체를 만들면 스프링 IoC 컨테이너가 관리하지 않습니다.
        // notBeanObject 객체는 Bean이 아닙니다.
        final SampleObject notBeanObject = new SampleObject();

        final var applicationContext = new StaticApplicationContext();

        // 스프링 기반 애플리케이션에서 스프링 IoC 컨테이너에 등록한 객체는 Bean이라 부릅니다.
        applicationContext.registerSingleton("sampleObject", SampleObject.class);

        // beanObject 객체는 Bean입니다.
        final SampleObject beanObject = applicationContext.getBean("sampleObject", SampleObject.class);

        //TODO: 해당 부분 질문( 기대상 무조건 true, 관리하지 않는 Bean 학습이라면, isNotEqualTo 는 어떤지 )
        //applicationContext.containsBean("sampleObject") shouldBe true
        assertThat(applicationContext.containsBean("notBeanObject")).isFalse();
        assertThat(beanObject).isNotEqualTo(notBeanObject);
    }
    @Test
    /**
     * """
     * Bean Definition
     * 빈은 단순 객체로 저장되는게 아닌 Bean Definition 이라는 객체에
     * 감싸진 후, BeanFactory 에 등록됩니다.
     * @see <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/config/BeanDefinition.html">BeanDefinition Javadoc</a>
     * """
     */
    void 빈_메타데이터_정의(){
        final var applicationContext = new StaticApplicationContext();

        applicationContext.registerSingleton("sampleObject", SampleObject.class);

        // beanDefinition 객체는 Bean 의 정보를 담고 있습니다.
        final BeanDefinition beanDefinition = applicationContext.getBeanDefinition("sampleObject");
        assertThat(beanDefinition.isSingleton()).isTrue();
        assertThat(beanDefinition.getBeanClassName()).contains("SampleObject");
    }
}
