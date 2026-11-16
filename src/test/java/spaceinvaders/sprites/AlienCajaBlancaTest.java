
package spaceinvaders.sprites;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import space_invaders.sprites.Alien;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Caja blanca - Alien.initAlien(int, int)")
class AlienCajaBlancaTest {

    // Tres regiones por eje: negativa, válida y superior al máximo.
    @ParameterizedTest(name = "{0} - Alien({1}, {2})")
    @CsvSource({
        // ID, x, y, xEsperada, yEsperada
        "AI-01,  -1,  -1,   0,   0",
        "AI-02,  -1, 175,   0, 175",
        "AI-03,  -1, 351,   0, 350",
        "AI-04, 179,  -1, 179,   0",
        "AI-05, 179, 175, 179, 175",
        "AI-06, 179, 351, 179, 350",
        "AI-07, 359,  -1, 358,   0",
        "AI-08, 359, 175, 358, 175",
        "AI-09, 359, 351, 358, 350"
    })
    void comprobarInitAlien(String id, int x, int y,
                            int xEsperada, int yEsperada) {

        // El constructor llama al método privado initAlien.
        Alien alien = new Alien(x, y);

        assertAll(id,
            () -> assertEquals(
                xEsperada, alien.getX(), id + " - X final"),
            () -> assertEquals(
                yEsperada, alien.getY(), id + " - Y final")
        );
    }
}
