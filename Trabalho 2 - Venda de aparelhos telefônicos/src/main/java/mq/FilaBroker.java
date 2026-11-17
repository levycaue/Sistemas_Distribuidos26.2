package mq;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import model.Pedido;

/**
 * Fila persistente simples em arquivo para demonstrar comunicação indireta.
 *
 * Os processos produtor e consumidor precisam apontar para o mesmo arquivo.
 * Use -Dfila.path com um caminho compartilhado se estiverem em diretórios ou
 * máquinas diferentes (por exemplo, em uma pasta de rede compartilhada).
 */
public final class FilaBroker {
    // O padrão funciona quando cliente e consumidor são iniciados na mesma pasta.
    private static final Path ARQUIVO_FILA = Paths.get(
            System.getProperty("fila.path", "pedidos_fila.dat"))
            .toAbsolutePath()
            .normalize();

    // Um arquivo de lock separado permanece estável mesmo quando a fila é trocada.
    private static final Path ARQUIVO_LOCK = ARQUIVO_FILA.resolveSibling(
            ARQUIVO_FILA.getFileName().toString() + ".lock");

    private FilaBroker() {
        // A classe só expõe operações estáticas.
    }

    /** Acrescenta o pedido e só retorna depois que a nova fila foi persistida. */
    public static synchronized void enfileirarPedido(Pedido pedido) throws IOException {
        if (pedido == null) {
            throw new IllegalArgumentException("O pedido não pode ser nulo.");
        }

        prepararDiretorio();
        try (FileChannel canal = FileChannel.open(ARQUIVO_LOCK,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             FileLock lock = canal.lock()) {
            List<Pedido> pedidos = lerFila();
            pedidos.add(pedido);
            salvarFila(pedidos);
        }
    }

    /**
     * Processa o primeiro pedido mantendo-o na fila até o callback terminar.
     * Se o callback falhar, a remoção não ocorre e o pedido poderá ser tentado
     * novamente quando o consumidor voltar a consultar a fila.
     */
    public static synchronized boolean processarProximo(Consumer<Pedido> processador)
            throws IOException {
        if (processador == null) {
            throw new IllegalArgumentException("O processador não pode ser nulo.");
        }

        prepararDiretorio();
        try (FileChannel canal = FileChannel.open(ARQUIVO_LOCK,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             FileLock lock = canal.lock()) {
            List<Pedido> pedidos = lerFila();
            if (pedidos.isEmpty()) {
                return false;
            }

            Pedido pedido = pedidos.get(0);
            processador.accept(pedido); // Uma exceção deixa o pedido persistido para retry.
            pedidos.remove(0);          // Confirmação: remove só após processamento bem-sucedido.
            salvarFila(pedidos);
            return true;
        }
    }

    private static void prepararDiretorio() throws IOException {
        Path diretorio = ARQUIVO_FILA.getParent();
        if (diretorio != null) {
            Files.createDirectories(diretorio);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Pedido> lerFila() throws IOException {
        if (!Files.exists(ARQUIVO_FILA)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream entrada = new ObjectInputStream(
                new FileInputStream(ARQUIVO_FILA.toFile()))) {
            Object conteudo = entrada.readObject();
            if (!(conteudo instanceof List<?>)) {
                throw new IOException("O arquivo da fila não contém uma lista de pedidos.");
            }

            List<?> itens = (List<?>) conteudo;
            for (Object item : itens) {
                if (!(item instanceof Pedido)) {
                    throw new IOException("O arquivo da fila contém um item inválido.");
                }
            }
            return (List<Pedido>) itens;
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new IOException("Não foi possível ler os pedidos persistidos.", e);
        }
    }

    private static void salvarFila(List<Pedido> pedidos) throws IOException {
        Path diretorio = ARQUIVO_FILA.getParent();
        Path temporario = Files.createTempFile(diretorio,
                ARQUIVO_FILA.getFileName().toString(), ".tmp");
        try {
            // Escreve e sincroniza o temporário antes de substituir a fila atual.
            try (FileOutputStream arquivo = new FileOutputStream(temporario.toFile());
                 ObjectOutputStream saida = new ObjectOutputStream(arquivo)) {
                saida.writeObject(pedidos);
                saida.flush();
                arquivo.getFD().sync();
            }

            try {
                Files.move(temporario, ARQUIVO_FILA,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                // Alguns sistemas de arquivos não oferecem troca atômica de arquivos.
                Files.move(temporario, ARQUIVO_FILA, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporario);
        }
    }
}
