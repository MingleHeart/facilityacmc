package org.facmc.gateway.service.impl;

import lombok.extern.log4j.Log4j2;
import org.facmc.gateway.mapper.UserDetailsMapper;
import org.facmc.gateway.pojo.User;
import org.facmc.gateway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDetailsMapper userDetailsMapper;

    @Override
    public User searchUserByUsername(String username) {
        User user = null;
        try {
            user = userDetailsMapper.searchUserByUsername(username);
        } catch (Exception e) {
            log.error(e.toString(), username);
        }
        return user;
    }
}
