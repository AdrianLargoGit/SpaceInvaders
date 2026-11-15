package main;

import javax.swing.*;
import java.awt.*;



/**
 * {@summary <span class="alert-small">⛔🧪</span> Clase de arranque del juego Space Invaders}
 * <br><br><span class="alert">⛔🧪 No es necesario probar esta clase mediante pruebas unitarias. </span>
 */
public class Main extends JFrame  {

    public Main() {

        initUI();
    }

    /**
     * {@summary<span class="alert-small">⛔🧪</span> Inicializa la interfaz de usuario del juego.}
     * 
     * Este método configura la ventana principal del juego, establece su título,
     * tamaño, comportamiento de cierre y posición en la pantalla. También añade
     * el panel de juego {@link Board} a la ventana.

     * <br><br><span class="alert">⛔🧪 No es necesario probar esta clase mediante pruebas unitarias. </span>
     */
    private void initUI() {

        add(new Board());

        setTitle("Space Invaders");
        setSize(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /**
     * {@summary<span class="alert-small">⛔🧪</span> Punto de entrada principal del juego.}
     * 
     * <br><br><span class="alert">⛔🧪 No es necesario probar esta clase mediante pruebas unitarias. </span>
     * @param args argumentos de línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {

            var ex = new Main();
            ex.setVisible(true);
        });
    }
}
