import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame fen = new JFrame();
            GamePlay gamePlay = new GamePlay();
            fen.setSize(717, 650);
            fen.setLocationRelativeTo(null);
            fen.setTitle("Brick Breaker");
            fen.setResizable(false);
            fen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fen.add(gamePlay);
            fen.setVisible(true);
            gamePlay.requestFocusInWindow();
        });
    }
}