package com.example.myapplication.utils.mappers.domain;

import com.example.myapplication.data.source.local.entities.Corretor;
import com.example.myapplication.ui.state.empresa.CorretorState;
import com.example.myapplication.utils.mappers.Mapper;

import java.math.BigDecimal;

import javax.inject.Inject;

public class CorretorMapper implements Mapper<CorretorState, Corretor> {

    @Inject
    public CorretorMapper() {
    }

    @Override
    public Corretor mapTo(CorretorState state) {
        return new Corretor(state.getId(), state.getNome(), state.getComissao().doubleValue(), state.getTipoComissao());
    }

    @Override
    public CorretorState mapFrom(Corretor c) {
        return new CorretorState(c.getIdCorretor(), c.getNome(), BigDecimal.valueOf(c.getComissao()), c.getTipoComissao(), false);
    }
}
