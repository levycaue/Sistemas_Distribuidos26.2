package io;

import model.Celular;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CelularOutputStream extends OutputStream {
    private final DataOutputStream out;
    private final Celular[] celulares;
    private final int numObjetos;

    public CelularOutputStream(OutputStream destino, Celular[] celulares, int numObjetos) {
        this.out = new DataOutputStream(destino);
        this.celulares = celulares;
        this.numObjetos = Math.min(numObjetos, celulares.length);
    }

    public void flushAndSend() throws IOException {
        out.writeInt(numObjetos);
        for (int i = 0; i < numObjetos; i++) {
            out.writeInt(celulares[i].getId());
            out.writeUTF(celulares[i].getNome());
            out.writeDouble(celulares[i].getPreco());
            out.writeUTF(celulares[i].getMarca());
            out.writeInt(celulares[i].getArmazenamentoGb());
        }
        out.flush();
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void close() throws IOException {
        flushAndSend();
        out.close();
    }
}