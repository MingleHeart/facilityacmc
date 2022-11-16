package org.facmc.gateway.pojo;

import java.util.*;

public class PathConfigMap {
    public static class PathMap {
        public static final List<AuthRole> SUPER_ADMIN_TEMPLATE = Arrays.asList(new AuthRole("SUPER"), new AuthRole("ADMIN"), new AuthRole("NORMAL"));
        public static final List<AuthRole> ADMIN_TEMPLATE = Arrays.asList(new AuthRole("ADMIN"), new AuthRole("NORMAL"));
        public static final List<AuthRole> NORMAL_TEMPLATE = Collections.singletonList(new AuthRole("NORMAL"));
        public static final Map<String, Object> pathRoles = new HashMap<String, Object>();

        static {
            pathRoles.put("/api/test", "SUPER");
            pathRoles.put("/api/test1", "ADMIN");
            pathRoles.put("/api/user/service/listUsersForSuperUser", "SUPER");
        }
    }
}
