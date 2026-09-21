import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * El candado del punto decimal.
 *
 * <p>Un {@code String.format} sin Locale usa el idioma DE LA MAQUINA: el mismo jar
 * escribe "$12.50" en un servidor ingles y "$12,50" en uno espanol o frances, y el
 * separador de millares cambia igual. Los plugins hablan ingles entero, asi que el
 * numero tiene que salir siempre igual. Se destapo el 21/9/2026 leyendo la barra de
 * accion que recibe el cliente: HudForge pintaba "tps 20,0".
 *
 * <p>Se arregla poniendo {@code java.util.Locale.ROOT} como primer argumento.
 */
class LocaleGuardTest {

    private static final Path SOURCES = Path.of("src", "main", "java");

    @Test
    @DisplayName("ningun numero que ve el jugador depende del idioma del sistema")
    void numbersDoNotFollowTheSystemLocale() throws IOException {
        List<String> culpables = new ArrayList<>();
        try (Stream<Path> files = Files.walk(SOURCES)) {
            for (Path p : files.filter(f -> f.toString().endsWith(".java")).toList()) {
                String codigo = Files.readString(p, StandardCharsets.UTF_8);
                if (codigo.contains("String.format(\"")) culpables.add(p.toString());
            }
        }
        assertTrue(culpables.isEmpty(),
            "String.format sin Locale (usa java.util.Locale.ROOT) en: " + culpables);
    }
}
