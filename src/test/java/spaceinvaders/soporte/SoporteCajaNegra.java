package spaceinvaders.soporte;

import main.Board;
import space_invaders.sprites.Alien;

import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SoporteCajaNegra {

    static {
        System.setProperty("java.awt.headless", "true");
    }

    public static KeyEvent tecla(int id, int keyCode) {
        return new KeyEvent(new JPanel(), id, System.currentTimeMillis(), 0, keyCode, KeyEvent.CHAR_UNDEFINED);
    }

    public static Board nuevoBoardControlado() {
        Board b = new Board();
        if (b.getTimer() != null) {
            b.getTimer().stop();
        }
        invocar(b, "gameInit");
        b.setInGame(true);
        b.setDeaths(0);
        b.setDirection(1);
        b.setMessage("");
        return b;
    }

    public static void invocar(Board b, String metodo) {
        try {
            Method m = Board.class.getDeclaredMethod(metodo);
            m.setAccessible(true);
            m.invoke(b);
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                throw new RuntimeException(cause);
            }
        } catch (ReflectiveOperationException roe) {
            throw new RuntimeException(roe);
        }
    }

    public static Alien alienEn(int x, int y) {
        Alien alien = new Alien(0, 0);
        alien.setX(x);
        alien.setY(y);
        return alien;
    }
}
