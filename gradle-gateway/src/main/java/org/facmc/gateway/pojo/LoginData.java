package org.facmc.gateway.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class LoginData {
    private String username;
    private String password;
    private String loginType;
}
