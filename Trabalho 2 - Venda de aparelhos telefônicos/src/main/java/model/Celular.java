package model;
public class Celular extends Produto {
    private static final long serialVersionUID = 1L;
    private String marca;
    private int armazenamentoGb;
    
    public Celular() {}
    
    public Celular(int id, String nome, double preco, String marca, int armazenamentoGb) {
        super(id, nome, preco);
        this.marca = marca;
        this.armazenamentoGb = armazenamentoGb;
    }
    public String getMarca() { return marca; }
    public int getArmazenamentoGb() { return armazenamentoGb; }
    @Override
    public String toString() {
        return "Celular{id=" + id + ", nome='" + nome + "', preco=" + preco + ", marca='" + marca + "', armazenamento=" + armazenamentoGb + "GB}";
    }
}
