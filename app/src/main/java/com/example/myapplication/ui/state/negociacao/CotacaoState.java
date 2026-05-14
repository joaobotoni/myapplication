package com.example.myapplication.ui.state.negociacao;

import java.math.BigDecimal;

public class CotacaoState {
    private final BigDecimal valorPorKg;
    private final BigDecimal valorPorCabeca;
    private final int quantidade;
    private final BigDecimal valorTotal;

    public CotacaoState(BigDecimal valorPorKg, BigDecimal valorPorCabeca, int quantidade, BigDecimal valorTotal) {
        this.valorPorKg = valorPorKg;
        this.valorPorCabeca = valorPorCabeca;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorPorKg() {
        return valorPorKg;
    }

    public BigDecimal getValorPorCabeca() {
        return valorPorCabeca;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }
}
