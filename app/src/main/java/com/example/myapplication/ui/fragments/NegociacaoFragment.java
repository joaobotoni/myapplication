package com.example.myapplication.ui.fragments;

import static com.example.myapplication.ui.helpers.FormatHelper.formatCurrency;
import static com.example.myapplication.ui.helpers.ViewHelper.getBigDecimal;
import static com.example.myapplication.ui.helpers.ViewHelper.getCheckedChipText;
import static com.example.myapplication.ui.helpers.ViewHelper.getInt;
import static com.example.myapplication.ui.helpers.ViewHelper.isNotEmpty;
import static com.example.myapplication.ui.helpers.ViewHelper.selectChip;
import static com.example.myapplication.ui.helpers.ViewHelper.setHelperText;
import static com.example.myapplication.ui.helpers.ViewHelper.setText;
import static com.example.myapplication.ui.helpers.ViewHelper.setTextSafely;

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
        freteTextWatcher = TextWatcherHelper.MoneyTextWatcher(this::aoFreteManualAlterado);
        especificacaoTextWatcher = TextWatcherHelper.SimpleTextWatcher(this::aoEspecificacaoAlterada);
        valorCabecaTextWatcher = TextWatcherHelper.MoneyTextWatcher(this::aoValorCabecaAlterado);
        valorKgTextWatcher = TextWatcherHelper.MoneyTextWatcher(this::aoValorKgAlterado);
    }

    private void configurarComportamentosDeTela() {
        configurarRecyclerViews();
        configurarTextWatchers();
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

    private void configurarRecyclerViewHorizontal(@NonNull RecyclerView recyclerView,
                                                  @NonNull RecyclerView.Adapter<?> adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void configurarTextWatchers() {
        configurarTextWatcherFrete();
        configurarTextWatcherIdade();
        configurarTextWatcherValorCabeca();
        configurarTextWatcherValorKg();
    }

    private void configurarTextWatcherFrete() {
        binding.campoFreteEntrada.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) binding.campoFreteEntrada.addTextChangedListener(freteTextWatcher);
            else binding.campoFreteEntrada.removeTextChangedListener(freteTextWatcher);
        });
    }

    private void configurarTextWatcherIdade() {
        binding.campoIdadeEntrada.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) binding.campoIdadeEntrada.addTextChangedListener(especificacaoTextWatcher);
            else binding.campoIdadeEntrada.removeTextChangedListener(especificacaoTextWatcher);
        });
    }

    private void configurarTextWatcherValorCabeca() {
        binding.campoValorCabecaEntrada.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) binding.campoValorCabecaEntrada.addTextChangedListener(valorCabecaTextWatcher);
            else binding.campoValorCabecaEntrada.removeTextChangedListener(valorCabecaTextWatcher);
        });
    }

    private void configurarTextWatcherValorKg() {
        binding.campoValorKgEntrada.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) binding.campoValorKgEntrada.addTextChangedListener(valorKgTextWatcher);
            else binding.campoValorKgEntrada.removeTextChangedListener(valorKgTextWatcher);
        });
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
        binding.toolbar.setOnClickListener(v -> executarNavegacaoDeRetorno());
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
    }

    private void onCategoriaSelecionada(@Nullable CategoriaState categoria) {
        categoriaAtual = categoria;
        if (isCategoriaNaoSelecionada(categoria)) return;
        animalViewModel.atualizarCategoria(categoria);
        transporteViewModel.recomendar(categoria.getId(), quantidade);
    }

    private void onCotacaoAtualizada(@Nullable CotacaoState cotacao) {
        cotacaoAtual = cotacao;
        atualizarValoresCotado(cotacao);
    }

    private void onPropostaAtualizada(@Nullable PropostaState proposta) {
        propostaAtual = proposta;
        atualizarValoresPedido(proposta);
    }

    private void onFechamentoAtualizado(@Nullable FechamentoState fechamento) {
        fechamentoAtual = fechamento;
        atualizarValoresFechamento(fechamento);
    }

    private void onFreteAtualizado(@Nullable PrecificacaoFreteState frete) {
        freteAtual = frete;
        atualizarEstadoCardFrete(frete);
    }

    private void onCorretorSelecionado(@Nullable CorretorState corretor) {
        corretorAtual = corretor;
        if (isCorretorNaoSelecionado(corretor)) {
            limparVariacao();
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
            limparFreteEDependencias();
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
        negociacaoViewModel.processarProposta(cotacaoAtual, BigDecimal.valueOf(peso), quantidade,
                frete.getValorTotal(), frete.getFreteState(), obterComissaoTotal());
    }

    private void processarFechamento(@NonNull CorretorState corretor) {
        if (isCotacaoIndisponivel() || isFreteIndisponivel()) return;
        negociacaoViewModel.processarFechamento(cotacaoAtual, BigDecimal.valueOf(peso), quantidade,
                freteAtual.getValorTotal(), freteAtual.getFreteState(), corretor.getComissao());
    }

    private void calcularIncidenciaFreteManual() {
        precificacaoFreteViewModel.calcularIncidencia(obterValorTotalFrete(),
                BigDecimal.valueOf(peso), quantidade, FreteState.MANUAL);
    }

    private void recalcularPorCabeca(@NonNull BigDecimal valor, @Nullable CotacaoState cotacao,
                                     @Nullable FechamentoState fechamento,
                                     @Nullable PrecificacaoFreteState frete) {
        if (isRecalculoInvalido(valor, cotacao, frete)) {
            limparProposta();
            return;
        }
        negociacaoViewModel.recalcularPropostaPorCabeca(cotacao, fechamento, valor,
                BigDecimal.valueOf(peso), quantidade,
                obterValorParcialFrete(frete), obterFreteState(frete));
    }

    private void recalcularPorKg(@NonNull BigDecimal valor, @Nullable CotacaoState cotacao,
                                 @Nullable FechamentoState fechamento,
                                 @Nullable PrecificacaoFreteState frete) {
        if (isRecalculoInvalido(valor, cotacao, frete)) {
            limparProposta();
            return;
        }
        negociacaoViewModel.recalcularPropostaPorKg(cotacao, fechamento, valor,
                BigDecimal.valueOf(peso), quantidade,
                obterValorParcialFrete(frete), obterFreteState(frete));
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

    private void limparFreteEDependencias() {
        limparHelperTextFrete();
        limparProposta();
        limparVariacao();
        limparCardFrete();
        limparSelecaoCorretor();
        limparCardCorretor();
    }

    private void limparProposta() {
//        negociacaoViewModel.limpar(cotacao);
        restaurarPlaceholderProposta();
        restaurarPlaceholderFechamento();
    }

    private void limparVariacao() {
        negociacaoViewModel.limparVariacao();
    }

    private void limparSelecaoCorretor() {
        corretorViewModel.limparSelecao();
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
        setText(binding.textoDescricaoEtapaCotado, descricao);
    }

    private void exibirValorEtapaPedido(@NonNull String valor) {
        setText(binding.textoValorEtapaPedido, valor);
    }

    private void exibirDescricaoEtapaPedido(@NonNull String descricao) {
        setText(binding.textoDescricaoEtapaPedido, descricao);
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
        setText(binding.textoDescricaoEtapaFinal, descricao);
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
        setText(binding.campoFreteEntrada, formatCurrency(frete.getValorTotal()));
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
        return String.format(Locale.getDefault(), "R$ %s/%s -> R$ %s/t",
                formatCurrency(corretor.getComissao()),
                corretor.getTipoComissao(),
                formatCurrency(corretor.getComissao().multiply(BigDecimal.valueOf(quantidade))));
    }

    private void exibirErroCategoriaParaFrete() {
        AlertHelper.showSnackBarErro(binding.getRoot(), getString(R.string.aviso_selecione_categoria_frete));
    }

    private void exibirErroFreteObrigatorio() {
        AlertHelper.showSnackBarErro(binding.getRoot(), getString(R.string.aviso_informe_frete_antes_corretor));
    }

    private void exibirErroCamposObrigatorios() {
        AlertHelper.showSnackBarErro(binding.getRoot(), getString(R.string.aviso_preencha_campos_obrigatorios));
    }

    private void navegarParaSimulacaoDeFrete() {
        if (isCategoriaInvalidaParaFrete()) {
            exibirErroCategoriaParaFrete();
            return;
        }
        executarNavegacaoSimulacaoFrete();
    }

    private void executarNavegacaoSimulacaoFrete() {
        NavHostFragment.findNavController(this).navigate(
                NegociacaoFragmentDirections
                        .actionNegociacaoFragmentToSimulacaoFreteeFragment()
                        .setCargaTotal(quantidade)
                        .setPesoMedio((float) peso));
    }

    private void executarNavegacaoDeRetorno() {
        NavHostFragment.findNavController(this).popBackStack();
    }

    private void abrirSeletorCorretor() {
        if (isCampoFreteVazio()) {
            exibirErroFreteObrigatorio();
            return;
        }
        exibirBottomSheetCorretor();
    }

    private void exibirBottomSheetEmpresa() {
        new EmpresaBottomSheetDialogFragment().show(getChildFragmentManager(), null);
    }

    private void exibirBottomSheetCorretor() {
        new CorretorBottomSheetDialogFragment().show(getChildFragmentManager(), null);
    }


    private void finalizar() {
        if (isFormularioIncompleto()) exibirErroCamposObrigatorios();
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
        return getBigDecimal(binding.campoFreteEntrada);
    }

    @NonNull
    private BigDecimal obterValorPorCabeca() {
        return getBigDecimal(binding.campoValorCabecaEntrada);
    }

    @NonNull
    private BigDecimal obterValorPorKg() {
        return getBigDecimal(binding.campoValorKgEntrada);
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
        return isIdadePreenchida() ? getInt(binding.campoIdadeEntrada) : null;
    }

    private boolean isFormularioIncompleto() {
        return !isIdadePreenchida() || !isSexoSelecionado()
                || isCategoriaInvalidaParaFrete() || isRacaNaoSelecionada(racaAtual);
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

    private boolean isFreteIndisponivel() {return freteAtual == null;}
    private boolean isCategoriaInvalidaParaFrete() {
        return categoriaAtual == null;
    }

    private boolean isCotacaoIndisponivel() {
        return cotacaoAtual == null;
    }

    private boolean isPropostaIndisponivel() {
        return propostaAtual == null;
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