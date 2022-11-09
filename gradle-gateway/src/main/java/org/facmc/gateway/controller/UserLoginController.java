package org.facmc.gateway.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class UserLoginController {
    @RequestMapping("/test")
    public String test() {
        return "success";
    }

    @RequestMapping("/test1")
    public String test1() {
        return "success1";
    }
}
