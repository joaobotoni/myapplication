package com.example.myapplication.ui.state.frete;

public class RotaState {
    private final String cidadeOrigem;
    private final String estadoOrigem;
    private final String cidadeDestino;
    private final String estadoDestino;
    private final double distancia;

    public RotaState(String cidadeOrigem, String estadoOrigem,
                     String cidadeDestino, String estadoDestino,
                     double distancia) {
        this.cidadeOrigem = cidadeOrigem;
        this.estadoOrigem = estadoOrigem;
        this.cidadeDestino = cidadeDestino;
        this.estadoDestino = estadoDestino;
        this.distancia = distancia;
    }

    public String getCidadeOrigem() {
        return cidadeOrigem;
    }

    public String getEstadoOrigem() {
        return estadoOrigem;
    }

    public String getCidadeDestino() {
        return cidadeDestino;
    }

    public String getEstadoDestino() {
        return estadoDestino;
    }

    public double getDistancia() {
        return distancia;
    }
}