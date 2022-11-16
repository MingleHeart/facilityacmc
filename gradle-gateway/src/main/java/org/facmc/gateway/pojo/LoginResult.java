package org.facmc.gateway.pojo;

import lombok.Data;

@Data
public class LoginResult {
    private String token;
    private MyUserDetials userDetails;
}
