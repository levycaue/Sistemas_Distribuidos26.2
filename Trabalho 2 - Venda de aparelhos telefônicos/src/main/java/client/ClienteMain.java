package client;

import java.util.Scanner;

import interfaces.Vendas;
import model.Capa;
import model.Celular;
import model.Pedido;
import model.Pelicula;
import model.PowerBank;
import model.Produto;
import model.Vendedor;
import mq.FilaBroker;
import rmi.ProtocoloRPC;
import rmi.VendasProxy;

public class ClienteMain {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // A conexão direta só é criada quando uma opção RPC é selecionada.
            Vendas proxy = null;

            System.out.println("=== SISTEMA DE VENDAS (RPC + FILA INDIRETA) ===");
            System.out.print("Informe o ID do Vendedor: ");
            int idVendedor = Integer.parseInt(scanner.nextLine());
            System.out.print("Informe o Nome do Vendedor: ");
            Vendedor vendedor = new Vendedor(idVendedor, scanner.nextLine());

            while (true) {
                System.out.println("\n--- MENU DE OPÇÕES ---");
                System.out.println("1. Processar Venda de um Produto (RPC Direto)");
                System.out.println("2. Consultar Estoque por ID (RPC Direto)");
                System.out.println("3. Confirmar Pedido (RPC Direto)");
                System.out.println("4. Listar Estoque (RPC Direto)");
                System.out.println("5. Confirmar Pedido Assíncrono via Fila (Trabalho 4)");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");

                int opcao = Integer.parseInt(scanner.nextLine());
                if (opcao == 0) {
                    break;
                }

                switch (opcao) {
                    case 1: {
                        proxy = obterProxy(proxy);
                        Produto produto = cadastrarProdutoTeclado(scanner);
                        int quantidade = lerQuantidade(scanner);
                        boolean sucesso = proxy.processarVenda(vendedor, produto, quantidade);
                        System.out.println("Resultado da venda: " + (sucesso ? "SUCESSO" : "FALHA"));
                        break;
                    }
                    case 2: {
                        proxy = obterProxy(proxy);
                        System.out.print("ID do produto no estoque: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        System.out.println("Produto retornado: " + proxy.consultarEstoque(id));
                        break;
                    }
                    case 3: {
                        proxy = obterProxy(proxy);
                        Produto produto = cadastrarProdutoTeclado(scanner);
                        int quantidade = lerQuantidade(scanner);
                        Pedido pedido = proxy.confirmarPedido(vendedor, produto, quantidade);
                        System.out.println("Pedido confirmado: " + pedido);
                        break;
                    }
                    case 4: {
                        proxy = obterProxy(proxy);
                        System.out.println("Catálogo: " + proxy.listarProdutosEstoque());
                        break;
                    }
                    case 5: {
                        // A opção de fila não abre socket nem precisa do servidor RPC.
                        Produto produto = cadastrarProdutoTeclado(scanner);
                        int quantidade = lerQuantidade(scanner);
                        Pedido pedido = new Pedido(vendedor, produto, quantidade);

                        // O cliente só confirma sucesso depois da gravação persistente.
                        FilaBroker.enfileirarPedido(pedido);
                        System.out.println("[FILA] Pedido " + pedido.getIdPedido()
                                + " persistido e aguardando processamento.");
                        break;
                    }
                    default:
                        System.out.println("Opção inválida!");
                }
            }
            System.out.println("Encerrando aplicação cliente.");
        } catch (Exception e) {
            System.err.println("Falha na aplicação cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Vendas obterProxy(Vendas proxy) throws Exception {
        if (proxy == null) {
            // Esta configuração é usada apenas pelas opções diretas 1 a 4.
            proxy = new VendasProxy(new ProtocoloRPC("localhost", 7070));
        }
        return proxy;
    }

    private static int lerQuantidade(Scanner scanner) {
        System.out.print("Quantidade: ");
        return Integer.parseInt(scanner.nextLine());
    }

    private static Produto cadastrarProdutoTeclado(Scanner scanner) {
        System.out.println("\nQual tipo de produto deseja inserir?");
        System.out.println("1. Celular");
        System.out.println("2. Capa");
        System.out.println("3. Película");
        System.out.println("4. PowerBank");
        System.out.println("5. Outro (Genérico)");
        System.out.print("Opção: ");
        int tipo = Integer.parseInt(scanner.nextLine());

        System.out.print("ID do Produto: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Nome/Descrição: ");
        String nome = scanner.nextLine();
        System.out.print("Preço: R$ ");
        double preco = Double.parseDouble(scanner.nextLine());

        switch (tipo) {
            case 1:
                System.out.print("Marca: ");
                String marca = scanner.nextLine();
                System.out.print("Armazenamento (GB): ");
                int gb = Integer.parseInt(scanner.nextLine());
                return new Celular(id, nome, preco, marca, gb);
            case 2:
                System.out.print("Modelo compatível: ");
                return new Capa(id, nome, preco, scanner.nextLine());
            case 3:
                System.out.print("Tipo de material: ");
                return new Pelicula(id, nome, preco, scanner.nextLine());
            case 4:
                System.out.print("Capacidade (mAh): ");
                return new PowerBank(id, nome, preco, Integer.parseInt(scanner.nextLine()));
            default:
                return new Produto(id, nome, preco);
        }
    }
}
