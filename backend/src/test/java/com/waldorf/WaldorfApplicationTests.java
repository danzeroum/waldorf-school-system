package com.waldorf;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class WaldorfApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que o contexto Spring sobe corretamente com H2
    }
}
