package org.facmc.common.pojo;

import java.util.*;

public class PathConfigMap {
    public static class PathMap {
        public static final List<AuthRole> SUPER_ADMIN_TEMPLATE = Arrays.asList(new AuthRole("super-admin"), new AuthRole("admin"), new AuthRole("normal"));
        public static final List<AuthRole> ADMIN_TEMPLATE = Arrays.asList(new AuthRole("admin"), new AuthRole("normal"));
        public static final List<AuthRole> NORMAL_TEMPLATE = Collections.singletonList(new AuthRole("normal"));
        public static final Map<String, List<AuthRole>> pathRoles = new HashMap<String, List<AuthRole>>();

        static {
            pathRoles.put("/api/test", SUPER_ADMIN_TEMPLATE);
            pathRoles.put("/api/test1", SUPER_ADMIN_TEMPLATE);
            pathRoles.put("/api/user/service/listUsersForSuperUser", SUPER_ADMIN_TEMPLATE);
        }
    }
}
