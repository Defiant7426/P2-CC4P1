import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;

class ClientHandler implements Runnable {
    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final List<ClientHandler> clients;
    private final String color;

    public ClientHandler(Socket socket, List<ClientHandler> clients, String color) {
        this.socket = socket; // Se asigna el socket del cliente
        this.clients = clients; // Se asigna la lista de clientes
        this.color = color; // Se asigna el color del cliente
        try {
            out = new PrintWriter(socket.getOutputStream(), true); // Se crea un buffer de escritura
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Se crea un buffer de lectura
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            out.println("COLOR " + color); // Envía el color asignado al cliente
            String inputLine; // Variable para almacenar los mensajes del cliente
            while ((inputLine = in.readLine()) != null) { // Se lee un mensaje del cliente
                if (inputLine.startsWith("MOVE")) { // Si el mensaje es para mover al jugador
                    String[] parts = inputLine.split(" "); // Se divide el mensaje en partes
                    int x = Integer.parseInt(parts[1]); // Se obtiene la posición x del jugador
                    int y = Integer.parseInt(parts[2]); // Se obtiene la posición y del jugador
                    MarioServer.playerInfo.get(this).x = x; // Se actualiza la posición x del jugador
                    MarioServer.playerInfo.get(this).y = y; // Se actualiza la posición y del jugador
                    broadcastPositions(); // Se envían las posiciones de los jugadores a todos los clientes
                }
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally { // Se cierra la conexión con el cliente
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clients.remove(this);
            MarioServer.playerInfo.remove(this); // Se elimina la información del jugador
            broadcastPositions();
        }
    }   

    private void broadcastPositions() {
        StringBuilder message = new StringBuilder("POSITIONS ");
        for (Map.Entry<ClientHandler, MarioServer.PlayerInfo> entry : MarioServer.playerInfo.entrySet()) {
            MarioServer.PlayerInfo info = entry.getValue();
            message.append(info.color).append(",").append(info.x).append(",").append(info.y).append(";");
        }
        for (ClientHandler client : clients) {
            client.out.println(message);
        }
        System.out.println("Broadcasting positions: " + message);
    }
}
