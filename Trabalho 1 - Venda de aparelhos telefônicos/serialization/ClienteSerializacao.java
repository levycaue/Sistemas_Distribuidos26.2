package serialization;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteSerializacao {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            MensagemDTO req = new MensagemDTO("REGISTRAR_VENDA", "Celular Samsung Galaxy S24 - Vendedor: Levy Cauê");
            oos.writeObject(req);
            oos.flush();

            // Desempacota reply
            MensagemDTO rep = (MensagemDTO) ois.readObject();
            System.out.println("Resposta do Servidor: [" + rep.getOperacao() + "] " + rep.getPayload());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}