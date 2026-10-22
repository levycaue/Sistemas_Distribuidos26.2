package model;
public class Pelicula extends Produto {
    private static final long serialVersionUID = 1L;
    private String tipoMaterial;
    
    public Pelicula() {}
    
    public Pelicula(int id, String nome, double preco, String tipoMaterial) {
        super(id, nome, preco);
        this.tipoMaterial = tipoMaterial;
    }
    public String getTipoMaterial() { return tipoMaterial; }
    @Override
    public String toString() {
        return "Pelicula{id=" + id + ", nome='" + nome + "', preco=" + preco + ", tipoMaterial='" + tipoMaterial + "'}";
    }
}
