package org.facmc.common.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

public class HttpContextUtils {
    public static final Class<ServerHttpRequest> CONTEXT_KEY = ServerHttpRequest.class;

    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.deferContextual(contextView -> Mono.just(contextView.get(CONTEXT_KEY)));
    }
}

