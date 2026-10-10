package client;

import model.Candidato;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Scanner;

public class EleitorClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 7000;
    private static final String IP_MULTICAST = "230.0.0.2";
    private static final int PORTA_MULTICAST = 6000;

    public static void main(String[] args) {
        // Thread para escutar notas informativas via Multicast UDP
        Thread threadMulticast = new Thread(() -> {
            try (MulticastSocket socketUdp = new MulticastSocket(PORTA_MULTICAST)) {
                InetAddress grupo = InetAddress.getByName(IP_MULTICAST);
                InetSocketAddress enderecoGrupo = new InetSocketAddress(grupo, PORTA_MULTICAST);
                NetworkInterface netInterface = NetworkInterface.getByName("lo"); // Ajustar interface de rede se necessário

                socketUdp.joinGroup(enderecoGrupo, netInterface);
                byte[] buffer = new byte[1024];

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socketUdp.receive(packet);
                    String mensagem = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("\n\n[NOTA INFORMATIVA DO ADMINISTRADOR - MULTICAST]:\n" + mensagem);
                    System.out.print("Escolha uma opção (ou Enter para sair): ");
                }
            } catch (Exception e) {
                // Tratamento de saída do grupo
            }
        });
        threadMulticast.start();

        // Comunicação TCP Unicast para login e votação
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            oos.writeObject("ELEITOR");
            oos.flush();

            Object respostaServidor = ois.readObject();
            if (respostaServidor instanceof String) {
                System.out.println((String) respostaServidor);
                return;
            }

            @SuppressWarnings("unchecked")
            List<Candidato> candidatos = (List<Candidato>) respostaServidor;
            System.out.println("\n--- CANDIDATOS EM VOTAÇÃO ---");
            for (Candidato c : candidatos) {
                System.out.println("[" + c.getId() + "] " + c.getNome());
            }

            System.out.print("Digite o ID do candidato em que deseja votar: ");
            int votoId = scanner.nextInt();
            oos.writeInt(votoId);
            oos.flush();

            String resultadoVoto = (String) ois.readObject();
            System.out.println("\n" + resultadoVoto);

        } catch (Exception e) {
            System.err.println("Erro na conexão com o servidor: " + e.getMessage());
        }
        System.exit(0);
    }
}