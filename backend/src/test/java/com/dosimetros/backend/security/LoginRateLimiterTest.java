package com.dosimetros.backend.security;

import com.dosimetros.backend.exception.TooManyRequestsException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginRateLimiterTest {

    @Test
    void bloqueaTrasCincoIntentosFallidos() {
        LoginRateLimiter limiter = new LoginRateLimiter();
        String ip = "1.2.3.4";

        // Los primeros 5 fallos aún no bloquean la verificación previa.
        for (int i = 0; i < 5; i++) {
            assertDoesNotThrow(() -> limiter.verificarPermitido(ip));
            limiter.registrarFallo(ip);
        }

        // Al sexto intento ya está bloqueado.
        assertThrows(TooManyRequestsException.class, () -> limiter.verificarPermitido(ip));
    }

    @Test
    void elExitoReiniciaElContador() {
        LoginRateLimiter limiter = new LoginRateLimiter();
        String ip = "5.6.7.8";

        for (int i = 0; i < 4; i++) {
            limiter.registrarFallo(ip);
        }
        limiter.registrarExito(ip); // reinicia

        // Tras reiniciar, vuelve a permitir.
        assertDoesNotThrow(() -> limiter.verificarPermitido(ip));
    }

    @Test
    void ipsDistintasSonIndependientes() {
        LoginRateLimiter limiter = new LoginRateLimiter();
        for (int i = 0; i < 5; i++) {
            limiter.registrarFallo("10.0.0.1");
        }
        // Otra IP no se ve afectada.
        assertDoesNotThrow(() -> limiter.verificarPermitido("10.0.0.2"));
    }
}
