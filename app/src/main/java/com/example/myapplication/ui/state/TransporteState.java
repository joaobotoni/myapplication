package com.example.myapplication.ui.state;

public class TransporteState {

    private final long id;
    private final String nomeVeiculo;
    private final int quantidade;
    private final int capacidade;
    private final int ocupacao;

    public TransporteState(long id, String nomeVeiculo, int quantidade, int capacidade, int ocupacao) {
        this.id = id;
        this.nomeVeiculo = nomeVeiculo;
        this.quantidade = quantidade;
        this.capacidade = capacidade;
        this.ocupacao = ocupacao;
    }

    public long getId() {
        return id;
    }

    public String getNomeVeiculo() {
        return nomeVeiculo;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public int getOcupacao() {
        return ocupacao;
    }
}
