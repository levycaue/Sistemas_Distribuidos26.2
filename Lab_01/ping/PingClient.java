import java.io.*;
import java.net.*;
import java.util.*;

public class PingClient {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Required arguments: host port");
            return;
        }

        String serverHost = args[0];
        int serverPort = Integer.parseInt(args[1]);

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(1000);

        InetAddress serverAddress = InetAddress.getByName(serverHost);

        long minRTT = Long.MAX_VALUE;
        long maxRTT = 0;
        long totalRTT = 0;
        int pacotesRecebidos = 0;

        for (int i = 0; i < 10; i++) {
            long sendTime = System.currentTimeMillis();
            String message = "PING " + i + " " + sendTime + "\r\n";
            byte[] sendData = message.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            socket.send(sendPacket);

            try {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                
                socket.receive(receivePacket);
                long receiveTime = System.currentTimeMillis();
                long rtt = receiveTime - sendTime;

                minRTT = Math.min(minRTT, rtt);
                maxRTT = Math.max(maxRTT, rtt);
                totalRTT += rtt;
                pacotesRecebidos++;

                String replyData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.print("Resposta do servidor: " + replyData.trim());
                System.out.println(" | RTT: " + rtt + " ms");

            } catch (SocketTimeoutException e) {
                System.out.println("Ping " + i + ": Tempo limite excedido (pacote perdido).");
            }
        }
        
        socket.close();

        System.out.println("\n--- Estatísticas do Ping ---");
        if (pacotesRecebidos > 0) {
            System.out.println("RTT Mínimo: " + minRTT + " ms");
            System.out.println("RTT Máximo: " + maxRTT + " ms");
            System.out.println("RTT Médio: " + (totalRTT / pacotesRecebidos) + " ms");
        } else {
            System.out.println("Nenhum pacote foi recebido com sucesso.");
        }
    }
}