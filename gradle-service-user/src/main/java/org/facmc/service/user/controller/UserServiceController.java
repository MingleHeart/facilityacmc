package org.facmc.service.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user/service")
public class UserServiceController {
    @RequestMapping("/test")
    public Mono<String> test() {
        return Mono.just("hello");
    }
}
