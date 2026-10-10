import java.net.*;
import java.util.Random;

public class ReliableUdpReceiver {
    private static final double LOSS_RATE = 0.3; // Simulando 30% de perda (Exercício 3)

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Required arguments: port");
            return;
        }
        int port = Integer.parseInt(args[0]);
        DatagramSocket socket = new DatagramSocket(port);
        Random random = new Random();
        
        System.out.println("Receiver Confiável iniciado na porta " + port);

        while (true) {
            byte[] buffer = new byte[1024];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socket.receive(request);

            String data = new String(request.getData(), 0, request.getLength()).trim();
            System.out.println("Recebido: " + data);

            if (random.nextDouble() < LOSS_RATE) {
                System.out.println(" -> ACK perdido artificialmente! (Sender terá que retransmitir)");
                continue;
            }

            String[] parts = data.split(" ", 2);
            String ackMessage = "ACK " + parts[0]; 
            byte[] ackData = ackMessage.getBytes();

            DatagramPacket reply = new DatagramPacket(ackData, ackData.length, request.getAddress(), request.getPort());
            socket.send(reply);
            System.out.println(" -> " + ackMessage + " enviado ao remetente.");
        }
    }
}