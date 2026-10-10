package multicast;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class ServidorMulticast {
    private static final String IP_MULTICAST = "230.0.0.1";
    private static final int PORTA_MULTICAST = 5000;
    private static final int PORTA_TCP = 6000;

    public static void main(String[] args) {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORTA_TCP)) {
                System.out.println("Servidor TCP de autenticação ouvindo na porta " + PORTA_TCP);
                while (true) {
                    Socket cliente = serverSocket.accept();
                    System.out.println("Vendedor conectado e autenticado via TCP: " + cliente.getRemoteSocketAddress());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress grupo = InetAddress.getByName(IP_MULTICAST);
            System.out.println("Servidor Multicast ativo em " + IP_MULTICAST + ":" + PORTA_MULTICAST);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Digite o texto da notificação (ex: ALERTA de estoque baixo): ");
                String msgTexto = scanner.nextLine();
                
                String json = "{\n  \"tipo\": \"ALERTA\",\n  \"mensagem\": \"" + msgTexto + "\",\n  \"timestamp\": " + System.currentTimeMillis() + "\n}";
                
                byte[] buffer = json.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, grupo, PORTA_MULTICAST);
                socket.send(packet);
                System.out.println("Mensagem multicast disparada com sucesso!\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}