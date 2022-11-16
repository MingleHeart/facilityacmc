package org.facmc.gateway.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.facmc.gateway.pojo.PathConfigMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 鉴权类
 */
@Component
@Slf4j
public class MyAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    private static final Logger LOG = LoggerFactory.getLogger(MyAuthorizationManager.class);
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * @param authentication the Authentication to check
     * @param object         the object to check
     * @return
     */
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext object) {
//        log.debug("{}", authentication.toString());
//        ServerWebExchange exchange = object.getExchange();
//        ServerHttpRequest request = exchange.getRequest();
//        String path = request.getURI().getPath();
//        log.debug("path:{}", path);
//        //从map中获取授权
//        List<AuthRole> pathRole = PathConfigMap.PathMap.pathRoles.get(path);
//        List<String> roles = pathRole.stream().map(r -> AuthConstant.ROLE_PRE + r.getRoleName()).collect(Collectors.toList());
//        String roleHeaders = request.getHeaders().getFirst(AuthConstant.ROLES_STRING_KEY);
//        List<AuthRole> roleList = Arrays.stream(roleHeaders.split(",")).map(AuthRole::new).collect(Collectors.toList());
//        long id = Long.parseLong(Objects.requireNonNull(request.getHeaders().getFirst(AuthConstant.USER_ID_KEY)));
//        String username = request.getHeaders().getFirst(AuthConstant.USERNAME_KEY);
//        MyUserDetials userDetials = new MyUserDetials(id, username, roleList);
//        authentication = Mono.just(new AuthenticationToken(userDetials, null, userDetials.getAuthorities()));
//        return authentication
//                .filter(Authentication::isAuthenticated)
//                .flatMapIterable(auth -> {
//                            log.debug(auth.getAuthorities().toString());
//                            return auth.getAuthorities();
//                        }
//                )
//                .map(GrantedAuthority::getAuthority)
//                .any(roles::contains)
//                .map(AuthorizationDecision::new)
//                .defaultIfEmpty(new AuthorizationDecision(false));

        return authentication.map(auth -> {
            ServerWebExchange exchange = object.getExchange();
            ServerHttpRequest request = exchange.getRequest();
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                String authorityAuthority = authority.getAuthority();
                String path = request.getURI().getPath();
                String pathRole = (String) PathConfigMap.PathMap.pathRoles.get(path);
                if (Objects.equals(authorityAuthority, pathRole)) {
                    return new AuthorizationDecision(true);
                }
            }
            return new AuthorizationDecision(false);
        }).defaultIfEmpty(new AuthorizationDecision(false));
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
