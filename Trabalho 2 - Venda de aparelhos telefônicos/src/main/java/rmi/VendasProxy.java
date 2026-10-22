package rmi;

import com.google.gson.Gson;
import interfaces.Vendas;
import model.Celular;
import model.Pedido;
import model.Produto;
import model.Vendedor;

// Classe Proxy do Cliente que encapsula as chamadas de rede via doOperation
public class VendasProxy implements Vendas {
    private ProtocoloRPC protocolo;
    private String objectReference = "ServicoVendas"; // Nome do serviço remoto
    private Gson gson = new Gson();

    public VendasProxy(ProtocoloRPC protocolo) {
        this.protocolo = protocolo;
    }

    @Override
    public boolean processarVenda(Vendedor vendedor, Produto produto, int quantidade) {
        Object[] args = {vendedor, produto, quantidade};
        byte[] argsBytes = gson.toJson(args).getBytes();
        
        byte[] reply = protocolo.doOperation(objectReference, "processarVenda", argsBytes);
        return gson.fromJson(new String(reply), Boolean.class);
    }

    
    @Override
    public Produto consultarEstoque(int idProduto) {
        Object[] args = {idProduto};
        byte[] argsBytes = gson.toJson(args).getBytes();
        
        byte[] reply = protocolo.doOperation(objectReference, "consultarEstoque", argsBytes);
        return gson.fromJson(new String(reply), Celular.class); 
    }

    @Override
    public String listarProdutosEstoque() {
        byte[] reply = protocolo.doOperation(objectReference, "listarProdutosEstoque", "{}".getBytes());
        return gson.fromJson(new String(reply), String.class);
    }

    @Override
    public Pedido confirmarPedido(Vendedor vendedor, Produto produto, int quantidade) {
        Object[] args = {vendedor, produto, quantidade};
        byte[] argsBytes = gson.toJson(args).getBytes();
        
        byte[] reply = protocolo.doOperation(objectReference, "confirmarPedido", argsBytes);
        return gson.fromJson(new String(reply), Pedido.class);
    }
}
