package org.facmc.gateway.config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * 从token获取用户
 */

public class SecurityContextRepository implements ServerSecurityContextRepository {
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return null;
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
//        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
//        String authorizaton = httpHeaders.getFirst(HttpHeaders.AUTHORIZATION);
//        if (StringUtils.isBlank(authorizaton)) {
//            return Mono.empty();
//        }
//
//        String token = authorizaton.substring(AuthConstant.TOKEN_HEAD.length());
//        if (StringUtils.isBlank(token)) {
//            return Mono.empty();
//        }
//
//        Claims claims = (Claims) Jwts.parser()
//                .setSigningKey("")
//                .parseClaimsJws(token)
//                .getBody();
//        String username = claims.getSubject();
//        long userId = claims.get(AuthConstant.USER_ID_KEY, Long.class);
//        String rolesStr = claims.get(AuthConstant.ROLES_STRING_KEY, String.class);
//        List<AuthRole> list = Arrays.stream(rolesStr.split(","))
//                .map(roleName -> new AuthRole().setRoleName(roleName))
//                .collect(Collectors.toList());
//
        System.out.println(exchange);
        return Mono.empty();
    }
}
