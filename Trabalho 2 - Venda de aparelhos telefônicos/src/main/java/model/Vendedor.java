package model;
import java.io.Serializable;
public class Vendedor implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idVendedor;
    private String nome;
    
    public Vendedor() {}
    
    public Vendedor(int idVendedor, String nome) {
        this.idVendedor = idVendedor;
        this.nome = nome;
    }
    public int getIdVendedor() { return idVendedor; }
    public String getNome() { return nome; }
    @Override
    public String toString() {
        return "Vendedor{idVendedor=" + idVendedor + ", nome='" + nome + "'}";
    }
}
