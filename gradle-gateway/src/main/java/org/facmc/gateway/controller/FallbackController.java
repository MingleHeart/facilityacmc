package org.facmc.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    @RequestMapping("/defaultFallback")
    public String fallback() {
        return "{\"rtnCode\":\"-1\",\"msg\":\"熔断降级\",\"code\":200}";
    }
}
