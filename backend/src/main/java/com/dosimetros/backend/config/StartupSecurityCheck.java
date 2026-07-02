package com.dosimetros.backend.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Verifica al arranque que no se estén usando los secretos de desarrollo por
 * defecto. En el perfil 'prod' aborta el inicio; en cualquier otro caso avisa.
 */
@Component
public class StartupSecurityCheck {

    private static final Logger log = LoggerFactory.getLogger(StartupSecurityCheck.class);

    // Valores por defecto que NO deben usarse en producción.
    private static final String DEFAULT_JWT_SECRET =
            "dev-insecure-jwt-secret-change-me-in-prod-0123456789";
    private static final String DEFAULT_DB_PASSWORD = "1234";

    private final Environment env;
    private final String jwtSecret;
    private final String dbPassword;

    public StartupSecurityCheck(Environment env,
                                @Value("${jwt.secret}") String jwtSecret,
                                @Value("${spring.datasource.password}") String dbPassword) {
        this.env = env;
        this.jwtSecret = jwtSecret;
        this.dbPassword = dbPassword;
    }

    @PostConstruct
    public void verificar() {
        boolean prod = Arrays.asList(env.getActiveProfiles()).contains("prod");
        boolean jwtInseguro = DEFAULT_JWT_SECRET.equals(jwtSecret);
        boolean dbInsegura = DEFAULT_DB_PASSWORD.equals(dbPassword);

        if (prod && (jwtInseguro || dbInsegura)) {
            StringBuilder msg = new StringBuilder("Configuración insegura en el perfil 'prod': ");
            if (jwtInseguro) msg.append("define la variable de entorno JWT_SECRET. ");
            if (dbInsegura) msg.append("define la variable de entorno DB_PASSWORD. ");
            throw new IllegalStateException(msg.toString());
        }

        if (jwtInseguro) {
            log.warn("=== SEGURIDAD: se está usando el JWT_SECRET por defecto. "
                    + "Define JWT_SECRET antes de exponer el sistema. ===");
        }
        if (dbInsegura) {
            log.warn("=== SEGURIDAD: se está usando la contraseña de BD por defecto. "
                    + "Define DB_PASSWORD antes de producción. ===");
        }
    }
}
