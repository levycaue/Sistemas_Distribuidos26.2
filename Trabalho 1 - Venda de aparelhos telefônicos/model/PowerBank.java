package model;

public class PowerBank extends Produto {
    private static final long serialVersionUID = 1L;
    private int capacidadeMah;

    public PowerBank(int id, String nome, double preco, int capacidadeMah) {
        super(id, nome, preco);
        this.capacidadeMah = capacidadeMah;
    }

    public int getCapacidadeMah() { return capacidadeMah; }

    @Override
    public String toString() {
        return "PowerBank{id=" + id + ", nome='" + nome + "', preco=" + preco + ", capacidade=" + capacidadeMah + "mAh}";
    }
}