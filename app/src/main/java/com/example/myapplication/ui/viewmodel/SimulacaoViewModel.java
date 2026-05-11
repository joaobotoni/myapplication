package com.example.myapplication.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.models.PrecificacaoBezerro;
import com.example.myapplication.data.repositories.PrecificacaoBezerroRepository;
import com.example.myapplication.data.repositories.ValorReferenciaRepository;
import com.example.myapplication.domain.implementation.PrecificacaoBezerroImplementation;
import com.example.myapplication.domain.strategy.PrecificacaoBezerroComFrete;
import com.example.myapplication.ui.helpers.TaskHelper;
import com.example.myapplication.ui.state.SimulacaoState;
import com.example.myapplication.utils.mappers.domain.PrecificacaoBezerroMapper;

import java.math.BigDecimal;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class SimulacaoViewModel extends ViewModel {
    private final PrecificacaoBezerroRepository repositorio;
    private final ValorReferenciaRepository valorReferenciaRepository;
    private final PrecificacaoBezerroMapper precificacaoBezerroMapper;
    private final TaskHelper taskHelper;
    private final MutableLiveData<SimulacaoState> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> error = new MutableLiveData<>(null);
    @Inject
    public SimulacaoViewModel(TaskHelper taskHelper,
                              PrecificacaoBezerroRepository repositorio,
                              ValorReferenciaRepository valorReferenciaRepository,
                              PrecificacaoBezerroMapper precificacaoBezerroMapper) {
        this.taskHelper = taskHelper;
        this.repositorio = repositorio;
        this.valorReferenciaRepository = valorReferenciaRepository;
        this.precificacaoBezerroMapper = precificacaoBezerroMapper;
    }

    public LiveData<SimulacaoState> getState() { return state; }
    public LiveData<Throwable> getError() { return error; }

    public void simular(BigDecimal peso, Integer quantidade) {
        taskHelper.execute(
                () -> precificacaoBezerroMapper.mapFrom(precificarComFrete(peso, quantidade)),
                state::postValue,
                error::postValue
        );
    }

    public void limpar() {
        state.setValue(null);
    }

    private PrecificacaoBezerro precificarComFrete(BigDecimal peso, Integer quantidade) {
        return new PrecificacaoBezerroImplementation(
                new PrecificacaoBezerroComFrete(repositorio), valorReferenciaRepository)
                .executar(peso, quantidade);
    }
}
