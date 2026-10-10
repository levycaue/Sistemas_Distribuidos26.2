package model;

import java.io.Serializable;

public abstract class Produto implements Serializable {
    private static final long serialVersionUID = 1L;
    protected int id;
    protected String nome;
    protected double preco;

    public Produto(int id, String nome, double preco) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }
}