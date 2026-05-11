package com.example.myapplication.ui.state.negociacao;

import com.example.myapplication.ui.state.FreteState;

import java.math.BigDecimal;

public class PropostaState {
    private final BigDecimal valorPorKg;
    private final BigDecimal valorPorCabeca;
    private final BigDecimal valorTotal;
    private final BigDecimal fretePorKg;
    private final FreteState freteState;
    private final boolean isFreteDescontado;

    public PropostaState(BigDecimal valorPorKg, BigDecimal valorPorCabeca, BigDecimal valorTotal, BigDecimal fretePorKg, FreteState freteState) {
        this.valorPorKg = valorPorKg;
        this.valorPorCabeca = valorPorCabeca;
        this.valorTotal = valorTotal;
        this.fretePorKg = fretePorKg;
        this.isFreteDescontado = fretePorKg != null && fretePorKg.compareTo(BigDecimal.ZERO) != 0;
        this.freteState = freteState;
    }

    public BigDecimal getValorPorKg() {
        return valorPorKg;
    }

    public BigDecimal getValorPorCabeca() {
        return valorPorCabeca;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public BigDecimal getFretePorKg() {
        return fretePorKg;
    }

    public FreteState getFreteState() {
        return freteState;
    }

    public boolean isFreteDescontado() {
        return isFreteDescontado;
    }
}
