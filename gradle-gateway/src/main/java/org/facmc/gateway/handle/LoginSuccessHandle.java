package org.facmc.gateway.handle;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

public class LoginSuccessHandle implements ServerAuthenticationSuccessHandler {
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        System.out.println(webFilterExchange);
        System.out.println("===========================");
        System.out.println(authentication);
        return null;
    }
}
