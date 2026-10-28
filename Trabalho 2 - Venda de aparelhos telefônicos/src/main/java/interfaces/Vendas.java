package interfaces;
import model.Produto;
import model.Vendedor;
import model.Pedido;

public interface Vendas {
    boolean processarVenda(Vendedor vendedor, Produto produto, int quantidade);
    Produto consultarEstoque(int idProduto);
    String listarProdutosEstoque();
    Pedido confirmarPedido(Vendedor vendedor, Produto produto, int quantidade);
}
