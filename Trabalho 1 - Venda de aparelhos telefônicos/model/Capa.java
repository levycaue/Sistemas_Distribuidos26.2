package model;

public class Capa extends Produto {
    private static final long serialVersionUID = 1L;
    private String compatibilidadeModelo;

    public Capa(int id, String nome, double preco, String compatibilidadeModelo) {
        super(id, nome, preco);
        this.compatibilidadeModelo = compatibilidadeModelo;
    }

    public String getCompatibilidadeModelo() { return compatibilidadeModelo; }

    @Override
    public String toString() {
        return "Capa{id=" + id + ", nome='" + nome + "', preco=" + preco + ", compatibilidade='" + compatibilidadeModelo + "'}";
    }
}