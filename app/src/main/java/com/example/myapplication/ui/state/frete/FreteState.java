package com.example.myapplication.ui.state.frete;

import java.math.BigDecimal;

public class FreteState {
    private final BigDecimal valorTotal;
    private final BigDecimal valorParcial;
    private final StatusFrete statusFrete;

    public FreteState(BigDecimal valorTotal, BigDecimal valorParcial, StatusFrete statusFrete) {
        this.valorTotal = valorTotal;
        this.valorParcial = valorParcial;
        this.statusFrete = statusFrete;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public BigDecimal getValorParcial() {
        return valorParcial;
    }

    public StatusFrete getFreteState() {
        return statusFrete;
    }
}