package rmi;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.google.gson.Gson;

public class ProtocoloRPC {
    private DatagramSocket socket;
    private Gson gson;
    private InetAddress serverAddress;
    private int serverPort;

    public ProtocoloRPC(String serverHost, int serverPort) throws Exception {
        this.socket = new DatagramSocket(); // Porta aleatória para o cliente
        this.serverAddress = InetAddress.getByName(serverHost);
        this.serverPort = serverPort;
        this.gson = new Gson();
    }

    public ProtocoloRPC(int port) throws Exception {
        this.socket = new DatagramSocket(port);
        this.gson = new Gson();
    }

    public byte[] doOperation(String objectRef, String methodId, byte[] arguments) {
        try {
            MensagemRPC request = new MensagemRPC(0, (int)(Math.random()*1000), objectRef, methodId, arguments);
            String jsonRequest = gson.toJson(request);
            byte[] reqBytes = jsonRequest.getBytes();
            
            DatagramPacket reqPacket = new DatagramPacket(reqBytes, reqBytes.length, serverAddress, serverPort);
            socket.send(reqPacket);

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
