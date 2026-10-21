package spaceinvaders.sprites;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import space_invaders.sprites.Alien;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
@DisplayName("Bomb: inicialización de caja negra")
class AlienBombCajaNegraTest {

    @DisplayName("Inicialización de Bomb: límites válidos")
    @ParameterizedTest
    @CsvSource({
            "BC-001, 179, 175, 179, 175",
            "BC-002, 0, 175, 0, 175",
            "BC-003, 1, 175, 1, 175",
            "BC-004, 357, 175, 357, 175",
            "BC-005, 358, 175, 358, 175",
            "BC-006, 179, 0, 179, 0",
            "BC-007, 179, 1, 179, 1",
            "BC-008, 179, 349, 179, 349",
            "BC-009, 179, 350, 179, 350",
    })
    void bombaLimitesNormales(String id, int x, int y, int expectedX, int expectedY) {
        Alien propietario = new Alien(179, 175);
        Alien.Bomb objeto = propietario.new Bomb(x, y);
        assertAll(id,
                () -> assertEquals(expectedX, objeto.getX(), id + " X"),
                () -> assertEquals(expectedY, objeto.getY(), id + " Y"));
    }

    @DisplayName("Inicialización de Bomb: una coordenada fuera del rango")
    @ParameterizedTest
    @CsvSource({
            "BC-010, -1, 175, 0, 175",
            "BC-011, 359, 175, 358, 175",
            "BC-012, 179, -1, 179, 0",
            "BC-013, 179, 351, 179, 350",
    })
    void bombaRobustezAdicional(String id, int x, int y, int expectedX, int expectedY) {
        Alien propietario = new Alien(179, 175);
        Alien.Bomb objeto = propietario.new Bomb(x, y);
        assertAll(id,
                () -> assertEquals(expectedX, objeto.getX(), id + " X"),
                () -> assertEquals(expectedY, objeto.getY(), id + " Y"));
    }

    @DisplayName("Inicialización de Bomb: combinaciones de límites válidos")
    @ParameterizedTest
    @CsvSource({
            "BC-014, 0, 0, 0, 0",
            "BC-015, 0, 1, 0, 1",
            "BC-016, 0, 349, 0, 349",
            "BC-017, 0, 350, 0, 350",
            "BC-018, 1, 0, 1, 0",
            "BC-019, 1, 1, 1, 1",
            "BC-020, 1, 349, 1, 349",
            "BC-021, 1, 350, 1, 350",
            "BC-022, 357, 0, 357, 0",
            "BC-023, 357, 1, 357, 1",
            "BC-024, 357, 349, 357, 349",
            "BC-025, 357, 350, 357, 350",
            "BC-026, 358, 0, 358, 0",
            "BC-027, 358, 1, 358, 1",
            "BC-028, 358, 349, 358, 349",
            "BC-029, 358, 350, 358, 350",
    })
    void bombaNormalPeorCasoAdicional(String id, int x, int y, int expectedX, int expectedY) {
        Alien propietario = new Alien(179, 175);
        Alien.Bomb objeto = propietario.new Bomb(x, y);
        assertAll(id,
                () -> assertEquals(expectedX, objeto.getX(), id + " X"),
                () -> assertEquals(expectedY, objeto.getY(), id + " Y"));
    }

    @DisplayName("Inicialización de Bomb: combinaciones con coordenadas inválidas")
    @ParameterizedTest
    @CsvSource({
            "BC-030, -1, -1, 0, 0",
            "BC-031, -1, 0, 0, 0",
            "BC-032, -1, 1, 0, 1",
            "BC-033, -1, 349, 0, 349",
            "BC-034, -1, 350, 0, 350",
            "BC-035, -1, 351, 0, 350",
            "BC-036, 0, -1, 0, 0",
            "BC-037, 0, 351, 0, 350",
            "BC-038, 1, -1, 1, 0",
            "BC-039, 1, 351, 1, 350",
            "BC-040, 357, -1, 357, 0",
            "BC-041, 357, 351, 357, 350",
            "BC-042, 358, -1, 358, 0",
            "BC-043, 358, 351, 358, 350",
            "BC-044, 359, -1, 358, 0",
            "BC-045, 359, 0, 358, 0",
            "BC-046, 359, 1, 358, 1",
            "BC-047, 359, 349, 358, 349",
            "BC-048, 359, 350, 358, 350",
            "BC-049, 359, 351, 358, 350",
    })
    void bombaRobustoPeorCasoAdicional(String id, int x, int y, int expectedX, int expectedY) {
        Alien propietario = new Alien(179, 175);
        Alien.Bomb objeto = propietario.new Bomb(x, y);
        assertAll(id,
                () -> assertEquals(expectedX, objeto.getX(), id + " X"),
                () -> assertEquals(expectedY, objeto.getY(), id + " Y"));
    }
}
