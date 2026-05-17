package com.example.myapplication.ui.state.empresa;

public class EmpresaState {

    private final int id;
    private final String nome;
    private final boolean isSelected;

    public EmpresaState(int id, String nome, boolean isSelected) {
        this.id = id;
        this.nome = nome;
        this.isSelected = isSelected;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
    public boolean isSelected() {
        return isSelected;
    }
}
