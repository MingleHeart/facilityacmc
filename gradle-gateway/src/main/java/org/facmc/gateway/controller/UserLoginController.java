package org.facmc.gateway.controller;

import lombok.extern.log4j.Log4j2;
import org.facmc.gateway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@CrossOrigin
@Log4j2
public class UserLoginController {
    @Autowired
    UserService userDetailsService;
    AuthenticationManager authenticationManager;

    @RequestMapping("/api/test")
//    @PreAuthorize("hasRole('super-admin')")
    public Mono<String> test() {
        return Mono.just("success");
    }

    @RequestMapping("/api/test1")
    public Mono<String> test1() {
        return Mono.just("success1");
    }

    @RequestMapping("/api/login")
    public Mono<String> login(@RequestBody String json) {
        return Mono.just(json);
    }

    @RequestMapping("/")
    public Mono<String> greet(Mono<Principal> principal) {
        return principal
                .map(Principal::getName)
                .map(name -> String.format("Hello, %s", name));
    }
}
