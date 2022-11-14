package org.facmc.gateway.config;

import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.facmc.common.pojo.AuthRole;
import org.facmc.common.pojo.MyUserDetials;
import org.facmc.common.utils.JwtTokenUtils;
import org.facmc.constant.AuthConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * 从token获取用户
 */
@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {
    @Resource
    private MyAuthenticationManager myAuthenticationManager;


    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
        String authorizaton = httpHeaders.getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authorizaton)) {
            return Mono.empty();
        }

        String token = authorizaton.substring(AuthConstant.TOKEN_HEAD.length());
        if (StringUtils.isBlank(token)) {
            return Mono.empty();
        }
        JwtTokenUtils jwtTokenUtils = new JwtTokenUtils();
        Claims claims = jwtTokenUtils.getClaimsFromToken(token);
        String username = claims.getSubject();
        long userId = claims.get(AuthConstant.USER_ID_KEY, Long.class);
        ArrayList<AuthRole> list = claims.get(AuthConstant.ROLES_STRING_KEY, ArrayList.class);
//        List<AuthRole> list = Arrays.stream(rolesStr.split(","))
//                .map(roleName -> new AuthRole().setRoleName(roleName))
//                .collect(Collectors.toList());
//        List<AuthRole> list = Arrays.stream(roles.split(","))
//                .map(roleName -> new AuthRole(roleName))
//                .collect(Collectors.toList());
        //构建用户令牌
        MyUserDetials userDetails = new MyUserDetials(userId, username, list);
        boolean validateToken = jwtTokenUtils.validateToken(token, userDetails);

        AuthenticationToken authToken = new AuthenticationToken(userDetails, null, userDetails.getAuthorities());
        // 从请求头中删除token，并添加解析出来的信息
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(AuthConstant.USER_ID_KEY, String.valueOf(userId))
                .header(AuthConstant.USERNAME_KEY, username)
                .header(AuthConstant.ROLES_STRING_KEY, list.toString())
                .headers(headers -> headers.remove(HttpHeaders.AUTHORIZATION))
                .build();
        exchange.mutate().request(request).build();

        System.out.println(authToken);
        return myAuthenticationManager.authenticate(authToken)
                .map(SecurityContextImpl::new);
    }
}
