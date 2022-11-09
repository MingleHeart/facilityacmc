package org.facmc.service.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/service")
public class UserServiceController {
    @RequestMapping("/test")
    public String test() {
        return "hello";
    }
}
