import java.io.*;
import java.net.*;
import java.util.*;

public class PingServer {
    private static final double LOSS_RATE = 0.3;
    private static final int AVERAGE_DELAY = 100; // ms
    private static DatagramSocket socket;

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Required arguments: port");
            return;
        }
        int port = Integer.parseInt(args[0]);
        
        Random random = new Random();
        socket = new DatagramSocket(port);
        System.out.println("Servidor iniciado na porta " + port);

        while (true) {
            byte[] buffer = new byte[1024];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            
            socket.receive(request);
            
            printData(request);
            
            if (random.nextDouble() < LOSS_RATE) {
                System.out.println("Reply not sent.");
                continue;
            }
            
            Thread.sleep((int) (random.nextDouble() * 2 * AVERAGE_DELAY));
            
            InetAddress clientHost = request.getAddress();
            int clientPort = request.getPort();
            byte[] buf = request.getData();
            DatagramPacket reply = new DatagramPacket(buf, buf.length, clientHost, clientPort);
            socket.send(reply);
            System.out.println("Reply sent.");
        }
    }

    private static void printData(DatagramPacket request) throws Exception {
        byte[] buf = request.getData();
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        InputStreamReader isr = new InputStreamReader(bais);
        BufferedReader br = new BufferedReader(isr);
        String line = br.readLine();
        System.out.println("Received from " + request.getAddress().getHostAddress() + ":" + request.getPort() + " - " + line);
    }
}