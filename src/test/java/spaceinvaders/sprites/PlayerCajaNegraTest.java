package spaceinvaders.sprites;

import main.Commons;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import space_invaders.sprites.Player;
import spaceinvaders.soporte.SoporteCajaNegra;

import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Player: pruebas de caja negra")
class PlayerCajaNegraTest {

    private static final int MAX_X = Commons.BOARD_WIDTH - Commons.PLAYER_WIDTH; // 358 - 15 = 343

    @Test
    @DisplayName("PC-001: Constructor e inicialización de posición de Player")
    void constructorPosicionInicial() {
        Player player = new Player();
        assertAll("PC-001",
                () -> assertEquals(Commons.BOARD_WIDTH / 2, player.getX(), "PC-001 X inicial esperada 179"),
                () -> assertEquals(Commons.GROUND - 10, player.getY(), "PC-001 Y inicial esperada 280")
        );
    }

    @Test
    @DisplayName("PC-002: Estado inicial de visibilidad e imagen de Player")
    void constructorEstadoInicial() {
        Player player = new Player();
        assertAll("PC-002",
                () -> assertTrue(player.isVisible(), "PC-002 isVisible debe ser true"),
                () -> assertFalse(player.isDying(), "PC-002 isDying debe ser false"),
                () -> assertNotNull(player.getImage(), "PC-002 getImage no debe ser null")
        );
    }

    @DisplayName("PA-001..003: Movimiento act con equivalencia débil normal (X0=179)")
    @ParameterizedTest
    @CsvSource({
            "PA-001, NONE, 179",
            "PA-002, LEFT, 177",
            "PA-003, RIGHT, 181"
    })
    void actEquivalenciaDebilNormal(String id, String action, int expectedX) {
        Player player = new Player();
        player.setX(179);
        if ("LEFT".equals(action)) {
            player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_LEFT));
        } else if ("RIGHT".equals(action)) {
            player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_RIGHT));
        }
        player.act();
        assertAll(id,
                () -> assertEquals(expectedX, player.getX(), id + " getX")
        );
    }

    @DisplayName("PA-004..PA-027: Movimiento act valores límite robustos sobre X0 x 3 estados de dx")
    @ParameterizedTest
    @CsvSource({
            "PA-004,  -1, NONE",
            "PA-005,  -1, LEFT",
            "PA-006,  -1, RIGHT",
            "PA-007,   0, NONE",
            "PA-008,   0, LEFT",
            "PA-009,   0, RIGHT",
            "PA-010,   1, NONE",
            "PA-011,   1, LEFT",
            "PA-012,   1, RIGHT",
            "PA-013, 342, NONE",
            "PA-014, 342, LEFT",
            "PA-015, 342, RIGHT",
            "PA-016, 343, NONE",
            "PA-017, 343, LEFT",
            "PA-018, 343, RIGHT",
            "PA-019, 344, NONE",
            "PA-020, 344, LEFT",
            "PA-021, 344, RIGHT",
            "PA-022, 358, NONE",
            "PA-023, 358, LEFT",
            "PA-024, 358, RIGHT",
            "PA-025, 359, NONE",
            "PA-026, 359, LEFT",
            "PA-027, 359, RIGHT"
    })
    void actValoresLimiteRobustos(String id, int x0, String action) {
        Player player = new Player();
        player.setX(x0);
        if ("LEFT".equals(action)) {
            player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_LEFT));
        } else if ("RIGHT".equals(action)) {
            player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_RIGHT));
        }
        player.act();
        assertAll(id,
                () -> assertTrue(player.getX() >= 0 && player.getX() <= MAX_X,
                        id + " getX debe estar dentro de [0, " + MAX_X + "], obtenido: " + player.getX())
        );
    }

    @Test
    @DisplayName("PA-028: X0=0, LEFT, act() x3 se queda quieta en el límite izquierdo")
    void actLimiteIzquierdoTresVeces() {
        Player player = new Player();
        player.setX(0);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_LEFT));
        player.act();
        int x1 = player.getX();
        player.act();
        int x2 = player.getX();
        player.act();
        int x3 = player.getX();
        assertAll("PA-028",
                () -> assertTrue(x1 >= 0, "PA-028 x1 debe ser >= 0, obtenido: " + x1),
                () -> assertEquals(x1, x2, "PA-028 x1 y x2 deben ser iguales"),
                () -> assertEquals(x2, x3, "PA-028 x2 y x3 deben ser iguales")
        );
    }

    @Test
    @DisplayName("PA-029: X0=MAX_X, RIGHT, act() x3 se queda quieta en el límite derecho")
    void actLimiteDerechoTresVeces() {
        Player player = new Player();
        player.setX(MAX_X);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_RIGHT));
        player.act();
        int x1 = player.getX();
        player.act();
        int x2 = player.getX();
        player.act();
        int x3 = player.getX();
        assertAll("PA-029",
                () -> assertTrue(x1 <= MAX_X, "PA-029 x1 debe ser <= MAX_X, obtenido: " + x1),
                () -> assertEquals(x1, x2, "PA-029 x1 y x2 deben ser iguales"),
                () -> assertEquals(x2, x3, "PA-029 x2 y x3 deben ser iguales")
        );
    }

    @Test
    @DisplayName("PA-030: X0=179, LEFT mantenida, act() x3 desplaza a 173")
    void actIzquierdaMantenidaTresVeces() {
        Player player = new Player();
        player.setX(179);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_LEFT));
        player.act();
        player.act();
        player.act();
        assertAll("PA-030",
                () -> assertEquals(173, player.getX(), "PA-030 getX esperado 173 tras 3 act()")
        );
    }

    @Test
    @DisplayName("PA-031: X0=179, RIGHT mantenida, act() x3 desplaza a 185")
    void actDerechaMantenidaTresVeces() {
        Player player = new Player();
        player.setX(179);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_RIGHT));
        player.act();
        player.act();
        player.act();
        assertAll("PA-031",
                () -> assertEquals(185, player.getX(), "PA-031 getX esperado 185 tras 3 act()")
        );
    }

    @Test
    @DisplayName("PKP-001: keyPressed flecha izquierda desplaza a 177")
    void keyPressedIzquierda() {
        Player player = new Player();
        player.setX(179);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_LEFT));
        player.act();
        assertAll("PKP-001",
                () -> assertEquals(177, player.getX(), "PKP-001 getX esperado 177")
        );
    }

    @Test
    @DisplayName("PKP-002: keyPressed flecha derecha desplaza a 181")
    void keyPressedDerecha() {
        Player player = new Player();
        player.setX(179);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_RIGHT));
        player.act();
        assertAll("PKP-002",
                () -> assertEquals(181, player.getX(), "PKP-002 getX esperado 181")
        );
    }

    @Test
    @DisplayName("PKP-003: keyPressed ESPACIO no altera posición (179)")
    void keyPressedEspacio() {
        Player player = new Player();
        player.setX(179);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_SPACE));
        player.act();
        assertAll("PKP-003",
                () -> assertEquals(179, player.getX(), "PKP-003 getX esperado 179")
        );
    }

    @Test
    @DisplayName("PKP-004: keyPressed ARRIBA no altera posición (179)")
    void keyPressedArriba() {
        Player player = new Player();
        player.setX(179);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_UP));
        player.act();
        assertAll("PKP-004",
                () -> assertEquals(179, player.getX(), "PKP-004 getX esperado 179")
        );
    }

    @Test
    @DisplayName("PKP-005: keyPressed tecla A no altera posición (179)")
    void keyPressedTeclaA() {
        Player player = new Player();
        player.setX(179);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_A));
        player.act();
        assertAll("PKP-005",
                () -> assertEquals(179, player.getX(), "PKP-005 getX esperado 179")
        );
    }

    @Test
    @DisplayName("PKP-006: keyPressed LEFT y luego SPACE mantiene movimiento a la izquierda (177)")
    void keyPressedIzquierdaYLuegoEspacio() {
        Player player = new Player();
        player.setX(179);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_LEFT));
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_SPACE));
        player.act();
        assertAll("PKP-006",
                () -> assertEquals(177, player.getX(), "PKP-006 getX esperado 177")
        );
    }

    @Test
    @DisplayName("PKP-007: keyPressed LEFT dos veces (autorepeat) mantiene movimiento simple a 177")
    void keyPressedIzquierdaAutorepeat() {
        Player player = new Player();
        player.setX(179);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_LEFT));
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_LEFT));
        player.act();
        assertAll("PKP-007",
                () -> assertEquals(177, player.getX(), "PKP-007 getX esperado 177")
        );
    }

    @Test
    @DisplayName("PKR-001: keyReleased LEFT cancela movimiento (179)")
    void keyReleasedIzquierda() {
        Player player = new Player();
        player.setX(179);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_LEFT));
        player.keyReleased(SoporteCajaNegra.tecla(KeyEvent.KEY_RELEASED, KeyEvent.VK_LEFT));
        player.act();
        assertAll("PKR-001",
                () -> assertEquals(179, player.getX(), "PKR-001 getX esperado 179")
        );
    }

    @Test
    @DisplayName("PKR-002: keyReleased RIGHT cancela movimiento (179)")
    void keyReleasedDerecha() {
        Player player = new Player();
        player.setX(179);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_RIGHT));
        player.keyReleased(SoporteCajaNegra.tecla(KeyEvent.KEY_RELEASED, KeyEvent.VK_RIGHT));
        player.act();
        assertAll("PKR-002",
                () -> assertEquals(179, player.getX(), "PKR-002 getX esperado 179")
        );
    }

    @Test
    @DisplayName("PKR-003: keyReleased SPACE no cancela movimiento izquierda activo (177)")
    void keyReleasedEspacioNoCancelaIzquierda() {
        Player player = new Player();
        player.setX(179);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_LEFT));
        player.keyReleased(SoporteCajaNegra.tecla(KeyEvent.KEY_RELEASED, KeyEvent.VK_SPACE));
        player.act();
        assertAll("PKR-003",
                () -> assertEquals(177, player.getX(), "PKR-003 getX esperado 177")
        );
    }

    @Test
    @DisplayName("PKR-004: solo keyReleased LEFT sin previo press mantiene reposo (179)")
    void keyReleasedSoloIzquierda() {
        Player player = new Player();
        player.setX(179);
        player.keyReleased(SoporteCajaNegra.tecla(KeyEvent.KEY_RELEASED, KeyEvent.VK_LEFT));
        player.act();
        assertAll("PKR-004",
                () -> assertEquals(179, player.getX(), "PKR-004 getX esperado 179")
        );
    }

    @Test
    @DisplayName("PKR-005: [LEFT p, act(177), LEFT r, act] detiene avance en 177 final")
    void keyReleasedIzquierdaTrasAct() {
        Player player = new Player();
        player.setX(179);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_LEFT));
        player.act();
        assertEquals(177, player.getX(), "PKR-005 X intermedio esperado 177");
        player.keyReleased(SoporteCajaNegra.tecla(KeyEvent.KEY_RELEASED, KeyEvent.VK_LEFT));
        player.act();
        assertAll("PKR-005",
                () -> assertEquals(177, player.getX(), "PKR-005 getX final esperado 177")
        );
    }

    @Test
    @DisplayName("PKR-006: [RIGHT p, act(181), RIGHT r, act] detiene avance en 181 final")
    void keyReleasedDerechaTrasAct() {
        Player player = new Player();
        player.setX(179);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_RIGHT));
        player.act();
        assertEquals(181, player.getX(), "PKR-006 X intermedio esperado 181");
        player.keyReleased(SoporteCajaNegra.tecla(KeyEvent.KEY_RELEASED, KeyEvent.VK_RIGHT));
        player.act();
        assertAll("PKR-006",
                () -> assertEquals(181, player.getX(), "PKR-006 getX final esperado 181")
        );
    }
}
