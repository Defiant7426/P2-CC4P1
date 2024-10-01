import java.util.*;
import java.io.*;
import java.net.*;

public class MarioServer {
    private static final int PORT = 5684; // Este es el puerto que se va a utilizar para la comunicación
    private static List<ClientHandler> clients = new ArrayList<>(); // Lista de clientes conectados
    static Map<ClientHandler, PlayerInfo> playerInfo = new HashMap<>();
    private static String[] colors = {"RED", "BLUE", "GREEN", "YELLOW", "ORANGE", "PINK", "CYAN", "MAGENTA"};
    private static int colorIndex = 0; // indice del color actual

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // Se crea el socket del servidor
            System.out.println("Servidor en espera de jugadores...");
            while (true){
                Socket clientSocket = serverSocket.accept(); // Se acepta la conexión de un cliente
                String color = colors[colorIndex % colors.length]; // Se selecciona un color para el jugador
                ClientHandler clientHandler = new ClientHandler(clientSocket, clients, color); // Se crea un nuevo manejador de cliente
                clients.add(clientHandler); // Se agrega el cliente a la lista de clientes
                playerInfo.put(clientHandler, new PlayerInfo(color, 0, 0)); // Se agrega la información del jugador
                colorIndex++; // Se incrementa el índice del color
                new Thread(clientHandler).start(); // Se inicia el hilo del cliente
            }
        } catch (IOException e) {
            System.err.println("Error al abrir el socket del servidor: " + e.getMessage());
        }
    }
    static class PlayerInfo { // La clase PlayerInfo se usara para almacenar la información de los jugadores
        String color;
        int x;
        int y;

        public PlayerInfo(String color, int x, int y) {
            this.color = color;
            this.x = x;
            this.y = y;
        }

    }
}
