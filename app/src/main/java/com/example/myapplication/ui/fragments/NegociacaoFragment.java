package com.example.myapplication.ui.fragments;

import static com.example.myapplication.ui.helpers.FormatHelper.formatCurrency;
import static com.example.myapplication.ui.helpers.ViewHelper.getBigDecimal;
import static com.example.myapplication.ui.helpers.ViewHelper.isNotEmpty;
import static com.example.myapplication.ui.helpers.ViewHelper.setHelperText;
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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentNegociacaoBinding;
import com.example.myapplication.ui.adapters.CategoriaAdapter;
import com.example.myapplication.ui.adapters.RacaAdapter;
import com.example.myapplication.ui.helpers.AlertHelper;
import com.example.myapplication.ui.helpers.TextWatcherHelper;
import com.example.myapplication.ui.state.CategoriaState;
import com.example.myapplication.ui.state.CorretorState;
import com.example.myapplication.ui.state.EmpresaState;
import com.example.myapplication.ui.state.FreteState;
import com.example.myapplication.ui.state.NegociacaoState;
import com.example.myapplication.ui.state.PrecificacaoFreteState;
import com.example.myapplication.ui.state.RacaState;
import com.example.myapplication.ui.state.negociacao.CotacaoState;
import com.example.myapplication.ui.state.negociacao.FechamentoState;
import com.example.myapplication.ui.state.negociacao.PropostaState;
import com.example.myapplication.ui.viewmodel.CategoriaViewModel;
import com.example.myapplication.ui.viewmodel.CorretorViewModel;
import com.example.myapplication.ui.viewmodel.EmpresaViewModel;
import com.example.myapplication.ui.viewmodel.NegociacaoViewModel;
import com.example.myapplication.ui.viewmodel.PrecificacaoFreteViewModel;
import com.example.myapplication.ui.viewmodel.RacaViewModel;
import com.example.myapplication.ui.viewmodel.TransporteViewModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NegociacaoFragment extends Fragment {
    private FragmentNegociacaoBinding binding;
    private CategoriaAdapter categoriaAdapter;
    private RacaAdapter racaAdapter;
    private RacaViewModel racaViewModel;
    private CategoriaViewModel categoriaViewModel;
    private TransporteViewModel transporteViewModel;
    private CorretorViewModel corretorViewModel;
    private EmpresaViewModel empresaViewModel;
    private NegociacaoViewModel negociacaoViewModel;
    private PrecificacaoFreteViewModel precificacaoFreteViewModel;
    private TextWatcher freteTextWatcher;
    private double peso;
    private int quantidade;
    private CategoriaState categoriaAtual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNegociacaoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializar();
    }

    private void inicializar() {
        extrairArgumentosDeNavegacao();
        iniciarDadosEspecificacao();
        inicializarDependencias();
        configurarEventosDeClique();
        configurarComportamentosDeTela();
        observarEstadosDasViewModels();
        processarCotacao();
    }

    private void extrairArgumentosDeNavegacao() {
        NegociacaoFragmentArgs args = NegociacaoFragmentArgs.fromBundle(requireArguments());
        quantidade = args.getCargaTotal();
        peso = args.getPesoMedio();
    }

    private void inicializarDependencias() {
        racaViewModel = new ViewModelProvider(requireActivity()).get(RacaViewModel.class);
        categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);
        transporteViewModel = new ViewModelProvider(requireActivity()).get(TransporteViewModel.class);
        corretorViewModel = new ViewModelProvider(requireActivity()).get(CorretorViewModel.class);
        empresaViewModel = new ViewModelProvider(requireActivity()).get(EmpresaViewModel.class);
        negociacaoViewModel = new ViewModelProvider(requireActivity()).get(NegociacaoViewModel.class);
        precificacaoFreteViewModel = new ViewModelProvider(requireActivity()).get(PrecificacaoFreteViewModel.class);
        freteTextWatcher = TextWatcherHelper.SimpleTextWatcher(this::aoFreteManualAlterado);
    }

    private void configurarComportamentosDeTela() {
        configurarRecyclerViewRacas();
        configurarRecyclerViewCategoria();
        configurarTextWatcherFrete();
    }

    private void configurarRecyclerViewRacas() {
        racaAdapter = new RacaAdapter(this::aoSelecionarRacaNaLista);
        configurarRecyclerViewHorizontal(binding.listaRacas, racaAdapter);
    }

    private void configurarRecyclerViewCategoria() {
        categoriaAdapter = new CategoriaAdapter(this::aoSelecionarCategoriaNaLista);
        configurarRecyclerViewHorizontal(binding.listaCategorias, categoriaAdapter);
    }

    private void configurarRecyclerViewHorizontal(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.Adapter<?> adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void configurarTextWatcherFrete() {
        binding.campoFreteEntrada.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) binding.campoFreteEntrada.addTextChangedListener(freteTextWatcher);
            else binding.campoFreteEntrada.removeTextChangedListener(freteTextWatcher);
        });
    }

    private void configurarEventosDeClique() {
        configurarCliqueEmpresa();
        configurarCliqueCorretor();
        configurarCliqueFrete();
        configurarCliqueVoltar();
    }

    private void configurarCliqueEmpresa() {
        binding.cardEmpresa.setOnClickListener(v -> exibirBottomSheetEmpresa());
    }

    private void configurarCliqueCorretor() {
        binding.cardCorretor.setOnClickListener(v -> exibirBottomSheetCorretor());
    }

    private void configurarCliqueFrete() {
        binding.cardFrete.setOnClickListener(v -> navegarParaSimulacaoDeFrete());
    }

    private void configurarCliqueVoltar() {
        binding.toolbar.setOnClickListener(v -> executarNavegacaoDeRetorno());
    }

    private void observarEstadosDasViewModels() {
        observarEstadoDasRacas();
        observarEstadosDasCategorias();
        observarEstadoDaNegociacao();
        observarEstadoDoCorretor();
        observarEstadoDaEmpresa();
        observarEstadoDoFrete();
    }

    private void observarEstadoDasRacas() {
        racaViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarListaRacas);
    }

    private void observarEstadosDasCategorias() {
        categoriaViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarListaCategoria);
        categoriaViewModel.getCategoriaSelecionada().observe(getViewLifecycleOwner(), this::aoAlterarCategoriaSelecionada);
    }

    private void observarEstadoDaNegociacao() {
        negociacaoViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarTabela);
    }

    private void observarEstadoDoCorretor() {
        corretorViewModel.getCorretorSelecionado().observe(getViewLifecycleOwner(), this::aoSelecionarCorretorNaLista);
    }

    private void observarEstadoDaEmpresa() {
        empresaViewModel.getEmpresaSelecionada().observe(getViewLifecycleOwner(), this::aoSelecionarEmpresaNaLista);
    }

    private void observarEstadoDoFrete() {
        precificacaoFreteViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarEstadoCardFrete);
    }

    private void iniciarDadosEspecificacao() {
        atualizarQuantidadeAnimais();
        atualizarPesoMedioAnimais();
    }

    private void atualizarQuantidadeAnimais() {
        exibirTextoQuantidade(String.format(Locale.getDefault(), "%d", quantidade));
    }

    private void atualizarPesoMedioAnimais() {
        exibirTextoPesoMedio(String.format(Locale.getDefault(), "%.2f", peso));
    }

    private void processarCotacao() {
        negociacaoViewModel.processarCotacao(BigDecimal.valueOf(peso), quantidade);
    }

    private void processarProposta(@NonNull PrecificacaoFreteState freteState) {
        negociacaoViewModel.processarProposta(BigDecimal.valueOf(peso), quantidade,
                freteState.getValorTotal(), freteState.getFreteState());
    }

    private void processarFechamento(@NonNull CorretorState corretorState) {
        negociacaoViewModel.processarFechamento(BigDecimal.valueOf(peso), quantidade,
                corretorState.getComissao());
    }

    private void aoFreteManualAlterado() {
        if (!isNotEmpty(binding.campoFreteEntrada)) {
            limparFreteEDependencias();
            return;
        }
        precificacaoFreteViewModel.calcularIncidencia(obterValorTotalFrete(),
                BigDecimal.valueOf(peso), quantidade, FreteState.MANUAL);
    }

    private void limparFreteEDependencias() {
        limparHelperTextFrete();
        limparProposta();
        limparFechamento();
        limparCardFrete();
        limparSelecaoCorretor();
        limparCardCorretor();
    }

    private void aoSelecionarRacaNaLista(@NonNull RacaState racaUiState) {
        racaViewModel.selecionarRaca(racaUiState);
    }

    private void aoSelecionarCategoriaNaLista(@NonNull CategoriaState categoriaState) {
        categoriaViewModel.selecionarCategoria(categoriaState);
    }

    private void aoAlterarCategoriaSelecionada(@Nullable CategoriaState categoriaState) {
        categoriaAtual = categoriaState;
        if (isCategoriaNaoSelecionada(categoriaState)) return;
        transporteViewModel.recomendar(categoriaAtual.getId(), quantidade);
    }

    private void aoSelecionarEmpresaNaLista(@Nullable EmpresaState empresaState) {
        if (isEmpresaNaoSelecionada(empresaState)) return;
        exibirNomeEmpresa(empresaState.getNome());
    }

    private void aoSelecionarCorretorNaLista(@Nullable CorretorState corretorState) {
        if (isCorretorNaoSelecionado(corretorState)) {
            limparFechamento();
            limparCardCorretor();
            return;
        }
        atualizarEstadoCorretor(corretorState);
        processarFechamento(corretorState);
    }

    private void atualizarListaRacas(@NonNull List<RacaState> racas) {
        racaAdapter.submitList(racas);
    }

    private void atualizarListaCategoria(@NonNull List<CategoriaState> categorias) {
        categoriaAdapter.submitList(categorias);
    }

    private void atualizarTabela(@Nullable NegociacaoState negociacaoState) {
        if (isNegociacaoSemEstado(negociacaoState)) return;
        atualizarValoresCotado(negociacaoState.getCotacao());
        atualizarValoresPedido(negociacaoState.getProposta());
        atualizarValoresFechamento(negociacaoState.getFechamento());
    }

    private void atualizarValoresCotado(@Nullable CotacaoState cotacaoState) {
        if (isCotacaoSemEstado(cotacaoState)) return;
        preencherCamposIniciais(cotacaoState);
        exibirValorEtapaCotado(formatCurrency(cotacaoState.getValorPorCabeca()));
        exibirDescricaoEtapaCotado(formatCurrency(cotacaoState.getValorPorKg()));
    }

    private void atualizarValoresPedido(@Nullable PropostaState propostaState) {
        if (isPropostaSemEstado(propostaState)) return;
        if (!isFreteDescontado(propostaState)) return;
        exibirValorEtapaPedido(formatCurrency(propostaState.getValorPorCabeca()));
        exibirDescricaoEtapaPedido(formatCurrency(propostaState.getValorPorKg()));
        exibirBadgeFrete(formatCurrency(propostaState.getFretePorKg()));
        exibirValorFornecedor(formatCurrency(propostaState.getValorTotal()));
    }

    private void atualizarValoresFechamento(@Nullable FechamentoState fechamentoState) {
        if (isFechamentoSemEstado(fechamentoState)) return;
        if (isComissaoAplicada(fechamentoState)) return;
        exibirValorEtapaFinal(formatCurrency(fechamentoState.getValorPorCabeca()));
        exibirDescricaoEtapaFinal(formatCurrency(fechamentoState.getValorPorKg()));
        exibirBadgeCorretor(formatCurrency(fechamentoState.getComissaoPorKg()));
        exibirValorTotal(formatCurrency(fechamentoState.getValorTotal()));
    }

    private void atualizarEstadoCardFrete(@Nullable PrecificacaoFreteState freteState) {
        if (isFreteSemEstado(freteState)) return;
        processarProposta(freteState);
        atualizarEstadoCampoFrete(freteState);
        if (isFreteManualEmEdicao(freteState)) return;
        exibirTextValorFrete(formatarTituloValorFrete(freteState));
        exibirTextValorFretePorKg(formatarDescricaoValorFrete(freteState));
    }

    private void atualizarEstadoCampoFrete(@NonNull PrecificacaoFreteState freteState) {
        preencherCampoValorFrete(freteState);
        exibirHelperTextFrete(formatCurrency(freteState.getValorParcial()));
    }

    private void preencherCampoValorFrete(@NonNull PrecificacaoFreteState freteState) {
        if (isFreteManualEmEdicao(freteState)) return;
        preencherCampoValorFrete(formatCurrency(freteState.getValorTotal()));
    }

    private void atualizarEstadoCorretor(@NonNull CorretorState corretorState) {
        exibirNomeCorretor(corretorState.getNome());
        exibirDescricaoCorretor(formatarDescricaoCorretor(corretorState));
    }

    @NonNull
    private String formatarDescricaoCorretor(@NonNull CorretorState corretorState) {
        BigDecimal comissao = corretorState.getComissao();
        BigDecimal total = comissao.multiply(BigDecimal.valueOf(quantidade));
        return String.format(Locale.getDefault(), "R$ %s/%s · R$ %s",
                formatCurrency(comissao), corretorState.getTipoComissao(), formatCurrency(total));
    }

    @NonNull
    private String formatarTituloValorFrete(@NonNull PrecificacaoFreteState freteState) {
        return String.format(Locale.getDefault(), "R$ %s", formatCurrency(freteState.getValorTotal()));
    }

    @NonNull
    private String formatarDescricaoValorFrete(@NonNull PrecificacaoFreteState freteState) {
        return String.format(Locale.getDefault(), "R$ %s", formatCurrency(freteState.getValorParcial()));
    }

    private void exibirTextoQuantidade(@NonNull String valor) {
        setText(binding.textoValorQuantidade, valor);
    }

    private void exibirTextoPesoMedio(@NonNull String valor) {
        setText(binding.textoValorPeso, valor);
    }

    private void preencherCamposIniciais(@NonNull CotacaoState cotacaoState) {
        preencherCampoValorPorCabeca(formatCurrency(cotacaoState.getValorPorCabeca()));
        preencherCampoValorPorKg(formatCurrency(cotacaoState.getValorPorKg()));
    }

    private void preencherCampoValorFrete(@NonNull String valor) {
        setText(binding.campoFreteEntrada, valor);
    }

    private void preencherCampoValorPorCabeca(@NonNull String valor) {
        setText(binding.campoValorCabecaEntrada, valor);
    }

    private void preencherCampoValorPorKg(@NonNull String valor) {
        setText(binding.campoValorKgEntrada, valor);
    }

    private void exibirHelperTextFrete(@NonNull String incidencia) {
        setHelperText(binding.campoFreteLayout, getString(R.string.helper_frete, incidencia));
    }

    private void exibirTextValorFrete(@NonNull String texto) {
        setText(binding.textoTituloFrete, texto);
    }

    private void exibirTextValorFretePorKg(@NonNull String texto) {
        setText(binding.textoDescricaoFrete, texto);
    }

    private void exibirNomeCorretor(@NonNull String nome) {
        setText(binding.textoNomeCorretor, nome);
    }

    private void exibirDescricaoCorretor(@NonNull String descricao) {
        setText(binding.textoDescricaoCorretor, descricao);
    }

    private void exibirNomeEmpresa(@NonNull String nome) {
        setText(binding.textoNomeEmpresa, nome);
    }

    private void exibirValorEtapaCotado(@NonNull String valor) {
        setText(binding.textoValorEtapaCotado, valor);
    }

    private void exibirDescricaoEtapaCotado(@NonNull String descricao) {
        setText(binding.textoDescricaoEtapaCotado, descricao);
    }

    private void exibirValorEtapaPedido(@NonNull String valor) {
        setText(binding.textoValorEtapaPedido, valor);
    }

    private void exibirDescricaoEtapaPedido(@NonNull String descricao) {
        setText(binding.textoDescricaoEtapaPedido, descricao);
    }

    private void exibirValorEtapaFinal(@NonNull String valor) {
        setText(binding.textoValorEtapaFinal, valor);
    }

    private void exibirDescricaoEtapaFinal(@NonNull String descricao) {
        setText(binding.textoDescricaoEtapaFinal, descricao);
    }

    private void exibirBadgeFrete(@NonNull String valor) {
        setText(binding.textoBadgeEtapaFrete, String.format(Locale.getDefault(), "+R$ %s", valor));
    }

    private void exibirBadgeCorretor(@NonNull String valor) {
        setText(binding.textoBadgeEtapaCorretor, String.format(Locale.getDefault(), "+R$ %s", valor));
    }

    private void exibirValorFornecedor(@NonNull String valor) {
        setText(binding.textoValorFornecedor, valor);
    }

    private void exibirValorTotal(@NonNull String valor) {
        setText(binding.textoValorTotal, valor);
    }

    private void exibirBottomSheetEmpresa() {
        new EmpresaBottomSheetDialogFragment().show(getChildFragmentManager(), null);
    }

    private void exibirBottomSheetCorretor() {
        new CorretorBottomSheetDialogFragment().show(getChildFragmentManager(), null);
    }

    private void limparProposta() {
        negociacaoViewModel.limparProposta();
    }

    private void limparFechamento() {
        negociacaoViewModel.limparFechamento();
    }

    private void limparSelecaoCorretor() {
        corretorViewModel.limparSelecao();
    }

    private void limparCardFrete() {
        exibirTextValorFrete(getString(R.string.titulo_frete));
        exibirTextValorFretePorKg(getString(R.string.descricao_frete_vazio));
    }

    private void limparCardCorretor() {
        exibirNomeCorretor(getString(R.string.texto_sem_corretor));
        exibirDescricaoCorretor(getString(R.string.descricao_sem_comissao));
    }

    private void limparHelperTextFrete() {
        exibirHelperTextFrete(formatCurrency(BigDecimal.ZERO));
    }

    private boolean isCategoriaInvalidaParaFrete() {
        return categoriaAtual == null;
    }

    private boolean isFreteManualEmEdicao(@NonNull PrecificacaoFreteState freteState) {
        return isFreteManual(freteState) && !isCampoFreteVazio();
    }

    private boolean isFreteManual(@NonNull PrecificacaoFreteState freteState) {
        return freteState.getFreteState() == FreteState.MANUAL;
    }

    private boolean isCampoFreteVazio() {
        return !isNotEmpty(binding.campoFreteEntrada);
    }

    private boolean isCorretorNaoSelecionado(@Nullable CorretorState corretorState) {
        return corretorState == null;
    }

    private boolean isEmpresaNaoSelecionada(@Nullable EmpresaState empresaState) {
        return empresaState == null;
    }

    private boolean isCategoriaNaoSelecionada(@Nullable CategoriaState categoriaState) {
        return categoriaState == null;
    }

    private boolean isNegociacaoSemEstado(@Nullable NegociacaoState negociacaoState) {
        return negociacaoState == null;
    }

    private boolean isCotacaoSemEstado(@Nullable CotacaoState cotacaoState) {
        return cotacaoState == null;
    }

    private boolean isPropostaSemEstado(@Nullable PropostaState propostaState) {
        return propostaState == null;
    }

    private boolean isFreteDescontado(@NonNull PropostaState propostaState) {
        return propostaState.isFreteDescontado();
    }
    private boolean isFechamentoSemEstado(@Nullable FechamentoState fechamentoState) {
        return fechamentoState == null;
    }

    private boolean isComissaoAplicada(@NonNull FechamentoState fechamentoState) {
        return fechamentoState.isComissaoAplicada();
    }

    private boolean isFreteSemEstado(@Nullable PrecificacaoFreteState freteState) {
        return freteState == null;
    }

    @NonNull
    private BigDecimal obterValorTotalFrete() {
        return getBigDecimal(binding.campoFreteEntrada);
    }

    private void navegarParaSimulacaoDeFrete() {
        if (isCategoriaInvalidaParaFrete()) {
            exibirErroDeCategoriaParaFrete();
            return;
        }
        executarNavegacaoSimulacaoFrete();
    }


    private void exibirErroDeCategoriaParaFrete() {
        AlertHelper.showSnackBarErro(binding.getRoot(), getString(R.string.aviso_selecione_categoria_frete));
    }

    private void executarNavegacaoSimulacaoFrete() {
        NegociacaoFragmentDirections.ActionNegociacaoFragmentToSimulacaoFreteeFragment directions =
                NegociacaoFragmentDirections.actionNegociacaoFragmentToSimulacaoFreteeFragment()
                        .setCargaTotal(quantidade).setPesoMedio((float) peso);
        NavHostFragment.findNavController(this).navigate(directions);
    }

    private void executarNavegacaoDeRetorno() {
        NavHostFragment.findNavController(this).popBackStack();
    }
}