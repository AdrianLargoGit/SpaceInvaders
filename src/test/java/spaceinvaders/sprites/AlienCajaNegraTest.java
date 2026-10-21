package spaceinvaders.sprites;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import space_invaders.sprites.Alien;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
@DisplayName("Alien: caja negra de inicialización y movimiento")
class AlienCajaNegraTest {
    @DisplayName("Inicialización de Alien: valores límite robustos de peor caso")
    @ParameterizedTest
    @CsvSource({
            "AC-001, 179, 175, 179, 175",
            "AC-002, 0, 175, 0, 175",
            "AC-003, 1, 175, 1, 175",
            "AC-004, 357, 175, 357, 175",
            "AC-005, 358, 175, 358, 175",
            "AC-006, 179, 0, 179, 0",
            "AC-007, 179, 1, 179, 1",
            "AC-008, 179, 349, 179, 349",
            "AC-009, 179, 350, 179, 350",
            "AC-010, -1, 175, 0, 175",
            "AC-011, 359, 175, 358, 175",
            "AC-012, 179, -1, 179, 0",
            "AC-013, 179, 351, 179, 350",
            "AC-014, 0, 0, 0, 0",
            "AC-015, 0, 1, 0, 1",
            "AC-016, 0, 349, 0, 349",
            "AC-017, 0, 350, 0, 350",
            "AC-018, 1, 0, 1, 0",
            "AC-019, 1, 1, 1, 1",
            "AC-020, 1, 349, 1, 349",
            "AC-021, 1, 350, 1, 350",
            "AC-022, 357, 0, 357, 0",
            "AC-023, 357, 1, 357, 1",
            "AC-024, 357, 349, 357, 349",
            "AC-025, 357, 350, 357, 350",
            "AC-026, 358, 0, 358, 0",
            "AC-027, 358, 1, 358, 1",
            "AC-028, 358, 349, 358, 349",
            "AC-029, 358, 350, 358, 350",
            "AC-030, -1, -1, 0, 0",
            "AC-031, -1, 0, 0, 0",
            "AC-032, -1, 1, 0, 1",
            "AC-033, -1, 349, 0, 349",
            "AC-034, -1, 350, 0, 350",
            "AC-035, -1, 351, 0, 350",
            "AC-036, 0, -1, 0, 0",
            "AC-037, 0, 351, 0, 350",
            "AC-038, 1, -1, 1, 0",
            "AC-039, 1, 351, 1, 350",
            "AC-040, 357, -1, 357, 0",
            "AC-041, 357, 351, 357, 350",
            "AC-042, 358, -1, 358, 0",
            "AC-043, 358, 351, 358, 350",
            "AC-044, 359, -1, 358, 0",
            "AC-045, 359, 0, 358, 0",
            "AC-046, 359, 1, 358, 1",
            "AC-047, 359, 349, 358, 349",
            "AC-048, 359, 350, 358, 350",
            "AC-049, 359, 351, 358, 350"
    })
    void inicializacionValoresLimiteRobustosPeorCaso(String id, int x, int y,
                                                     int expectedX, int expectedY) {
        Alien objeto = new Alien(x, y);
        assertAll(id,
                () -> assertEquals(expectedX, objeto.getX(), id + " X"),
                () -> assertEquals(expectedY, objeto.getY(), id + " Y"));
    }

    @DisplayName("Movimiento de Alien: equivalencia débil normal, izquierda, reposo y derecha")
    @ParameterizedTest
    @CsvSource({
            "AM-001, 179, 175, -1, 178, 175",
            "AM-002, 179, 175,  0, 179, 175",
            "AM-003, 179, 175,  1, 180, 175"
    })
    void movimientoClasesEquivalencia(String id, int x, int y, int direction,
                                      int expectedX, int expectedY) {
        Alien alien = new Alien(179, 175);
        alien.setX(x);
        alien.setY(y);
        alien.act(direction);
        assertAll(id,
                () -> assertEquals(expectedX, alien.getX(), id + " X"),
                () -> assertEquals(expectedY, alien.getY(), id + " Y"));
    }
}
