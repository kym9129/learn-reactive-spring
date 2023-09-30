package com.learnreactivespring.learnreactivespring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest // @WebFluxTest와 @SpringBootTest에서 동시에 @BootstrapWith를 호출하여 에러 발생. SpringBootTest는 안써도 됨
@WebFluxTest
public class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient; // 스프링에서 제공하는 non-blocking client

    @Test
    void flux_approach1(){
        Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
//                .accept(MediaType.APPLICATION_JSON_UTF8) // deprecated
                .exchange() // 여기서 api를 실제로 호출하는 것 같음
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(integerFlux)
                .expectSubscription() // onSubscribe() 되는지를 체크하는 것 같음
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .verifyComplete();
    }

    @Test
    void flux_approach2(){
        webTestClient.get().uri("/flux")
//                .accept(MediaType.APPLICATION_JSON_UTF8) // deprecated
                .exchange()
                .expectStatus().isOk()
//                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    void flux_approach3(){
        List<Integer> expectedIntegerList = Arrays.asList(1, 2, 3, 4);

        EntityExchangeResult<List<Integer>> entityExchangeResult = webTestClient
                .get().uri("/flux")
//                .accept(MediaType.APPLICATION_JSON_UTF8) // deprecated
                .exchange()
                .expectStatus().isOk()
//                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Integer.class)
                .returnResult();

        assertEquals(expectedIntegerList, entityExchangeResult.getResponseBody());
    }

    @Test
    void flux_approach4(){
        List<Integer> expectedIntegerList = Arrays.asList(1, 2, 3, 4);

        EntityExchangeResult<List<Integer>> entityExchangeResult = webTestClient
                .get().uri("/flux")
//                .accept(MediaType.APPLICATION_JSON_UTF8) // deprecated
                .exchange()
                .expectStatus().isOk()
//                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Integer.class)
                .consumeWith((response) -> { // 여기서 ClassCastException 발생. DefaultListBodySpec -> EntityExchangeResult 로 캐스트 시도. 왜지?
                    assertEquals(expectedIntegerList, response.getResponseBody());
                });
    }

    @Test
    void flux_stream() {
        Flux<Long> longStreamFlux = webTestClient.get().uri("/fluxstream")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longStreamFlux)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .thenCancel() // 안끝나는 api라서 여기서 직접 멈추기
                .verify();
    }

    @Test
    void mono() {
        Integer expectedValue = 1;

        webTestClient.get().uri("/mono")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith((response) -> {
                    assertEquals(expectedValue, response.getResponseBody());
                });
    }
}
