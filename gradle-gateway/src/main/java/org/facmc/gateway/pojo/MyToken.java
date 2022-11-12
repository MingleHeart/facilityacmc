package org.facmc.gateway.pojo;

import lombok.Data;

@Data
public class MyToken {
    public String token;

    public MyToken(String token) {
        this.token = token;
    }
}
