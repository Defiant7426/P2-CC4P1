# CC4P1 Programación Concurrente y Distribuida - PC 2

Alumnos:

- De la Cruz Valdiviezo, Pedro Luis David
- Luis Angel Azaña Vega

Mario Bros Multijugador.
Mario Bros. es un videojuego clásico desarrollado por Nintendo y lanzado en 1983. En este juego, los jugadores controlan a Mario, un fontanero, y a su hermano Luigi. El objetivo principal es eliminar a las criaturas que emergen de las tuberías en cada nivel, golpeándolas desde abajo para voltearlas y luego pateándolas para eliminarlas. Este juego se desarrolla en un entorno de plataformas y se juega en una pantalla fija. En 1985, Nintendo lanzó Super Mario Bros, una secuela que se convirtió en uno de los juegos más influyentes de todos los tiempos. En este juego, Mario debe rescatar a la Princesa Peach del villano Bowser en el Reino Champiñón. Los jugadores deben atravesar varios niveles llenos de enemigos y obstáculos, utilizando potenciadores como el Super Champiñón, la Flor de Fuego y la Estrella para ayudar a Mario en su misión

# Objetivo del juego

El objetivo principal de Mario Bros. es eliminar a todas las criaturas que emergen de las tuberías en cada nivel. Los jugadores controlan a Mario y, en el modo multijugador, a su hermano Luigi. Deben evitar que las criaturas lleguen al fondo de la pantalla y se vuelvan más rápidas y peligrosas

# Movimiento y Controles

Movimiento: Mario, Luigi y los Hongos pueden moverse a la izquierda y a la derecha.
Salto: Los personajes pueden saltar para golpear las plataformas desde abajo.
Golpeo: Al golpear las plataformas, los jugadores pueden voltear a las criaturas que están encima.

# Enemigos y obstáculos

Criaturas: Los enemigos incluyen tortugas (Koopas), cangrejos (Sidesteppers) y moscas (Fighter Flies). Cada tipo de criatura tiene un comportamiento diferente. 

Plataformas y Tuberías: Las criaturas emergen de las tuberías y se mueven a lo largo de las plataformas. Los jugadores deben estar atentos a su patrón de movimiento.

Fuego: A medida que avanza el juego, aparecerán bolas de fuego que se mueven a través
de la pantalla y deben ser evitadas.

# Eliminación de criaturas

Voltear y Patear: Para eliminar a una criatura, los jugadores deben golpear la plataforma
desde abajo para voltearla y luego patearla antes de que se recupere.

Puntos: Los jugadores ganan puntos por cada criatura eliminada y por recoger monedas
que aparecen ocasionalmente.

# Puntaje o monedas

Monedas: Recoger monedas otorga puntos adicionales

![image.png](img/image.png)

# Jugadores

Los Jugadores pueden esta representados por Luigi de varios colores, Mario de varios colores o hongos de varios colores. Los jugadores pueden se representados con caracteres:

- Mario M
- Luigi L
- Hongo H

Mario Bros Multijugador
Multijugador

De 1 hasta “r” jugadores pueden enfrentarse en el modo combate, cada jugador puede estar en una pc diferente, conectados en red.

Niveles

Desarrollar un nivel por integrante.

Tablero de puntuación

Agregar un tablero para saber quién comió más monedas.

Adaptar el Mario Bros de modo que en el juego se puede competir contra “r” rivales
humanos que estarán en la red

Desarrollar un modo multijugador, donde varios jugadores (rivales) jueguen entre ellos y contra los otros Jugadores, y donde gana el que consiga comer más monedas y no chocar con sus rivales, objetos o enemigos.
El escenario es extenso y el jugador puede moverse y recorrer por todo el usuario ya sea derecha, izquierda, saltando o bajando. Un jugador puede estar representado con caracteres, por ejemplo.

![image.png](img/image%201.png)

Debe funcionar en java, y como mínimo en Java 8 com.

Los grupos mayores a 2 agregar dos niveles por integrante adicional, constan de nuevos escenarios como mayor interactividad y velocidad de acuerdo a los niveles.

Para el servidor definir un campo para el puerto y para el cliente definir dos campos de la ip y del puerto

Las tareas de los participantes son:

- No usar websocket, socketio y otras librerías.
- El servidor.
- Los clientes y los diferentes tipos de enemigos.
- La interfaz grafica y ejecutar en red.
- Los grupos que tengan más de dos participantes, desarrollaran 2 escenarios
adicionales por cada participante adicional.

# Solución

Vamos a utilizar IntelliJ IDEA para gestionar el juego y para que varios jugadores puedan jugar a traves de el, para ello planteamos la estructura de nuestro proyecto:

## 1. Estructura del Proyecto

- Modulo del servidor: Gestionara la comunicación entre los jugadores y el control de los niveles
- Módulo del cliente: Representará a los jugadores y se encargará de la lógica del movimiento, recolección de monedas y eliminación de enemigos
- Interfaz Gráfica (GUI): Utilizará Java Swing para representar el juego visualmente
- Conexión de Red: Implementa las clases para la conexión del cliente-servidor utilizando `Socket` y `ServerSocket`

## 2. Client Handle

La clase `ClientHandler` extiende `Runnable` para permitir que cada cliente se ejecute en su propio hilo, permitiendo que el servidor maneje varias conexiones simultáneas.

```java
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

```

Explicación:

La clase ClientHandler es responsable de manejar la comunicación con un cliente específico. Implementa Runnable para permitir que cada cliente se ejecute en su propio hilo. Esto facilita que el servidor pueda manejar múltiples conexiones de clientes simultáneamente.

La clase utiliza BufferedReader y PrintWriter para leer y escribir mensajes entre el cliente y el servidor. El método run() maneja la lógica principal del cliente, incluyendo la recepción del nombre del jugador y la transmisión de mensajes a todos los clientes conectados.

### ¿Cómo funciona `ClientHandler`?

- Cuando un nuevo jugador se conecta, se crea un nuevo hilo utilizando `ClientHandler`, y este hilo se ejecuta en paralelo con otros clientes.
- El servidor espera las acciones de los jugadores (movimientos o mensajes) y luego las retransmite a todos los clientes, asegurando que todos vean la misma información sobre el estado del juego.
- Por ejemplo, si un jugador recolecta una moneda o salta, esta acción se puede transmitir a través del servidor a todos los demás jugadores conectados para que vean los mismos cambios en sus pantallas.

## 2. Servidor

- El servidor se encargará de recibir las conexiones de los jugadores, gestionar la lógica del juego y sincronizar el estado de los clientes
- Utiliza `ServerSocket` para crear un servidor que escuche en el puerto definido.
- Maneja el envío de información entre los clientes y el servidor (por ejemplo, las posiciones de los jugadores, las monedas recolectadas, y el estado de los enemigos).

```java
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
```

Explicación:

La clase MarioServer es la clase principal del servidor. Esta clase utiliza un ServerSocket para escuchar conexiones entrantes en un puerto específico (en este caso, el puerto 5684). Cuando un cliente se conecta, el servidor crea una nueva instancia de ClientHandler para manejar esa conexión específica. Cada ClientHandler se ejecuta en su propio hilo, permitiendo que el servidor maneje múltiples clientes simultáneamente.

### ¿Cómo funciona `MarioServer`?

- Inicialización: El servidor se inicia y comienza a escuchar conexiones en el puerto especificado (5684).
- Aceptación de conexiones: Cuando un cliente se conecta, el servidor acepta la conexión y crea un nuevo ClientHandler para manejar esa conexión específica.
- Manejo de múltiples clientes: Cada ClientHandler se ejecuta en su propio hilo, permitiendo que el servidor maneje múltiples clientes de forma concurrente. Esto asegura que las acciones de un jugador no bloqueen o ralenticen el juego para otros jugadores.

## 3. Cliente

- El cliente manejará los controles de movimiento y enviará las acciones al servidor.
- El jugador puede moverse en 4 direcciones (izquierda, derecha, saltar y agacharse).
- Para la conexión, el cliente debe especificar la IP y el puerto del servidor.

El cliente se implementará utilizando Java Swing para la interfaz gráfica y sockets para la comunicación con el servidor. A continuación, se muestra un ejemplo básico de cómo podría estructurarse la clase principal del cliente:

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class MarioClient extends JFrame {
    private static final String SERVER_IP = "localhost"; // IP del servidor
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
```

Explicación:

La clase MarioClient es la implementación del cliente para el juego Mario Bros Multijugador. Esta clase extiende JFrame para crear una ventana gráfica y utiliza sockets para la comunicación con el servidor. La clase maneja la interfaz de usuario, los controles del jugador y la sincronización con el servidor.

### ¿Cómo funciona MarioClient?

- Interfaz gráfica: Utiliza Java Swing para crear una ventana con un panel de juego donde se dibuja al jugador.
- Controles: Implementa un KeyListener para capturar las teclas presionadas por el usuario y mover al jugador en consecuencia.

## Primeros pasos

Al ejecutar nuestro programa tendremos lo siguiente:

Al iniciar el servidor, veremos un mensaje indicando que está en espera de jugadores. Cuando ejecutamos el cliente, se abrirá una ventana con un rectángulo rojo representando al jugador. Podemos mover este rectángulo usando las teclas de flecha, y el cliente enviará las actualizaciones de posición al servidor.

Al ejecutar el servidor:

![image.png](img/image%202.png)

Al ejecutar el cliente:

![image.png](img/image%203.png)

Cuando presionamos las flechas, el “mario” rojo se mueve y en la parte del cliente nos sale lo siguiente:

![image.png](img/image%204.png)

y por parte del servidor nos sale:

![image.png](img/image%205.png)

Hasta el momento tenemos el siguiente diagrama 

![image.png](img/image%206.png)

## 3. Creando al segundo jugador

Vamos a crear un segundo jugador y tambien diferenciarlo del segundo jugador con el color verde, para ello vamos a realizar los siguientes pasos:

1. Modificaremos `MarioServer.java` para manejar multiples jugadores y sus posiciones.
2. Modificaremos  `MarioClient.java` para recibir y dibujar las posiciones de todos los jugadores.

### Paso 1: Modificar `MarioServer.java`

Ahora, vamos a actualizar el código del servidor para manejar múltiples jugadores. Modificaremos la clase ClientHandler para asignar un color a cada jugador y mantener un registro de sus posiciones. Esto nos permitirá sincronizar la información de todos los jugadores conectados y enviarla a cada cliente.

```java
import java.util.*;
import java.io.*;
import java.net.*;

public class MarioServer {
    private static final int PORT = 5684; // Este es el puerto que se va a utilizar para la comunicación
    private static List<ClientHandler> clients = new ArrayList<>(); // Lista de clientes conectados
    private static Map<ClientHandler, PlayerInfo> playerInfo = new HashMap<>();
    private static String[] colors = {"RED", "BLUE", "GREEN", "YELLOW", "ORANGE", "PINK", "CYAN", "MAGENTA"};
    private static int colorIndex = 0; // Índice del color actual

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
```

### Paso 2: Modificar `ClientHandler.java`

Ahora, necesitamos modificar la clase ClientHandler para manejar la información de los jugadores y sincronizarla entre todos los clientes conectados. 

Aquí está el código modificado para la clase ClientHandler:

```java
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
            client.out.println(message.toString());
        }
    }
}
```

Ahora que hemos modificado tanto el servidor como el manejador de clientes, es momento de actualizar el cliente para que pueda manejar múltiples jugadores. Esto implicará cambios en la forma en que recibimos y procesamos los mensajes del servidor, así como en cómo dibujamos a los jugadores en la pantalla. Vamos a realizar estos cambios paso a paso para asegurarnos de que todo funcione correctamente.

Ahora, vamos a modificar el cliente para que pueda manejar múltiples jugadores. Esto implicará cambios significativos en la clase MarioClient.java. Primero, necesitaremos actualizar la forma en que recibimos y procesamos los mensajes del servidor para manejar la información de todos los jugadores. Luego, modificaremos el método de dibujo para representar a cada jugador con su color correspondiente en la pantalla.

```java
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
```

Este código actualizado permite que el cliente maneje múltiples jugadores, cada uno con su propio color. La clase ahora mantiene un mapa de jugadores, actualiza sus posiciones según los mensajes del servidor, y los dibuja en el panel de juego con sus respectivos colores.

## Creando el mapa

Para crear un mapa más interesante y desafiante para nuestro juego de Mario Bros Multijugador, podemos agregar plataformas y obstáculos. Esto no solo hará que el juego sea visualmente más atractivo, sino que también proporcionará una experiencia de juego más rica y divertida. Implementaremos estas características paso a paso, comenzando con la adición de plataformas simples.

Para implementar las plataformas, primero necesitamos crear una clase Platform que represente cada plataforma en el juego. Luego, modificaremos la clase MarioClient para incluir una lista de plataformas y actualizaremos el método de dibujo para representarlas en la pantalla. Finalmente, ajustaremos la lógica de movimiento de los jugadores para que no puedan atravesar las plataformas, implementando así una física básica de colisiones.

Ahora, vamos a implementar estas mejoras paso a paso:

1. Crear la clase Platform
2. Modificar MarioClient para incluir plataformas
3. Actualizar la lógica de movimiento y colisiones

### Paso 1: Crear la clase Platform

Primero, crearemos una clase simple llamada Platform que representará cada plataforma en nuestro juego. Esta clase contendrá las coordenadas y dimensiones de la plataforma:

```java
public class Platform {
    int x, y, width, height;

    public Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
```

### Paso 2: Modificar MarioClient

Ahora, modificaremos la clase MarioClient para incluir una lista de plataformas y actualizaremos el método de dibujo para representarlas en la pantalla:

Aquí están los cambios necesarios en la clase MarioClient:

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class MarioClient extends JFrame implements KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String HOST = "172.17.32.23";
    private static final int PORT = 5684;
    private List<Platform> plataforms;

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
        initializePlataforms();
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

            for(Platform platform : plataforms){
                g.setColor(getColorFromString(platform.color));
                g.fillRect(platform.x, platform.y, platform.width, platform.height);
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
```

![image.png](img/image%207.png)

## Añadiendo físicas al juego

Para añadir físicas básicas a nuestro juego de Mario Bros Multijugador, necesitamos implementar la detección de colisiones entre los jugadores y las plataformas. Esto incluirá la gravedad, para que los personajes caigan cuando no estén sobre una plataforma, y la capacidad de saltar. Además, debemos asegurarnos de que los jugadores no puedan atravesar las plataformas, lo que hará que el juego sea más realista y desafiante

Implementaremos estas físicas paso a paso:

1. Añadir gravedad a los personajes
2. Implementar detección de colisiones con las plataformas
3. Agregar la capacidad de saltar

Comencemos modificando la clase Player para incluir velocidad vertical y un estado de salto:

Aquí está la modificación de la clase Player para incluir velocidad vertical y estado de salto:

```java
private static class Player {
    int x, y;
    String color;
    double velocityY;
    boolean isJumping;

    Player(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.velocityY = 0;
        this.isJumping = false;
    }
}
```

Ahora, implementaremos la gravedad y la detección de colisiones en el método keyPressed de la clase MarioClient:

Ahora, modificaremos el método keyPressed en la clase MarioClient para implementar la gravedad y la detección de colisiones:

```java
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
```

A continuación, implementaremos los métodos applyGravity y checkCollisions:

Aquí están los métodos applyGravity y checkCollisions que implementaremos en la clase MarioClient:

```java
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
```

Estos métodos implementan la gravedad básica y la detección de colisiones con las plataformas, permitiendo que los personajes caigan, salten y se detengan en las plataformas.

## Creando los enemigo

Para añadir un enemigo a nuestro juego de Mario Bros Multijugador, crearemos una nueva clase llamada Enemy. Esta clase representará a los enemigos que se moverán por las plataformas y que los jugadores deberán evitar. Implementaremos un movimiento básico para el enemigo y la lógica para que los jugadores pierdan si entran en contacto con él.

Primero, crearemos la clase Enemy con propiedades básicas como posición, velocidad y dirección. Luego, implementaremos un método para actualizar la posición del enemigo y otro para detectar colisiones con los jugadores. Finalmente, integraremos esta nueva clase en nuestro juego principal, MarioClient, para manejar la lógica del enemigo y su interacción con los jugadores.

Ahora, implementaremos la clase Enemy:

```java
public class Enemy {
    private int x, y;
    private int width = 30;
    private int height = 30;
    private int speed = 2;
    private int direction = 1; // 1 para derecha, -1 para izquierda

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
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
               y + height == platform.y;
    }

    public boolean collidesWithPlayer(Player player) {
        return x < player.x + 30 &&
               x + width > player.x &&
               y < player.y + 30 &&
               y + height > player.y;
    }

    // Getters y setters según sea necesario
}
```

Esta clase Enemy implementa el movimiento básico del enemigo y la detección de colisiones con los jugadores. Ahora, integraremos esta clase en nuestro juego principal, MarioClient.

Para integrar la clase Enemy en MarioClient, necesitaremos crear una lista de enemigos, actualizarlos en cada frame del juego, y manejar las colisiones con los jugadores. Además, deberemos dibujar los enemigos en el panel del juego y agregar lógica para que los jugadores pierdan vidas o reinicien su posición al colisionar con un enemigo. Implementaremos estos cambios paso a paso para asegurar un funcionamiento correcto del juego.

Ahora, modificaremos la clase MarioClient para incluir la lógica de los enemigos. Primero, añadiremos una lista para almacenar los enemigos y un método para inicializarlos. Luego, actualizaremos el método de pintura para dibujar los enemigos en el panel del juego. Finalmente, implementaremos la lógica de colisión entre jugadores y enemigos, y el manejo de las vidas de los jugadores.

Los cambios que haremos en la clase MarioClient para integrar los enemigos:

1. Añadir una lista para almacenar los enemigos
2. Crear un método para inicializar los enemigos
3. Actualizar el método paintComponent para dibujar los enemigos
4. Implementar la lógica de colisión entre jugadores y enemigos
5. Manejar las vidas de los jugadores

Ahora implementaremos estos cambios en la clase MarioClient:

```java
public class MarioClient extends JFrame implements KeyListener {
    // ... (código existente)

    private List<Enemy> enemies;
    private static final int INITIAL_LIVES = 3;
    private Map<String, Integer> playerLives;

    public MarioClient() {
        // ... (código existente)
        enemies = new ArrayList<>();
        playerLives = new HashMap<>();
        initializeEnemies();
        initializePlayerLives();
    }

    private void initializeEnemies() {
        enemies.add(new Enemy(100, HEIGHT - 60));
        enemies.add(new Enemy(300, HEIGHT - 120));
    }

    private void initializePlayerLives() {
        for (String color : players.keySet()) {
            playerLives.put(color, INITIAL_LIVES);
        }
    }

    // ... (resto del código)
}
```

Estos cambios inicializan la lista de enemigos y las vidas de los jugadores. A continuación, actualizaremos el método paintComponent para dibujar los enemigos y mostrar las vidas de los jugadores.

Ahora actualizaremos el método paintComponent para dibujar los enemigos y mostrar las vidas de los jugadores:

```java
@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    // ... (código existente para dibujar plataformas y jugadores)

    // Dibujar enemigos
    g.setColor(Color.RED);
    for (Enemy enemy : enemies) {
        g.fillRect(enemy.getX(), enemy.getY(), 30, 30);
    }

    // Mostrar vidas de los jugadores
    g.setColor(Color.WHITE);
    int y = 20;
    for (Map.Entry<String, Integer> entry : playerLives.entrySet()) {
        g.drawString(entry.getKey() + ": " + entry.getValue() + " vidas", 10, y);
        y += 20;
    }
}
```

Este código actualiza el método paintComponent para dibujar los enemigos como rectángulos rojos y mostrar las vidas de los jugadores en la pantalla. A continuación, implementaremos la lógica de colisión entre jugadores y enemigos, y el manejo de las vidas de los jugadores.

Ahora implementaremos la lógica de colisión entre jugadores y enemigos en el método update de la clase MarioClient. Este método se encargará de mover los enemigos, detectar colisiones y actualizar las vidas de los jugadores. También añadiremos un método para reiniciar la posición de un jugador cuando pierda una vida.

Aquí está la implementación del método update y el método para reiniciar la posición de un jugador:

```java
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
                    // Implementar lógica de fin del juego para este jugador
                }
            }
        }
    }
}

private void resetPlayerPosition(Player player) {
    // Reiniciar la posición del jugador a un punto seguro
    player.x = 50;
    player.y = HEIGHT - 60;
    player.velocityY = 0;
    player.isJumping = false;
}
```

Estos métodos manejan la lógica de colisión entre jugadores y enemigos, actualizan las vidas de los jugadores y reinician sus posiciones cuando pierden una vida. Ahora, solo necesitamos llamar al método update en el bucle principal del juego para que estas mecánicas funcionen correctamente.

Para finalizar la implementación de nuestro juego Mario Bros Multijugador, necesitamos integrar el método update en el bucle principal. Esto asegurará que las colisiones, el movimiento de los enemigos y la actualización de las vidas de los jugadores se manejen correctamente en cada frame del juego. Añadiremos una llamada al método update dentro del bucle principal, justo antes de repintar el panel del juego.

```java
public void run() {
    while (true) {
        update(); // Llamada al nuevo método update
        repaint();
        try {
            Thread.sleep(1000 / 60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

Con esta adición, nuestro juego Mario Bros Multijugador ahora incluye enemigos que se mueven, colisiones entre jugadores y enemigos, y un sistema de vidas. Los jugadores perderán vidas al tocar a los enemigos y el juego manejará correctamente estas interacciones en cada frame.

## Paralelizando el servidor

Para mejorar el rendimiento y la capacidad de nuestro juego Mario Bros Multijugador, implementaremos la paralelización en el servidor. Esto nos permitirá manejar múltiples conexiones de clientes de manera más eficiente y reducir la latencia en el juego. Utilizaremos hilos (threads) para procesar las solicitudes de los clientes de forma concurrente, lo que mejorará la escalabilidad de nuestro servidor.

Para implementar la paralelización en nuestro servidor, seguiremos estos pasos:

1. Crear un pool de hilos para manejar las conexiones de los clientes.
2. Modificar el bucle principal del servidor para aceptar conexiones y asignarlas a hilos del pool.
3. Implementar una clase Runnable para procesar las solicitudes de los clientes en paralelo.

Ahora implementaremos estos pasos para paralelizar nuestro servidor de Mario Bros Multijugador. Comenzaremos creando una clase ClientHandler que implementará la interfaz Runnable. Esta clase se encargará de manejar las comunicaciones con un cliente específico en un hilo separado.

Primero, implementaremos la clase ClientHandler que se encargará de manejar las comunicaciones con cada cliente en un hilo separado. Esta clase implementará la interfaz Runnable y contendrá la lógica necesaria para procesar las solicitudes del cliente y enviar actualizaciones del estado del juego. Luego, modificaremos la clase principal del servidor para utilizar un ExecutorService que gestionará el pool de hilos y asignará las conexiones entrantes a los ClientHandlers disponibles.

```java
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private GameState gameState;

    public ClientHandler(Socket socket, GameState gameState) {
        this.clientSocket = socket;
        this.gameState = gameState;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Procesar la entrada del cliente y actualizar el estado del juego
                processInput(inputLine);
                // Enviar actualizaciones del estado del juego a todos los clientes
                broadcastGameState();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processInput(String input) {
        // Implementar la lógica para procesar la entrada del cliente
        // y actualizar el estado del juego
    }

    private void broadcastGameState() {
        // Implementar la lógica para enviar actualizaciones del estado del juego
        // a todos los clientes conectados
    }
}
```

Esta clase ClientHandler maneja la comunicación con un cliente específico en un hilo separado. Ahora, modificaremos la clase principal del servidor para utilizar un ExecutorService y asignar las conexiones entrantes a los ClientHandlers: