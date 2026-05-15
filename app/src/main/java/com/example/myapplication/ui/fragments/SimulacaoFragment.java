package com.example.myapplication.ui.fragments;

import static com.example.myapplication.ui.helpers.FormatHelper.formatCurrency;
import static com.example.myapplication.ui.helpers.FormatHelper.formatInteger;

import static com.example.myapplication.ui.helpers.TextWatcherHelper.simpleTextWatcher;
import static com.example.myapplication.ui.helpers.ViewHelper.anyEmpty;
import static com.example.myapplication.ui.helpers.ViewHelper.parseFloat;
import static com.example.myapplication.ui.helpers.ViewHelper.parseInt;
import static com.example.myapplication.ui.helpers.ViewHelper.setText;

import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentSimulacaoBinding;
import com.example.myapplication.ui.helpers.NavigationHelper;
import com.example.myapplication.ui.state.negociacao.CotacaoState;
import com.example.myapplication.ui.viewmodel.SimulacaoViewModel;

import java.math.BigDecimal;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SimulacaoFragment extends Fragment {
    private FragmentSimulacaoBinding binding;
    private SimulacaoViewModel simulacaoViewModel;
    private TextWatcher pesoWatcher;
    private TextWatcher quantidadeWatcher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSimulacaoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarDependencias();
        configurarComportamentosDeTela();
        observarEstadoDaViewModel();
    }

    @Override
    public void onDestroyView() {
        removerTextWatchers();
        binding = null;
        super.onDestroyView();
    }

    private void inicializarDependencias() {
        simulacaoViewModel = new ViewModelProvider(requireActivity()).get(SimulacaoViewModel.class);
    }

    private void configurarComportamentosDeTela() {
        configurarTextWatcherEntradas();
        configurarAcaoBotaoProsseguir();
    }

    private void observarEstadoDaViewModel() {
        simulacaoViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarCartao);
    }

    private void configurarTextWatcherEntradas() {
        pesoWatcher = simpleTextWatcher(this::aoAlterarEntrada);
        quantidadeWatcher = simpleTextWatcher(this::aoAlterarEntrada);
        binding.campoPesoEntrada.addTextChangedListener(pesoWatcher);
        binding.campoQuantidadeEntrada.addTextChangedListener(quantidadeWatcher);
    }

    private void removerTextWatchers() {
        if (binding == null) return;
        binding.campoPesoEntrada.removeTextChangedListener(pesoWatcher);
        binding.campoQuantidadeEntrada.removeTextChangedListener(quantidadeWatcher);
    }

    private void configurarAcaoBotaoProsseguir() {
        binding.botaoProsseguir.setOnClickListener(v -> processarTentativaDeProsseguir());
    }

    private void aoAlterarEntrada() {
        atualizarEstadoDoBotaoProsseguir();
        simularNegociacao();
    }

    private void processarTentativaDeProsseguir() {
        if (isEntradaInvalida()) return;
        navegarParaNegociacao();
    }

    private void simularNegociacao() {
        if (isEntradaInvalida()) {
            simulacaoViewModel.limpar();
            return;
        }
        simulacaoViewModel.processarCotacao(obterPesoMedio(), obterCargaTotal());
    }

    private void atualizarEstadoDoBotaoProsseguir() {
        binding.botaoProsseguir.setEnabled(isEntradaValida());
    }

    private void atualizarCartao(@Nullable CotacaoState estado) {
        if (isEstadoVazio(estado)) {
            limparCartao();
            return;
        }
        atualizarValorTotal(estado);
        atualizarQuantidade(estado);
        atualizarValorPorCabeca(estado);
        atualizarValorPorKg(estado);
    }

    private void atualizarValorTotal(@NonNull CotacaoState estado) {
        exibirValorTotal(formatCurrency(estado.getValorTotal()));
    }

    private void atualizarQuantidade(@NonNull CotacaoState estado) {
        exibirQuantidade(formatInteger(estado.getQuantidade()));
    }

    private void atualizarValorPorCabeca(@NonNull CotacaoState estado) {
        exibirValorPorCabeca(formatCurrency(estado.getValorPorCabeca()));
    }

    private void atualizarValorPorKg(@NonNull CotacaoState estado) {
        exibirValorPorKg(formatCurrency(estado.getValorPorKg()));
    }

    private void limparCartao() {
        exibirValorTotal(placeholderValorTotal());
        exibirQuantidade(placeholderQuantidade());
        exibirValorPorCabeca(placeholderValorMonetario());
        exibirValorPorKg(placeholderValorMonetario());
    }

    private void exibirValorTotal(@NonNull String valor) {
        setText(binding.textoValorTotalDestacado, valor);
    }

    private void exibirQuantidade(@NonNull String quantidade) {
        setText(binding.textoValorQuantidade, quantidade);
    }

    private void exibirValorPorCabeca(@NonNull String valor) {
        setText(binding.textoValorPorCabeca, valor);
    }

    private void exibirValorPorKg(@NonNull String valor) {
        setText(binding.textoValorPorKg, valor);
    }

    @NonNull
    private String placeholderValorTotal() {
        return getString(R.string.placeholder_valor_total);
    }

    @NonNull
    private String placeholderQuantidade() {
        return getString(R.string.placeholder_valor_quantidade);
    }

    @NonNull
    private String placeholderValorMonetario() {
        return getString(R.string.placeholder_valor_monetario);
    }

    private int obterCargaTotal() {
        return parseInt(binding.campoQuantidadeEntrada);
    }

    private BigDecimal obterPesoMedio() {
        return new BigDecimal(parseFloat(binding.campoPesoEntrada));
    }

    private boolean isEntradaValida() {
        return !anyEmpty(binding.campoQuantidadeEntrada, binding.campoPesoEntrada);
    }

    private boolean isEntradaInvalida() {
        return !isEntradaValida();
    }

    private boolean isEstadoVazio(@Nullable CotacaoState estado) {
        return estado == null;
    }

    private void navegarParaNegociacao() {
        NavigationHelper.navegar(this, R.id.simulacaoFragment,
                SimulacaoFragmentDirections.actionSimulacaoFragmentToNegociacaoFragment()
                        .setCargaTotal(obterCargaTotal())
                        .setPesoMedio(obterPesoMedio().floatValue()));
    }
}
