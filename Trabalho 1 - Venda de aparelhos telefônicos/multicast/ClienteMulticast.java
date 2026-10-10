package multicast;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class ClienteMulticast {
    private static final String IP_MULTICAST = "230.0.0.1";
    private static final int PORTA_MULTICAST = 5000;
    private static final int PORTA_TCP = 6000;

    public static void main(String[] args) {
        try {
            Socket socketTcp = new Socket("localhost", PORTA_TCP);
            System.out.println("Conexão TCP estabelecida para autenticação.");
        } catch (IOException e) {
            System.err.println("Erro na autenticação TCP: " + e.getMessage());
            return;
        }

        Thread threadEscuta = new Thread(() -> {
            try (MulticastSocket socketUdp = new MulticastSocket(PORTA_MULTICAST)) {
                InetAddress grupo = InetAddress.getByName(IP_MULTICAST);
                InetSocketAddress enderecoGrupo = new InetSocketAddress(grupo, PORTA_MULTICAST);
                NetworkInterface netInterface = NetworkInterface.getByName("lo"); // Ajustar interface de rede se necessário

                socketUdp.joinGroup(enderecoGrupo, netInterface);
                System.out.println("Entrou no grupo multicast: " + IP_MULTICAST);

                byte[] buffer = new byte[1024];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socketUdp.receive(packet);
                    String mensagem = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("\n[MENSAGEM MULTICAST RECEBIDA]:\n" + mensagem);
                    System.out.print("Pressione Enter para sair ou continuar: ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        threadEscuta.start();

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine(); // Mantém a aplicação rodando
        System.out.println("Encerrando cliente...");
        System.exit(0);
    }
}