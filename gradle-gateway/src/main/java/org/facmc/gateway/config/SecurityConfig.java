package org.facmc.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {
    //    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.formLogin()
//                .loginProcessingUrl("/test")
//                .loginPage("http://localhost:39091/login");
//
//        //授权
//        http.authorizeHttpRequests()
//                .antMatchers("/api/test").permitAll()
//                .anyRequest().authenticated();
//    }
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.formLogin();
        httpSecurity
                .authorizeExchange()
                .pathMatchers("/api/login").permitAll()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .anyExchange().authenticated()
                .and()
                .csrf()
                .disable()
                .cors();
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder getPW() {
        return new BCryptPasswordEncoder();
//        {
//            /***
//             * 重写密码加密方式
//             * @param rawPassword 密码
//             * @return 使用MD5加盐加密
//             */
//            @Override
//            public String encode(CharSequence rawPassword) {
//                return Md5SaltUtils.generate(rawPassword.toString());
//            }
//
//            /***
//             * 密码验证
//             * @param rawPassword the raw password to encode and match
//             * @param encodedPassword the encoded password from storage to compare with
//             * @return
//             */
//            @Override
//            public boolean matches(CharSequence rawPassword, String encodedPassword) {
//                return Md5SaltUtils.verify(rawPassword.toString(), encodedPassword);
//            }
//        };
    }


    // 对并发session进行管理
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
