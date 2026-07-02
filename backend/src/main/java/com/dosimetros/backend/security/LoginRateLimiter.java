package com.dosimetros.backend.security;

import com.dosimetros.backend.exception.TooManyRequestsException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limitador simple en memoria para el login: tras varios intentos fallidos
 * desde la misma IP, bloquea temporalmente para mitigar fuerza bruta.
 */
@Component
public class LoginRateLimiter {

    private static final int MAX_INTENTOS = 5;
    private static final long VENTANA_MS = 15 * 60 * 1000L;   // se cuentan fallos en esta ventana
    private static final long BLOQUEO_MS = 15 * 60 * 1000L;   // duración del bloqueo

    private static class Estado {
        int fallos;
        long primerFallo;
        long bloqueadoHasta;
    }

    private final Map<String, Estado> porIp = new ConcurrentHashMap<>();

    public synchronized void verificarPermitido(String ip) {
        Estado e = porIp.get(ip);
        long ahora = System.currentTimeMillis();
        if (e != null && e.bloqueadoHasta > ahora) {
            long seg = (e.bloqueadoHasta - ahora) / 1000;
            throw new TooManyRequestsException(
                    "Demasiados intentos de inicio de sesión. Reintenta en " + seg + " segundos.");
        }
    }

    public synchronized void registrarFallo(String ip) {
        long ahora = System.currentTimeMillis();
        Estado e = porIp.computeIfAbsent(ip, k -> new Estado());
        if (ahora - e.primerFallo > VENTANA_MS) {
            e.fallos = 0;
            e.primerFallo = ahora;
        }
        e.fallos++;
        if (e.fallos >= MAX_INTENTOS) {
            e.bloqueadoHasta = ahora + BLOQUEO_MS;
        }
    }

    public synchronized void registrarExito(String ip) {
        porIp.remove(ip);
    }
}
