package spaceinvaders.sprites;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import space_invaders.sprites.Shot;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Shot: pruebas de caja negra")
class ShotCajaNegraTest {

    // Javadoc, Constant Field Values
    private static final int H_SPACE = 6;
    private static final int V_SPACE = 1;

    @Test
    @DisplayName("SC-001: Constructor por defecto crea disparo sin configurar (0,0)")
    void constructorPorDefecto() {
        Shot shot = new Shot();
        assertAll("SC-001",
                () -> assertEquals(0, shot.getX(), "SC-001 getX esperado 0"),
                () -> assertEquals(0, shot.getY(), "SC-001 getY esperado 0")
        );
    }

    @DisplayName("SC-002..SC-010: Constructor Shot(x,y) con coordenadas en rango del tablero")
    @ParameterizedTest
    @CsvSource({
            "SC-002,   0,   0,   6,  -1",
            "SC-003,   0, 175,   6, 174",
            "SC-004,   0, 350,   6, 349",
            "SC-005, 179,   0, 185,  -1",
            "SC-006, 179, 175, 185, 174",
            "SC-007, 179, 350, 185, 349",
            "SC-008, 358,   0, 364,  -1",
            "SC-009, 358, 175, 364, 174",
            "SC-010, 358, 350, 364, 349"
    })
    void constructorConCoordenadas(String id, int x, int y, int expectedX, int expectedY) {
        Shot shot = new Shot(x, y);
        assertAll(id,
                () -> assertEquals(expectedX, shot.getX(), id + " X (x + H_SPACE)"),
                () -> assertEquals(expectedY, shot.getY(), id + " Y (y - V_SPACE)")
        );
    }

    @Test
    @DisplayName("SC-011: Shot(179, 280) inicializado en posición del jugador da (185, 279)")
    void constructorPosicionJugador() {
        Shot shot = new Shot(179, 280);
        assertAll("SC-011",
                () -> assertEquals(179 + H_SPACE, shot.getX(), "SC-011 X esperado 185"),
                () -> assertEquals(280 - V_SPACE, shot.getY(), "SC-011 Y esperado 279")
        );
    }

    @Test
    @DisplayName("SC-012: Estado inicial del disparo con coordenadas (visible, no dying, imagen no nula)")
    void constructorEstadoInicial() {
        Shot shot = new Shot(179, 280);
        assertAll("SC-012",
                () -> assertTrue(shot.isVisible(), "SC-012 isVisible debe ser true"),
                () -> assertFalse(shot.isDying(), "SC-012 isDying debe ser false"),
                () -> assertNotNull(shot.getImage(), "SC-012 getImage no debe ser null")
        );
    }
}
