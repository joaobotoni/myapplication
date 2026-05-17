package com.example.myapplication.utils.mappers.domain;


import com.example.myapplication.data.models.Transporte;
import com.example.myapplication.ui.state.frete.TransporteState;
import com.example.myapplication.utils.mappers.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
public class TransporteMapper implements Mapper<List<TransporteState>, List<Transporte>> {
    @Inject
    public TransporteMapper() {
    }
    @Override
    public List<Transporte> mapTo(List<TransporteState> transporteStates) {
        return transporteStates.stream().map(transporteState -> new Transporte(
                        transporteState.getId(),
                        transporteState.getNomeVeiculo(),
                        transporteState.getQuantidade(),
                        transporteState.getCapacidade(),
                        transporteState.getOcupacao()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TransporteState> mapFrom(List<Transporte> transportes) {
        return transportes.stream().map(t -> new TransporteState(
                        t.getId(),
                        t.getNomeVeiculo(),
                        t.getQuantidade(),
                        t.getCapacidade(),
                        t.getOcupacao()))
                .collect(Collectors.toList());
    }
}
