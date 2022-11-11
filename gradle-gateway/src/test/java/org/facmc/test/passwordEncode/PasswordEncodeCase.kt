package org.facmc.test.passwordEncode

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootTest(classes = [PasswordEncodeCase::class])
class PasswordEncodeCase {
    @Test
    public fun encode() {
        val pw: BCryptPasswordEncoder = BCryptPasswordEncoder();
        println(pw.encode("123456"));
    }
}