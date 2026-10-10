import java.net.*;
import java.util.*;

public class PingClientTimer {
    static int sequence = 0;
    static int pacotesRecebidos = 0;
    static long minRTT = Long.MAX_VALUE;
    static long maxRTT = 0;
    static long totalRTT = 0;

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Required arguments: host port");
            return;
        }

        String serverHost = args[0];
        int serverPort = Integer.parseInt(args[1]);
        InetAddress serverAddress = InetAddress.getByName(serverHost);
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(1000); 

        Timer timer = new Timer();
        System.out.println("Iniciando envios com Timer (1 Ping por segundo)...");

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (sequence >= 10) {
                    timer.cancel();
                    socket.close();
                    System.out.println("\n--- Estatísticas do Ping ---");
                    if (pacotesRecebidos > 0) {
                        System.out.println("RTT Mínimo: " + minRTT + " ms");
                        System.out.println("RTT Máximo: " + maxRTT + " ms");
                        System.out.println("RTT Médio: " + (totalRTT / pacotesRecebidos) + " ms");
                    } else {
                        System.out.println("Nenhum pacote foi recebido com sucesso.");
                    }
                    System.exit(0);
                }

                try {
                    long sendTime = System.currentTimeMillis();
                    String message = "PING " + sequence + " " + sendTime + "\r\n";
                    byte[] sendData = message.getBytes();

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                    socket.send(sendPacket);

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
                    System.out.println("Resposta: " + replyData.trim() + " | RTT: " + rtt + " ms");

                } catch (SocketTimeoutException e) {
                    System.out.println("Ping " + sequence + ": Tempo limite excedido (pacote perdido).");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sequence++;
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }
}