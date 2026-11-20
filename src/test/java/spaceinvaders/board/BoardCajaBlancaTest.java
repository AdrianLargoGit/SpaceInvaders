package spaceinvaders.board;

import main.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import space_invaders.sprites.Alien;
import space_invaders.sprites.Shot;

import javax.swing.SwingUtilities;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardCajaBlancaTest {
    private Board board;

    @BeforeEach
    void prepararTablero() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            board = new Board();
            board.getTimer().stop();
        });
        board.setAliens(new ArrayList<>());
        board.setDeaths(0);
        board.setInGame(true);
        board.setMessage("partida");
        board.setDirection(1);
        board.getPlayer().setX(100);
        board.getPlayer().setY(100);
        board.getShot().die();
    }

    private static Alien crearAlien(int[] fila) {
        Alien alien = new Alien(100, 100);
        alien.setX(fila[0]);
        alien.setY(fila[1]);
        if (fila[2] == 0) {
            alien.die();
        }
        return alien;
    }

    private void comprobarUpdate(int bajas, boolean victoria) {
        board.setDeaths(bajas);
        board.getPlayer().keyPressed(new KeyEvent(board, KeyEvent.KEY_PRESSED, 0,
                0, KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED));
        board.update();
        assertEquals(List.of(!victoria, victoria ? "Game won!" : "partida", 102),
                List.of(board.isInGame(), board.getMessage(), board.getPlayer().getX()));
    }

    private void comprobarDisparo(int[] origen, boolean visible, int[][] aliens, int indiceImpacto) {
        Shot disparo = new Shot();
        disparo.setX(origen[0]);
        disparo.setY(origen[1]);
        if (!visible) {
            disparo.die();
        }
        board.setShot(disparo);
        for (int[] alien : aliens) {
            board.getAliens().add(crearAlien(alien));
        }
        board.update_shots();
        int siguienteY = origen[1] - 4;
        boolean sigueVisible = visible && indiceImpacto < 0 && siguienteY > 0;
        List<Object> esperado = new ArrayList<>(List.of(indiceImpacto >= 0 ? 1 : 0, sigueVisible));
        List<Object> obtenido = new ArrayList<>(List.of(board.getDeaths(), disparo.isVisible()));
        if (sigueVisible || !visible) {
            esperado.add(visible ? siguienteY : origen[1]);
            obtenido.add(disparo.getY());
        }
        for (int i = 0; i < aliens.length; i++) {
            esperado.add(i == indiceImpacto);
            obtenido.add(board.getAliens().get(i).isDying());
        }
        assertEquals(esperado, obtenido);
    }

    private void comprobarAliens(int[][] aliens, int direccion, int[][] posiciones,
                                 int direccionFinal, boolean invasion) {
        for (int[] alien : aliens) {
            board.getAliens().add(crearAlien(alien));
        }
        board.setDirection(direccion);
        board.update_aliens();
        List<Object> esperado = new ArrayList<>(List.of(direccionFinal, !invasion,
                invasion ? "Invasion!" : "partida"));
        List<Object> obtenido = new ArrayList<>(List.of(board.getDirection(), board.isInGame(),
                board.getMessage()));
        for (int i = 0; i < aliens.length; i++) {
            esperado.add(posiciones[i][0]);
            esperado.add(posiciones[i][1]);
            Alien alien = board.getAliens().get(i);
            obtenido.add(alien.getX());
            obtenido.add(alien.getY());
        }
        assertEquals(esperado, obtenido);
    }

    private static class AzarDelCaso extends Random {
        private final int[] valores;
        private int siguiente;

        AzarDelCaso(int[] valores) {
            this.valores = valores;
        }

        @Override
        public int nextInt(int limite) {
            return valores[siguiente++];
        }
    }

    private void comprobarBombas(int[] jugador, boolean jugadorVisible, int[][] entradas,
                                 int[][] posiciones, boolean impacto) {
        board.getPlayer().setX(jugador[0]);
        board.getPlayer().setY(jugador[1]);
        if (!jugadorVisible) {
            board.getPlayer().die();
        }
        int[] azar = new int[entradas.length];
        for (int i = 0; i < entradas.length; i++) {
            int[] fila = entradas[i];
            Alien alien = crearAlien(fila);
            Alien.Bomb bomba = alien.getBomb();
            bomba.setX(fila[4]);
            bomba.setY(fila[5]);
            bomba.setDestroyed(fila[6] == 1);
            board.getAliens().add(alien);
            azar[i] = fila[3];
        }
        board.setGenerator(new AzarDelCaso(azar));
        board.update_bomb();
        List<Object> esperado = new ArrayList<>(List.of(impacto, false));
        List<Object> obtenido = new ArrayList<>(List.of(board.getPlayer().isDying(),
                board.getShot().isDying()));
        for (int i = 0; i < entradas.length; i++) {
            Alien.Bomb bomba = board.getAliens().get(i).getBomb();
            for (int valor : posiciones[i]) {
                esperado.add(valor);
            }
            obtenido.add(bomba.getX());
            obtenido.add(bomba.getY());
            obtenido.add(bomba.isDestroyed() ? 1 : 0);
        }
        assertEquals(esperado, obtenido);
    }

    private static int[][] filaDeAliens(int inicioX) {
        int[][] fila = new int[24][];
        for (int i = 0; i < fila.length; i++) {
            fila[i] = new int[]{inicioX + i, 100, 1};
        }
        return fila;
    }

    @Test
    @DisplayName("GI-01 - gameInit: Bucles anidados / único recorrido factible")
    void GI_01() {
        board.gameInit();
        List<Integer> esperado = new ArrayList<>();
        List<Integer> obtenido = new ArrayList<>();
        esperado.add(24);
        obtenido.add(board.getAliens().size());
        for (int fila = 0; fila < 4; fila++) {
            for (int columna = 0; columna < 6; columna++) {
                Alien alien = board.getAliens().get(fila * 6 + columna);
                esperado.add(150 + 18 * columna);
                esperado.add(5 + 18 * fila);
                obtenido.add(alien.getX());
                obtenido.add(alien.getY());
            }
        }
        esperado.add(1);
        esperado.add(1);
        obtenido.add(board.getPlayer() != null ? 1 : 0);
        obtenido.add(board.getShot() != null ? 1 : 0);
        assertEquals(esperado, obtenido);
    }

    @DisplayName("update: caminos de victoria")
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "UP-01, 0, false",
            "UP-02, 24, true",
            "UP-03, 25, false"
    })
    void actualizacion(String caso, int bajas, boolean victoria) {
        comprobarUpdate(bajas, victoria);
    }

    @Test
    @DisplayName("SH-01 - update_shots: Caminos / decisiones")
    void SH_01() {
        comprobarDisparo(new int[]{105, 105}, false, new int[][]{{100, 100, 1}}, -1);
    }

    @Test
    @DisplayName("SH-02 - update_shots: Caminos / decisiones")
    void SH_02() {
        comprobarDisparo(new int[]{105, 105}, true, new int[][]{}, -1);
    }

    @Test
    @DisplayName("SH-03 - update_shots: Caminos / decisiones")
    void SH_03() {
        comprobarDisparo(new int[]{105, 3}, true, new int[][]{}, -1);
    }

    @Test
    @DisplayName("SH-04 - update_shots: Caminos / decisiones")
    void SH_04() {
        comprobarDisparo(new int[]{105, 105}, true, new int[][]{{100, 100, 0}}, -1);
    }

    @Test
    @DisplayName("SH-05 - update_shots: Caminos / decisiones")
    void SH_05() {
        comprobarDisparo(new int[]{105, 105}, true, new int[][]{{100, 100, 1}, {100, 100, 1}}, 0);
    }

    @Test
    @DisplayName("SH-06 - update_shots: Caminos / decisiones")
    void SH_06() {
        comprobarDisparo(new int[]{105, 105}, true, new int[][]{{100, 100, 1}, {100, 100, 0}}, 0);
    }

    @Test
    @DisplayName("SH-07 - update_shots: Caminos / decisiones")
    void SH_07() {
        comprobarDisparo(new int[]{105, 3}, true, new int[][]{{100, 0, 1}}, 0);
    }

    @DisplayName("update_shots: condiciones de colisión y bordes")
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "SC-01, 99, 99, -1",
            "SC-02, 99, 105, -1",
            "SC-03, 99, 113, -1",
            "SC-04, 105, 99, -1",
            "SC-05, 105, 105, 0",
            "SC-06, 105, 113, -1",
            "SC-07, 113, 99, -1",
            "SC-08, 113, 105, -1",
            "SC-09, 113, 113, -1",
            "SE-01, 100, 100, 0",
            "SE-02, 112, 112, 0"
    })
    void colisionDisparoAlien(String caso, int x, int y, int indiceImpacto) {
        comprobarDisparo(new int[]{x, y}, true, new int[][]{{100, 100, 1}}, indiceImpacto);
    }

    @Test
    @DisplayName("SE-03 - update_shots: Igualdades de los predicados (complemento)")
    void SE_03() {
        comprobarDisparo(new int[]{105, 4}, true, new int[][]{}, -1);
    }

    @Test
    @DisplayName("SH-08 - update_shots: Bucle: formación de 24 y salida")
    void SH_08() {
        comprobarDisparo(new int[]{300, 200}, true, filaDeAliens(100), -1);
    }

    @Test
    @DisplayName("AL-01 - update_aliens: Caminos / decisiones")
    void AL_01() {
        comprobarAliens(new int[][]{}, 1, new int[][]{}, 1, false);
    }

    @DisplayName("update_aliens: decisiones de giro, visibilidad e invasión")
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "AL-02, 100, 100, 1, 1, 101, 100, 1, false",
            "AL-03, 100, 100, 0, 1, 100, 100, 1, false",
            "AL-04, 5, 100, 1, -1, 6, 115, 1, false",
            "AL-05, 100, 100, 1, -1, 99, 100, -1, false",
            "AL-06, 328, 100, 1, 1, 327, 115, -1, false",
            "AL-07, 328, 100, 1, -1, 327, 100, -1, false",
            "AL-08, 5, 3481, 1, 1, 6, 3481, 1, true",
            "AL-09, 5, 3480, 1, 1, 6, 3480, 1, true",
            "AL-10, 5, 303, 1, 1, 6, 303, 1, true",
            "AL-11, 5, 100, 1, 1, 6, 100, 1, false"
    })
    void actualizacionDeUnAlien(String caso, int x, int y, int visible, int direccion,
                                int xFinal, int yFinal, int direccionFinal, boolean invasion) {
        comprobarAliens(new int[][]{{x, y, visible}}, direccion,
                new int[][]{{xFinal, yFinal, visible}}, direccionFinal, invasion);
    }

    @Test
    @DisplayName("AL-12 - update_aliens: Bucles anidados; descenso de toda la lista")
    void AL_12() {
        comprobarAliens(new int[][]{{328, 100, 1}, {200, 100, 0}}, 1, new int[][]{{327, 115, 1}, {200, 115, 0}}, -1, false);
    }

    @Test
    @DisplayName("AL-13 - update_aliens: Repetición de giros; 2 iteraciones")
    void AL_13() {
        comprobarAliens(new int[][]{{328, 100, 1}, {328, 100, 1}}, 1, new int[][]{{327, 115, 1}, {327, 115, 1}}, -1, false);
    }

    @Test
    @DisplayName("AL-14 - update_aliens: Bucles concatenados / dependencia de dirección")
    void AL_14() {
        comprobarAliens(new int[][]{{5, 100, 1}, {100, 100, 1}}, -1, new int[][]{{6, 115, 1}, {101, 115, 1}}, 1, false);
    }

    @Test
    @DisplayName("AL-15 - update_aliens: Bucles: formación de 24 y salida")
    void AL_15() {
        comprobarAliens(filaDeAliens(100), 1, filaDeAliens(101), 1, false);
    }

    @Test
    @DisplayName("BO-01 - update_bomb: Caminos / decisiones")
    void BO_01() {
        comprobarBombas(new int[]{100, 100}, true, new int[][]{}, new int[][]{}, false);
    }

    @Test
    @DisplayName("BO-02 - update_bomb: Caminos / decisiones")
    void BO_02() {
        comprobarBombas(new int[]{100, 100}, true, new int[][]{{20, 20, 1, 0, 20, 20, 1}}, new int[][]{{20, 20, 1}}, false);
    }

    @Test
    @DisplayName("BO-03 - update_bomb: Caminos / decisiones")
    void BO_03() {
        comprobarBombas(new int[]{100, 100}, true, new int[][]{{20, 20, 1, 0, 20, 285, 0}}, new int[][]{{20, 286, 1}}, false);
    }

    @Test
    @DisplayName("BO-04 - update_bomb: Caminos / decisiones")
    void BO_04() {
        comprobarBombas(new int[]{100, 100}, false, new int[][]{{20, 20, 1, 0, 20, 20, 0}}, new int[][]{{20, 21, 0}}, false);
    }

    @Test
    @DisplayName("BO-05 - update_bomb: Caminos / decisiones")
    void BO_05() {
        comprobarBombas(new int[]{100, 100}, false, new int[][]{{20, 20, 1, 0, 20, 20, 1}}, new int[][]{{20, 20, 1}}, false);
    }

    @Test
    @DisplayName("BO-06 - update_bomb: Igualdad tras movimiento esperado: llegada al suelo")
    void BO_06() {
        comprobarBombas(new int[]{100, 100}, true, new int[][]{{20, 20, 1, 0, 20, 284, 0}}, new int[][]{{20, 285, 1}}, false);
    }

    @Test
    @DisplayName("BO-07 - update_bomb: Dependencias: lanzamiento e impacto inmediato")
    void BO_07() {
        comprobarBombas(new int[]{100, 100}, true, new int[][]{{105, 105, 1, 5, 20, 20, 1}}, new int[][]{{105, 105, 1}}, true);
    }

    @Test
    @DisplayName("BO-08 - update_bomb: Bucle: dos bombas sobre el mismo jugador")
    void BO_08() {
        comprobarBombas(new int[]{100, 100}, true, new int[][]{{20, 20, 1, 0, 105, 105, 0}, {30, 30, 1, 0, 105, 105, 0}}, new int[][]{{105, 105, 1}, {105, 105, 1}}, true);
    }

    @DisplayName("update_bomb: reglas de lanzamiento y visibilidad")
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "BS-01, true, 1, 5, 1, 20, 21, 0",
            "BS-02, true, 1, 5, 0, 50, 51, 0",
            "BS-03, true, 0, 5, 1, 50, 50, 1",
            "BS-04, true, 0, 5, 0, 50, 51, 0",
            "BS-05, true, 1, 0, 1, 50, 50, 1",
            "BS-06, true, 1, 0, 0, 50, 51, 0",
            "BS-07, true, 0, 0, 1, 50, 50, 1",
            "BS-08, true, 0, 0, 0, 50, 51, 0",
            "BG-01, false, 1, 0, 0, 50, 51, 0",
            "BG-02, false, 1, 0, 1, 50, 50, 1"
    })
    void generacionYVisibilidadBomba(String caso, boolean jugadorVisible, int alienVisible, int azar,
                                     int destruida, int xFinal, int yFinal, int destruidaFinal) {
        comprobarBombas(new int[]{100, 100}, jugadorVisible,
                new int[][]{{20, 20, alienVisible, azar, 50, 50, destruida}},
                new int[][]{{xFinal, yFinal, destruidaFinal}}, false);
    }

    @DisplayName("update_bomb: condiciones de impacto y bordes")
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "BC-01, 99, 99, 100, false",
            "BC-02, 99, 105, 106, false",
            "BC-03, 99, 111, 112, false",
            "BC-04, 105, 99, 100, false",
            "BC-05, 105, 105, 105, true",
            "BC-06, 105, 111, 112, false",
            "BC-07, 116, 99, 100, false",
            "BC-08, 116, 105, 106, false",
            "BC-09, 116, 111, 112, false",
            "BE-01, 100, 100, 100, true",
            "BE-02, 115, 110, 110, true"
    })
    void colisionBombaJugador(String caso, int x, int y, int yFinal, boolean impacto) {
        comprobarBombas(new int[]{100, 100}, true, new int[][]{{20, 20, 1, 0, x, y, 0}},
                new int[][]{{x, yFinal, impacto ? 1 : 0}}, impacto);
    }

    @Test
    @DisplayName("BO-09 - update_bomb: Bucle: 24 bombas y salida")
    void BO_09() {
        int[][] entradas = new int[24][];
        int[][] posiciones = new int[24][];
        for (int i = 0; i < entradas.length; i++) {
            int x = 20 + i;
            entradas[i] = new int[]{x, 20, 1, 0, x, 20, 0};
            posiciones[i] = new int[]{x, 21, 0};
        }
        comprobarBombas(new int[]{100, 100}, true, entradas, posiciones, false);
    }
}