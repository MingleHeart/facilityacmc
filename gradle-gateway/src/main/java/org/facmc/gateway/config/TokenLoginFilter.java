package org.facmc.gateway.config;

import org.facmc.gateway.service.UserDetailsService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    // 用于接收传进来的bean对象
    private StringRedisTemplate redisTemplate;

    private AuthenticationManager authenticationManager;

    private UserDetailsService loginService;

    private SessionRegistry sessionRegistry;

    public TokenLoginFilter(AuthenticationManager authenticationManager, SessionRegistry sessionRegistry, UserDetailsService loginService, StringRedisTemplate redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.sessionRegistry = sessionRegistry;
        this.loginService = loginService;
        this.redisTemplate = redisTemplate;
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/basic-api/_user/login", "POST"));
        //设置一个账号只能拥有一个session对象
        ConcurrentSessionControlAuthenticationStrategy sessionStrategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
        sessionStrategy.setMaximumSessions(1);
        this.setSessionAuthenticationStrategy(sessionStrategy);
    }

    // 第一步：获取表单信息,security会在请求拦截器之前
//    @SneakyThrows
//    @Override
//    public Authentication attemptAuthentication(ServerHttpRequest req, S res)
//            throws AuthenticationException {
//        //获取表单提交的数据
//        Map map = new ObjectMapper().readValue(req.getInputStream(), Map.class);
//        String operName = (String) map.get("operName");
//        String password = (String) map.get("password");
//        // 将用户名存入session域中
//        req.getSession().setAttribute("operName", operName);
//        // 最终交给security校验密码
//        System.out.println("账号为：" + operName);
//        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(operName, password));
//    }
}
