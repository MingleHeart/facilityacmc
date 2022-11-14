package org.facmc.gateway.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.facmc.common.pojo.AuthRole;
import org.facmc.common.pojo.PathConfigMap;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 鉴权类
 */
@Component
@Slf4j
public class MyAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext object) {
        log.debug("{}", authentication.toString());
        ServerWebExchange exchange = object.getExchange();
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        log.debug("path:{}", path);
        //从map中获取授权
        List<AuthRole> pathRole = PathConfigMap.PathMap.pathRoles.get(path);
        List<String> roles = pathRole.stream().map(AuthRole::getRoleName).collect(Collectors.toList());
        return authentication
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(auth -> {
                            log.debug(auth.getAuthorities().toString());
                            return auth.getAuthorities();
                        }
                )
                .map(GrantedAuthority::getAuthority)
                .any(pathRole::contains)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

    @Override
    public Mono<Void> verify(Mono<Authentication> authentication, AuthorizationContext object) {

        return check(authentication, object)
                .filter(AuthorizationDecision::isGranted)
                .switchIfEmpty(Mono.defer(() -> {
                    Map<String, String> e = new HashMap<>();
                    String body = JSON.toJSONString(e.put("error", "验证失败"));
                    return Mono.error(new AccessDeniedException(body));
                }))
                .flatMap(d -> Mono.empty());
    }
}
