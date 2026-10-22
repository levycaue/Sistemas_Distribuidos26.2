package rmi;

import com.google.gson.Gson;
import java.net.*;

public class ProtocoloRPC {
    private DatagramSocket socket;
    private Gson gson;
    private InetAddress serverAddress;
    private int serverPort;

    // Construtor para Cliente
    public ProtocoloRPC(String serverHost, int serverPort) throws Exception {
        this.socket = new DatagramSocket(); // Porta aleatória para o cliente
        this.serverAddress = InetAddress.getByName(serverHost);
        this.serverPort = serverPort;
        this.gson = new Gson();
    }

    // Construtor para Servidor
    public ProtocoloRPC(int port) throws Exception {
        this.socket = new DatagramSocket(port);
        this.gson = new Gson();
    }

    // Cliente envia requisição e espera resposta (Section 5.2 Coulouris)
    public byte[] doOperation(String objectRef, String methodId, byte[] arguments) {
        try {
            // Gerar requestId simulado (pode ser incrementado no proxy)
            MensagemRPC request = new MensagemRPC(0, (int)(Math.random()*1000), objectRef, methodId, arguments);
            String jsonRequest = gson.toJson(request);
            byte[] reqBytes = jsonRequest.getBytes();
            
            DatagramPacket reqPacket = new DatagramPacket(reqBytes, reqBytes.length, serverAddress, serverPort);
            socket.send(reqPacket);

            // Aguardar Resposta
            byte[] buffer = new byte[8192];
            DatagramPacket repPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(repPacket);

            MensagemRPC reply = gson.fromJson(new String(repPacket.getData(), 0, repPacket.getLength()), MensagemRPC.class);
            return reply.arguments;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Servidor escuta requisições
    public DatagramPacket getRequest() {
        try {
            byte[] buffer = new byte[8192];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            return packet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Servidor envia resposta
    public void sendReply(byte[] replyBytes, InetAddress clientHost, int clientPort) {
        try {
            DatagramPacket packet = new DatagramPacket(replyBytes, replyBytes.length, clientHost, clientPort);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Gson getGson() {
        return gson;
    }
}
