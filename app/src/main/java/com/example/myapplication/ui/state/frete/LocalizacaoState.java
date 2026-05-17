package com.example.myapplication.ui.state.frete;

import android.location.Address;

import java.util.Collections;
import java.util.List;

public class LocalizacaoState {
    private final List<Address> localizacoes;
    private final boolean carregando;
    public LocalizacaoState() {
        this.localizacoes = Collections.emptyList();
        this.carregando = false;
    }
    public LocalizacaoState(List<Address> localizacoes, boolean carregando) {
        this.localizacoes = localizacoes;
        this.carregando = carregando;
    }
    public List<Address> getLocalizacoes() {
        return localizacoes;
    }

    public boolean isCarregando() {
        return carregando;
    }
}