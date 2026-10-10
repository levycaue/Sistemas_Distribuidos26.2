package serialization;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorSerializacao {
    public static void main(String[] args) {
        int porta = 12345;
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            System.out.println("Servidor de Vendas de Telefones rodando na porta " + porta);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> {
                    try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                         ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
                        
                        MensagemDTO req = (MensagemDTO) ois.readObject();
                        System.out.println("Requisição recebida -> Op: " + req.getOperacao() + " | Dados: " + req.getPayload());

                        MensagemDTO rep = new MensagemDTO("REPLY", "Venda processada com sucesso para: " + req.getPayload());
                        oos.writeObject(rep);
                        oos.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}