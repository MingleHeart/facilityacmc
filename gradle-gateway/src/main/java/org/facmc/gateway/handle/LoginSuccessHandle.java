package org.facmc.gateway.handle;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.facmc.gateway.constant.AuthConstant;
import org.facmc.gateway.pojo.LoginResult;
import org.facmc.gateway.pojo.MyToken;
import org.facmc.gateway.pojo.MyUserDetials;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        return Mono.defer(() -> Mono
                        .just(webFilterExchange.getExchange().getResponse()))
                .flatMap(response -> {
                    DataBufferFactory dataBufferFactory = response.bufferFactory();

                    //生成Token
                    Map<String, Object> map = new HashMap<String, Object>();
                    Object principal = authentication.getPrincipal();
                    System.out.println(principal);
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
                    LoginResult result = new LoginResult();
                    result.setToken(token);
                    result.setUserDetails(userDetails);

                    MyToken token1 = new MyToken(token);
                    System.out.println(JSON.toJSONString(token1));
//                    redisTemplate.opsForHash().put(AuthConstant.TOKEN_REDIS_KEY, userDetails.getUserId(), JSON.toJSONString(token1));
                    stringRedisTemplate.opsForHash().put(AuthConstant.TOKEN_REDIS_KEY, userDetails.getUserId().toString(), JSON.toJSONString(token1));
                    response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    DataBuffer dataBuffer = dataBufferFactory.wrap(JSON.toJSONBytes(result));
                    return response.writeWith(Mono.just(dataBuffer));
                });
    }
}
