package interfaces;
import model.Produto;
import model.Vendedor;
import model.Pedido;

// Interface remota do Serviço
public interface Vendas {
    // Requisito: 4 métodos para invocação remota
    boolean processarVenda(Vendedor vendedor, Produto produto, int quantidade);
    Produto consultarEstoque(int idProduto);
    String listarProdutosEstoque();
    Pedido confirmarPedido(Vendedor vendedor, Produto produto, int quantidade);
}
