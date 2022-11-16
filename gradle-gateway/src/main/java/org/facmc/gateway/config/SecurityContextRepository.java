package org.facmc.gateway.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.facmc.constant.AuthConstant;
import org.facmc.gateway.pojo.AuthRole;
import org.facmc.gateway.pojo.MyUserDetials;
import org.facmc.gateway.utils.JwtTokenUtils;
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
import java.util.List;

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
        String roles = claims.get(AuthConstant.ROLES_STRING_KEY, String.class);
        String authorities = claims.get(AuthConstant.AUTHORITIES, String.class);
        JSONArray roleArray = JSON.parseArray(roles);
        List<AuthRole> authRoleList = new ArrayList<AuthRole>();
        for (Object o : roleArray) {
            JSONObject jsonObject = (JSONObject) o;
            String role = jsonObject.getString("roleName");
            AuthRole authRole = new AuthRole(role);
            authRoleList.add(authRole);
        }

//        List<AuthRole> list = Arrays.stream(rolesStr.split(","))
//                .map(roleName -> new AuthRole().setRoleName(roleName))
//                .collect(Collectors.toList());
//        List<AuthRole> list = Arrays.stream(roles.split(","))
//                .map(roleName -> new AuthRole(roleName))
//                .collect(Collectors.toList());
        //构建用户令牌
        MyUserDetials userDetails = new MyUserDetials(userId, username, authRoleList);
        boolean validateToken = jwtTokenUtils.validateToken(token, userDetails);

        AuthenticationToken authToken = new AuthenticationToken(userDetails, null, userDetails.getAuthorities());
        // 从请求头中删除token，并添加解析出来的信息
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(AuthConstant.USER_ID_KEY, String.valueOf(userId))
                .header(AuthConstant.USERNAME_KEY, username)
                .header(AuthConstant.ROLES_STRING_KEY, authRoleList.toString()
                        .replace("[", "").replace("]", ""))
                .headers(headers -> headers.remove(HttpHeaders.AUTHORIZATION))
                .build();
        exchange.mutate().request(request).build();
        if (authToken.isAuthenticated()) {
            SecurityContextImpl securityContext = new SecurityContextImpl(authToken);
            return Mono.just(securityContext);
        }
        return myAuthenticationManager.authenticate(authToken).map(SecurityContextImpl::new);
    }
}
