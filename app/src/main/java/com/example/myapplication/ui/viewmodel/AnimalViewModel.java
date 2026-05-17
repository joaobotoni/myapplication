package com.example.myapplication.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.ui.state.animal.AnimalState;
import com.example.myapplication.ui.state.animal.CategoriaState;
import com.example.myapplication.ui.state.animal.AnimalEspecificacaoState;
import com.example.myapplication.ui.state.animal.RacaState;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class AnimalViewModel extends ViewModel {
    private RacaState raca;
    private CategoriaState categoria;
    private AnimalEspecificacaoState especificacao;
    private final MutableLiveData<AnimalState> animalState = new MutableLiveData<>(null);

    @Inject
    public AnimalViewModel() {
    }

    public LiveData<AnimalState> getAnimalState() {
        return animalState;
    }

    public void atualizarRaca(RacaState raca) {
        this.raca = raca;
        animalState.setValue(build());
    }

    public void atualizarCategoria(CategoriaState categoria) {
        this.categoria = categoria;
        animalState.setValue(build());
    }

    public void atualizarEspecificacao(String sexo, Integer idade) {
        this.especificacao = new AnimalEspecificacaoState(sexo, idade);
        animalState.setValue(build());
    }

    private AnimalState build() {
        return new AnimalState(especificacao, raca, categoria);
    }
}
