import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class MarioClient extends JFrame {
    private static final String SERVER_IP = "172.17.32.23"; // IP del servidor
    private static final int SERVER_PORT = 5684; // Puerto del servidor
    private Socket socket; // Socket del cliente
    private PrintWriter out; // Buffer de escritura
    private BufferedReader in; // Buffer de lectura
    private JPanel gamePanel; // Panel del juego
    private int playerX = 0, playerY = 0; // Posición del jugador

    public MarioClient() {
        setTitle("Mario Bros Multijugador");
        setSize(800, 600); // Tamaño de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Al cerrar la ventana, se cierra el programa

        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) { // Método para dibujar en el panel
                super.paintComponent(g); // Se llama al método paintComponent de la clase padre
                g.setColor(Color.RED); // Se establece el color rojo
                g.fillRect(playerX, playerY, 30, 30); // Se dibuja un rectángulo en la posición del jugador
            }
        };
        add(gamePanel); // Se agrega el panel al frame

        addKeyListener(new KeyAdapter() { // Se agrega un KeyListener al frame
            @Override
            public void keyPressed(KeyEvent e) { // Método que se ejecuta al presionar una tecla
                movePlayer(e.getKeyCode()); // Se mueve el jugador en función de la tecla presionada
            }
        });

        setFocusable(true); // Se establece el foco en el frame

        connectToServer(); // Se conecta al servidor
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT); // Se crea el socket del cliente
            out = new PrintWriter(socket.getOutputStream(), true); // Se crea un buffer de escritura
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Se crea un buffer de lectura

            // Iniciar un hilo para recibir mensajes del servidor
            new Thread(this::receiveMessages).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void movePlayer(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT: // Si se presiona la tecla de la izquierda
                playerX -= 5; // Se mueve el jugador hacia la izquierda
                break;
            case KeyEvent.VK_RIGHT: // Si se presiona la tecla de la derecha
                playerX += 5; // Se mueve el jugador hacia la derecha
                break;
            case KeyEvent.VK_UP: // Si se presiona la tecla de arriba
                playerY -= 5; // Se mueve el jugador hacia arriba
                break;
            case KeyEvent.VK_DOWN: // Si se presiona la tecla de abajo
                playerY += 5; // Se mueve el jugador hacia abajo
                break;
        }
        gamePanel.repaint(); // Se vuelve a pintar el panel
        sendPosition(); // Se envía la posición del jugador al servidor
    }

    private void sendPosition() { // Método para enviar la posición del jugador al servidor
        out.println("MOVE " + playerX + " " + playerY); // Se envía un mensaje al servidor con la posición del jugador
    }

    private void receiveMessages() {
        try {
            String message; // Variable para almacenar los mensajes del servidor
            while ((message = in.readLine()) != null) { // Se lee un mensaje del servidor
                System.out.println("Mensaje del servidor: " + message);
                // Aquí se procesarían los mensajes del servidor
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Se crea una instancia de MarioClient y se hace visible
        SwingUtilities.invokeLater(() -> new MarioClient().setVisible(true));
    }
}