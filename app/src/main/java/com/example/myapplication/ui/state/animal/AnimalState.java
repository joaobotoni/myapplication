package com.example.myapplication.ui.state.animal;

public class AnimalState {
    private final AnimalEspecificacaoState especificacao;
    private final RacaState raca;
    private final CategoriaState categoria;
    public AnimalState(AnimalEspecificacaoState especificacao, RacaState raca, CategoriaState categoria) {
        this.especificacao = especificacao;
        this.raca = raca;
        this.categoria = categoria;
    }

    public AnimalEspecificacaoState getEspecificacao() {
        return especificacao;
    }

    public RacaState getRaca() {
        return raca;
    }
    public CategoriaState getCategoria() {
        return categoria;
    }
}
