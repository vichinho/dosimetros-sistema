package com.dosimetros.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Verifica que todo el contexto de Spring (beans, seguridad, JPA) carga
 * correctamente. Usa H2 en memoria (perfil 'test'), sin requerir MySQL.
 */
@SpringBootTest
@ActiveProfiles("test")
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }
}
