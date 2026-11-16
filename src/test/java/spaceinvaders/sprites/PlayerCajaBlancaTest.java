
package spaceinvaders.sprites;

import main.Commons;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import space_invaders.sprites.Player;
import spaceinvaders.soporte.SoporteCajaNegra;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Caja blanca - Player")
class PlayerCajaBlancaTest {

    private static final int MAX_X =
        Commons.BOARD_WIDTH - Commons.PLAYER_WIDTH;

    // ==========================================
    // 1. Player.initPlayer()
    // ==========================================

    @Test
    @DisplayName("PI-01: inicialización con imagen")
    void initPlayerConImagen() {
        Player player = new Player();

        assertAll("PI-01",
            () -> assertEquals(179, player.getX()),
            () -> assertEquals(280, player.getY()),
            () -> assertNotNull(player.getImage())
        );
    }

    @Test
    @DisplayName("PI-02: imagen inexistente")
    void initPlayerSinImagen()
            throws ReflectiveOperationException {

        // Se carga una copia de Player sin acceso
        // al recurso gráfico del jugador.
        ClassLoader sinImagen =
            new ClassLoader(Player.class.getClassLoader()) {

            @Override
            public URL getResource(String name) {
                if ("images/player.png".equals(name)) {
                    return null;
                }
                return super.getResource(name);
            }

            @Override
            protected Class<?> loadClass(
                    String name, boolean resolve)
                    throws ClassNotFoundException {

                if (!"space_invaders.sprites.Player"
                        .equals(name)) {
                    return super.loadClass(name, resolve);
                }

                synchronized (getClassLoadingLock(name)) {
                    Class<?> clazz = findLoadedClass(name);

                    if (clazz == null) {
                        try (InputStream stream =
                            Player.class.getResourceAsStream(
                                "/space_invaders/sprites/Player.class")) {

                            if (stream == null) {
                                throw new ClassNotFoundException(name);
                            }

                            byte[] bytes = stream.readAllBytes();
                            clazz = defineClass(
                                name, bytes, 0, bytes.length);

                        } catch (IOException ex) {
                            throw new ClassNotFoundException(
                                name, ex);
                        }
                    }

                    if (resolve) {
                        resolveClass(clazz);
                    }
                    return clazz;
                }
            }
        };

        Class<?> copiaPlayer = Class.forName(
            "space_invaders.sprites.Player",
            true,
            sinImagen
        );

        InvocationTargetException error = assertThrows(
            InvocationTargetException.class,
            () -> copiaPlayer.getConstructor().newInstance()
        );

        assertInstanceOf(
            IllegalStateException.class,
            error.getCause()
        );
    }

    // ==========================================
    // 2. Player.act()
    // ==========================================

    @ParameterizedTest(
        name = "{0} - act X={1}, estado={2}"
    )
    @CsvSource({
        // ID, X inicial, estado, X esperada
        "PA-01,   179, NONE,   179",
        "PA-02,   179, LEFT,   177",
        "PA-03,   179, RIGHT,  181",
        "PA-04,     0, LEFT,     0",
        "PA-05,   343, RIGHT,  343",
        "PA-06, 10750, NONE,   343"
    })
    void comprobarAct(
            String id,
            int xInicial,
            String estado,
            int xEsperada) {

        Player player = new Player();
        player.setX(xInicial);

        if ("LEFT".equals(estado)) {
            player.keyPressed(
                evento(KeyEvent.KEY_PRESSED, KeyEvent.VK_LEFT)
            );
        } else if ("RIGHT".equals(estado)) {
            player.keyPressed(
                evento(KeyEvent.KEY_PRESSED, KeyEvent.VK_RIGHT)
            );
        }

        int yInicial = player.getY();

        player.act();

        assertAll(id,
            () -> assertEquals(
                xEsperada, player.getX(),
                "Posición horizontal incorrecta"
            ),
            () -> assertEquals(
                yInicial, player.getY(),
                "La coordenada Y no debe cambiar"
            ),
            () -> assertTrue(
                player.getX() >= 0 &&
                player.getX() <= MAX_X,
                "La nave debe permanecer dentro del tablero"
            )
        );
    }

    // ==========================================
    // 3. Player.keyPressed()
    // ==========================================

    @ParameterizedTest(name = "{0} - keyPressed {1}")
    @CsvSource({
        "KP-01, LEFT,  177",
        "KP-02, RIGHT, 181",
        "KP-03, SPACE, 179"
    })
    void comprobarKeyPressed(
            String id,
            String nombreTecla,
            int xEsperada) {

        Player player = new Player();
        player.setX(179);

        player.keyPressed(
            evento(
                KeyEvent.KEY_PRESSED,
                codigo(nombreTecla)
            )
        );

        player.act();

        assertEquals(
            xEsperada,
            player.getX(),
            id + " - Posición tras pulsación"
        );
    }

    // ==========================================
    // 4. Player.keyReleased()
    // ==========================================

    @ParameterizedTest(name = "{0} - keyReleased {1}")
    @CsvSource({
        "KR-01, LEFT,  179",
        "KR-02, RIGHT, 179",
        "KR-03, SPACE, 177"
    })
    void comprobarKeyReleased(
            String id,
            String nombreTecla,
            int xEsperada) {

        Player player = new Player();
        player.setX(179);

        // Creamos primero un movimiento activo.
        int teclaInicial =
            "RIGHT".equals(nombreTecla)
                ? KeyEvent.VK_RIGHT
                : KeyEvent.VK_LEFT;

        player.keyPressed(
            evento(KeyEvent.KEY_PRESSED, teclaInicial)
        );

        player.keyReleased(
            evento(
                KeyEvent.KEY_RELEASED,
                codigo(nombreTecla)
            )
        );

        player.act();

        assertEquals(
            xEsperada,
            player.getX(),
            id + " - Posición tras liberación"
        );
    }

    // ==========================================
    // Métodos auxiliares para los eventos
    // ==========================================

    private static KeyEvent evento(
            int tipo, int codigoTecla) {

        return SoporteCajaNegra.tecla(
            tipo, codigoTecla);
    }

    private static int codigo(String nombreTecla) {
        switch (nombreTecla) {
            case "LEFT":
                return KeyEvent.VK_LEFT;
            case "RIGHT":
                return KeyEvent.VK_RIGHT;
            default:
                return KeyEvent.VK_SPACE;
        }
    }
}
