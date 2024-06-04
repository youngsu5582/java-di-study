package ioc;

import lifecycle.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Bean의 라이프 사이클
 * 스프링 프레임워크는 Bean의 특성을 커스터마이징 할 수 있는 다양한 인터페이스를 제공합니다.
 * 여기서는 Bean의 라이프 사이클을 커스터마이징하는 방법에 대해 알아보겠습니다.
 * <p>
 * 스프링이 제공하는 콜백 인터페이스를 사용하지 않는 경우 일반적으로 init(), destroy(), close() 등의 이름을 가진 메서드를 작성합니다.
 * 라이프 사이클을 위한 콜백 메서드는 프로젝트 전체에서 동일한 이름을 사용하여 일관성을 보장하는 것이 좋습니다.
 *
 *@see <a href="https://github.com/kang-hyungu/di-study/blob/master/src/test/kotlin/ioc/Lifecycle.kt">원문 코드</a>
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/Lifecycle.html">ApplicationContext Javadoc</a>
 */
@DisplayName("Bean의 라이프 사이클")
public class Lifecycle {
    /**
     * """
     * 라이프 사이클 콜백(Lifecycle Callbacks)
     * 라이프 사이클(Lifecycle)이란 Bean이 생성되고 초기화되며 사용되고 소멸되는 프로세스를 의미합니다.
     * 컨테이너의 Bean 라이프 사이클 관리와 상호 작용하기 위해 InitializingBean 및 DisposableBean 인터페이스를 구현할 수 있습니다.
     * 전자의 경우 afterPropertiesSet()을 호출하고 후자의 경우 destroy()를 호출하여 빈이 초기화 및 소멸 시 특정 작업을 수행하도록 합니다.
     * <p>
     * 내부적으로 스프링 프레임워크는 BeanPostProcessor 구현을 사용하여 찾을 수 있는 모든 콜백 인터페이스를 처리하고 적절한 메서드를 호출합니다.
     * 스프링이 기본적으로 제공하지 않는 사용자 정의 기능이나 기타 라이프사이클 동작이 필요한 경우 BeanPostProcessor를 직접 구현할 수 있습니다.
     * 자세한 내용은 공식 문서의 컨테이너 확장 포인트(Container Extension Points)를 참조하세요.
     * 초기화 및 소멸 콜백 외에도 스프링은 컨테이너의 자체 라이프 사이클에 따라 객체가 시작 및 종료 프로세스에 참여할 수 있도록 라이프 사이클 인터페이스를 구현할 수도 있습니다.
     * """
     */
    @Nested
    @DisplayName("라이프 사이클 콜백")
    class LifeCycleCase {
        /**
         * """
         * 초기화 콜백(Initialization Callbacks)
         * InitializingBean 인터페이스는 Bean이 생성된 후에 초기화 작업을 수행해야 하는 경우 사용합니다.
         * InitializingBean 인터페이스는 코드를 스프링에 불필요하게 결합하므로 사용하지 않는 것이 좋습니다.
         * 대신 @PostConstruct를 사용하거나 @Bean의 initMethod 속성에 POJO 초기화 메서드를 지정하는 것이 좋습니다.
         * <p>
         * 일반적으로 @PostConstruct 및 초기화 메서드는 컨테이너의 싱글톤 생성 잠금 내에서 실행된다는 점에 유의하세요.
         * Bean 인스턴스는 @PostConstruct 메서드에서 반환된 후에만 완전히 초기화되어 다른 Bean에 의해 참조될 수 있습니다.
         * 초기화 메서드에서 외부 Bean 액세스를 통한 추가 작업을 수행하면 교착 상태가 발생할 위험이 있으니 주의하세요.
         * """
         */
        @Test
        void 초기화_콜백() {
            final var applicationContext = new AnnotationConfigApplicationContext(LifecycleConfig.class);

            final var initializingSampleObject = applicationContext.getBean(InitializingSampleObject.class);
            final var pojoSampleObject = applicationContext.getBean(PojoSampleObject.class);
            final var postConstructSampleObject = applicationContext.getBean(PostConstructSampleObject.class);

            assertThat(initializingSampleObject.getMessage()).isEqualTo("InitializingSampleObject.afterPropertiesSet() method called");
            assertThat(pojoSampleObject.getMessage()).isEqualTo("PojoSampleObject.init() method called");
            assertThat(postConstructSampleObject.getMessage()).isEqualTo("PostConstructSampleObject.init() method called");
        }
        /**
         *         """
         *         소멸 콜백(Destruction Callbacks)
         *         DisposableBean 인터페이스는 Bean이 소멸되기 전에 정리 작업을 수행해야 하는 경우 사용합니다.
         *         예를 들면, 연결된 커넥션 풀링의 해제가 있습니다.
         *         싱글톤 Bean의 경우 컨테이너가 종료될 때 Bean이 소멸됩니다.
         *         DisposableBean 인터페이스를 구현하면 빈을 포함하는 컨테이너가 파괴될 때 빈이 콜백을 받을 수 있습니다.
         *         InitializingBean 인터페이스와 마찬가지로 DisposableBean 인터페이스도 사용하지 않는 것이 좋습니다.
         *         대신 @PreDestroy를 사용하거나 @Bean의 destroyMethod 속성에 POJO 소멸 메서드를 지정합니다.
         *
         *         사용자의 편의를 위해 컨테이너는 @Bean 메서드에서 반환된 객체에 대해 소멸 메서드를 유추합니다.
         *         컨테이너는 public close() 또는 shutdown() 메서드를 감지하는 'destroy 메서드 추론(destroy method inference)'도 지원합니다.
         *         java.lang.AutoCloseable 또는 java.io.Closeable 구현도 자동으로 일치시킵니다.
         *         특정 @Bean에 대한 destroy 메서드 추론을 사용하지 않으려면 빈 문자열을 값으로 지정합니다(예: @Bean(destroyMethod="")).
         *         참고: 라이프 사이클이 컨테이너에서 완전히 제어되는 빈에서만 호출되며, 싱글톤이 아닌 스코프는 보장하지 않습니다.
         *         """
         */
        @Test
        void 소멸_콜백(){
            final var applicationContext = new AnnotationConfigApplicationContext(LifecycleConfig.class);

            final var disposableSampleObject = applicationContext.getBean(DisposableSampleObject.class);
            final var pojoSampleObject = applicationContext.getBean(PojoSampleObject.class);
            final var preDestroySampleObject = applicationContext.getBean(PreDestroySampleObject.class);

            // 소멸 콜백 테스트를 위해 ApplicaionContext를 종료합니다.
            applicationContext.close();

            assertThat(disposableSampleObject.getMessage()).isEqualTo("DisposableSampleObject.destroy() method called");
            assertThat(pojoSampleObject.getMessage()).isEqualTo("PojoSampleObject.close() method called");
            assertThat(preDestroySampleObject.getMessage()).isEqualTo("PostConstructSampleObject.close() method called");
        }
    }

}
