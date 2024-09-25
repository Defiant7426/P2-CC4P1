import java.util.*;
import java.io.*;
import java.net.*;

public class MarioServer {
    private static final int PORT = 5684; // Este es el puerto que se va a utilizar para la comunicación
    private static List<ClientHandler> clients = new ArrayList<>(); // Lista de clientes conectados

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // Se crea el socket del servidor
            System.out.println("Servidor en espera de jugadores...");
            while (true){
                Socket clientSocket = serverSocket.accept(); // Se acepta la conexión de un cliente
                ClientHandler clientHandler = new ClientHandler(clientSocket, clients); // Se crea un nuevo hilo para el cliente
                clients.add(clientHandler); // Se agrega el cliente a la lista de clientes
                new Thread(clientHandler).start(); // Se inicia el hilo del cliente
            }
        } catch (IOException e) {
            System.err.println("Error al abrir el socket del servidor: " + e.getMessage());
        }
    }
}
