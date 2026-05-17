package com.example.myapplication.utils.mappers.domain;

import com.example.myapplication.data.models.PrecificacaoFrete;
import com.example.myapplication.ui.state.frete.FreteState;
import com.example.myapplication.utils.mappers.Mapper;

import jakarta.inject.Inject;

public class PrecificacaoFreteMapper implements Mapper<FreteState, PrecificacaoFrete> {

    @Inject
    public PrecificacaoFreteMapper() {
    }

    @Override
    public PrecificacaoFrete mapTo(FreteState precificacaoFreteUiState) {
        return new PrecificacaoFrete(precificacaoFreteUiState.getValorTotal(), precificacaoFreteUiState.getValorParcial());
    }

    @Override
    public FreteState mapFrom(PrecificacaoFrete o) {
        return new FreteState(o.getValorTotal(), o.getValorParcial());
    }
}
