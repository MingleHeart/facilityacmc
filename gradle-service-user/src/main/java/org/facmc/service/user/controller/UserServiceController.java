package org.facmc.service.user.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import org.facmc.service.user.pojo.User;
import org.facmc.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/service")
@Log4j2
public class UserServiceController {
    @Autowired
    private UserService userService;

    @RequestMapping("/test")
    public Mono<String> test() {
        return Mono.just("hello");
    }

    @RequestMapping("/listUsersForSuperUser")
    public Mono<String> listUsersSuper() {
        Map<String, Object> res = new HashMap<String, Object>();
        List<User> listForSuperUser = new ArrayList<User>();
        int rtnCode;
        String rtnMsg;
        try {
            listForSuperUser = userService.listForSuperUser();
            if (listForSuperUser != null) {
                rtnCode = 1;
                rtnMsg = "success";
                res.put("rtnCode", rtnCode);
                res.put("rtnMsg", rtnMsg);
                res.put("result", listForSuperUser);

            } else {
                rtnCode = 0;
                rtnMsg = "failure";
                res.put("rtnCode", rtnCode);
                res.put("rtnMsg", rtnMsg);
                res.put("result", null);
            }
        } catch (Exception e) {
            res.put("rtnMsg", e);
            res.put("rtnCode", -1);
            res.put("result", null);
            log.debug("{}", e.toString());
        }
        res.put("bean", new Object());
        res.put("beans", new ArrayList<Object>());
        res.put("object", UserServiceController.class);
        return Mono.just(JSON.toJSONString(res));
    }

    @RequestMapping("/listUserByBelong")
    public Mono<String> listUserByBelong(@RequestBody String json) {
        Map<String, Object> res = new HashMap<String, Object>();
        List<User> listUserByBelong = new ArrayList<User>();
        int rtnCode;
        String rtnMsg;
        try {
            listUserByBelong = userService.ListByBelond(json);
            if (listUserByBelong != null) {
                rtnCode = 1;
                rtnMsg = "success";
                res.put("rtnCode", rtnCode);
                res.put("rtnMsg", rtnMsg);
                res.put("result", listUserByBelong);
            } else {
                rtnCode = 0;
                rtnMsg = "failure";
                res.put("rtnCode", rtnCode);
                res.put("rtnMsg", rtnMsg);
                res.put("result", null);
            }

        } catch (Exception e) {
            rtnCode = -1;
            rtnMsg = e.toString();
            res.put("rtnCode", rtnCode);
            res.put("rtnMsg", rtnMsg);
            res.put("result", null);
            log.debug("{}", e.toString());
        }
        res.put("bean", new Object());
        res.put("beans", new ArrayList<Object>());
        res.put("object", UserServiceController.class);
        return Mono.just(JSON.toJSONString(res));
    }

    @RequestMapping("/addUser")
    public Mono<String> addUser(@RequestBody String json, Mono<Principal> principal) {
        Map<String, Object> res = new HashMap<>();
        int rtnCode;
        String rtnMsg;
        try {
            if (userService.addUser(json, principal)) {
                rtnCode = 1;
                rtnMsg = "success";
                res.put("rtnCode", rtnCode);
                res.put("rtnMsg", rtnMsg);
                res.put("result", "success");
            } else {
                rtnCode = 0;
                rtnMsg = "failure";
                res.put("rtnCode", rtnCode);
                res.put("rtnMsg", rtnMsg);
                res.put("result", null);
            }
        } catch (Exception e) {
            rtnCode = -1;
            rtnMsg = e.toString();
            res.put("rtnCode", rtnCode);
            res.put("rtnMsg", rtnMsg);
            res.put("result", null);
            log.debug("{}", e.toString());
        }
        res.put("bean", new Object());
        res.put("beans", new ArrayList<Object>());
        res.put("object", UserServiceController.class);
        return Mono.just(JSON.toJSONString(res));
    }
}
