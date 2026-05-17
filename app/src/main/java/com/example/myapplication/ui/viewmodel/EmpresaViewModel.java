package com.example.myapplication.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.repositories.EmpresaRepository;
import com.example.myapplication.ui.helpers.TaskHelper;
import com.example.myapplication.ui.state.empresa.EmpresaState;
import com.example.myapplication.utils.mappers.domain.EmpresaMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EmpresaViewModel extends ViewModel {
    private final EmpresaRepository repositorio;
    private final EmpresaMapper mapper;
    private final TaskHelper taskHelper;
    private final TaskHelper.Cancellables tarefas = new TaskHelper.Cancellables();
    private final MutableLiveData<List<EmpresaState>> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> error = new MutableLiveData<>(null);
    private final MutableLiveData<EmpresaState> empresaSelecionada = new MutableLiveData<>(null);

    @Inject
    public EmpresaViewModel(EmpresaRepository repositorio, EmpresaMapper mapper, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
        this.taskHelper = taskHelper;
        carregar();
    }

    public LiveData<List<EmpresaState>> getState() {
        return state;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public LiveData<EmpresaState> getEmpresaSelecionada() {
        return empresaSelecionada;
    }

    public void selecionarEmpresa(EmpresaState selecionada) {
        if (state.getValue() == null) return;

        List<EmpresaState> newList = state.getValue().stream()
                .map(item -> new EmpresaState(
                        item.getId(),
                        item.getNome(),
                        Objects.equals(item.getId(), selecionada.getId())))
                .collect(Collectors.toList());

        state.setValue(newList);
        empresaSelecionada.setValue(selecionada);
    }

    public void carregar() {
        listarEmpresas();
    }

    private void listarEmpresas() {
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

    public void limparSelecao() {
        empresaSelecionada.setValue(null);
        if (state.getValue() == null) return;
        List<EmpresaState> newList = state.getValue().stream()
                .map(item -> new EmpresaState(item.getId(), item.getNome(), false))
                .collect(Collectors.toList());
        state.setValue(newList);
    }
}
