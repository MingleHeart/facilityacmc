package org.facmc.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.facmc.gateway.pojo.User;
import org.facmc.gateway.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
@Log4j2
public class UserLoginController {

    @Autowired
    UserDetailsService userDetailsService;
    AuthenticationManager authenticationManager;

    @RequestMapping("/test")
    public String test() {
        return "success";
    }

    @RequestMapping("/test1")
    public String test1() {
        return "success1";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestBody String json) {
        Authentication authentication = null;
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            JSONObject params = jsonObject.getJSONObject("params");
            String username = params.getString("username");
            String password = params.getString("password");
            User user = userDetailsService.searchUserByUsername(username);
            System.out.println(user);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            log.error(e.toString(), json);
        }
        return "hello";
    }
}
