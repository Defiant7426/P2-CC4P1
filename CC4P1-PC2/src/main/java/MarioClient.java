import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class MarioClient extends JFrame implements KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String HOST = "172.17.32.23";
    private static final int PORT = 5684;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GamePanel gamePanel;
    private Map<String, Player> players = new HashMap<>();
    private String myColor;

    public MarioClient() {
        setTitle("Mario Bros Multijugador");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        gamePanel = new GamePanel();
        add(gamePanel);
        addKeyListener(this);

        connectToServer();
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
                players.put(myColor, new Player(0, 0, myColor));
            }

            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                myPlayer.x -= 5;
                break;
            case KeyEvent.VK_RIGHT:
                myPlayer.x += 5;
                break;
            case KeyEvent.VK_UP:
                myPlayer.y -= 5;
                break;
            case KeyEvent.VK_DOWN:
                myPlayer.y += 5;
                break;
        }
        gamePanel.repaint();
        sendPosition();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No se necesita implementar
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

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (Player player : players.values()) {
                g.setColor(getColorFromString(player.color));
                g.fillRect(player.x, player.y, 30, 30);
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
            default: return Color.BLACK;
        }
    }

    private static class Player {
        int x, y;
        String color;

        Player(int x, int y, String color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MarioClient().setVisible(true));
    }
}