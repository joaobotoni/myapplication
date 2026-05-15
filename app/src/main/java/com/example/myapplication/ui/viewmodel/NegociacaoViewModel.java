package com.example.myapplication.ui.viewmodel;

import static com.example.myapplication.utils.DecimalUtil.ARREDONDAMENTO_PADRAO;
import static com.example.myapplication.utils.DecimalUtil.CEM;
import static com.example.myapplication.utils.DecimalUtil.ESCALA_CALCULO;
import static com.example.myapplication.utils.DecimalUtil.ESCALA_MONETARIA;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.models.PrecificacaoBezerro;
import com.example.myapplication.data.repositories.PrecificacaoBezerroRepository;
import com.example.myapplication.data.repositories.ValorReferenciaRepository;
import com.example.myapplication.domain.contract.PrecificacaoBezerroStrategy;
import com.example.myapplication.domain.implementation.PrecificacaoBezerroImplementation;
import com.example.myapplication.domain.strategy.PrecificacaoBezerroComFreteEComissao;
import com.example.myapplication.domain.strategy.PrecificacaoBezerroSemFrete;
import com.example.myapplication.ui.helpers.TaskHelper;
import com.example.myapplication.ui.state.FreteState;
import com.example.myapplication.ui.state.negociacao.CotacaoState;
import com.example.myapplication.ui.state.negociacao.FechamentoState;
import com.example.myapplication.ui.state.negociacao.NegociacaoState;
import com.example.myapplication.ui.state.negociacao.PropostaState;

import java.math.BigDecimal;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class NegociacaoViewModel extends ViewModel {
    private final PrecificacaoBezerroRepository precificacaoBezerroRepository;
    private final ValorReferenciaRepository valorReferenciaRepository;
    private final TaskHelper taskHelper;
    private final TaskHelper.Cancellables tarefas = new TaskHelper.Cancellables();
    private final MutableLiveData<NegociacaoState> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> error = new MutableLiveData<>(null);
    private final LiveData<PropostaState> proposta = Transformations.map(state, s -> s != null ? s.getProposta() : null);
    private final LiveData<FechamentoState> fechamento = Transformations.map(state, s -> s != null ? s.getFechamento() : null);
    private final LiveData<Double> variacao = Transformations.map(state, this::extrairVariacao);

    @Inject
    public NegociacaoViewModel(PrecificacaoBezerroRepository precificacaoBezerroRepository,
                               ValorReferenciaRepository valorReferenciaRepository, TaskHelper taskHelper) {
        this.precificacaoBezerroRepository = precificacaoBezerroRepository;
        this.valorReferenciaRepository = valorReferenciaRepository;
        this.taskHelper = taskHelper;
    }

    public LiveData<NegociacaoState> getState() {
        return state;
    }

    public LiveData<PropostaState> getProposta() {
        return proposta;
    }

    public LiveData<FechamentoState> getFechamento() {
        return fechamento;
    }

    public LiveData<Double> getVariacao() {
        return variacao;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public void processarNegociacao(CotacaoState cotacao, BigDecimal peso, Integer quantidade, BigDecimal freteTotalLote, FreteState freteState, BigDecimal comissaoTotal) {
        tarefas.adicionar(taskHelper.execute(
                () -> criarNegociacao(cotacao, peso, quantidade, freteTotalLote, freteState, comissaoTotal),
                state::postValue,
                error::postValue
        ));
    }

    public void recalcularPropostaPorKg(CotacaoState cotacao, FechamentoState fechamento, BigDecimal novoValorPorKg, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, FreteState freteState) {
        tarefas.adicionar(taskHelper.execute(
                () -> atualizarPropostaPorKg(cotacao, fechamento, novoValorPorKg, peso, quantidade, fretePorKg, freteState),
                state::postValue,
                error::postValue
        ));
    }

    public void recalcularPropostaPorCabeca(CotacaoState cotacao, FechamentoState fechamento, BigDecimal novoValorPorCabeca, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, FreteState freteState) {
        tarefas.adicionar(taskHelper.execute(
                () -> atualizarPropostaPorCabeca(cotacao, fechamento, novoValorPorCabeca, peso, quantidade, fretePorKg, freteState),
                state::postValue,
                error::postValue
        ));
    }

    public void limpar() {
        state.setValue(new NegociacaoState(null, null, null));
    }

    public void limparParcialmente(CotacaoState cotacao) {
        state.setValue(new NegociacaoState(cotacao, null, null));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tarefas.cancelarTudo();
    }

    private NegociacaoState criarNegociacao(CotacaoState cotacao, BigDecimal peso, Integer quantidade, BigDecimal freteTotalLote, FreteState freteState, BigDecimal comissaoTotal) {
        BigDecimal fretePorKg = distribuirFretePorKg(freteTotalLote, peso, quantidade);
        PropostaState proposta = precificarProposta(peso, quantidade, fretePorKg, freteState);
        BigDecimal comissaoPorKg = distribuirComissaoPorKg(comissaoTotal, peso);
        FechamentoState fechamento = precificarFechamento(peso, quantidade, comissaoPorKg);
        return new NegociacaoState(cotacao, proposta, fechamento);
    }

    private NegociacaoState atualizarPropostaPorKg(CotacaoState cotacao, FechamentoState fechamento, BigDecimal novoValorPorKg, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, FreteState freteState) {
        BigDecimal novoValorPorCabeca = converterKgParaCabeca(novoValorPorKg, peso);
        return atualizarPropostaEFechamento(cotacao, fechamento, novoValorPorKg, novoValorPorCabeca, peso, quantidade, fretePorKg, freteState);
    }

    private NegociacaoState atualizarPropostaPorCabeca(CotacaoState cotacao, FechamentoState fechamento, BigDecimal novoValorPorCabeca, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, FreteState freteState) {
        BigDecimal novoValorPorKg = converterCabecaParaKg(novoValorPorCabeca, peso);
        return atualizarPropostaEFechamento(cotacao, fechamento, novoValorPorKg, novoValorPorCabeca, peso, quantidade, fretePorKg, freteState);
    }

    private NegociacaoState atualizarPropostaEFechamento(CotacaoState cotacao, FechamentoState fechamento, BigDecimal valorPorKg, BigDecimal valorPorCabeca, BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, FreteState freteState) {
        PropostaState propostaReajustada = novaProposta(valorPorKg, valorPorCabeca, quantidade, fretePorKg, freteState);
        FechamentoState fechamentoReajustado = atualizarFechamento(fechamento, valorPorKg, fretePorKg, peso, quantidade);
        return new NegociacaoState(cotacao, propostaReajustada, fechamentoReajustado);
    }

    private PropostaState precificarProposta(BigDecimal peso, Integer quantidade, BigDecimal fretePorKg, FreteState freteState) {
        PrecificacaoBezerro p = precificar(new PrecificacaoBezerroSemFrete(precificacaoBezerroRepository, fretePorKg), peso, quantidade);
        return novaProposta(p.getValorPorKg(), p.getValorPorCabeca(), quantidade, fretePorKg, freteState);
    }

    private FechamentoState precificarFechamento(BigDecimal peso, Integer quantidade, BigDecimal comissaoPorKg) {
        PrecificacaoBezerro p = precificar(new PrecificacaoBezerroComFreteEComissao(precificacaoBezerroRepository, comissaoPorKg), peso, quantidade);
        return novoFechamento(p.getValorPorKg(), p.getValorPorCabeca(), p.getValorTotal(), comissaoPorKg);
    }

    private PrecificacaoBezerro precificar(PrecificacaoBezerroStrategy strategy, BigDecimal peso, Integer quantidade) {
        return new PrecificacaoBezerroImplementation(strategy, valorReferenciaRepository).executar(peso, quantidade);
    }

    private PropostaState novaProposta(BigDecimal valorPorKg, BigDecimal valorPorCabeca, Integer quantidade, BigDecimal fretePorKg, FreteState freteState) {
        return new PropostaState(valorPorKg, valorPorCabeca, calcularTotalLote(valorPorCabeca, quantidade), fretePorKg, freteState);
    }

    private FechamentoState novoFechamento(BigDecimal valorPorKg, BigDecimal valorPorCabeca, BigDecimal valorTotal, BigDecimal comissaoPorKg) {
        return new FechamentoState(valorPorKg, valorPorCabeca, valorTotal, comissaoPorKg);
    }

    private FechamentoState atualizarFechamento(FechamentoState fechamento, BigDecimal valorPorKgProposta, BigDecimal fretePorKg, BigDecimal peso, Integer quantidade) {
        if (fechamento == null || fechamento.getComissaoPorKg() == null) return fechamento;
        BigDecimal novoValorPorKg = valorPorKgProposta.add(fretePorKg).add(fechamento.getComissaoPorKg()).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
        BigDecimal novoValorPorCabeca = converterKgParaCabeca(novoValorPorKg, peso);
        return new FechamentoState(novoValorPorKg, novoValorPorCabeca, calcularTotalLote(novoValorPorCabeca, quantidade), fechamento.getComissaoPorKg());
    }

    private BigDecimal distribuirFretePorKg(BigDecimal freteTotalLote, BigDecimal peso, Integer quantidade) {
        BigDecimal pesoTotal = peso.multiply(BigDecimal.valueOf(quantidade));
        if (pesoTotal.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return freteTotalLote.divide(pesoTotal, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO)
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal distribuirComissaoPorKg(BigDecimal comissaoTotal, BigDecimal peso) {
        if (peso.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return comissaoTotal.divide(peso, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO)
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal converterKgParaCabeca(BigDecimal valorPorKg, BigDecimal peso) {
        return valorPorKg.multiply(peso).setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal converterCabecaParaKg(BigDecimal valorPorCabeca, BigDecimal peso) {
        return valorPorCabeca.divide(peso, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO)
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal calcularTotalLote(BigDecimal valorPorCabeca, Integer quantidade) {
        return valorPorCabeca.multiply(BigDecimal.valueOf(quantidade))
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private Double extrairVariacao(NegociacaoState s) {
        if (s == null || s.getCotacao() == null || s.getFechamento() == null) return 0.0;
        return calcularVariacao(s.getCotacao(), s.getFechamento());
    }

    private double calcularVariacao(CotacaoState cotacao, FechamentoState fechamentoState) {
        BigDecimal valorCotacao = cotacao.getValorTotal();
        BigDecimal valorFechamento = fechamentoState.getValorTotal();
        return valorFechamento.subtract(valorCotacao)
                .divide(valorCotacao, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO)
                .multiply(CEM)
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO)
                .doubleValue();
    }
}
