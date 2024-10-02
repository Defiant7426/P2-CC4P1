import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class MarioClient extends JFrame implements KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String HOST = "localhost";//"192.168.18.7";
    private static final int PORT = 5684;
    private List<Platform> plataforms;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GamePanel gamePanel;
    private Map<String, Player> players = new HashMap<>();
    private String myColor;
    private Timer timer;

    private final int GRAVEDAD = 5;
    private final int SALTO_Y = -80;
    private final int VELOCIDAD_X = 10;

    private List<Enemy> enemies;
    private static final int INITIAL_LIVES = 3;
    private Map<String, Integer> playerLives;



    public MarioClient() {
        setTitle("Mario Bros Multijugador");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        gamePanel = new GamePanel();
        add(gamePanel);
        addKeyListener(this);

        connectToServer();
        initializePlataforms();

        enemies = new ArrayList<>();
        playerLives = new HashMap<>();
        initializeEnemies();
        initializePlayerLives();

        startGameLoop();
    }

    private void initializeEnemies() {
        enemies.add(new Enemy(400, 340, 1));
        enemies.add(new Enemy(450, 340, -1));
        //enemies.add(new Enemy(600, 340));
    }

    private void initializePlayerLives() {
        for (String color : players.keySet()) {
            playerLives.put(color, INITIAL_LIVES);
        }
    }

    private void initializePlataforms(){
        plataforms = new ArrayList<>();
        plataforms.add(new Platform(0, 370, 1000, 50, "BROWN")); // suelo
        plataforms.add(new Platform(310, 265, 40, 105, "GREEN")); // Muro 1
        plataforms.add(new Platform(550, 265, 40, 105, "GREEN")); // Muro 2
    }

    private void connectToServer() {
        try {
            socket = new Socket(HOST, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Recibir el color asignado por el servidor
            String colorMessage = in.readLine();
            if (colorMessage.startsWith("COLOR")) {
                myColor = colorMessage.split(" ")[1];
                players.put(myColor, new Player(25, 340, myColor));
            }

            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startGameLoop() {
        timer = new Timer(20, e -> {
            Player myPlayer = players.get(myColor);
            applyGravity(myPlayer);
            checkCollisions(myPlayer);
            update();
            gamePanel.repaint();
            sendPosition();
        });
        timer.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No se necesita implementar
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Player myPlayer = players.get(myColor);
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                myPlayer.x -= VELOCIDAD_X;
                break;
            case KeyEvent.VK_RIGHT:
                myPlayer.x += VELOCIDAD_X;
                break;
            case KeyEvent.VK_UP:
                if (!myPlayer.isJumping) {
                    myPlayer.velocityY = SALTO_Y;
                    myPlayer.isJumping = true;
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No se necesita implementar
    }

    private void applyGravity(Player player) {
        player.velocityY += GRAVEDAD; // Gravedad
        player.y += (int) player.velocityY;
    }

    private void checkCollisions(Player player) {
        for (Platform platform : plataforms) {
            if (player.x < platform.x + platform.width &&
                    player.x + 30 > platform.x &&
                    player.y < platform.y + platform.height &&
                    player.y + 30 > platform.y) {
                // Colision detectada
                if (player.velocityY > 0) {
                    // El jugador esta cayendo
                    player.y = platform.y - 30;
                    player.velocityY = 0;
                    player.isJumping = true;
                } else {
                    // El jugador esta saltando
                    player.y = platform.y + platform.height;
                    player.velocityY = 0;
                    player.isJumping = true;
                }
            }
        }

        if (player.y > HEIGHT) {
            player.y = HEIGHT;
            player.velocityY = 0;
            player.isJumping = false;
        }

        if (player.y < 0) {
            player.y = 0;
            player.velocityY = 0;
            player.isJumping = true;
        }

        if (player.x < 0) {
            player.x = 0;
        }

        if (player.x > WIDTH - 45) {
            player.x = WIDTH - 45;
        }
    }

    private void sendPosition() {
        Player myPlayer = players.get(myColor);
        out.println("MOVE " + myPlayer.x + " " + myPlayer.y);
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("POSITIONS")) {
                    updatePlayerPositions(message.substring(10));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updatePlayerPositions(String positionsData) {
        String[] playerPositions = positionsData.split(";");
        for (String playerPosition : playerPositions) {
            String[] data = playerPosition.split(",");
            if (data.length == 3) {
                String color = data[0];
                int x = Integer.parseInt(data[1]);
                int y = Integer.parseInt(data[2]);
                players.put(color, new Player(x, y, color));
            }
        }
        gamePanel.repaint();
    }

    private void update() {
        for (Enemy enemy : enemies) {
            enemy.move(plataforms);
            for (Map.Entry<String, Player> entry : players.entrySet()) {
                Player player = entry.getValue();
                if (enemy.collidesWithPlayer(player)) {
                    String color = entry.getKey();
                    playerLives.put(color, playerLives.get(color) - 1);
                    resetPlayerPosition(player);
                    if (playerLives.get(color) <= 0) {
                        System.out.println("El jugador " + color + " ha perdido todas sus vidas");
                    }
                }
            }
        }
    }

    private void resetPlayerPosition(Player player) {
        // Reiniciar la posición del jugador a un punto seguro
        player.x = 25;
        player.y = 340;
        player.velocityY = 0;
        player.isJumping = false;
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (Player player : players.values()) {
                g.setColor(getColorFromString(player.color));
                g.fillRect(player.x, player.y, 30, 30);
            }

            for(Platform platform : plataforms){
                g.setColor(getColorFromString(platform.color));
                g.fillRect(platform.x, platform.y, platform.width, platform.height);
            }

            for (Enemy enemy : enemies) {
                g.setColor(Color.RED);
                g.fillOval(enemy.x, enemy.y, enemy.width, enemy.height);
            }

            g.setColor(Color.BLACK);
            int y = 20;
            for (Map.Entry<String, Integer> entry : playerLives.entrySet()) {
                g.drawString(entry.getKey() + ": " + entry.getValue() + " vidas", 10, y);
                y += 20;
            }
        }
    }

    private Color getColorFromString(String colorStr) {
        switch (colorStr.toUpperCase()) {
            case "RED": return Color.RED;
            case "BLUE": return Color.BLUE;
            case "GREEN": return Color.GREEN;
            case "YELLOW": return Color.YELLOW;
            case "ORANGE": return Color.ORANGE;
            case "PINK": return Color.PINK;
            case "CYAN": return Color.CYAN;
            case "MAGENTA": return Color.MAGENTA;
            case "WHITE": return Color.WHITE;
            case "BROWN": return new Color(139, 69, 19);
            default: return Color.BLACK;
        }
    }

    private static class Player {
        int x, y;
        String color;
        double velocityX;
        double velocityY;
        boolean isJumping;
        final double MAX_VELOCITY_X = 10;

        Player(int x, int y, String color) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.velocityX = 0;
            this.velocityY = 0;
            this.isJumping = false;
        }
    }

    private static class Enemy {
        private int x, y;
        private int width = 30;
        private int height = 30;
        private int speed = 2;
        private int direction; // 1 para derecha, -1 para izquierda

        public Enemy(int x, int y, int direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }

        public void move(List<Platform> platforms) {
            x += speed * direction;

            // Cambiar dirección si llega al borde de una plataforma
            for (Platform platform : platforms) {
                if (isOnPlatform(platform)) {
                    if (x <= platform.x || x + width >= platform.x + platform.width) {
                        direction *= -1;
                        break;
                    }
                }
            }
        }

        private boolean isOnPlatform(Platform platform) {
            return x < platform.x + platform.width &&
                   x + width > platform.x &&
                   y  < platform.y + platform.height &&
                    y + height > platform.y;
        }

        public boolean collidesWithPlayer(Player player) {
            return x < player.x + 30 &&
                    x + width > player.x &&
                    y < player.y + 30 &&
                    y + height > player.y;
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MarioClient().setVisible(true));
    }
}