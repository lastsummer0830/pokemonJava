package swing_version;

import javax.swing.SwingUtilities;

public class GameMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MapPlayer player = StartGame.start();

            if (player != null) {
                new MapMain(player);
            }
        });
    }
}