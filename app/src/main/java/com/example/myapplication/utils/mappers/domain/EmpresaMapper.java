package com.example.myapplication.utils.mappers.domain;

import com.example.myapplication.data.source.local.entities.Empresa;
import com.example.myapplication.ui.state.empresa.EmpresaState;
import com.example.myapplication.utils.mappers.Mapper;

import javax.inject.Inject;

public class EmpresaMapper implements Mapper<EmpresaState, Empresa> {

    @Inject
    public EmpresaMapper() {}

    @Override
    public Empresa mapTo(EmpresaState state) {
        return new Empresa(state.getId(), state.getNome());
    }

    @Override
    public EmpresaState mapFrom(Empresa e) {
        return new EmpresaState(e.getIdEmpresa(), e.getNome(), false);
    }
}
