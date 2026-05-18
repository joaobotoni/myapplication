package com.example.myapplication.utils.mappers.domain;


import com.example.myapplication.data.models.PrecificacaoBezerro;
import com.example.myapplication.ui.state.negociacao.CotacaoState;
import com.example.myapplication.utils.mappers.Mapper;

import javax.inject.Inject;

public class PrecificacaoBezerroMapper implements Mapper<CotacaoState, PrecificacaoBezerro> {

    @Inject
    public PrecificacaoBezerroMapper() {
    }

    @Override
    public PrecificacaoBezerro mapTo(CotacaoState cotacaoState) {
        return new PrecificacaoBezerro(
                cotacaoState.getValorPorKg(),
                cotacaoState.getValorPorCabeca(),
                cotacaoState.getValorTotal(),
                cotacaoState.getQuantidade()
        );
    }

    @Override
    public CotacaoState mapFrom(PrecificacaoBezerro precificacaoBezerro) {
        return new CotacaoState(
                precificacaoBezerro.getValorPorKg(),
                precificacaoBezerro.getValorPorCabeca(),
                precificacaoBezerro.getQuantidade(),
                precificacaoBezerro.getValorTotal()
        );
    }
}
