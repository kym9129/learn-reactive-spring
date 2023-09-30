package com.learnreactivespring.learnreactivespring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> returnFlux() {
        return Flux.just(1, 2, 3, 4)
                .delayElements(Duration.ofSeconds(1)) // api 호출 시 1초마다 onNext()를 호출하고 마지막 onNext() 시점에 리턴
                .log();
    }

//    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE) // deprecated 돼서 안먹힘. 대안 못찾겠음
    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_NDJSON_VALUE) // 공식문서는 이걸 쓰라는데 브라우저에서 api 콜하면 바로 렌더링 안되고 파일로 저장됨
//    @GetMapping(value = "/fluxstream")
    public Flux<Long> returnFluxStream() {
        return Flux.interval(Duration.ofSeconds(1)) // 매 초당 0, 1, 2, ... 로 무한 찍기
                .log();
    }

    @GetMapping("/mono")
    public Mono<Integer> returnMono() {
        return Mono.just(1)
                .log();
    }
}
