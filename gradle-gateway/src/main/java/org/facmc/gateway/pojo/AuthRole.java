package org.facmc.gateway.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AuthRole {
    private String roleName;
}
