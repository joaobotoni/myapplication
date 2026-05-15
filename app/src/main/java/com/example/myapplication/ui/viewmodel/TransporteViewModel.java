package com.example.myapplication.ui.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.repositories.TransporteRepository;
import com.example.myapplication.ui.helpers.TaskHelper;
import com.example.myapplication.ui.state.TransporteState;
import com.example.myapplication.utils.mappers.domain.TransporteMapper;

import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class TransporteViewModel extends ViewModel {
    private final TransporteRepository repositorio;
    private final TaskHelper taskHelper;
    private final TaskHelper.Cancellables tarefas = new TaskHelper.Cancellables();
    private final MutableLiveData<List<TransporteState>> state = new MutableLiveData<>();
    private final MutableLiveData<Throwable> error = new MutableLiveData<>();
    private final TransporteMapper transporteMapper;

    @Inject
    public TransporteViewModel(TransporteRepository repositorio, TaskHelper taskHelper, TransporteMapper transporteMapper) {
        this.repositorio = repositorio;
        this.taskHelper = taskHelper;
        this.transporteMapper = transporteMapper;
    }

    public LiveData<List<TransporteState>> getState() {
        return state;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public void recomendar(long categoria, int quantidade) {
        tarefas.adicionar(taskHelper.execute(
                () -> transporteMapper
                        .mapFrom(repositorio.recomendarTransportes(categoria, quantidade)),
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