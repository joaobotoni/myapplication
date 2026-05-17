package com.example.myapplication.ui.state.negociacao;

import com.example.myapplication.ui.state.frete.StatusFrete;

import java.math.BigDecimal;

public class PropostaState {
    private final BigDecimal valorPorKg;
    private final BigDecimal valorPorCabeca;
    private final BigDecimal valorTotal;
    private final BigDecimal fretePorKg;
    private final StatusFrete statusFrete;
    private final boolean isFreteDescontado;

    public PropostaState(BigDecimal valorPorKg, BigDecimal valorPorCabeca, BigDecimal valorTotal, BigDecimal fretePorKg, StatusFrete statusFrete) {
        this.valorPorKg = valorPorKg;
        this.valorPorCabeca = valorPorCabeca;
        this.valorTotal = valorTotal;
        this.fretePorKg = fretePorKg;
        this.isFreteDescontado = statusFrete != null && statusFrete != StatusFrete.NAO_SELECIONADO;
        this.statusFrete = statusFrete;
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

    public StatusFrete getFreteState() {
        return statusFrete;
    }

    public boolean isFreteDescontado() {
        return isFreteDescontado;
    }
}
