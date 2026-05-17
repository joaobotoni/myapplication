package com.example.myapplication.ui.state.empresa;

import java.math.BigDecimal;

public class CorretorState {

    private final int id;
    private final String nome;
    private final BigDecimal comissao;
    private final String tipoComissao;
    private final boolean isSelected;

    public CorretorState(int id, String nome, BigDecimal comissao, String tipoComissao, boolean isSelected) {
        this.id = id;
        this.nome = nome;
        this.comissao = comissao;
        this.tipoComissao = tipoComissao;
        this.isSelected = isSelected;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getComissao() {
        return comissao;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getTipoComissao() {
        return tipoComissao;
    }
}
