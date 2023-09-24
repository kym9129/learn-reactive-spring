package com.learnreactivespring.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    void fluxTest() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
//                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("After Error")) // 한번 에러가 발생하면 이후 데이터는 보내지 않는다
                .log(); // onSubscribe 시점부터 과정을 찍어줌

        stringFlux.subscribe(
                System.out::println, // consumer
                (e) -> System.err.println("Exception is " + e), // error consumer
                () -> System.out.println("Completed") // complete consumer
        );
    }

    @Test
    void fluxTestElements_WithoutError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();

        // StepVerifier : reactor-test 모듈에서 제공하는 테스트용 인터페이스
        StepVerifier.create(stringFlux)
//                .expectNext("Spring") // 순서 달라지면 test fail
//                .expectNext("Spring Boot")
//                .expectNext("Reactive Spring") // 여기까지는 아무 일도 안 일어남
                .expectNext("Spring", "Spring Boot", "Reactive Spring") // 여러개 한꺼번에 넣어도 됨
                .verifyComplete() // 여기서부터 data flow가 발생
        ;
    }

    @Test
    void fluxTestElements_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3) // 이렇게 하면 Flux 요소 3개 다 받고 다음으로 넘어감
//                .expectNext("Spring", "Spring Boot", "Reactive Spring") //
//                .expectNext("Spring")
//                .expectNext("Spring Boot")
//                .expectNext("Reactive Spring")
//                .verifyError() // expectNext()에서 호출 가능
//                .expectErrorMessage("Exception Occurred")
                .expectError(RuntimeException.class)
                .verify() // expectError(), expectErrorMessage()에서 호출됐음. expectNext()에서는 안나옴
        ;
    }

    @Test
    void monoTest() {
        Mono<String> stringMono = Mono.just("Spring");

        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete()
                ;
    }

    @Test
    void monoTest_Error() {
        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")))
                .expectError(RuntimeException.class)
                .verify()
        ;
    }
}
