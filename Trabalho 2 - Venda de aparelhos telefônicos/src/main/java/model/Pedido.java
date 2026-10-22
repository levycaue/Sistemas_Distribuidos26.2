package model;
import java.io.Serializable;
import java.util.UUID;

public class Pedido implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String idPedido;
    private Vendedor vendedor; // Agregação (tem-um)
    private Produto produto;   // Agregação (tem-um)
    private int quantidade;
    
    public Pedido() {}

    public Pedido(Vendedor vendedor, Produto produto, int quantidade) {
        this.idPedido = UUID.randomUUID().toString();
        this.vendedor = vendedor;
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public String getIdPedido() { return idPedido; }
    public Vendedor getVendedor() { return vendedor; }
    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }

    @Override
    public String toString() {
        return "Pedido{idPedido='" + idPedido + "', vendedor=" + vendedor.getNome() + ", produto=" + produto.getNome() + ", quantidade=" + quantidade + "}";
    }
}
