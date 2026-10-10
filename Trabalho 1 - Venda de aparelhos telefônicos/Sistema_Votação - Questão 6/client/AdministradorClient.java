package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class AdministradorClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 7000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            oos.writeObject("ADMIN");
            oos.flush();

            String status = (String) ois.readObject();
            if (!"AUTENTICADO_ADMIN".equals(status)) {
                System.out.println("Falha na autenticação do Administrador.");
                return;
            }

            System.out.println("=== PAINEL DO ADMINISTRADOR ===");
            System.out.println("1. Adicionar Candidato");
            System.out.println("2. Remover Candidato");
            System.out.println("3. Enviar Nota Informativa (Multicast)");
            System.out.print("Escolha a opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir quebra de linha

            if (opcao == 1) {
                oos.writeObject("ADICIONAR");
                System.out.print("Digite o nome do novo candidato: ");
                String nome = scanner.nextLine();
                oos.writeObject(nome);
                oos.flush();
            } else if (opcao == 2) {
                oos.writeObject("REMOVER");
                System.out.print("Digite o ID do candidato a remover: ");
                int id = scanner.nextInt();
                oos.writeInt(id);
                oos.flush();
            } else if (opcao == 3) {
                oos.writeObject("NOTIFICAR");
                System.out.print("Digite a nota informativa para os eleitores: ");
                String nota = scanner.nextLine();
                oos.writeObject(nota);
                oos.flush();
            }

            String resposta = (String) ois.readObject();
            System.out.println("Resposta do Servidor: " + resposta);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}