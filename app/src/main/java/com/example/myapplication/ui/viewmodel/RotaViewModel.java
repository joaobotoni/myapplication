package com.example.myapplication.ui.viewmodel;

import android.location.Address;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.myapplication.data.models.Rota;
import com.example.myapplication.data.repositories.LocalizacaoRepository;
import com.example.myapplication.ui.helpers.TaskHelper;
import com.example.myapplication.ui.state.RotaState;
import com.example.myapplication.utils.mappers.domain.RotaMapper;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RotaViewModel extends ViewModel {
    private final LocalizacaoRepository repositorio;
    private final TaskHelper taskHelper;
    private final TaskHelper.Cancellables tarefas = new TaskHelper.Cancellables();
    private final MutableLiveData<RotaState> state = new MutableLiveData<>();
    private final MutableLiveData<Throwable> error = new MutableLiveData<>();
    private final RotaMapper rotaMapper;

    @Inject
    public RotaViewModel(TaskHelper taskHelper, LocalizacaoRepository repositorio, RotaMapper rotaMapper) {
        this.repositorio = repositorio;
        this.taskHelper = taskHelper;
        this.rotaMapper = rotaMapper;
    }

    public LiveData<RotaState> getState() {
        return state;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public void selecionar(Address origem, String destinoQuery) {
        tarefas.adicionar(taskHelper.execute(
                () -> calcularRota(origem, destinoQuery),
                state::postValue,
                error::postValue
        ));
    }

    private RotaState calcularRota(Address origem, String destinoQuery) throws Exception {
        Address destino = repositorio.enderecoPorNome(destinoQuery).orElseThrow();
        Rota resposta = repositorio.calcularRota(origem, destino);
        return rotaMapper.mapFrom(resposta);
    }

    public void limpar() {
        state.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tarefas.cancelarTudo();
    }
}