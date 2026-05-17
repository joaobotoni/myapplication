package com.example.myapplication.utils.mappers.domain;



import com.example.myapplication.data.models.Rota;
import com.example.myapplication.ui.state.frete.RotaState;
import com.example.myapplication.utils.mappers.Mapper;

import javax.inject.Inject;

public class RotaMapper implements Mapper<RotaState, Rota> {

    @Inject
    public RotaMapper() {
    }

    @Override
    public Rota mapTo(RotaState rotaState) {
        return new Rota(
                rotaState.getCidadeOrigem(),
                rotaState.getEstadoOrigem(),
                rotaState.getCidadeDestino(),
                rotaState.getEstadoDestino(),
                rotaState.getDistancia());
    }

    @Override
    public RotaState mapFrom(Rota rota) {
        return new RotaState(
                rota.getCidadeOrigem(),
                rota.getEstadoOrigem(),
                rota.getCidadeDestino(),
                rota.getEstadoDestino(),
                rota.getDistancia());
    }
}
