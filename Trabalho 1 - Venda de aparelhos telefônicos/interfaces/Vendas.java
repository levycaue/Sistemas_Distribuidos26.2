package interfaces;

import model.Produto;
import model.Vendedor;

public interface Vendas {
    boolean processarVenda(Vendedor vendedor, Produto produto, int quantidade);
    Produto consultarEstoque(int idProduto);
}