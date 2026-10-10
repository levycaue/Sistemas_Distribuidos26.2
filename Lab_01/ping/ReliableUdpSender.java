import java.net.*;

public class ReliableUdpSender {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Required arguments: host port");
            return;
        }
        
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        InetAddress address = InetAddress.getByName(host);
        
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(1000);

        String[] mensagens = {"Mensagem A", "Mensagem B", "Mensagem C"};
        int seqNum = 0; 

        for (String msg : mensagens) {
            boolean ackRecebido = false;
            String pacote = seqNum + " " + msg;
            byte[] sendData = pacote.getBytes();

            while (!ackRecebido) {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
                socket.send(sendPacket);
                System.out.println("Enviando: " + pacote);

                try {
                    byte[] ackBuffer = new byte[1024];
                    DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
                    socket.receive(ackPacket);

                    String ack = new String(ackPacket.getData(), 0, ackPacket.getLength()).trim();
                    if (ack.equals("ACK " + seqNum)) {
                        System.out.println("Sucesso! Confirmação recebida: " + ack + "\n");
                        ackRecebido = true; 
                        seqNum++; 
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Timeout detectado! Retransmitindo o pacote " + seqNum + "...");
                }
            }
        }
        socket.close();
        System.out.println("Todas as mensagens foram entregues de forma confiável!");
    }
}