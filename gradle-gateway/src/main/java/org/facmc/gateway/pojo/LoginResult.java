package org.facmc.gateway.pojo;

import lombok.Data;
import org.facmc.common.pojo.MyUserDetials;

@Data
public class LoginResult {
    private String token;
    private MyUserDetials userDetails;
}
