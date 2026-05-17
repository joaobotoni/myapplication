package com.example.myapplication.ui.state.animal;

public class AnimalEspecificacaoState {
    private final String sexo;
    private final Integer idade;
    public AnimalEspecificacaoState(String sexo, Integer idade) {
        this.sexo = sexo;
        this.idade = idade;
    }

    public String getSexo() { return sexo; }
    public Integer getIdade() { return idade; }
}
