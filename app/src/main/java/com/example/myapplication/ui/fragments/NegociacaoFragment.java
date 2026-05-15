package com.example.myapplication.ui.fragments;

import static com.example.myapplication.ui.helpers.AlertHelper.showSnackBarErro;
import static com.example.myapplication.ui.helpers.AlertHelper.showSnackBarSucesso;
import static com.example.myapplication.ui.helpers.FormatHelper.formatCurrency;
import static com.example.myapplication.ui.helpers.NavigationHelper.navegar;
import static com.example.myapplication.ui.helpers.ViewHelper.getCheckedChipText;
import static com.example.myapplication.ui.helpers.ViewHelper.isNotEmpty;
import static com.example.myapplication.ui.helpers.ViewHelper.parseDecimal;
import static com.example.myapplication.ui.helpers.ViewHelper.parseInt;
import static com.example.myapplication.ui.helpers.ViewHelper.selectChip;
import static com.example.myapplication.ui.helpers.ViewHelper.setHelperText;
import static com.example.myapplication.ui.helpers.ViewHelper.setText;
import static com.example.myapplication.ui.helpers.ViewHelper.setTextSafely;
import static com.example.myapplication.utils.DecimalUtil.createCurrencyFormat;

import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentNegociacaoBinding;
import com.example.myapplication.ui.adapters.CategoriaAdapter;
import com.example.myapplication.ui.adapters.RacaAdapter;
import com.example.myapplication.ui.helpers.NavigationHelper;
import com.example.myapplication.ui.helpers.TextWatcherHelper;
import com.example.myapplication.ui.state.CorretorState;
import com.example.myapplication.ui.state.EmpresaState;
import com.example.myapplication.ui.state.FreteState;
import com.example.myapplication.ui.state.PrecificacaoFreteState;
import com.example.myapplication.ui.state.animal.AnimalState;
import com.example.myapplication.ui.state.animal.CategoriaState;
import com.example.myapplication.ui.state.animal.EspecificacaoAnimalState;
import com.example.myapplication.ui.state.animal.RacaState;
import com.example.myapplication.ui.state.negociacao.CotacaoState;
import com.example.myapplication.ui.state.negociacao.FechamentoState;
import com.example.myapplication.ui.state.negociacao.PropostaState;
import com.example.myapplication.ui.viewmodel.AnimalViewModel;
import com.example.myapplication.ui.viewmodel.CategoriaViewModel;
import com.example.myapplication.ui.viewmodel.CorretorViewModel;
import com.example.myapplication.ui.viewmodel.EmpresaViewModel;
import com.example.myapplication.ui.viewmodel.NegociacaoViewModel;
import com.example.myapplication.ui.viewmodel.PrecificacaoFreteViewModel;
import com.example.myapplication.ui.viewmodel.RacaViewModel;
import com.example.myapplication.ui.viewmodel.SimulacaoViewModel;
import com.example.myapplication.ui.viewmodel.TransporteViewModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NegociacaoFragment extends Fragment {

    private static final String TAG_BOTTOM_SHEET_EMPRESA = "EmpresaBottomSheet";
    private static final String TAG_BOTTOM_SHEET_CORRETOR = "CorretorBottomSheet";

    private FragmentNegociacaoBinding binding;
    private CategoriaAdapter categoriaAdapter;
    private RacaAdapter racaAdapter;

    private RacaViewModel racaViewModel;
    private CategoriaViewModel categoriaViewModel;
    private TransporteViewModel transporteViewModel;
    private CorretorViewModel corretorViewModel;
    private EmpresaViewModel empresaViewModel;
    private SimulacaoViewModel simulacaoViewModel;
    private NegociacaoViewModel negociacaoViewModel;
    private PrecificacaoFreteViewModel precificacaoFreteViewModel;
    private AnimalViewModel animalViewModel;

    private TextWatcher freteTextWatcher;
    private TextWatcher especificacaoTextWatcher;
    private TextWatcher valorCabecaTextWatcher;
    private TextWatcher valorKgTextWatcher;

    private double peso;
    private int quantidade;

    private CotacaoState cotacaoAtual;
    private PropostaState propostaAtual;
    private FechamentoState fechamentoAtual;
    private PrecificacaoFreteState freteAtual;
    private CorretorState corretorAtual;
    private CategoriaState categoriaAtual;
    private RacaState racaAtual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNegociacaoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializar();
    }

    @Override
    public void onDestroyView() {
        removerTextWatchers();
        super.onDestroyView();
        binding = null;
    }

    private void inicializar() {
        extrairArgumentosDeNavegacao();
        iniciarDadosEspecificacao();
        inicializarDependencias();
        configurarComportamentosDeTela();
        configurarEventosDeClique();
        observarEstadosDasViewModels();
        processarCotacao();
    }

    private void extrairArgumentosDeNavegacao() {
        NegociacaoFragmentArgs args = NegociacaoFragmentArgs.fromBundle(requireArguments());
        quantidade = args.getCargaTotal();
        peso = args.getPesoMedio();
    }

    private void iniciarDadosEspecificacao() {
        exibirQuantidadeAnimais();
        exibirPesoMedioAnimais();
    }

    private void inicializarDependencias() {
        inicializarViewModels();
        inicializarTextWatchers();
    }

    private void inicializarViewModels() {
        racaViewModel = new ViewModelProvider(requireActivity()).get(RacaViewModel.class);
        categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);
        transporteViewModel = new ViewModelProvider(requireActivity()).get(TransporteViewModel.class);
        corretorViewModel = new ViewModelProvider(requireActivity()).get(CorretorViewModel.class);
        empresaViewModel = new ViewModelProvider(requireActivity()).get(EmpresaViewModel.class);
        simulacaoViewModel = new ViewModelProvider(requireActivity()).get(SimulacaoViewModel.class);
        negociacaoViewModel = new ViewModelProvider(requireActivity()).get(NegociacaoViewModel.class);
        precificacaoFreteViewModel = new ViewModelProvider(requireActivity()).get(PrecificacaoFreteViewModel.class);
        animalViewModel = new ViewModelProvider(requireActivity()).get(AnimalViewModel.class);
    }

    private void inicializarTextWatchers() {
        especificacaoTextWatcher = TextWatcherHelper.simpleTextWatcher(this::aoEspecificacaoAlterada);
        freteTextWatcher = TextWatcherHelper.moneyTextWatcher(Double.MAX_VALUE, createCurrencyFormat(), this::aoFreteManualAlterado);
        valorCabecaTextWatcher = TextWatcherHelper.moneyTextWatcher(Double.MAX_VALUE, createCurrencyFormat(), this::aoValorCabecaAlterado);
        valorKgTextWatcher = TextWatcherHelper.moneyTextWatcher(Double.MAX_VALUE, createCurrencyFormat(), this::aoValorKgAlterado);
    }

    private void configurarComportamentosDeTela() {
        configurarRecyclerViews();
        anexarTextWatchers();
        configurarChipGroupSexo();
    }

    private void configurarRecyclerViews() {
        configurarRecyclerViewRacas();
        configurarRecyclerViewCategorias();
    }

    private void configurarRecyclerViewRacas() {
        racaAdapter = new RacaAdapter(this::aoSelecionarRacaNaLista);
        configurarRecyclerViewHorizontal(binding.listaRacas, racaAdapter);
    }

    private void configurarRecyclerViewCategorias() {
        categoriaAdapter = new CategoriaAdapter(this::aoSelecionarCategoriaNaLista);
        configurarRecyclerViewHorizontal(binding.listaCategorias, categoriaAdapter);
    }

    private void configurarRecyclerViewHorizontal(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.Adapter<?> adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void anexarTextWatchers() {
        binding.campoFreteEntrada.addTextChangedListener(freteTextWatcher);
        binding.campoIdadeEntrada.addTextChangedListener(especificacaoTextWatcher);
        binding.campoValorCabecaEntrada.addTextChangedListener(valorCabecaTextWatcher);
        binding.campoValorKgEntrada.addTextChangedListener(valorKgTextWatcher);
    }

    private void removerTextWatchers() {
        if (binding == null) return;
        binding.campoFreteEntrada.removeTextChangedListener(freteTextWatcher);
        binding.campoIdadeEntrada.removeTextChangedListener(especificacaoTextWatcher);
        binding.campoValorCabecaEntrada.removeTextChangedListener(valorCabecaTextWatcher);
        binding.campoValorKgEntrada.removeTextChangedListener(valorKgTextWatcher);
    }

    private void configurarChipGroupSexo() {
        binding.listaSexos.setOnCheckedStateChangeListener((group, checkedIds) -> aoEspecificacaoAlterada());
    }

    private void configurarEventosDeClique() {
        configurarCliqueEmpresa();
        configurarCliqueCorretor();
        configurarCliqueFrete();
        configurarCliqueVoltar();
        configurarCliqueFinalizar();
    }

    private void configurarCliqueEmpresa() {
        binding.cardEmpresa.setOnClickListener(v -> exibirBottomSheetEmpresa());
    }

    private void configurarCliqueCorretor() {
        binding.cardCorretor.setOnClickListener(v -> abrirSeletorCorretor());
    }

    private void configurarCliqueFrete() {
        binding.cardFrete.setOnClickListener(v -> navegarParaSimulacaoDeFrete());
    }

    private void configurarCliqueVoltar() {
        binding.toolbar.setOnClickListener(v -> NavigationHelper.voltar(this));
    }

    private void configurarCliqueFinalizar() {
        binding.botaoFinalizar.setOnClickListener(v -> finalizar());
    }

    private void observarEstadosDasViewModels() {
        observarCategorias();
        observarRacas();
        observarAnimal();
        observarCotacao();
        observarCorretor();
        observarProposta();
        observarFechamento();
        observarFrete();
        observarVariacao();
        observarEmpresa();
    }

    private void observarRacas() {
        racaViewModel.getState().observe(getViewLifecycleOwner(), this::onListaRacasAtualizada);
        racaViewModel.getRacaSelecionada().observe(getViewLifecycleOwner(), this::onRacaSelecionada);
    }

    private void observarCategorias() {
        categoriaViewModel.getState().observe(getViewLifecycleOwner(), this::onListaCategoriasAtualizada);
        categoriaViewModel.getCategoriaSelecionada().observe(getViewLifecycleOwner(), this::onCategoriaSelecionada);
    }

    private void observarCotacao() {
        simulacaoViewModel.getState().observe(getViewLifecycleOwner(), this::onCotacaoAtualizada);
    }

    private void observarProposta() {
        negociacaoViewModel.getProposta().observe(getViewLifecycleOwner(), this::onPropostaAtualizada);
    }

    private void observarFechamento() {
        negociacaoViewModel.getFechamento().observe(getViewLifecycleOwner(), this::onFechamentoAtualizado);
    }

    private void observarFrete() {
        precificacaoFreteViewModel.getState().observe(getViewLifecycleOwner(), this::onFreteAtualizado);
    }

    private void observarVariacao() {
        negociacaoViewModel.getVariacao().observe(getViewLifecycleOwner(), this::exibirVariacaoPercentual);
    }

    private void observarCorretor() {
        corretorViewModel.getCorretorSelecionado().observe(getViewLifecycleOwner(), this::onCorretorSelecionado);
    }

    private void observarEmpresa() {
        empresaViewModel.getEmpresaSelecionada().observe(getViewLifecycleOwner(), this::onEmpresaSelecionada);
    }

    private void observarAnimal() {
        animalViewModel.getAnimalState().observe(getViewLifecycleOwner(), this::onAnimalAtualizado);
    }

    private void onListaRacasAtualizada(@NonNull List<RacaState> racas) {
        racaAdapter.submitList(racas);
    }

    private void onListaCategoriasAtualizada(@NonNull List<CategoriaState> categorias) {
        categoriaAdapter.submitList(categorias);
    }

    private void onRacaSelecionada(@Nullable RacaState raca) {
        racaAtual = raca;
        if (isRacaNaoSelecionada(raca)) return;
        animalViewModel.atualizarRaca(raca);
        atualizarEstadoDoBotaoFinalizar();
    }

    private void onCategoriaSelecionada(@Nullable CategoriaState categoria) {
        categoriaAtual = categoria;
        if (isCategoriaNaoSelecionada(categoria)) return;
        animalViewModel.atualizarCategoria(categoria);
        transporteViewModel.recomendar(categoria.getId(), quantidade);
        atualizarEstadoDoBotaoFinalizar();
    }

    private void onCotacaoAtualizada(@Nullable CotacaoState cotacao) {
        cotacaoAtual = cotacao;
        atualizarValoresCotado(cotacao);
        atualizarEstadoDoBotaoFinalizar();
    }

    private void onPropostaAtualizada(@Nullable PropostaState proposta) {
        propostaAtual = proposta;
        atualizarValoresPedido(proposta);
        atualizarEstadoDoBotaoFinalizar();
    }

    private void onFechamentoAtualizado(@Nullable FechamentoState fechamento) {
        fechamentoAtual = fechamento;
        atualizarValoresFechamento(fechamento);
        atualizarEstadoDoBotaoFinalizar();
    }

    private void onFreteAtualizado(@Nullable PrecificacaoFreteState frete) {
        freteAtual = frete;
        atualizarEstadoCardFrete(frete);
    }

    private void onCorretorSelecionado(@Nullable CorretorState corretor) {
        corretorAtual = corretor;
        if (isCorretorNaoSelecionado(corretor)) {
            limparCardCorretor();
            return;
        }
        exibirDadosCorretor(corretor);
        processarFechamento(corretor);
    }

    private void onEmpresaSelecionada(@Nullable EmpresaState empresa) {
        if (isEmpresaNaoSelecionada(empresa)) return;
        exibirNomeEmpresa(empresa.getNome());
    }

    private void onAnimalAtualizado(@Nullable AnimalState animal) {
        if (animal == null) return;
        restaurarEspecificacao(animal.getEspecificacao());
    }

    private void aoFreteManualAlterado() {
        if (isCampoFreteVazio() || isValorZero(obterValorTotalFrete())) {
            limparFrete();
            return;
        }
        calcularIncidenciaFreteManual();
    }

    private void aoValorCabecaAlterado() {
        recalcularPorCabeca(obterValorPorCabeca(), cotacaoAtual, fechamentoAtual, freteAtual);
    }

    private void aoValorKgAlterado() {
        recalcularPorKg(obterValorPorKg(), cotacaoAtual, fechamentoAtual, freteAtual);
    }

    private void aoEspecificacaoAlterada() {
        animalViewModel.atualizarEspecificacao(obterValorSexoSelecionado(), obterTextoIdade());
        atualizarEstadoDoBotaoFinalizar();
    }

    private void aoSelecionarRacaNaLista(@NonNull RacaState raca) {
        racaViewModel.selecionarRaca(raca);
    }

    private void aoSelecionarCategoriaNaLista(@NonNull CategoriaState categoria) {
        categoriaViewModel.selecionarCategoria(categoria);
    }

    private void processarCotacao() {
        simulacaoViewModel.processarCotacao(BigDecimal.valueOf(peso), quantidade);
    }

    private void processarProposta(@NonNull PrecificacaoFreteState frete) {
        if (isCotacaoIndisponivel()) return;
        negociacaoViewModel.processarNegociacao(cotacaoAtual, BigDecimal.valueOf(peso), quantidade, frete.getValorTotal(), frete.getFreteState(), obterComissaoTotal());
    }

    private void processarFechamento(@NonNull CorretorState corretor) {
        if (isCotacaoIndisponivel() || isFreteIndisponivel()) return;
        negociacaoViewModel.processarNegociacao(cotacaoAtual, BigDecimal.valueOf(peso), quantidade, freteAtual.getValorTotal(), freteAtual.getFreteState(), corretor.getComissao());
    }

    private void calcularIncidenciaFreteManual() {
        precificacaoFreteViewModel.calcularIncidencia(obterValorTotalFrete(), BigDecimal.valueOf(peso), quantidade, FreteState.MANUAL);
    }

    private void recalcularPorCabeca(@NonNull BigDecimal valor, @Nullable CotacaoState cotacao, @Nullable FechamentoState fechamento, @Nullable PrecificacaoFreteState frete) {
        if (isRecalculoInvalido(valor, cotacao, frete)) {
            limparPropostaEFechamento();
            setTextSafely(binding.campoValorKgEntrada, formatCurrency(BigDecimal.ZERO), valorKgTextWatcher);
            return;
        }
        negociacaoViewModel.recalcularPropostaPorCabeca(cotacao, fechamento, valor, BigDecimal.valueOf(peso), quantidade, obterValorParcialFrete(frete), obterFreteState(frete));
    }

    private void recalcularPorKg(@NonNull BigDecimal valor, @Nullable CotacaoState cotacao, @Nullable FechamentoState fechamento, @Nullable PrecificacaoFreteState frete) {
        if (isRecalculoInvalido(valor, cotacao, frete)) {
            limparPropostaEFechamento();
            setTextSafely(binding.campoValorCabecaEntrada, formatCurrency(BigDecimal.ZERO), valorCabecaTextWatcher);
            return;
        }
        negociacaoViewModel.recalcularPropostaPorKg(cotacao, fechamento, valor, BigDecimal.valueOf(peso), quantidade, obterValorParcialFrete(frete), obterFreteState(frete));
    }

    private void atualizarValoresCotado(@Nullable CotacaoState cotacao) {
        if (isCotacaoSemEstado(cotacao)) return;
        preencherCamposEditaveis(cotacao.getValorPorCabeca(), cotacao.getValorPorKg());
        exibirValorEtapaCotado(formatCurrency(cotacao.getValorPorCabeca()));
        exibirDescricaoEtapaCotado(formatCurrency(cotacao.getValorPorKg()));
    }

    private void atualizarValoresPedido(@Nullable PropostaState proposta) {
        if (isPropostaSemEstado(proposta) || isFreteNaoDescontado(proposta)) {
            restaurarPlaceholderProposta();
            return;
        }
        preencherCamposEditaveis(proposta.getValorPorCabeca(), proposta.getValorPorKg());
        exibirValorEtapaPedido(formatCurrency(proposta.getValorPorCabeca()));
        exibirDescricaoEtapaPedido(formatCurrency(proposta.getValorPorKg()));
        exibirBadgeFrete(formatCurrency(proposta.getFretePorKg()));
        exibirValorFornecedor(formatCurrency(proposta.getValorTotal()));
    }

    private void atualizarValoresFechamento(@Nullable FechamentoState fechamento) {
        if (isFechamentoSemEstado(fechamento) || isComissaoNaoAplicada(fechamento)) {
            restaurarPlaceholderFechamento();
            return;
        }
        exibirValorEtapaFinal(formatCurrency(fechamento.getValorPorCabeca()));
        exibirDescricaoEtapaFinal(formatCurrency(fechamento.getValorPorKg()));
        exibirBadgeCorretor(formatCurrency(fechamento.getComissaoPorKg()));
        exibirValorTotal(formatCurrency(fechamento.getValorTotal()));
    }

    private void atualizarEstadoCardFrete(@Nullable PrecificacaoFreteState frete) {
        if (isFreteSemEstado(frete)) return;
        processarProposta(frete);
        preencherCampoValorFrete(frete);
        exibirHelperTextFrete(formatCurrency(frete.getValorParcial()));
        if (isFreteManualEmEdicao(frete)) return;
        exibirTituloFrete(formatarTituloValorFrete(frete));
        exibirDescricaoFrete(formatarDescricaoValorFrete(frete));
    }

    private void exibirDadosCorretor(@NonNull CorretorState corretor) {
        exibirNomeCorretor(corretor.getNome());
        exibirDescricaoCorretor(formatarDescricaoCorretor(corretor));
    }

    private void restaurarEspecificacao(@Nullable EspecificacaoAnimalState especificacao) {
        if (especificacao == null) return;
        restaurarIdade(especificacao.getIdade());
        restaurarSexo(especificacao.getSexo());
    }

    private void restaurarIdade(@Nullable Integer idade) {
        if (idade == null) return;
        setTextSafely(binding.campoIdadeEntrada, String.valueOf(idade), especificacaoTextWatcher);
    }

    private void restaurarSexo(@Nullable String sexo) {
        if (sexo == null) return;
        selectChip(binding.listaSexos, sexo);
    }

    private void limparFrete() {
        limparHelperTextFrete();
        limparPropostaEFechamento();
        limparCardFrete();
        limparCardCorretor();
        atualizarValoresCotado(cotacaoAtual);
    }

    private void limparPropostaEFechamento() {
        restaurarPlaceholderProposta();
        restaurarPlaceholderFechamento();
    }

    private void limparHelperTextFrete() {
        exibirHelperTextFrete(formatCurrency(BigDecimal.ZERO));
    }

    private void limparCardFrete() {
        exibirTituloFrete(getString(R.string.titulo_frete));
        exibirDescricaoFrete(getString(R.string.descricao_frete_vazio));
    }

    private void limparCardCorretor() {
        exibirNomeCorretor(getString(R.string.texto_sem_corretor));
        exibirDescricaoCorretor(getString(R.string.descricao_sem_comissao));
    }

    private void restaurarPlaceholderProposta() {
        exibirPlaceholderValorEtapaPedido();
        exibirPlaceholderDescricaoEtapaPedido();
        exibirPlaceholderBadgeFrete();
        exibirPlaceholderValorFornecedor();
    }

    private void restaurarPlaceholderFechamento() {
        exibirPlaceholderValorEtapaFinal();
        exibirPlaceholderDescricaoEtapaFinal();
        exibirPlaceholderBadgeCorretor();
        exibirPlaceholderValorTotal();
    }

    private void exibirPlaceholderValorEtapaPedido() {
        setText(binding.textoValorEtapaPedido, getString(R.string.placeholder_valor_monetario));
    }

    private void exibirPlaceholderDescricaoEtapaPedido() {
        setText(binding.textoDescricaoEtapaPedido, getString(R.string.placeholder_valor_por_kg_default));
    }

    private void exibirPlaceholderBadgeFrete() {
        setText(binding.textoBadgeEtapaFrete, getString(R.string.placeholder_badge_diferenca_default));
    }

    private void exibirPlaceholderValorFornecedor() {
        setText(binding.textoValorFornecedor, getString(R.string.placeholder_valor_total));
    }

    private void exibirPlaceholderValorEtapaFinal() {
        setText(binding.textoValorEtapaFinal, getString(R.string.placeholder_valor_monetario));
    }

    private void exibirPlaceholderDescricaoEtapaFinal() {
        setText(binding.textoDescricaoEtapaFinal, getString(R.string.placeholder_valor_por_kg_default));
    }

    private void exibirPlaceholderBadgeCorretor() {
        setText(binding.textoBadgeEtapaCorretor, getString(R.string.placeholder_badge_diferenca_default));
    }

    private void exibirPlaceholderValorTotal() {
        setText(binding.textoValorTotal, getString(R.string.placeholder_valor_total));
    }

    private void exibirQuantidadeAnimais() {
        setText(binding.textoValorQuantidade, String.format(Locale.getDefault(), "%d", quantidade));
    }

    private void exibirPesoMedioAnimais() {
        setText(binding.textoValorPeso, String.format(Locale.getDefault(), "%.2f", peso));
    }

    private void exibirValorEtapaCotado(@NonNull String valor) {
        setText(binding.textoValorEtapaCotado, valor);
    }

    private void exibirDescricaoEtapaCotado(@NonNull String descricao) {
        setText(binding.textoDescricaoEtapaCotado, String.format(Locale.getDefault(), "R$ %s", descricao));
    }

    private void exibirValorEtapaPedido(@NonNull String valor) {
        setText(binding.textoValorEtapaPedido, valor);
    }

    private void exibirDescricaoEtapaPedido(@NonNull String descricao) {
        setText(binding.textoDescricaoEtapaPedido, String.format(Locale.getDefault(), "R$ %s", descricao));
    }

    private void exibirBadgeFrete(@NonNull String valor) {
        setText(binding.textoBadgeEtapaFrete, String.format(Locale.getDefault(), "+R$ %s/kg", valor));
    }

    private void exibirValorFornecedor(@NonNull String valor) {
        setText(binding.textoValorFornecedor, valor);
    }

    private void exibirValorEtapaFinal(@NonNull String valor) {
        setText(binding.textoValorEtapaFinal, valor);
    }

    private void exibirDescricaoEtapaFinal(@NonNull String descricao) {
        setText(binding.textoDescricaoEtapaFinal, String.format(Locale.getDefault(), "R$ %s", descricao));
    }

    private void exibirBadgeCorretor(@NonNull String valor) {
        setText(binding.textoBadgeEtapaCorretor, String.format(Locale.getDefault(), "+R$ %s/kg", valor));
    }

    private void exibirValorTotal(@NonNull String valor) {
        setText(binding.textoValorTotal, valor);
    }

    private void exibirVariacaoPercentual(@NonNull Double variacao) {
        setText(binding.textoValorVariacao, String.format(Locale.getDefault(), "%.2f%%", variacao));
    }

    private void exibirTituloFrete(@NonNull String texto) {
        setText(binding.textoTituloFrete, texto);
    }

    private void exibirDescricaoFrete(@NonNull String texto) {
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

    private void exibirHelperTextFrete(@NonNull String incidencia) {
        setHelperText(binding.campoFreteLayout, getString(R.string.helper_frete, incidencia));
    }

    private void preencherCamposEditaveis(@NonNull BigDecimal valorPorCabeca, @NonNull BigDecimal valorPorKg) {
        setTextSafely(binding.campoValorCabecaEntrada, formatCurrency(valorPorCabeca), valorCabecaTextWatcher);
        setTextSafely(binding.campoValorKgEntrada, formatCurrency(valorPorKg), valorKgTextWatcher);
    }

    private void preencherCampoValorFrete(@NonNull PrecificacaoFreteState frete) {
        if (isFreteManualEmEdicao(frete)) return;
        setTextSafely(binding.campoFreteEntrada, formatCurrency(frete.getValorTotal()), freteTextWatcher);
    }

    @NonNull
    private String formatarTituloValorFrete(@NonNull PrecificacaoFreteState frete) {
        return String.format(Locale.getDefault(), "R$ %s/c", formatCurrency(frete.getValorTotal()));
    }

    @NonNull
    private String formatarDescricaoValorFrete(@NonNull PrecificacaoFreteState frete) {
        return String.format(Locale.getDefault(), "R$ %s/kg", formatCurrency(frete.getValorParcial()));
    }

    @NonNull
    private String formatarDescricaoCorretor(@NonNull CorretorState corretor) {
        return String.format(Locale.getDefault(), "R$ %s/%s - R$ %s/t",
                formatCurrency(corretor.getComissao()), corretor.getTipoComissao(),
                formatCurrency(corretor.getComissao().multiply(BigDecimal.valueOf(quantidade))));
    }

    private void exibirErroCategoriaParaFrete() {
        showSnackBarErro(binding.getRoot(), getString(R.string.aviso_selecione_categoria_frete));
    }

    private void exibirErroFreteObrigatorio() {
        showSnackBarErro(binding.getRoot(), getString(R.string.aviso_informe_frete_antes_corretor));
    }

    private void exibirErroCamposObrigatorios() {
        showSnackBarErro(binding.getRoot(), getString(R.string.aviso_preencha_campos_obrigatorios));
    }

    private void exibirSucessoFinalizacao() {
        showSnackBarSucesso(binding.getRoot(), getString(R.string.sucesso_negociacao_finalizada));
    }

    private void navegarParaSimulacaoDeFrete() {
        if (isCategoriaNaoSelecionada(categoriaAtual)) {
            exibirErroCategoriaParaFrete();
            return;
        }
       navegar(this, R.id.negociacaoFragment,
                NegociacaoFragmentDirections.actionNegociacaoFragmentToSimulacaoFreteeFragment()
                        .setCargaTotal(quantidade).setPesoMedio((float) peso));
    }

    private void abrirSeletorCorretor() {
        if (isCampoFreteVazio()) {
            exibirErroFreteObrigatorio();
            return;
        }
        exibirBottomSheetCorretor();
    }

    private void exibirBottomSheetEmpresa() {
        FragmentManager fm = getChildFragmentManager();
        if (fm.findFragmentByTag(TAG_BOTTOM_SHEET_EMPRESA) != null) return;
        new EmpresaBottomSheetDialogFragment().show(fm, TAG_BOTTOM_SHEET_EMPRESA);
    }

    private void exibirBottomSheetCorretor() {
        FragmentManager fm = getChildFragmentManager();
        if (fm.findFragmentByTag(TAG_BOTTOM_SHEET_CORRETOR) != null) return;
        new CorretorBottomSheetDialogFragment().show(fm, TAG_BOTTOM_SHEET_CORRETOR);
    }

    private void finalizar() {
        if (isFormularioIncompleto()) {
            exibirErroCamposObrigatorios();
            return;
        }
        exibirSucessoFinalizacao();
        NavigationHelper.voltar(this);
    }

    private void atualizarEstadoDoBotaoFinalizar() {
        binding.botaoFinalizar.setEnabled(isProntoParaFinalizar());
    }

    @NonNull
    private BigDecimal obterValorParcialFrete(@Nullable PrecificacaoFreteState frete) {
        return frete != null ? frete.getValorParcial() : BigDecimal.ZERO;
    }

    @NonNull
    private FreteState obterFreteState(@Nullable PrecificacaoFreteState frete) {
        return frete != null ? frete.getFreteState() : FreteState.NAO_SELECIONADO;
    }

    @NonNull
    private BigDecimal obterValorTotalFrete() {
        return parseDecimal(binding.campoFreteEntrada);
    }

    @NonNull
    private BigDecimal obterValorPorCabeca() {
        return parseDecimal(binding.campoValorCabecaEntrada);
    }

    @NonNull
    private BigDecimal obterValorPorKg() {
        return parseDecimal(binding.campoValorKgEntrada);
    }

    @Nullable
    private String obterValorSexoSelecionado() {
        return getCheckedChipText(binding.listaSexos);
    }

    private BigDecimal obterComissaoTotal() {
        return isCorretorNaoSelecionado(corretorAtual) ? BigDecimal.ZERO : corretorAtual.getComissao();
    }

    @Nullable
    private Integer obterTextoIdade() {
        return isIdadePreenchida() ? parseInt(binding.campoIdadeEntrada) : null;
    }

    private boolean isProntoParaFinalizar() {
        return !isFormularioIncompleto() && !isNegociacaoIncompleta();
    }

    private boolean isNegociacaoIncompleta() {
        return isCotacaoSemEstado(cotacaoAtual) || isPropostaSemEstado(propostaAtual) || isFechamentoSemEstado(fechamentoAtual);
    }

    private boolean isFormularioIncompleto() {
        return !isIdadePreenchida() || !isSexoSelecionado() || isCategoriaNaoSelecionada(categoriaAtual) || isRacaNaoSelecionada(racaAtual);
    }

    private boolean isIdadePreenchida() {
        return isNotEmpty(binding.campoIdadeEntrada);
    }

    private boolean isSexoSelecionado() {
        return isNotEmpty(obterValorSexoSelecionado());
    }

    private boolean isCampoFreteVazio() {
        return !isNotEmpty(binding.campoFreteEntrada);
    }

    private boolean isFreteIndisponivel() {
        return freteAtual == null;
    }

    private boolean isCotacaoIndisponivel() {
        return cotacaoAtual == null;
    }

    private boolean isCotacaoSemEstado(@Nullable CotacaoState cotacao) {
        return cotacao == null;
    }

    private boolean isPropostaSemEstado(@Nullable PropostaState proposta) {
        return proposta == null;
    }

    private boolean isFechamentoSemEstado(@Nullable FechamentoState fechamento) {
        return fechamento == null;
    }

    private boolean isFreteSemEstado(@Nullable PrecificacaoFreteState frete) {
        return frete == null;
    }

    private boolean isFreteNaoDescontado(@NonNull PropostaState proposta) {
        return !proposta.isFreteDescontado();
    }

    private boolean isComissaoNaoAplicada(@NonNull FechamentoState fechamento) {
        return !fechamento.isComissaoAplicada();
    }

    private boolean isFreteManualEmEdicao(@NonNull PrecificacaoFreteState frete) {
        return isFreteManual(frete) && !isCampoFreteVazio();
    }

    private boolean isFreteManual(@NonNull PrecificacaoFreteState frete) {
        return frete.getFreteState() == FreteState.MANUAL;
    }

    private boolean isCorretorNaoSelecionado(@Nullable CorretorState corretor) {
        return corretor == null;
    }

    private boolean isEmpresaNaoSelecionada(@Nullable EmpresaState empresa) {
        return empresa == null;
    }

    private boolean isCategoriaNaoSelecionada(@Nullable CategoriaState categoria) {
        return categoria == null;
    }

    private boolean isRacaNaoSelecionada(@Nullable RacaState raca) {
        return raca == null;
    }

    private boolean isValorZero(@NonNull BigDecimal valor) {
        return valor.compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean isRecalculoInvalido(@NonNull BigDecimal valor, @Nullable CotacaoState cotacao, @Nullable PrecificacaoFreteState frete) {
        return isValorZero(valor) || cotacao == null || frete == null;
    }
}