import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class GamePlay extends JPanel implements KeyListener, ActionListener {

    private boolean play = false;
    private int score = 0;
    private int ligne = 3;
    private int colonne = 8;
    private int totalBricks = ligne * colonne;

    private String playerName = "Joueur"; // Nom par défaut du joueur
    private ArrayList<Player> highScores = new ArrayList<>();
    private final String TOP5_Scores = "top5scores.dat";
    private int nomEstPris = 0;

    private Timer timer;
    private int duree = 7;

    private int playerX = 300;
    private int ballposX = 342;
    private int ballposY = 532;
    private int ballXdir = -2;
    private int ballYdir = -2;

    private MapGenerator map;

    public GamePlay() {
        chargerScores();
        map = new MapGenerator(ligne, colonne);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(duree, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.black);
        g.fillRect(1, 1, 700, 600);

        // Dessin des briques
        map.draw((Graphics2D) g);

        // Ajout des bordures
        g.setColor(Color.green);
        g.fillRect(0, 0, ligne, 600);
        g.fillRect(0, 0, 700, ligne);
        g.fillRect(700, 0, ligne, 600);

        // Affichage du score
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("" + score, 590, 30);

        // Paddle & balle
        g.setColor(Color.green);
        g.fillRect(playerX, 550, 100, 8);
        g.setColor(Color.yellow);
        g.fillOval(ballposX, ballposY, 18, 18);

        // Fin du jeu
        if (totalBricks <= 0 || ballposY > 570) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            update_highScores();

            int panelX = 150;
            int panelY = 200;
            int panelWidth = 400;
            int panelHeight = 300;
            int arc = 25;

            g.setColor(new Color(0, 0, 0, 180));
            g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, arc, arc);

            GradientPaint borderGradient = new GradientPaint(
                    panelX, panelY, (totalBricks <= 0) ? new Color(0, 255, 0) : new Color(255, 0, 0),
                    panelX + panelWidth, panelY + panelHeight, new Color(0, 100, 255),
                    true
            );

            ((Graphics2D)g).setPaint(borderGradient);
            ((Graphics2D)g).setStroke(new BasicStroke(4));
            g.drawRoundRect(panelX, panelY, panelWidth, panelHeight, arc, arc);

            // Message principal
            String text = (totalBricks <= 0)
                    ? "Bravo, Score : " + score
                    : "Game Over, Score : " + score;

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString(text, 182, 252);

            g.setColor((totalBricks <= 0) ? Color.GREEN : Color.RED);
            g.drawString(text, 180, 250);

            // Instruction pour recommencer
            g.setColor(new Color(255, 255, 255, 200)); // Blanc semi-transparent
            g.setFont(new Font("Arial", Font.ITALIC, 18));
            g.drawString("Appuyez sur Entrée pour recommencer", 190, 290);

            // TOP 5
            g.setColor(Color.CYAN);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("  TOP 5 SCORES  ", 160, 330);
            g.setColor(new Color(255, 255, 255, 50));
            g.fillRoundRect(180, 350, 340, 150, 15, 15);

            // Liste des scores avec dégradé de couleur
            for (int i = 0; i < Math.min(highScores.size(), 5); i++) {
                Player entry = highScores.get(i);
                Color rankColor = new Color(
                        255 - (i * 30),
                        100 + (i * 30),
                        100 + (i * 20)
                );

                g.setColor(rankColor);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.drawString((i+1) + ". " + entry.name, 200, 380 + i * 25);

                g.setColor(Color.WHITE);
                g.drawString(": " + entry.score + " pts", 320, 380 + i * 25);
            }
        }
        g.dispose();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();

        if (nomEstPris != 1) {
            playerName = JOptionPane.showInputDialog(this, "Entrez votre nom:", "Nom du joueur", JOptionPane.PLAIN_MESSAGE);
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Player";
            }
            nomEstPris = 1;
        }

        if (play) {
            // Contact balle/padle
            if (new Rectangle(ballposX, ballposY, 18, 18)
                    .intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballYdir = -ballYdir;
            }

            // Contact balle/briques
            int n = map.map.length;
            int m = map.map[0].length;
            A: for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 57;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballposX, ballposY, 18, 18);

                        if (ballRect.intersects(rect)) {
                            map.updateValue(i, j);
                            map.draw((Graphics2D) getGraphics());
                            if (map.map[i][j] == 0) {
                                totalBricks--;
                            }
                            score += 2;
                            playSound("sons/break.wav");
                            if (ballposX + 19 <= rect.x || ballposX + 1 >= rect.x + rect.width) {
                                ballXdir = -ballXdir;
                            } else {
                                ballYdir = -ballYdir;
                            }
                            break A;
                        }
                    }
                }
            }

            ballposX += ballXdir;
            ballposY += ballYdir;

            // Contact balle/murs
            if (ballposX < 0) {
                ballXdir = -ballXdir;
            }
            if (ballposY < 0) {
                ballYdir = -ballYdir;
            }
            if (ballposX > 670) {
                ballXdir = -ballXdir;
            }

            // son de la défaite
            if (ballposY > 570) {
                playSound("sons/lost.wav");
            }
        }

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 599) {
                playerX = 599;
            } else {
                moveRight();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX <= 5) {
                playerX = 5;
            } else {
                moveLeft();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                play = true;
                ballposX = 342;
                ballposY = 532;
                ballXdir = -2;
                ballYdir = -2;
                playerX = 300;
                score = 0;
                totalBricks = ligne * colonne;
                map = new MapGenerator(ligne, colonne);

                repaint();
            }
        }
    }

    public void moveRight() {
        play = true;
        playerX += 25;
    }

    public void moveLeft() {
        play = true;
        playerX -= 25;
    }

    public void playSound(String soundFile) {
        try {
            File file = new File(soundFile);
            if (file.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void chargerScores() {
        File file = new File(TOP5_Scores);
        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof ArrayList) {
                    highScores = (ArrayList<Player>) obj;
                    highScores = highScores.stream()
                            .distinct()
                            .collect(Collectors.toCollection(ArrayList::new));
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                highScores = new ArrayList<>();
            }
        } else {
            highScores = new ArrayList<>();
        }
    }

    public void sauvegarderTOP5_Scores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TOP5_Scores))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update_highScores() {
        ArrayList<Player> newScores = new ArrayList<>(highScores);
        newScores.add(new Player(playerName, score));

        newScores.sort((a, b) -> Integer.compare(b.score, a.score));
        highScores = new ArrayList<>(newScores.stream()
                .distinct()
                .limit(5)
                .collect(Collectors.toList()));

        sauvegarderTOP5_Scores();
    }

}