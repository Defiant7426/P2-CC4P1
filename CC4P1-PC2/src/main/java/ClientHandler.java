import java.io.*;
import java.net.*;
import java.util.List;

public class ClientHandler implements Runnable { // Se crea la clase ClientHandler que implementa la interfaz Runnable
    private Socket clientSocket;
    private List<ClientHandler> clients;
    private PrintWriter output;
    private BufferedReader input;
    private String playerName;

    public ClientHandler(Socket socket, List<ClientHandler> clients) throws IOException {
        this.clientSocket = socket;
        this.clients = clients;
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Se crea un buffer de lectura
        output = new PrintWriter(clientSocket.getOutputStream(), true); // Se crea un buffer de escritura
    }

    @Override
    public void run() {
        try {
            output.println("Bienvenido al servidor de Mario Bros. Introduce tu nombre: ");
            playerName = input.readLine(); // Se lee el nombre del jugador
            broadcastMessage(playerName + " se ha unido al juego."); // Se envía un mensaje a todos los jugadores

            String clientMessage;
            while ((clientMessage = input.readLine()) != null) { // Se lee el mensaje del cliente
                handleClientMessage(clientMessage); // Se procesa el mensaje del cliente
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close(); // Se cierra el socket del cliente
                clients.remove(this); // Se elimina el cliente de la lista de clientes
                broadcastMessage(playerName + " ha abandonado el juego."); // Se envía un mensaje a todos los jugadores
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.output.println(message); // Se envía el mensaje a todos los clientes
        }
    }

    private void handleClientMessage(String message) {
        System.out.println(playerName + ": " + message); // Se imprime el mensaje en la consola
        broadcastMessage(playerName + ": " + message); // Se envía el mensaje a todos los jugadores
    }

}
