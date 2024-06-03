package ioc;

import circular.CircularDependencyConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 의존성(Dependencies)
 * 일반적인 엔터프라이즈 애플리케이션은 단일 객체(또는 스프링 용어로 Bean)로 구성되지 않습니다.
 * 가장 단순한 애플리케이션이라도 최종 사용자에게 일관된 애플리케이션으로 보이기 위해 함께 작동하는 여러 객체가 있습니다.
 * 객체가 다른 객체와 협업하기 위해 의존성을 갖게 됩니다.
 * 의존성은 컨테이너에 의해 관리되는 객체 간의 관계를 의미합니다.
 * 객체가 협업할 수 있도록 의존성을 설정하는 방법을 살펴보겠습니다.
 *
 * @see <a href="https://github.com/kang-hyungu/di-study/blob/master/src/test/kotlin/ioc/Dependencies.kt">원문 코드</a>
 */
@DisplayName("의존성(Dependencies)")
class Dependencies {
    /**
     * """
     * 생성자 기반 의존성 주입(Constructor-based Dependency Injection)
     * 생성자 기반 DI는 컨테이너가 각각 의존성을 나타내는 여러 인수를 생성자로 전달하며 수행됩니다.
     * 특정 인수를 사용하여 정적 팩토리 메서드를 호출하여 빈을 생성할 때도 거의 동일합니다.
     *
     * @see ConstructorMovieLister
     * """
     */
    @Test
    void 생성자_기반() {
        // @ComponentScan으로 ConstructorMovieLister와 DefaultMovieFinder를 찾아서 Bean으로 등록하고 의존성을 주입합니다.
        final var applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

        final var constructorMovieLister =
                applicationContext.getBean("constructorMovieLister", ConstructorMovieLister.class);

        // 스프링 컨테이너가 DefaultMovieFinder를 주입했는지 확인합시다.
        assertThat(constructorMovieLister).isInstanceOf(ConstructorMovieLister.class);
        assertThat(constructorMovieLister.getMovieFinder()).isInstanceOf(DefaultMovieFinder.class);
    }

    /**
     * """
     * 설정자(또는 세터) 기반 의존성 주입(Setter-based Dependency Injection)
     * 세터 기반 DI는 컨테이너가 인수가 없는 생성자 또는 인수가 없는 정적 팩토리 메서드로 Bean을 인스턴스화한 다음에 setter 메서드를 호출하여 수행됩니다.
     * 설정자 기반 DI는 생성자 기반 DI 보다 더 유연하게 의존성을 설정할 수 있습니다.
     * 생성자 기반 DI는 빈을 생성할 때 모든 의존성을 지정해야 하지만 설정자 기반 DI는 필요한 의존성만 지정할 수 있습니다.
     * 하지만 의존성이 지정되기 전에 객체의 기능을 사용하면 런타임 오류가 발생할 수 있습니다.
     *
     * @see SetterMovieLister
     * """
     */
    @Test
    void 세터_기반() {
        // @ComponentScan으로 SetterMovieLister와 DefaultMovieFinder를 찾아서 Bean으로 등록하고 의존성을 주입합니다.
        final var applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

        final var setterMovieLister = applicationContext.getBean("setterMovieLister", SetterMovieLister.class);

        // 스프링 컨테이너가 DefaultMovieFinder를 주입했는지 확인합시다.
        assertThat(setterMovieLister).isInstanceOf(SetterMovieLister.class);
        assertThat(setterMovieLister.getMovieFinder()).isInstanceOf(DefaultMovieFinder.class);
    }

    /**
     * """
     * ❗순환 의존성(Circular dependencies)
     * 순환 의존성은 두 개 이상의 모듈이나 클래스가 서로 의존하는 상황을 말합니다.
     * 생성자 주입을 사용할 때 주의해야 할 사항 중 하나는 순환 의존성을 피해야 한다는 것입니다.
     * 생성자 주입을 잘못 사용하면 순환 의존성 시나리오가 발생할 수 있습니다.
     * 예를 들어, 클래스 A가 클래스 B에 의존하고, 동시에 클래스 B가 클래스 A에 의존하는 경우를 말합니다.
     * 클래스 A와 B가 순환 참조 상태라면 Spring IoC 컨테이너는 런타임에 예외를 던집니다.
     * <p>
     * 한 가지 해결책은 클래스의 일부 소스 코드를 생성자 대신 setter로 구성하도록 편집하는 것입니다.
     * 또는 생성자 주입을 피하고 setter 주입만 사용하는 방법도 있습니다.
     * 권장되지는 않지만 setter 주입을 사용하여 순환 의존성을 구성할 수 있습니다.
     * """
     */
    @Test
    void 순환_의존성_발생() {
        //TODO: 현재 BeanCurrentlyInCreationException 이 아닌 UnsatisfiedDependencyException 예외 발생
        // SpringBootStarter 제거하고, 실행해도 UnsatisfiedDependencyException 발생

        assertThatThrownBy(() ->
                new AnnotationConfigApplicationContext(CircularDependencyConfig.class))
                .isInstanceOf(UnsatisfiedDependencyException.class);
    }
}
