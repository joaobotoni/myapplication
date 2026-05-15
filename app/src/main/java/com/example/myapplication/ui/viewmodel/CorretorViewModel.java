package com.example.myapplication.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.repositories.CorretorRepository;
import com.example.myapplication.ui.helpers.TaskHelper;
import com.example.myapplication.ui.state.CorretorState;
import com.example.myapplication.utils.mappers.domain.CorretorMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CorretorViewModel extends ViewModel {
    private final CorretorRepository repositorio;
    private final CorretorMapper mapper;
    private final TaskHelper taskHelper;
    private final TaskHelper.Cancellables tarefas = new TaskHelper.Cancellables();
    private final MutableLiveData<List<CorretorState>> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> error = new MutableLiveData<>(null);
    private final MutableLiveData<CorretorState> corretorSelecionado = new MutableLiveData<>(null);

    @Inject
    public CorretorViewModel(CorretorRepository repositorio, CorretorMapper mapper, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
        this.taskHelper = taskHelper;
        carregar();
    }

    public LiveData<List<CorretorState>> getState() {
        return state;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public LiveData<CorretorState> getCorretorSelecionado() {
        return corretorSelecionado;
    }

    public void selecionarCorretor(CorretorState selecionado) {
        if (state.getValue() == null) return;

        List<CorretorState> newList = state.getValue().stream()
                .map(item -> new CorretorState(
                        item.getId(),
                        item.getNome(),
                        item.getComissao(),
                        item.getTipoComissao(),
                        Objects.equals(item.getId(), selecionado.getId())))
                .collect(Collectors.toList());

        state.setValue(newList);
        corretorSelecionado.setValue(selecionado);
    }

    public void carregar() {
        listarCorretores();
    }

    private void listarCorretores() {
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
        corretorSelecionado.setValue(null);
        if (state.getValue() == null) return;
        List<CorretorState> newList = state.getValue().stream()
                .map(item -> new CorretorState(
                        item.getId(),
                        item.getNome(),
                        item.getComissao(),
                        item.getTipoComissao(),
                        false))
                .collect(Collectors.toList());
        state.setValue(newList);
    }
}
