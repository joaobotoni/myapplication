package com.example.myapplication.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.repositories.RacaRepository;
import com.example.myapplication.ui.helpers.TaskHelper;
import com.example.myapplication.ui.state.animal.RacaState;
import com.example.myapplication.utils.mappers.domain.RacaMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RacaViewModel extends ViewModel {
    private final RacaRepository repositorio;
    private final RacaMapper mapper;
    private final TaskHelper taskHelper;
    private final TaskHelper.Cancellables tarefas = new TaskHelper.Cancellables();
    private final MutableLiveData<List<RacaState>> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> error = new MutableLiveData<>(null);
    private final MutableLiveData<RacaState> racaSelecionada = new MutableLiveData<>(null);

    @Inject
    public RacaViewModel(RacaRepository repositorio, RacaMapper mapper, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
        this.taskHelper = taskHelper;
        carregar();
    }

    public LiveData<List<RacaState>> getState() {
        return state;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public LiveData<RacaState> getRacaSelecionada() {
        return racaSelecionada;
    }

    public void selecionarRaca(RacaState selecionada) {
        if (state.getValue() == null) return;

        List<RacaState> newList = state.getValue().stream()
                .map(item -> new RacaState(
                        item.getId(),
                        item.getDescricao(),
                        Objects.equals(item.getId(), selecionada.getId())))
                .collect(Collectors.toList());

        state.setValue(newList);
        racaSelecionada.setValue(selecionada);
    }

    public void carregar() {
        listarRacas();
    }

    private void listarRacas() {
        tarefas.adicionar(taskHelper.execute(
                () -> repositorio.getAll().stream()
                        .map(mapper::mapFrom)
                        .collect(Collectors.toList()),
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