package org.facmc.oauth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest(classes = EncodeTestCase.class)
public class EncodeTestCase {
    @Test
    public void encode() {
        BCryptPasswordEncoder pw = new BCryptPasswordEncoder();
        boolean matches = pw.matches("123123", "$2a$10$9PzeYzC6eBLmskZ19uaPC.Ac7zMufS56oiExaxP2/4rp9U3UE5yCi");
        System.out.println("=======================================================");
        System.out.println(matches);
    }
}
