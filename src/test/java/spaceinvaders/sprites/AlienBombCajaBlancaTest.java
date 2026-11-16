
package spaceinvaders.sprites;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import space_invaders.sprites.Alien;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Caja blanca - Alien.Bomb.initBomb(int, int)")
class AlienBombCajaBlancaTest {

    // BI-01: ambas condiciones verdaderas.
    // BI-02: primera falsa (cortocircuito).
    // BI-03: primera verdadera, segunda falsa.
    // BI-04: ambas coordenadas superiores al límite.
    // BI-05 y BI-06: comprobación de valores negativos.
    @ParameterizedTest(name = "{0} - Bomb({1}, {2})")
    @CsvSource({
        // ID, x, y, xEsperada, yEsperada
        "BI-01, 179, 175, 179, 175",
        "BI-02, 359, 175, 358, 175",
        "BI-03, 179, 351, 179, 350",
        "BI-04, 359, 351, 358, 350",
        "BI-05,  -1, 175,   0, 175",
        "BI-06, 179,  -1, 179,   0"
    })
    void comprobarInitBomb(String id, int x, int y,
                           int xEsperada, int yEsperada) {

        Alien propietario = new Alien(179, 175);
        Alien.Bomb bomba = propietario.new Bomb(x, y);

        assertAll(id,
            () -> assertEquals(
                xEsperada, bomba.getX(), id + " - X final"),
            () -> assertEquals(
                yEsperada, bomba.getY(), id + " - Y final")
        );
    }
}
