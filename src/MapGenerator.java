import java.awt.*;

public class MapGenerator {
    public int[][] map;
    public int brickWidth;
    public int brickHeight;

    public MapGenerator(int ligne, int colonne) {
        map = new int[ligne][colonne];
        int n = map.length;
        int m = map[0].length;
        for (int i = 0; i <n; i++) {
            for (int j = 0; j < m; j++) {
                if (i % 2 == 0 && j % 2 == 0) {
                    map[i][j] = 2;
                } else { map[i][j] = 1; }
            }
        }
        brickWidth = 600 / colonne;
        brickHeight = 200 / ligne;
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    if (i % 2 == 0 && j % 2 == 0 && map[i][j] == 2) {
                        drawBrick2(g, j, i);
                    } else { drawBrick1(g, j, i); }
                }
            }
        }
    }

    public void drawBrick1(Graphics2D g, int j, int i) {
        g.setColor(Color.white);
        g.fillRect(j * brickWidth + 57, i * brickHeight + 50, brickWidth, brickHeight);

        g.setStroke(new BasicStroke(3));
        g.setColor(Color.black);
        g.drawRect(j * brickWidth + 57, i * brickHeight + 50, brickWidth, brickHeight);
    }

    public void drawBrick2(Graphics2D g, int j, int i) {
        g.setColor(Color.blue);
        g.fillRect(j * brickWidth + 57, i * brickHeight + 50, brickWidth, brickHeight);

        g.setStroke(new BasicStroke(3));
        g.setColor(Color.black);
        g.drawRect(j * brickWidth + 57, i * brickHeight + 50, brickWidth, brickHeight);
    }

    public void updateValue(int ligne, int colonne) {
        if (map[ligne][colonne] > 0) {
            map[ligne][colonne]--;
        }
    }
}