package server;

import rmi.ProtocoloRPC;
import rmi.VendasDispatcher;

public class ServidorMain {
    public static void main(String[] args) {
        try {
            // Servidor escuta na porta 7070
            ProtocoloRPC protocolo = new ProtocoloRPC(7070);
            ServicoVendasImpl servicoReal = new ServicoVendasImpl();
            
            VendasDispatcher dispatcher = new VendasDispatcher(servicoReal, protocolo);
            dispatcher.escutar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}