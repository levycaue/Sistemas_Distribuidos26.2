package server;

import interfaces.Vendas;
import java.util.HashMap;
import java.util.Map;
import model.Celular;
import model.Pedido;
import model.Produto;
import model.Vendedor;

// Implementação real do serviço remoto
public class ServicoVendasImpl implements Vendas {
    private Map<Integer, Produto> estoque = new HashMap<>();

    public ServicoVendasImpl() {
        estoque.put(1, new Celular(1, "Galaxy S24", 5000.0, "Samsung", 256));
    }

    @Override
    public boolean processarVenda(Vendedor vendedor, Produto produto, int quantidade) {
        System.out.println("[SERVER] Venda Processada! Vendedor: " + vendedor.getNome() + " | Produto: " + produto.getNome() + " | Qtd: " + quantidade);
        return true;
    }

    @Override
    public Produto consultarEstoque(int idProduto) {
        System.out.println("[SERVER] Consultando estoque do produto ID: " + idProduto);
        return estoque.get(idProduto);
    }

    @Override
    public String listarProdutosEstoque() {
        System.out.println("[SERVER] Listando estoque...");
        return estoque.toString();
    }

    @Override
    public Pedido confirmarPedido(Vendedor vendedor, Produto produto, int quantidade) {
        System.out.println("[SERVER] Registrando pedido para " + vendedor.getNome() + " | Produto: " + produto.getNome() + " | Qtd: " + quantidade);
        return new Pedido(vendedor, produto, quantidade);
    }
}
