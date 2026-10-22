package rmi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import interfaces.Vendas;
import java.net.DatagramPacket;
import model.Pedido;
import model.Produto;
import model.Vendedor;

public class VendasDispatcher {
    private Vendas servicoReal;
    private ProtocoloRPC protocolo;
    private Gson gson;

    public VendasDispatcher(Vendas servicoReal, ProtocoloRPC protocolo) {
        this.servicoReal = servicoReal;
        this.protocolo = protocolo;
        this.gson = protocolo.getGson();
    }

    public void escutar() {
        System.out.println("Servidor RMI/RPC aguardando requisições...");
        while (true) {
            DatagramPacket requestPacket = protocolo.getRequest();
            String jsonRequest = new String(requestPacket.getData(), 0, requestPacket.getLength());
            MensagemRPC request = gson.fromJson(jsonRequest, MensagemRPC.class);
            
            byte[] resultadoBytes = null;

            if ("ServicoVendas".equals(request.objectReference)) {
                try {
                    switch (request.methodId) {
                        case "processarVenda": {
                            JsonArray args = gson.fromJson(new String(request.arguments), JsonArray.class);
                            Vendedor v = gson.fromJson(args.get(0), Vendedor.class);
                            Produto p = gson.fromJson(args.get(1), Produto.class); 
                            int qtd = args.get(2).getAsInt();
                            boolean sucesso = servicoReal.processarVenda(v, p, qtd);
                            resultadoBytes = gson.toJson(sucesso).getBytes();
                            break;
                        }
                        case "consultarEstoque": {
                            JsonArray args = gson.fromJson(new String(request.arguments), JsonArray.class);
                            int idProd = args.get(0).getAsInt();
                            Produto p = servicoReal.consultarEstoque(idProd);
                            resultadoBytes = gson.toJson(p).getBytes();
                            break;
                        }
                        case "listarProdutosEstoque": {
                            String lista = servicoReal.listarProdutosEstoque();
                            resultadoBytes = gson.toJson(lista).getBytes();
                            break;
                        }
                        case "confirmarPedido": {
                            JsonArray args = gson.fromJson(new String(request.arguments), JsonArray.class);
                            Vendedor v = gson.fromJson(args.get(0), Vendedor.class);
                            Produto p = gson.fromJson(args.get(1), Produto.class); 
                            int qtd = args.get(2).getAsInt();
                            Pedido pedido = servicoReal.confirmarPedido(v, p, qtd);
                            resultadoBytes = gson.toJson(pedido).getBytes();
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    resultadoBytes = gson.toJson(null).getBytes();
                }
            }

            MensagemRPC reply = new MensagemRPC(1, request.requestId, request.objectReference, request.methodId, resultadoBytes);
            protocolo.sendReply(gson.toJson(reply).getBytes(), requestPacket.getAddress(), requestPacket.getPort());
        }
    }
}