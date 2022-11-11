package org.facmc.gateway.handle;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.facmc.gateway.constant.AuthConstant;
import org.facmc.gateway.pojo.MyUserDetials;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoginSuccessHandle implements ServerAuthenticationSuccessHandler {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        return Mono.defer(() -> Mono
                        .just(webFilterExchange.getExchange().getResponse()))
                .flatMap(response -> {
                    DataBufferFactory dataBufferFactory = response.bufferFactory();

                    //生成Token
                    Map<String, Object> map = new HashMap<String, Object>();
                    MyUserDetials userDetails = (MyUserDetials) authentication.getPrincipal();
                    map.put(AuthConstant.USERNAME_KEY, userDetails.getUsername());
                    map.put(AuthConstant.USER_ID_KEY, userDetails.getUserId());
//                    String rolesStr = userDetails.getRoles().stream().map(AuthRole::getRoleName)
//                            .collect(Collectors.joining(","));
//                    map.put(AuthConstant.ROLES_STRING_KEY, rolesStr);
                    String token = Jwts.builder()
                            .setId(userDetails.getUserId().toString())
                            .setSubject(userDetails.getUsername())
                            .signWith(SignatureAlgorithm.HS256, AuthConstant.TOKEN_HEAD).compact();
                    System.out.println(token);
                    redisTemplate.opsForValue().set("AuthConstant.TOKEN_REDIS_KEY" + userDetails.getUserId(), token, 600);
                    return Mono.empty();
                });
    }
}
