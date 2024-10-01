import java.net.InetAddress;
import java.net.UnknownHostException;

public class GetServerIP {
    public static void main(String[] args) {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("La dirección IP de esta máquina es: " + ip.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}