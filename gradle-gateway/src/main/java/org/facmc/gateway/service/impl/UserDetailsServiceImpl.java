package org.facmc.gateway.service.impl;


import lombok.extern.log4j.Log4j2;
import org.facmc.gateway.mapper.UserDetailsMapper;
import org.facmc.gateway.pojo.MyUserDetials;
import org.facmc.gateway.pojo.PathConfigMap;
import org.facmc.gateway.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Log4j2
public class UserDetailsServiceImpl {


    @Autowired
    private UserDetailsMapper userDetailsMapper;
    @Resource
    private RedisTemplate<Object, Object> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void saveCacheUser(UserDetails user) {
        redisTemplate.opsForValue().set(user.getUsername(), user);
    }

    public UserDetails queryCacheUser(String username) {
        return (UserDetails) redisTemplate.opsForValue().get(username);
    }


    public MyUserDetials findByUsername(String username) {
        User user = userDetailsMapper.searchUserByUsername(username);
        if (user.getUserName() == null) {
            throw new UsernameNotFoundException("未找到用户");
        }
        if (user.getUserPermission() == 2) {
            return new MyUserDetials(username, user.getEncryption(),
                    PathConfigMap.PathMap.SUPER_ADMIN_TEMPLATE,
                    user.getIsEnable(), user.getId(), AuthorityUtils.commaSeparatedStringToAuthorityList("SUPER,ADMIN,NORMAL"));
//            return Mono.just(org.springframework.security.core.userdetails.User
//                    .withUsername(username)
//                    .password(user.getEncryption())
//                    .roles("super-admin", "admin", "normal")
//                    .build()
//            );
//            return Mono.just(new org.springframework.security.core.userdetails.User(username, user.getEncryption(),
//                    AuthorityUtils.commaSeparatedStringToAuthorityList("super-admin")));
        } else if (user.getUserPermission() == 1) {
            return new MyUserDetials(username, user.getEncryption(),
                    PathConfigMap.PathMap.ADMIN_TEMPLATE,
                    user.getIsEnable(), user.getId(), AuthorityUtils.commaSeparatedStringToAuthorityList("ADMIN,NORMAL"));
//            return Mono.just(org.springframework.security.core.userdetails.User
//                    .withUsername(username)
//                    .password(user.getEncryption())
//                    .roles("admin", "normal")
//                    .build()
//            );
        }
        return new MyUserDetials(username, user.getEncryption(),
                PathConfigMap.PathMap.NORMAL_TEMPLATE,
                user.getIsEnable(), user.getId(), AuthorityUtils.commaSeparatedStringToAuthorityList("NORMAL"));
//        return Mono.just(org.springframework.security.core.userdetails.User
//                .withUsername(username)
//                .password(user.getEncryption())
//                .roles("normal")
//                .build()
//        );
    }
}
