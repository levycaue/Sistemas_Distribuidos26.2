import io.CelularInputStream;
import io.CelularOutputStream;
import model.Celular;
import java.io.*;

public class TesteStreams {
    public static void main(String[] args) {
        Celular[] celulares = {
            new Celular(1, "iPhone 15", 5500.0, "Apple", 128),
            new Celular(2, "Galaxy S24", 4800.0, "Samsung", 256)
        };

        String nomeFicheiro = "celulares.dat";

        try (FileOutputStream fos = new FileOutputStream(nomeFicheiro);
             CelularOutputStream cos = new CelularOutputStream(fos, celulares, celulares.length)) {
            cos.flushAndSend();
            System.out.println("[SUCESSO] Celulares escritos no ficheiro com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileInputStream fis = new FileInputStream(nomeFicheiro);
             CelularInputStream cis = new CelularInputStream(fis)) {
            Celular[] celularesLidos = cis.lerCelulares();
            System.out.println("\n[SUCESSO] Celulares recuperados do ficheiro:");
            for (Celular c : celularesLidos) {
                System.out.println(" -> " + c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}