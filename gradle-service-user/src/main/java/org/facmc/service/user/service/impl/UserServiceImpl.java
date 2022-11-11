package org.facmc.service.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.facmc.common.utils.SnowFlake;
import org.facmc.service.user.mapper.UserServiceMapper;
import org.facmc.service.user.pojo.User;
import org.facmc.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.List;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserServiceMapper userServiceMapper;

    @Override
    public List<User> listForSuperUser() throws Exception {
        return userServiceMapper.listForSuperUser();
    }

    @Override
    public List<User> ListByBelond(String json) throws Exception {
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject params = jsonObject.getJSONObject("params");
        long id = params.getLong("id");
        return userServiceMapper.listByBelond(id);
    }

    @Override
    public boolean addUser(String json, Mono<Principal> principal) throws Exception {

        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject params = jsonObject.getJSONObject("params");
        User user = new User();
        user.setUserName(params.getString("username"));
        if (userServiceMapper.countByUserNameInt(user.getUserName()) != 0) {
            return false;
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setEncryption(passwordEncoder.encode(params.getString("password")));
        user.setId(SnowFlake.nextId());
        user.setUserCom(params.getString("userCom"));
        user.setUserPermission(params.getInteger("userPermission"));
        Mono<String> map = principal.map(Principal::getName);
        String block = map.block();
        System.out.println(block);
        user.setBelongId(params.getLong("belongId"));
        user.setIsEnable(params.getInteger("isEnable"));
        user.setRName(params.getString("rName"));
        userServiceMapper.addUser(user);
        userServiceMapper.addUserPermission(user);
        return userServiceMapper.countByUserNameInt(user.getUserName()) != 0;
    }
}
