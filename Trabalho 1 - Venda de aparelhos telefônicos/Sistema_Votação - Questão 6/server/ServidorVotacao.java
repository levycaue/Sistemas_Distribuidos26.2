package server;

import model.Candidato;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServidorVotacao {
    private static final int PORTA_TCP = 7000;
    private static final String IP_MULTICAST = "230.0.0.2";
    private static final int PORTA_MULTICAST = 6000;
    
    private static final List<Candidato> candidatos = Collections.synchronizedList(new ArrayList<>());
    private static volatile boolean votacaoAberta = true;
    private static final int TEMPO_VOTACAO_SEGUNDOS = 60;

    public static void main(String[] args) {
        candidatos.add(new Candidato(1, "Candidato A (Tecnologia)"));
        candidatos.add(new Candidato(2, "Candidato B (Inovação)"));

        new Thread(() -> {
            try {
                Thread.sleep(TEMPO_VOTACAO_SEGUNDOS * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            votacaoAberta = false;
            System.out.println("\n[TEMPO ESGOTADO] A votação foi encerrada pelo servidor!");
            calcularResultadosFinais();
        }).start();

        try (ServerSocket serverSocket = new ServerSocket(PORTA_TCP)) {
            System.out.println("Servidor de Votação TCP rodando na porta " + PORTA_TCP);
            System.out.println("Votação aberta por " + TEMPO_VOTACAO_SEGUNDOS + " segundos.");

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void calcularResultadosFinais() {
        int totalVotos = candidatos.stream().mapToInt(Candidato::getVotos).sum();
        System.out.println("\n====== RESULTADOS FINAIS DA VOTAÇÃO ======");
        System.out.println("Total de votos apurados: " + totalVotos);
        
        Candidato vencedor = null;
        int maxVotos = -1;

        for (Candidato c : candidatos) {
            double percentual = totalVotos > 0 ? (c.getVotos() * 100.0) / totalVotos : 0.0;
            System.out.printf("- %s: %d votos (%.2f%%)\n", c.getNome(), c.getVotos(), percentual);
            if (c.getVotos() > maxVotos) {
                maxVotos = c.getVotos();
                vencedor = c;
            }
        }

        if (vencedor != null && totalVotos > 0) {
            System.out.println("\nCANDIDATO VENCEDOR: " + vencedor.getNome() + " com " + vencedor.getVotos() + " votos!");
        } else {
            System.out.println("\nNenhum voto registrado.");
        }
        System.out.println("===========================================");
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {

                String tipoUsuario = (String) ois.readObject(); // 

                if ("ELEITOR".equalsIgnoreCase(tipoUsuario)) {
                    if (!votacaoAberta) {
                        oos.writeObject("ERRO: Votação encerrada.");
                        return;
                    }
                    oos.writeObject(new ArrayList<>(candidatos));
                    oos.flush();

                    int idVoto = ois.readInt();
                    synchronized (candidatos) {
                        Optional<Candidato> escolhido = candidatos.stream().filter(c -> c.getId() == idVoto).findFirst();
                        if (escolhido.isPresent() && votacaoAberta) {
                            escolhido.get().incrementarVotos();
                            oos.writeObject("SUCESSO: Voto computado com sucesso para " + escolhido.get().getNome());
                        } else {
                            oos.writeObject("ERRO: Candidato inválido ou votação encerrada.");
                        }
                    }

                } else if ("ADMIN".equalsIgnoreCase(tipoUsuario)) {
                    oos.writeObject("AUTENTICADO_ADMIN");
                    oos.flush();

                    String acao = (String) ois.readObject(); 
                    if ("ADICIONAR".equals(acao)) {
                        String nomeCandidato = (String) ois.readObject();
                        int novoId = candidatos.size() + 1;
                        candidatos.add(new Candidato(novoId, nomeCandidato));
                        oos.writeObject("Candidato adicionado com sucesso!");
                    } else if ("REMOVER".equals(acao)) {
                        int idRemover = ois.readInt();
                        candidatos.removeIf(c -> c.getId() == idRemover);
                        oos.writeObject("Candidato removido com sucesso!");
                    } else if ("NOTIFICAR".equals(acao)) {
                        String notaInformativa = (String) ois.readObject();
                        enviarMulticastNota(notaInformativa);
                        oos.writeObject("Nota informativa enviada via multicast!");
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    private static void enviarMulticastNota(String nota) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress grupo = InetAddress.getByName(IP_MULTICAST);
            String jsonNota = "{\n  \"tipo\": \"NOTA_INFORMATIVA\",\n  \"mensagem\": \"" + nota + "\",\n  \"timestamp\": " + System.currentTimeMillis() + "\n}";
            byte[] buffer = jsonNota.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, grupo, PORTA_MULTICAST);
            socket.send(packet);
            System.out.println("[ADMIN BROADCAST] Nota informativa enviada via Multicast UDP.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}