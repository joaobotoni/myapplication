package com.example.myapplication.ui.state.negociacao;

import java.math.BigDecimal;

public class FechamentoState {
    private final BigDecimal valorPorKg;
    private final BigDecimal valorPorCabeca;
    private final BigDecimal valorTotal;
    private final BigDecimal comissaoPorKg;
    private final boolean isComissaoAplicada;

    public FechamentoState(BigDecimal valorPorKg, BigDecimal valorPorCabeca, BigDecimal valorTotal, BigDecimal comissaoPorKg) {
        this.valorPorKg = valorPorKg;
        this.valorPorCabeca = valorPorCabeca;
        this.valorTotal = valorTotal;
        this.comissaoPorKg = comissaoPorKg;
        this.isComissaoAplicada = comissaoPorKg != null && comissaoPorKg.compareTo(BigDecimal.ZERO) != 0;
    }


    public BigDecimal getValorPorKg() { return valorPorKg; }
    public BigDecimal getValorPorCabeca() { return valorPorCabeca; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public BigDecimal getComissaoPorKg() { return comissaoPorKg; }
    public boolean isComissaoAplicada() { return isComissaoAplicada; }
}
