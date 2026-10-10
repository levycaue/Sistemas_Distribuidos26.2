package io;

import model.Celular;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CelularInputStream extends InputStream {
    private final DataInputStream in;

    public CelularInputStream(InputStream origem) {
        this.in = new DataInputStream(origem);
    }

    public Celular[] lerCelulares() throws IOException {
        int numObjetos = in.readInt();
        Celular[] celulares = new Celular[numObjetos];
        for (int i = 0; i < numObjetos; i++) {
            int id = in.readInt();
            String nome = in.readUTF();
            double preco = in.readDouble();
            String marca = in.readUTF();
            int armazenamento = in.readInt();
            celulares[i] = new Celular(id, nome, preco, marca, armazenamento);
        }
        return celulares;
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}