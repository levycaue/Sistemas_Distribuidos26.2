package client;

import interfaces.Vendas;
import java.util.Scanner;
import model.*;
import rmi.ProtocoloRPC;
import rmi.VendasProxy;

public class ClienteMain {
    public static void main(String[] args) {
        try {
            ProtocoloRPC protocolo = new ProtocoloRPC("localhost", 7070);
            Vendas proxy = new VendasProxy(protocolo);
            Scanner scanner = new Scanner(System.in);

            System.out.println("=== SISTEMA DE VENDAS (RMI/RPC) ===");
            System.out.print("Informe o ID do Vendedor: ");
            int idVendedor = Integer.parseInt(scanner.nextLine());
            System.out.print("Informe o Nome do Vendedor: ");
            String nomeVendedor = scanner.nextLine();
            Vendedor vendedor = new Vendedor(idVendedor, nomeVendedor);

            while (true) {
                System.out.println("\n--- MENU DE OPÇÕES ---");
                System.out.println("1. Processar Venda de um Produto");
                System.out.println("2. Consultar Estoque por ID");
                System.out.println("3. Confirmar Pedido");
                System.out.println("4. Listar Estoque");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");
                
                int opcao = Integer.parseInt(scanner.nextLine());
                if (opcao == 0) break;

                switch (opcao) {
                    case 1: {
                        Produto p = cadastrarProdutoTeclado(scanner);
                        System.out.print("Quantidade: ");
                        int qtd = Integer.parseInt(scanner.nextLine());
                        boolean ok = proxy.processarVenda(vendedor, p, qtd);
                        System.out.println("Resultado da venda: " + (ok ? "SUCESSO" : "FALHA"));
                        break;
                    }
                    case 2: {
                        System.out.print("ID do produto no estoque: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        Produto p = proxy.consultarEstoque(id);
                        System.out.println("Produto retornado: " + p);
                        break;
                    }
                    case 3: {
                        Produto p = cadastrarProdutoTeclado(scanner);
                        System.out.print("Quantidade: ");
                        int qtd = Integer.parseInt(scanner.nextLine());
                        Pedido pedido = proxy.confirmarPedido(vendedor, p, qtd);
                        System.out.println("Pedido Confirmado: " + pedido);
                        break;
                    }
                    case 4: {
                        String catalogo = proxy.listarProdutosEstoque();
                        System.out.println("Catálogo: " + catalogo);
                        break;
                    }
                    default:
                        System.out.println("Opção inválida!");
                }
            }
            System.out.println("Encerrando aplicação cliente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                String comp = scanner.nextLine();
                return new Capa(id, nome, preco, comp);
            case 3:
                System.out.print("Tipo de material: ");
                String mat = scanner.nextLine();
                return new Pelicula(id, nome, preco, mat);
            case 4:
                System.out.print("Capacidade (mAh): ");
                int mah = Integer.parseInt(scanner.nextLine());
                return new PowerBank(id, nome, preco, mah);
            default:
                return new Produto(id, nome, preco);
        }
    }
}