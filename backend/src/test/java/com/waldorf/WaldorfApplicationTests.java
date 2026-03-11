package com.waldorf;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
class WaldorfApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que o contexto Spring sobe corretamente com H2 e sem infraestrutura externa
    }
}
