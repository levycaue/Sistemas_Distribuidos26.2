package model;

import java.io.Serializable;

public class Candidato implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nome;
    private int votos;

    public Candidato(int id, String nome) {
        this.id = id;
        this.nome = nome;
        this.votos = 0;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public synchronized int getVotos() { return votos; }
    public synchronized void incrementarVotos() { this.votos++; }

    @Override
    public String toString() {
        return "ID: " + id + " | Nome: " + nome + " | Votos: " + votos;
    }
}