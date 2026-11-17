package server;

import model.Pedido;
import mq.FilaBroker;

/** Consome pedidos de forma assíncrona, sem conexão direta com o cliente. */
public class ServidorConsumidorFila {
    public static void main(String[] args) {
        ServicoVendasImpl servico = new ServicoVendasImpl();
        System.out.println("=== SERVIDOR CONSUMIDOR DE FILA (TRABALHO 4) ===");
        System.out.println("Aguardando pedidos persistidos... (Ctrl+C para sair)\n");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                // O callback só termina quando o serviço de vendas processa o pedido.
                boolean processou = FilaBroker.processarProximo(pedido -> processar(servico, pedido));
                if (!processou) {
                    Thread.sleep(2000); // Evita consulta ocupada quando a fila está vazia.
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                // Se o processamento falhar, o broker mantém o pedido para nova tentativa.
                System.err.println("Falha ao processar pedido; ele continua na fila: "
                        + e.getMessage());
                try {
                    Thread.sleep(2000); // Limita tentativas e mensagens de erro repetidas.
                } catch (InterruptedException interrompido) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private static void processar(ServicoVendasImpl servico, Pedido pedido) {
        System.out.println("[PROCESSADOR] Pedido recebido da fila:");
        System.out.println("  -> ID: " + pedido.getIdPedido());

        // Reutiliza a lógica do serviço do Trabalho 2 no caminho indireto.
        boolean sucesso = servico.processarVenda(
                pedido.getVendedor(), pedido.getProduto(), pedido.getQuantidade());
        if (!sucesso) {
            throw new IllegalStateException("O serviço de vendas recusou o pedido.");
        }

        System.out.println("[SUCESSO] Pedido processado; será confirmado na fila.\n");
    }
}
