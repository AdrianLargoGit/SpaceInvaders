package spaceinvaders.board;

import main.Board;
import main.Commons;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import space_invaders.sprites.Alien;
import space_invaders.sprites.Player;
import space_invaders.sprites.Shot;
import spaceinvaders.soporte.SoporteCajaNegra;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Board: pruebas de caja negra")
class BoardCajaNegraTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = SoporteCajaNegra.nuevoBoardControlado();
    }

    @AfterEach
    void tearDown() {
        if (board != null && board.getTimer() != null) {
            board.getTimer().stop();
        }
    }

    // =========================================================================
    // 4.3 BG - gameInit
    // =========================================================================

    @Test
    @DisplayName("BG-001: gameInit inicializa exactamente 24 aliens")
    void gameInitCantidadAliens() {
        assertAll("BG-001",
                () -> assertEquals(Commons.NUMBER_OF_ALIENS_TO_DESTROY, board.getAliens().size(),
                        "BG-001 se esperaban 24 aliens")
        );
    }

    @Test
    @DisplayName("BG-002: gameInit ubica los aliens en la matriz esperada sin depender del orden")
    void gameInitPosicionesAliens() {
        Set<Point> esperado = new HashSet<>();
        for (int fila = 0; fila < Commons.ALIEN_ROWS; fila++) {
            for (int col = 0; col < Commons.ALIEN_COLUMNS; col++) {
                esperado.add(new Point(
                        Commons.ALIEN_INIT_X + Commons.ALIEN_SEPARATOR * col,
                        Commons.ALIEN_INIT_Y + Commons.ALIEN_SEPARATOR * fila
                ));
            }
        }

        Set<Point> obtenido = new HashSet<>();
        for (Alien alien : board.getAliens()) {
            obtenido.add(new Point(alien.getX(), alien.getY()));
        }

        assertAll("BG-002",
                () -> assertEquals(24, obtenido.size(), "BG-002 deben existir 24 posiciones distintas"),
                () -> assertEquals(esperado, obtenido, "BG-002 el conjunto de posiciones de aliens debe coincidir con la matriz")
        );
    }

    @Test
    @DisplayName("BG-003: gameInit inicializa jugador en (179, 280)")
    void gameInitJugador() {
        Player player = board.getPlayer();
        assertAll("BG-003",
                () -> assertNotNull(player, "BG-003 getPlayer no debe ser null"),
                () -> assertEquals(Commons.BOARD_WIDTH / 2, player.getX(), "BG-003 player X esperado 179"),
                () -> assertEquals(Commons.GROUND - 10, player.getY(), "BG-003 player Y esperado 280")
        );
    }

    @Test
    @DisplayName("BG-004: gameInit inicializa objeto Shot no nulo")
    void gameInitDisparo() {
        assertAll("BG-004",
                () -> assertNotNull(board.getShot(), "BG-004 getShot no debe ser null")
        );
    }

    @Test
    @DisplayName("BG-005: gameInit inicializa todos los aliens visibles y sin estado dying")
    void gameInitEstadoAliens() {
        assertAll("BG-005",
                () -> assertTrue(board.getAliens().stream().allMatch(a -> a.isVisible() && !a.isDying()),
                        "BG-005 todos los aliens deben ser visibles y no dying")
        );
    }

    @Test
    @DisplayName("BG-006: llamar gameInit dos veces no acumula aliens y conserva las mismas posiciones")
    void gameInitIdempotenciaAliens() {
        SoporteCajaNegra.invocar(board, "gameInit");

        Set<Point> esperado = new HashSet<>();
        for (int fila = 0; fila < Commons.ALIEN_ROWS; fila++) {
            for (int col = 0; col < Commons.ALIEN_COLUMNS; col++) {
                esperado.add(new Point(
                        Commons.ALIEN_INIT_X + Commons.ALIEN_SEPARATOR * col,
                        Commons.ALIEN_INIT_Y + Commons.ALIEN_SEPARATOR * fila
                ));
            }
        }

        Set<Point> obtenido = new HashSet<>();
        for (Alien alien : board.getAliens()) {
            obtenido.add(new Point(alien.getX(), alien.getY()));
        }

        assertAll("BG-006",
                () -> assertEquals(24, board.getAliens().size(), "BG-006 deben seguir habiendo 24 aliens"),
                () -> assertEquals(esperado, obtenido, "BG-006 las posiciones deben mantenerse identicas")
        );
    }

    // =========================================================================
    // 4.3 BU - update
    // =========================================================================

    @Test
    @DisplayName("BU-001: update con deaths=0 mantiene la partida en progreso")
    void updateDeaths0PartidaEnProgreso() {
        board.setDeaths(0);
        SoporteCajaNegra.invocar(board, "update");
        assertAll("BU-001",
                () -> assertTrue(board.isInGame(), "BU-001 inGame debe ser true"),
                () -> assertNotEquals("Game won!", board.getMessage(), "BU-001 mensaje no debe ser 'Game won!'")
        );
    }

    @Test
    @DisplayName("BU-002: update con deaths=23 mantiene la partida en progreso")
    void updateDeaths23PartidaEnProgreso() {
        board.setDeaths(23);
        SoporteCajaNegra.invocar(board, "update");
        assertAll("BU-002",
                () -> assertTrue(board.isInGame(), "BU-002 inGame debe ser true"),
                () -> assertNotEquals("Game won!", board.getMessage(), "BU-002 mensaje no debe ser 'Game won!'")
        );
    }

    @Test
    @DisplayName("BU-003: update con deaths=24 finaliza la partida con victoria")
    void updateDeaths24Victoria() {
        board.setDeaths(24);
        SoporteCajaNegra.invocar(board, "update");
        assertAll("BU-003",
                () -> assertFalse(board.isInGame(), "BU-003 inGame debe ser false"),
                () -> assertEquals("Game won!", board.getMessage(), "BU-003 mensaje debe ser 'Game won!'")
        );
    }

    @Test
    @DisplayName("BU-004: update invoca Player.act() desplazando jugador con LEFT")
    void updateInvocaPlayerAct() {
        Player player = new Player();
        player.setX(179);
        player.keyPressed(SoporteCajaNegra.tecla(KeyEvent.KEY_PRESSED, KeyEvent.VK_LEFT));
        board.setPlayer(player);

        SoporteCajaNegra.invocar(board, "update");

        assertAll("BU-004",
                () -> assertEquals(177, board.getPlayer().getX(), "BU-004 jugador X esperado 177 tras update")
        );
    }

    @Test
    @DisplayName("BU-005: update invoca update_shots() moviendo disparo verticalmente")
    void updateInvocaUpdateShots() {
        Shot shot = new Shot();
        shot.setX(20);
        shot.setY(200);
        board.setShot(shot);

        SoporteCajaNegra.invocar(board, "update");

        assertAll("BU-005",
                () -> assertEquals(196, board.getShot().getY(), "BU-005 disparo Y esperado 196 (200 - SHOT_SPEED)")
        );
    }

    @Test
    @DisplayName("BU-006: update invoca update_aliens() desplazando formación horizontalmente")
    void updateInvocaUpdateAliens() {
        board.setDirection(1);
        List<Point> prev = new ArrayList<>();
        for (Alien alien : board.getAliens()) {
            prev.add(new Point(alien.getX(), alien.getY()));
        }

        SoporteCajaNegra.invocar(board, "update");

        List<Alien> actuales = board.getAliens();
        assertAll("BU-006",
                () -> {
                    for (int i = 0; i < actuales.size(); i++) {
                        Point pPrev = prev.get(i);
                        Alien aAct = actuales.get(i);
                        assertEquals(pPrev.x + 1, aAct.getX(), "BU-006 alien " + i + " X desplazado +1");
                        assertEquals(pPrev.y, aAct.getY(), "BU-006 alien " + i + " Y sin cambio");
                    }
                }
        );
    }

    @Test
    @DisplayName("BU-007: update invoca update_bomb() descendiendo bomba activa")
    void updateInvocaUpdateBomb() {
        Alien alien = SoporteCajaNegra.alienEn(20, 20);
        alien.getBomb().setDestroyed(false);
        alien.getBomb().setX(50);
        alien.getBomb().setY(100);
        board.setAliens(new ArrayList<>(List.of(alien)));

        SoporteCajaNegra.invocar(board, "update");

        assertAll("BU-007",
                () -> assertEquals(50, alien.getBomb().getX(), "BU-007 bomba X conservada en 50"),
                () -> assertEquals(101, alien.getBomb().getY(), "BU-007 bomba Y descendida a 101")
        );
    }

    // =========================================================================
    // 4.3 BS - update_shots
    // =========================================================================

    @Test
    @DisplayName("BS-001: disparo invisible no interactúa ni se mueve")
    void updateShotsDisparoInvisible() {
        Alien alienA = SoporteCajaNegra.alienEn(100, 100);
        board.setAliens(new ArrayList<>(List.of(alienA)));
        board.setDeaths(0);

        Shot shot = new Shot();
        shot.setX(106);
        shot.setY(108);
        shot.die();
        board.setShot(shot);

        SoporteCajaNegra.invocar(board, "update_shots");

        assertAll("BS-001",
                () -> assertEquals(108, board.getShot().getY(), "BS-001 shot Y debe seguir 108"),
                () -> assertEquals(0, board.getDeaths(), "BS-001 deaths debe seguir 0"),
                () -> assertFalse(alienA.isDying(), "BS-001 alien A no debe estar dying")
        );
    }

    @Test
    @DisplayName("BS-002: disparo visible en vacío avanza hacia arriba")
    void updateShotsDisparoVacio() {
        Alien alienA = SoporteCajaNegra.alienEn(100, 100);
        board.setAliens(new ArrayList<>(List.of(alienA)));
        board.setDeaths(0);

        Shot shot = new Shot();
        shot.setX(20);
        shot.setY(200);
        board.setShot(shot);

        SoporteCajaNegra.invocar(board, "update_shots");

        assertAll("BS-002",
                () -> assertEquals(20, board.getShot().getX(), "BS-002 shot X conservado 20"),
                () -> assertEquals(196, board.getShot().getY(), "BS-002 shot Y avanzado a 196"),
                () -> assertTrue(board.getShot().isVisible(), "BS-002 shot sigue visible"),
                () -> assertEquals(0, board.getDeaths(), "BS-002 deaths sigue 0"),
                () -> assertFalse(alienA.isDying(), "BS-002 alien A no debe estar dying")
        );
    }

    @Test
    @DisplayName("BS-003: impacto directo en alien destruye alien, incrementa deaths y oculta disparo")
    void updateShotsImpactoAlien() {
        Alien alienA = SoporteCajaNegra.alienEn(100, 100);
        board.setAliens(new ArrayList<>(List.of(alienA)));
        board.setDeaths(5);

        Shot shot = new Shot();
        shot.setX(106);
        shot.setY(108);
        board.setShot(shot);

        SoporteCajaNegra.invocar(board, "update_shots");

        assertAll("BS-003",
                () -> assertTrue(alienA.isDying(), "BS-003 alien A debe estar dying"),
                () -> assertEquals(6, board.getDeaths(), "BS-003 deaths debe incrementarse a 6"),
                () -> assertFalse(board.getShot().isVisible(), "BS-003 shot no debe ser visible")
        );
    }

    @Test
    @DisplayName("BS-004: dos aliens superpuestos sólo destruye exactamente a uno")
    void updateShotsDosAliensSuperpuestos() {
        Alien alienA1 = SoporteCajaNegra.alienEn(100, 100);
        Alien alienA2 = SoporteCajaNegra.alienEn(100, 100);
        board.setAliens(new ArrayList<>(List.of(alienA1, alienA2)));
        board.setDeaths(0);

        Shot shot = new Shot();
        shot.setX(106);
        shot.setY(108);
        board.setShot(shot);

        SoporteCajaNegra.invocar(board, "update_shots");

        assertAll("BS-004",
                () -> assertEquals(1, board.getDeaths(), "BS-004 deaths esperado 1"),
                () -> assertTrue(alienA1.isDying() ^ alienA2.isDying(), "BS-004 exactamente un alien debe estar dying"),
                () -> assertFalse(board.getShot().isVisible(), "BS-004 shot no debe ser visible")
        );
    }

    @Test
    @DisplayName("BS-005: alien invisible no colisiona con disparo")
    void updateShotsAlienInvisible() {
        Alien alienA = SoporteCajaNegra.alienEn(100, 100);
        alienA.die();
        board.setAliens(new ArrayList<>(List.of(alienA)));
        board.setDeaths(0);

        Shot shot = new Shot();
        shot.setX(106);
        shot.setY(108);
        board.setShot(shot);

        SoporteCajaNegra.invocar(board, "update_shots");

        assertAll("BS-005",
                () -> assertEquals(0, board.getDeaths(), "BS-005 deaths debe seguir 0"),
                () -> assertFalse(alienA.isDying(), "BS-005 alien A no debe estar dying"),
                () -> assertTrue(board.getShot().isVisible(), "BS-005 shot sigue visible"),
                () -> assertEquals(104, board.getShot().getY(), "BS-005 shot Y avanzado a 104")
        );
    }

    @DisplayName("BS-006..009: límites en coordenada X frente a Alien en 100..112")
    @ParameterizedTest
    @CsvSource({
            "BS-006,  99, 0, 104, true,  false",
            "BS-007, 100, 1, 108, false, true",
            "BS-008, 111, 1, 108, false, true",
            "BS-009, 113, 0, 104, true,  false"
    })
    void updateShotsLimitesX(String id, int shotX, int expectedDeaths, int expectedShotY,
                             boolean expectedShotVisible, boolean expectedAlienDying) {
        Alien alienA = SoporteCajaNegra.alienEn(100, 100);
        board.setAliens(new ArrayList<>(List.of(alienA)));
        board.setDeaths(0);

        Shot shot = new Shot();
        shot.setX(shotX);
        shot.setY(108);
        board.setShot(shot);

        SoporteCajaNegra.invocar(board, "update_shots");

        assertAll(id,
                () -> assertEquals(expectedDeaths, board.getDeaths(), id + " deaths"),
                () -> assertEquals(expectedShotVisible, board.getShot().isVisible(), id + " shot visible"),
                () -> assertEquals(expectedAlienDying, alienA.isDying(), id + " alien dying"),
                () -> {
                    if (expectedShotVisible) {
                        assertEquals(expectedShotY, board.getShot().getY(), id + " shot Y");
                    }
                }
        );
    }

    @Test
    @DisplayName("BS-010: disparo por debajo del alien (106, 160) no colisiona")
    void updateShotsPorDebajo() {
        Alien alienA = SoporteCajaNegra.alienEn(100, 100);
        board.setAliens(new ArrayList<>(List.of(alienA)));
        board.setDeaths(0);

        Shot shot = new Shot();
        shot.setX(106);
        shot.setY(160);
        board.setShot(shot);

        SoporteCajaNegra.invocar(board, "update_shots");

        assertAll("BS-010",
                () -> assertEquals(0, board.getDeaths(), "BS-010 deaths debe seguir 0"),
                () -> assertTrue(board.getShot().isVisible(), "BS-010 shot debe seguir visible"),
                () -> assertEquals(156, board.getShot().getY(), "BS-010 shot Y avanzado a 156"),
                () -> assertFalse(alienA.isDying(), "BS-010 alien A no debe estar dying")
        );
    }

    @Test
    @DisplayName("BS-011: disparo por encima del alien (106, 60) no colisiona")
    void updateShotsPorEncima() {
        Alien alienA = SoporteCajaNegra.alienEn(100, 100);
        board.setAliens(new ArrayList<>(List.of(alienA)));
        board.setDeaths(0);

        Shot shot = new Shot();
        shot.setX(106);
        shot.setY(60);
        board.setShot(shot);

        SoporteCajaNegra.invocar(board, "update_shots");

        assertAll("BS-011",
                () -> assertEquals(0, board.getDeaths(), "BS-011 deaths debe seguir 0"),
                () -> assertTrue(board.getShot().isVisible(), "BS-011 shot debe seguir visible"),
                () -> assertEquals(56, board.getShot().getY(), "BS-011 shot Y avanzado a 56"),
                () -> assertFalse(alienA.isDying(), "BS-011 alien A no debe estar dying")
        );
    }

    @Test
    @DisplayName("BS-012: disparo en (20, 3) sale por arriba del tablero y se vuelve invisible")
    void updateShotsSalePorArriba() {
        Shot shot = new Shot();
        shot.setX(20);
        shot.setY(3);
        board.setShot(shot);

        SoporteCajaNegra.invocar(board, "update_shots");

        assertAll("BS-012",
                () -> assertFalse(board.getShot().isVisible(), "BS-012 shot debe volverse invisible")
        );
    }

    @Test
    @DisplayName("BS-013: disparo en (20, 8) permanece dentro del tablero en Y=4")
    void updateShotsPermaneceEnTablero() {
        Shot shot = new Shot();
        shot.setX(20);
        shot.setY(8);
        board.setShot(shot);

        SoporteCajaNegra.invocar(board, "update_shots");

        assertAll("BS-013",
                () -> assertTrue(board.getShot().isVisible(), "BS-013 shot debe seguir visible"),
                () -> assertEquals(4, board.getShot().getY(), "BS-013 shot Y esperado 4")
        );
    }

    @Test
    @DisplayName("BS-014: colisión de disparo con bomba activa destruye bomba y oculta disparo")
    void updateShotsImpactoBomba() {
        Alien alienB = SoporteCajaNegra.alienEn(200, 50);
        alienB.getBomb().setDestroyed(false);
        alienB.getBomb().setX(210);
        alienB.getBomb().setY(150);
        board.setAliens(new ArrayList<>(List.of(alienB)));
        board.setDeaths(0);

        Shot shot = new Shot();
        shot.setX(210);
        shot.setY(154);
        board.setShot(shot);

        SoporteCajaNegra.invocar(board, "update_shots");

        assertAll("BS-014",
                () -> assertFalse(board.getShot().isVisible(), "BS-014 shot debe ser invisible"),
                () -> assertTrue(alienB.getBomb().isDestroyed(), "BS-014 bomba debe ser destruida"),
                () -> assertEquals(0, board.getDeaths(), "BS-014 deaths debe seguir 0")
        );
    }

    @Test
    @DisplayName("BS-015: disparo hacia alien B destruye B y no afecta alien A")
    void updateShotsSelectivoAlien() {
        Alien alienA = SoporteCajaNegra.alienEn(100, 100);
        Alien alienB = SoporteCajaNegra.alienEn(200, 100);
        board.setAliens(new ArrayList<>(List.of(alienA, alienB)));
        board.setDeaths(0);

        Shot shot = new Shot();
        shot.setX(206);
        shot.setY(108);
        board.setShot(shot);

        SoporteCajaNegra.invocar(board, "update_shots");

        assertAll("BS-015",
                () -> assertTrue(alienB.isDying(), "BS-015 alien B debe estar dying"),
                () -> assertFalse(alienA.isDying(), "BS-015 alien A no debe estar dying"),
                () -> assertEquals(1, board.getDeaths(), "BS-015 deaths esperado 1")
        );
    }

    // =========================================================================
    // 4.3 BA - update_aliens
    // =========================================================================

    @Test
    @DisplayName("BA-001: borde derecho, X=100 avanza a la derecha sin descender")
    void updateAliensBordeDerechoSinDisparoX100() {
        Alien l = SoporteCajaNegra.alienEn(100, 50);
        Alien m = SoporteCajaNegra.alienEn(150, 70);
        Alien r = SoporteCajaNegra.alienEn(200, 90);
        board.setAliens(new ArrayList<>(List.of(l, m, r)));
        board.setDirection(1);

        SoporteCajaNegra.invocar(board, "update_aliens");

        assertAll("BA-001",
                () -> assertEquals(1, board.getDirection(), "BA-001 direction debe seguir 1"),
                () -> assertEquals(50, l.getY(), "BA-001 L Y sin cambio"),
                () -> assertEquals(70, m.getY(), "BA-001 M Y sin cambio"),
                () -> assertEquals(90, r.getY(), "BA-001 R Y sin cambio"),
                () -> assertEquals(101, l.getX(), "BA-001 L X avanzado"),
                () -> assertEquals(151, m.getX(), "BA-001 M X avanzado"),
                () -> assertEquals(201, r.getX(), "BA-001 R X avanzado")
        );
    }

    @Test
    @DisplayName("BA-002: borde derecho, X=326 no dispara inversión")
    void updateAliensBordeDerechoSinDisparoX326() {
        Alien l = SoporteCajaNegra.alienEn(326, 50);
        Alien m = SoporteCajaNegra.alienEn(150, 70);
        Alien r = SoporteCajaNegra.alienEn(200, 90);
        board.setAliens(new ArrayList<>(List.of(l, m, r)));
        board.setDirection(1);

        SoporteCajaNegra.invocar(board, "update_aliens");

        assertAll("BA-002",
                () -> assertEquals(1, board.getDirection(), "BA-002 direction debe seguir 1"),
                () -> assertEquals(50, l.getY(), "BA-002 L Y sin cambio"),
                () -> assertEquals(70, m.getY(), "BA-002 M Y sin cambio"),
                () -> assertEquals(90, r.getY(), "BA-002 R Y sin cambio"),
                () -> assertEquals(327, l.getX(), "BA-002 L X esperado 327"),
                () -> assertEquals(151, m.getX(), "BA-002 M X esperado 151"),
                () -> assertEquals(201, r.getX(), "BA-002 R X esperado 201")
        );
    }

    @Test
    @DisplayName("BA-003: borde derecho, X=328 dispara inversión a direction=-1 y descenso +15")
    void updateAliensBordeDerechoInversionX328() {
        Alien l = SoporteCajaNegra.alienEn(328, 50);
        Alien m = SoporteCajaNegra.alienEn(150, 70);
        Alien r = SoporteCajaNegra.alienEn(200, 90);
        board.setAliens(new ArrayList<>(List.of(l, m, r)));
        board.setDirection(1);

        SoporteCajaNegra.invocar(board, "update_aliens");

        assertAll("BA-003",
                () -> assertEquals(-1, board.getDirection(), "BA-003 direction esperado -1"),
                () -> assertEquals(65, l.getY(), "BA-003 L Y descendido a 65 (+15)"),
                () -> assertEquals(85, m.getY(), "BA-003 M Y descendido a 85 (+15)"),
                () -> assertEquals(105, r.getY(), "BA-003 R Y descendido a 105 (+15)")
        );
    }

    @Test
    @DisplayName("BA-004: borde derecho, X=340 dispara inversión a direction=-1 y descenso +15")
    void updateAliensBordeDerechoInversionX340() {
        Alien l = SoporteCajaNegra.alienEn(340, 50);
        Alien m = SoporteCajaNegra.alienEn(150, 70);
        Alien r = SoporteCajaNegra.alienEn(200, 90);
        board.setAliens(new ArrayList<>(List.of(l, m, r)));
        board.setDirection(1);

        SoporteCajaNegra.invocar(board, "update_aliens");

        assertAll("BA-004",
                () -> assertEquals(-1, board.getDirection(), "BA-004 direction esperado -1"),
                () -> assertEquals(65, l.getY(), "BA-004 L Y descendido a 65 (+15)"),
                () -> assertEquals(85, m.getY(), "BA-004 M Y descendido a 85 (+15)"),
                () -> assertEquals(105, r.getY(), "BA-004 R Y descendido a 105 (+15)")
        );
    }

    @Test
    @DisplayName("BA-005: dos aliens en borde derecho ejecutan un único descenso (+15)")
    void updateAliensDosEnBordeDerechoUnSoloDescenso() {
        Alien l = SoporteCajaNegra.alienEn(335, 50);
        Alien m = SoporteCajaNegra.alienEn(150, 70);
        Alien r = SoporteCajaNegra.alienEn(340, 90);
        board.setAliens(new ArrayList<>(List.of(l, m, r)));
        board.setDirection(1);

        SoporteCajaNegra.invocar(board, "update_aliens");

        assertAll("BA-005",
                () -> assertEquals(-1, board.getDirection(), "BA-005 direction esperado -1"),
                () -> assertEquals(65, l.getY(), "BA-005 L Y descendido a 65 (+15)"),
                () -> assertEquals(85, m.getY(), "BA-005 M Y descendido a 85 (+15)"),
                () -> assertEquals(105, r.getY(), "BA-005 R Y descendido a 105 (+15)")
        );
    }

    @Test
    @DisplayName("BA-006: borde izquierdo, X=100 avanza a la izquierda sin descender")
    void updateAliensBordeIzquierdoSinDisparoX100() {
        Alien l = SoporteCajaNegra.alienEn(100, 50);
        Alien m = SoporteCajaNegra.alienEn(150, 70);
        Alien r = SoporteCajaNegra.alienEn(200, 90);
        board.setAliens(new ArrayList<>(List.of(l, m, r)));
        board.setDirection(-1);

        SoporteCajaNegra.invocar(board, "update_aliens");

        assertAll("BA-006",
                () -> assertEquals(-1, board.getDirection(), "BA-006 direction debe seguir -1"),
                () -> assertEquals(50, l.getY(), "BA-006 L Y sin cambio"),
                () -> assertEquals(70, m.getY(), "BA-006 M Y sin cambio"),
                () -> assertEquals(90, r.getY(), "BA-006 R Y sin cambio"),
                () -> assertEquals(99, l.getX(), "BA-006 L X esperado 99"),
                () -> assertEquals(149, m.getX(), "BA-006 M X esperado 149"),
                () -> assertEquals(199, r.getX(), "BA-006 R X esperado 199")
        );
    }

    @Test
    @DisplayName("BA-007: borde izquierdo, X=7 no dispara inversión")
    void updateAliensBordeIzquierdoSinDisparoX7() {
        Alien l = SoporteCajaNegra.alienEn(7, 50);
        Alien m = SoporteCajaNegra.alienEn(150, 70);
        Alien r = SoporteCajaNegra.alienEn(200, 90);
        board.setAliens(new ArrayList<>(List.of(l, m, r)));
        board.setDirection(-1);

        SoporteCajaNegra.invocar(board, "update_aliens");

        assertAll("BA-007",
                () -> assertEquals(-1, board.getDirection(), "BA-007 direction debe seguir -1"),
                () -> assertEquals(50, l.getY(), "BA-007 L Y sin cambio"),
                () -> assertEquals(70, m.getY(), "BA-007 M Y sin cambio"),
                () -> assertEquals(90, r.getY(), "BA-007 R Y sin cambio"),
                () -> assertEquals(6, l.getX(), "BA-007 L X esperado 6"),
                () -> assertEquals(149, m.getX(), "BA-007 M X esperado 149"),
                () -> assertEquals(199, r.getX(), "BA-007 R X esperado 199")
        );
    }

    @Test
    @DisplayName("BA-008: borde izquierdo, X=5 dispara inversión a direction=1 y descenso +15")
    void updateAliensBordeIzquierdoInversionX5() {
        Alien l = SoporteCajaNegra.alienEn(5, 50);
        Alien m = SoporteCajaNegra.alienEn(150, 70);
        Alien r = SoporteCajaNegra.alienEn(200, 90);
        board.setAliens(new ArrayList<>(List.of(l, m, r)));
        board.setDirection(-1);

        SoporteCajaNegra.invocar(board, "update_aliens");

        assertAll("BA-008",
                () -> assertEquals(1, board.getDirection(), "BA-008 direction esperado 1"),
                () -> assertEquals(65, l.getY(), "BA-008 L Y descendido a 65 (+15)"),
                () -> assertEquals(85, m.getY(), "BA-008 M Y descendido a 85 (+15)"),
                () -> assertEquals(105, r.getY(), "BA-008 R Y descendido a 105 (+15)")
        );
    }

    @Test
    @DisplayName("BA-009: borde izquierdo, X=0 dispara inversión a direction=1 y descenso +15")
    void updateAliensBordeIzquierdoInversionX0() {
        Alien l = SoporteCajaNegra.alienEn(0, 50);
        Alien m = SoporteCajaNegra.alienEn(150, 70);
        Alien r = SoporteCajaNegra.alienEn(200, 90);
        board.setAliens(new ArrayList<>(List.of(l, m, r)));
        board.setDirection(-1);

        SoporteCajaNegra.invocar(board, "update_aliens");

        assertAll("BA-009",
                () -> assertEquals(1, board.getDirection(), "BA-009 direction esperado 1"),
                () -> assertEquals(65, l.getY(), "BA-009 L Y descendido a 65 (+15)"),
                () -> assertEquals(85, m.getY(), "BA-009 M Y descendido a 85 (+15)"),
                () -> assertEquals(105, r.getY(), "BA-009 R Y descendido a 105 (+15)")
        );
    }

    @Test
    @DisplayName("BA-010: alien visible en (179, 100) no invade, juego sigue")
    void updateAliensNoInvasionY100() {
        Alien alien = SoporteCajaNegra.alienEn(179, 100);
        board.setAliens(new ArrayList<>(List.of(alien)));
        board.setDirection(1);

        SoporteCajaNegra.invocar(board, "update_aliens");

        assertAll("BA-010",
                () -> assertTrue(board.isInGame(), "BA-010 inGame debe ser true")
        );
    }

    @Test
    @DisplayName("BA-011: alien visible en (179, 303) invade el suelo finalizando juego")
    void updateAliensInvasionY303() {
        Alien alien = SoporteCajaNegra.alienEn(179, 303);
        board.setAliens(new ArrayList<>(List.of(alien)));
        board.setDirection(1);

        SoporteCajaNegra.invocar(board, "update_aliens");

        assertAll("BA-011",
                () -> assertFalse(board.isInGame(), "BA-011 inGame debe ser false"),
                () -> assertEquals("Invasion!", board.getMessage(), "BA-011 mensaje debe ser 'Invasion!'")
        );
    }

    @Test
    @DisplayName("BA-012: alien visible en (179, 350) invade el suelo finalizando juego")
    void updateAliensInvasionY350() {
        Alien alien = SoporteCajaNegra.alienEn(179, 350);
        board.setAliens(new ArrayList<>(List.of(alien)));
        board.setDirection(1);

        SoporteCajaNegra.invocar(board, "update_aliens");

        assertAll("BA-012",
                () -> assertFalse(board.isInGame(), "BA-012 inGame debe ser false"),
                () -> assertEquals("Invasion!", board.getMessage(), "BA-012 mensaje debe ser 'Invasion!'")
        );
    }

    @Test
    @DisplayName("BA-013: alien invisible en el suelo no dispara invasión")
    void updateAliensInvisibleEnSueloNoInvasion() {
        Alien alienInvisible = SoporteCajaNegra.alienEn(179, 350);
        alienInvisible.die();
        Alien alienVisible = SoporteCajaNegra.alienEn(150, 70);
        board.setAliens(new ArrayList<>(List.of(alienInvisible, alienVisible)));
        board.setDirection(1);

        SoporteCajaNegra.invocar(board, "update_aliens");

        assertAll("BA-013",
                () -> assertTrue(board.isInGame(), "BA-013 inGame debe ser true"),
                () -> assertEquals(179, alienInvisible.getX(), "BA-013 alien invisible conserva X=179"),
                () -> assertEquals(151, alienVisible.getX(), "BA-013 alien visible pasa a X=151")
        );
    }

    @Test
    @Tag("informativo")
    @DisplayName("BA-I1: Informativo zona límite invasión en y=278")
    void informativoInvasionY278() {
        Alien alien = SoporteCajaNegra.alienEn(179, 278);
        board.setAliens(new ArrayList<>(List.of(alien)));
        board.setDirection(1);
        SoporteCajaNegra.invocar(board, "update_aliens");
        System.out.println("BA-I1 (y=278): isInGame=" + board.isInGame() + ", getMessage='" + board.getMessage() + "'");
    }

    @Test
    @Tag("informativo")
    @DisplayName("BA-I2: Informativo zona límite invasión en y=290")
    void informativoInvasionY290() {
        Alien alien = SoporteCajaNegra.alienEn(179, 290);
        board.setAliens(new ArrayList<>(List.of(alien)));
        board.setDirection(1);
        SoporteCajaNegra.invocar(board, "update_aliens");
        System.out.println("BA-I2 (y=290): isInGame=" + board.isInGame() + ", getMessage='" + board.getMessage() + "'");
    }

    @Test
    @Tag("informativo")
    @DisplayName("BA-I3: Informativo zona límite invasión en y=302")
    void informativoInvasionY302() {
        Alien alien = SoporteCajaNegra.alienEn(179, 302);
        board.setAliens(new ArrayList<>(List.of(alien)));
        board.setDirection(1);
        SoporteCajaNegra.invocar(board, "update_aliens");
        System.out.println("BA-I3 (y=302): isInGame=" + board.isInGame() + ", getMessage='" + board.getMessage() + "'");
    }

    // =========================================================================
    // 4.3 BB - update_bomb
    // =========================================================================

    @Test
    @DisplayName("BB-001: avance simple de una bomba hacia abajo (+1)")
    void updateBombAvanceSimple() {
        Alien alien = SoporteCajaNegra.alienEn(20, 20);
        alien.getBomb().setDestroyed(false);
        alien.getBomb().setX(50);
        alien.getBomb().setY(100);
        board.setAliens(new ArrayList<>(List.of(alien)));
        board.setPlayer(new Player());

        SoporteCajaNegra.invocar(board, "update_bomb");

        assertAll("BB-001",
                () -> assertEquals(50, alien.getBomb().getX(), "BB-001 X esperado 50"),
                () -> assertEquals(101, alien.getBomb().getY(), "BB-001 Y esperado 101"),
                () -> assertFalse(alien.getBomb().isDestroyed(), "BB-001 bomba no debe estar destruida")
        );
    }

    @Test
    @DisplayName("BB-002: múltiples bombas activas simultáneas avanzan independientemente")
    void updateBombMultiplesBombas() {
        Alien a1 = SoporteCajaNegra.alienEn(20, 20);
        a1.getBomb().setDestroyed(false);
        a1.getBomb().setX(50);
        a1.getBomb().setY(100);

        Alien a2 = SoporteCajaNegra.alienEn(20, 20);
        a2.getBomb().setDestroyed(false);
        a2.getBomb().setX(120);
        a2.getBomb().setY(150);

        Alien a3 = SoporteCajaNegra.alienEn(20, 20);
        a3.getBomb().setDestroyed(false);
        a3.getBomb().setX(300);
        a3.getBomb().setY(200);

        board.setAliens(new ArrayList<>(List.of(a1, a2, a3)));
        board.setPlayer(new Player());

        SoporteCajaNegra.invocar(board, "update_bomb");

        assertAll("BB-002",
                () -> assertEquals(101, a1.getBomb().getY(), "BB-002 a1 Y"),
                () -> assertEquals(151, a2.getBomb().getY(), "BB-002 a2 Y"),
                () -> assertEquals(201, a3.getBomb().getY(), "BB-002 a3 Y"),
                () -> assertFalse(a1.getBomb().isDestroyed(), "BB-002 a1 no destruida"),
                () -> assertFalse(a2.getBomb().isDestroyed(), "BB-002 a2 no destruida"),
                () -> assertFalse(a3.getBomb().isDestroyed(), "BB-002 a3 no destruida")
        );
    }

    @Test
    @DisplayName("BB-003: bomba en Y=283 no alcanza suelo y no es destruida")
    void updateBombSueloY283() {
        Alien a = SoporteCajaNegra.alienEn(20, 20);
        a.getBomb().setDestroyed(false);
        a.getBomb().setX(50);
        a.getBomb().setY(283);
        board.setAliens(new ArrayList<>(List.of(a)));
        board.setPlayer(new Player());

        SoporteCajaNegra.invocar(board, "update_bomb");

        assertAll("BB-003",
                () -> assertFalse(a.getBomb().isDestroyed(), "BB-003 bomba no debe estar destruida")
        );
    }

    @Test
    @DisplayName("BB-004: bomba en Y=285 alcanza suelo y es destruida")
    void updateBombSueloY285() {
        Alien a = SoporteCajaNegra.alienEn(20, 20);
        a.getBomb().setDestroyed(false);
        a.getBomb().setX(50);
        a.getBomb().setY(285);
        board.setAliens(new ArrayList<>(List.of(a)));
        board.setPlayer(new Player());

        SoporteCajaNegra.invocar(board, "update_bomb");

        assertAll("BB-004",
                () -> assertTrue(a.getBomb().isDestroyed(), "BB-004 bomba debe ser destruida")
        );
    }

    @Test
    @DisplayName("BB-005: bomba en Y=290 alcanza suelo y es destruida")
    void updateBombSueloY290() {
        Alien a = SoporteCajaNegra.alienEn(20, 20);
        a.getBomb().setDestroyed(false);
        a.getBomb().setX(50);
        a.getBomb().setY(290);
        board.setAliens(new ArrayList<>(List.of(a)));
        board.setPlayer(new Player());

        SoporteCajaNegra.invocar(board, "update_bomb");

        assertAll("BB-005",
                () -> assertTrue(a.getBomb().isDestroyed(), "BB-005 bomba debe ser destruida")
        );
    }

    @Test
    @DisplayName("BB-006: bomba en Y=340 alcanza suelo y es destruida")
    void updateBombSueloY340() {
        Alien a = SoporteCajaNegra.alienEn(20, 20);
        a.getBomb().setDestroyed(false);
        a.getBomb().setX(50);
        a.getBomb().setY(340);
        board.setAliens(new ArrayList<>(List.of(a)));
        board.setPlayer(new Player());

        SoporteCajaNegra.invocar(board, "update_bomb");

        assertAll("BB-006",
                () -> assertTrue(a.getBomb().isDestroyed(), "BB-006 bomba debe ser destruida")
        );
    }

    @Test
    @DisplayName("BB-007: bomba en (186, 282) impacta al jugador (isDying=true)")
    void updateBombImpactoCentroJugador() {
        Alien a = SoporteCajaNegra.alienEn(20, 20);
        a.getBomb().setDestroyed(false);
        a.getBomb().setX(186);
        a.getBomb().setY(282);
        board.setAliens(new ArrayList<>(List.of(a)));
        board.setPlayer(new Player());

        SoporteCajaNegra.invocar(board, "update_bomb");

        assertAll("BB-007",
                () -> assertTrue(board.getPlayer().isDying(), "BB-007 player isDying debe ser true")
        );
    }

    @DisplayName("BB-008..011: límites en X de impacto en jugador en Y=282")
    @ParameterizedTest
    @CsvSource({
            "BB-008, 178, false",
            "BB-009, 179, true",
            "BB-010, 193, true",
            "BB-011, 195, false"
    })
    void updateBombLimitesXJugador(String id, int bombX, boolean expectedPlayerDying) {
        Alien a = SoporteCajaNegra.alienEn(20, 20);
        a.getBomb().setDestroyed(false);
        a.getBomb().setX(bombX);
        a.getBomb().setY(282);
        board.setAliens(new ArrayList<>(List.of(a)));
        board.setPlayer(new Player());

        SoporteCajaNegra.invocar(board, "update_bomb");

        assertAll(id,
                () -> assertEquals(expectedPlayerDying, board.getPlayer().isDying(),
                        id + " player isDying esperado: " + expectedPlayerDying)
        );
    }

    @Test
    @DisplayName("BB-012: bomba en (186, 200) no impacta (Y no coincide) y avanza a 201")
    void updateBombFueraDeRangoYJugador() {
        Alien a = SoporteCajaNegra.alienEn(20, 20);
        a.getBomb().setDestroyed(false);
        a.getBomb().setX(186);
        a.getBomb().setY(200);
        board.setAliens(new ArrayList<>(List.of(a)));
        board.setPlayer(new Player());

        SoporteCajaNegra.invocar(board, "update_bomb");

        assertAll("BB-012",
                () -> assertFalse(board.getPlayer().isDying(), "BB-012 player no debe estar dying"),
                () -> assertEquals(201, a.getBomb().getY(), "BB-012 bomba Y debe avanzar a 201")
        );
    }

    @Test
    @DisplayName("BB-013: jugador invisible (die()) no cambia su estado isDying ante bomba")
    void updateBombJugadorInvisible() {
        Player player = new Player();
        player.die();
        boolean estadoAntes = player.isDying();
        board.setPlayer(player);

        Alien a = SoporteCajaNegra.alienEn(20, 20);
        a.getBomb().setDestroyed(false);
        a.getBomb().setX(186);
        a.getBomb().setY(282);
        board.setAliens(new ArrayList<>(List.of(a)));

        SoporteCajaNegra.invocar(board, "update_bomb");

        assertAll("BB-013",
                () -> assertEquals(estadoAntes, board.getPlayer().isDying(),
                        "BB-013 isDying no debe cambiar respecto al estado previo")
        );
    }

    @Test
    @DisplayName("BB-014: bomba destruida en coordenadas del jugador no impacta")
    void updateBombBombaDestruidaNoImpacta() {
        Alien a = SoporteCajaNegra.alienEn(20, 20);
        a.getBomb().setDestroyed(true);
        a.getBomb().setX(186);
        a.getBomb().setY(282);
        board.setAliens(new ArrayList<>(List.of(a)));
        board.setPlayer(new Player());

        SoporteCajaNegra.invocar(board, "update_bomb");

        assertAll("BB-014",
                () -> assertFalse(board.getPlayer().isDying(), "BB-014 player no debe estar dying")
        );
    }

    @Test
    @DisplayName("BB-015: [estocástico, creación] posición de bomba creada coincide con su alien")
    void updateBombEstocasticoCreacion() {
        Player p = new Player();
        p.die();
        board.setPlayer(p);

        int creaciones = 0;
        for (int i = 0; i < 1000; i++) {
            for (Alien alien : board.getAliens()) {
                alien.getBomb().setDestroyed(true);
            }
            SoporteCajaNegra.invocar(board, "update_bomb");

            for (Alien alien : board.getAliens()) {
                if (!alien.getBomb().isDestroyed() && alien.getBomb().getY() <= alien.getY() + Commons.BOMB_SPEED) {
                    creaciones++;
                    assertEquals(alien.getX(), alien.getBomb().getX(),
                            "BB-015 coordenada X de bomba debe coincidir con X de su alien");
                }
            }
        }

        final int totalCreaciones = creaciones;
        assertAll("BB-015",
                () -> assertTrue(totalCreaciones > 0, "BB-015 debe haberse creado al menos una bomba en 1000 llamadas")
        );
    }

    @Test
    @DisplayName("BB-016: [estocástico, frecuencia] tasa de generación de bombas en rango esperado [0.05, 0.09]")
    void updateBombEstocasticoFrecuencia() {
        Player p = new Player();
        p.die();
        board.setPlayer(p);

        int totalBombas = 0;
        final int iteraciones = 3000;
        final int numAliens = 24;

        for (int i = 0; i < iteraciones; i++) {
            for (Alien alien : board.getAliens()) {
                alien.getBomb().setDestroyed(true);
            }
            SoporteCajaNegra.invocar(board, "update_bomb");

            for (Alien alien : board.getAliens()) {
                if (!alien.getBomb().isDestroyed()) {
                    totalBombas++;
                }
            }
        }

        double proporcion = (double) totalBombas / (iteraciones * numAliens);
        assertAll("BB-016",
                () -> assertTrue(proporcion >= 0.05 && proporcion <= 0.09,
                        "BB-016 proporcion obtenida (" + proporcion + ") fuera del rango [0.05, 0.09]")
        );
    }

    @Test
    @DisplayName("BB-017: [aliens invisibles] 24 aliens muertos nunca generan bombas")
    void updateBombAliensInvisiblesNoGeneranBombas() {
        for (Alien alien : board.getAliens()) {
            alien.die();
            alien.getBomb().setDestroyed(true);
        }
        Player p = new Player();
        p.die();
        board.setPlayer(p);

        for (int i = 0; i < 2000; i++) {
            SoporteCajaNegra.invocar(board, "update_bomb");
            for (Alien alien : board.getAliens()) {
                assertFalse(!alien.getBomb().isDestroyed(),
                        "BB-017 alien muerto no debe generar bombas");
                alien.getBomb().setDestroyed(true);
            }
        }
    }
}
