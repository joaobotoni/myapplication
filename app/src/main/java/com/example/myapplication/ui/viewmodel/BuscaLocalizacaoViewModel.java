package com.example.myapplication.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.myapplication.data.repositories.LocalizacaoRepository;
import com.example.myapplication.ui.helpers.TaskHelper;
import com.example.myapplication.ui.state.BuscaLocalizacaoState;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BuscaLocalizacaoViewModel extends ViewModel {
    private final LocalizacaoRepository repositorio;
    private final TaskHelper taskHelper;
    private final TaskHelper.Cancellables tarefas = new TaskHelper.Cancellables();
    private final MutableLiveData<BuscaLocalizacaoState> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> error = new MutableLiveData<>(null);
    @Inject
    public BuscaLocalizacaoViewModel(LocalizacaoRepository repositorio, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.taskHelper = taskHelper;
    }

    public LiveData<BuscaLocalizacaoState> getState() {
        return state;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public void buscar(String consulta, double latitude, double longitude) {
        tarefas.adicionar(taskHelper.execute(
                () -> {
                    String codigo = repositorio.paisDeCoordenadas(latitude, longitude).orElseThrow(() ->
                            new RuntimeException("Código do pais não encontrado"));
                    return new BuscaLocalizacaoState(repositorio.enderecosPorTexto(consulta, codigo), false);
                },
                state::postValue,
                error::postValue
        ));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tarefas.cancelarTudo();
    }
}