package org.facmc.gateway.handle;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class LoginFailureHandle implements ServerAuthenticationFailureHandler {
    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        return Mono.defer(() -> Mono.just(webFilterExchange.getExchange().getResponse()).flatMap(response -> {

            log.debug(response.toString());

            DataBufferFactory dataBufferFactory = response.bufferFactory();
            Map<String, String> error = new HashMap<String, String>();
            error.put("error", exception.toString());
//            ResultVO<Object> result = ResultVO.error(ResultEnum.GATEWAY_SYS_ERROR);
//
//            // 账号不存在
//            if (exception instanceof UsernameNotFoundException) {
//                result = ResultVO.error(ResultEnum.ACCOUNT_NOT_EXIST);
//                // 用户名或密码错误
//            } else if (exception instanceof BadCredentialsException) {
//                result = ResultVO.error(ResultEnum.LOGIN_PASSWORD_ERROR);
//                // 账号已过期
//            } else if (exception instanceof AccountExpiredException) {
//                result = ResultVO.error(ResultEnum.ACCOUNT_EXPIRED);
//                // 账号已被锁定
//            } else if (exception instanceof LockedException) {
//                result = ResultVO.error(ResultEnum.ACCOUNT_LOCKED);
//                // 用户凭证已失效
//            } else if (exception instanceof CredentialsExpiredException) {
//                result = ResultVO.error(ResultEnum.ACCOUNT_CREDENTIAL_EXPIRED);
//                // 账号已被禁用
//            } else if (exception instanceof DisabledException) {
//                result = ResultVO.error(ResultEnum.ACCOUNT_DISABLE);
//            } else if (exception instanceof AuthenticationServiceException) {
//                result.setMsg(exception.getMessage());
//            }

            DataBuffer dataBuffer = dataBufferFactory.wrap(JSONObject.toJSONString(error).getBytes());
            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            return response.writeWith(Mono.just(dataBuffer));
        }));
    }
}
