package org.facmc.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {
    @RequestMapping("/defaultFallback")
    public Mono<String> fallback() {
        return Mono.just("{\"rtnCode\":\"-1\",\"msg\":\"熔断降级\",\"code\":200}");
    }
}
