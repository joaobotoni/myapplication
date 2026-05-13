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
import com.example.myapplication.ui.state.negociacao.NegociacaoState;
import com.example.myapplication.ui.state.negociacao.PropostaState;
import com.example.myapplication.ui.viewmodel.AnimalViewModel;
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
    private AnimalViewModel animalViewModel;
    private TextWatcher freteTextWatcher;
    private TextWatcher especificacaoTextWatcher;
    private TextWatcher valorCabecaTextWatcher;
    private TextWatcher valorKgTextWatcher;
    private double peso;
    private int quantidade;
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
        processarCotacaoSeNecessario();
    }

    private void extrairArgumentosDeNavegacao() {
        NegociacaoFragmentArgs args = NegociacaoFragmentArgs.fromBundle(requireArguments());
        quantidade = args.getCargaTotal();
        peso = args.getPesoMedio();
    }

    private void iniciarDadosEspecificacao() {
        atualizarQuantidadeAnimais();
        atualizarPesoMedioAnimais();
    }

    private void inicializarDependencias() {
        racaViewModel = new ViewModelProvider(requireActivity()).get(RacaViewModel.class);
        categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);
        transporteViewModel = new ViewModelProvider(requireActivity()).get(TransporteViewModel.class);
        corretorViewModel = new ViewModelProvider(requireActivity()).get(CorretorViewModel.class);
        empresaViewModel = new ViewModelProvider(requireActivity()).get(EmpresaViewModel.class);
        negociacaoViewModel = new ViewModelProvider(requireActivity()).get(NegociacaoViewModel.class);
        precificacaoFreteViewModel = new ViewModelProvider(requireActivity()).get(PrecificacaoFreteViewModel.class);
        animalViewModel = new ViewModelProvider(requireActivity()).get(AnimalViewModel.class);
        freteTextWatcher = TextWatcherHelper.MoneyTextWatcher(this::aoFreteManualAlterado);
        especificacaoTextWatcher = TextWatcherHelper.SimpleTextWatcher(this::aoEspecificacaoAlterada);
        valorCabecaTextWatcher = TextWatcherHelper.MoneyTextWatcher(this::aoValorCabecaAlterado);
        valorKgTextWatcher = TextWatcherHelper.MoneyTextWatcher(this::aoValorKgAlterado);
    }

    private void configurarComportamentosDeTela() {
        configurarRecyclerViewRacas();
        configurarRecyclerViewCategorias();
        configurarTextWatcherFrete();
        configurarTextWatcherIdade();
        configurarTextWatcherValorCabeca();
        configurarTextWatcherValorKg();
        configurarChipGroupSexo();
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
        observarEstadoDasRacas();
        observarEstadoDasCategorias();
        observarEstadoDaNegociacao();
        observarEstadoDoCorretor();
        observarEstadoDaEmpresa();
        observarEstadoDoFrete();
        observarVariacaoPercentual();
        observarEstadoDoAnimal();
    }

    private void observarEstadoDasRacas() {
        racaViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarListaRacas);
        racaViewModel.getRacaSelecionada().observe(getViewLifecycleOwner(), this::aoAlterarRacaSelecionada);
    }

    private void observarEstadoDasCategorias() {
        categoriaViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarListaCategorias);
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

    private void observarVariacaoPercentual() {
        negociacaoViewModel.getVariacao().observe(getViewLifecycleOwner(), this::exibirVariacaoPercentual);
    }

    private void observarEstadoDoAnimal() {
        animalViewModel.getAnimalState().observe(getViewLifecycleOwner(), this::restaurarEstadoAnimal);
    }

    private void aoFreteManualAlterado() {
        if (isCampoFreteVazio()) {
            limparFreteEDependencias();
            return;
        }
        precificacaoFreteViewModel.calcularIncidencia(obterValorTotalFrete(), BigDecimal.valueOf(peso), quantidade, FreteState.MANUAL);
    }

    private void aoValorCabecaAlterado() {
        if (isCampoValorCabecaVazio()) {
            limparVariacao();
            limparCardCorretor();
            limparSelecaoCorretor();
            restaurarPlaceholderFechamento();
            return;
        }
        negociacaoViewModel.recalcularPropostaPorCabeca(obterValorPorCabeca(), BigDecimal.valueOf(peso), quantidade);
    }

    private void aoValorKgAlterado() {
        if (isCampoValorKgVazio()) {
            limparVariacao();
            limparCardCorretor();
            limparSelecaoCorretor();
            restaurarPlaceholderFechamento();
            return;
        }
        negociacaoViewModel.recalcularPropostaPorKg(obterValorPorKg(), BigDecimal.valueOf(peso), quantidade);
    }

    private void aoEspecificacaoAlterada() {
        animalViewModel.atualizarEspecificacao(obterValorSexoSelecionado(), obterTextoIdade());
    }

    private void aoAlterarCategoriaSelecionada(@Nullable CategoriaState categoriaState) {
        categoriaAtual = categoriaState;
        if (isCategoriaNaoSelecionada(categoriaState)) return;
        animalViewModel.atualizarCategoria(categoriaState);
        transporteViewModel.recomendar(categoriaAtual.getId(), quantidade);
    }

    private void aoAlterarRacaSelecionada(@Nullable RacaState racaState) {
        racaAtual = racaState;
        if (isRacaNaoSelecionada(racaState)) return;
        animalViewModel.atualizarRaca(racaState);
    }

    private void aoSelecionarEmpresaNaLista(@Nullable EmpresaState empresaState) {
        if (isEmpresaNaoSelecionada(empresaState)) return;
        exibirNomeEmpresa(empresaState.getNome());
    }

    private void aoSelecionarRacaNaLista(@NonNull RacaState racaState) {
        racaViewModel.selecionarRaca(racaState);
    }

    private void aoSelecionarCategoriaNaLista(@NonNull CategoriaState categoriaState) {
        categoriaViewModel.selecionarCategoria(categoriaState);
    }

    private void aoSelecionarCorretorNaLista(@Nullable CorretorState corretorState) {
        if (isCorretorNaoSelecionado(corretorState)) {
            limparValores();
            limparVariacao();
            limparCardCorretor();
            return;
        }
        atualizarEstadoCorretor(corretorState);
        processarFechamento(corretorState);
    }

    private void atualizarQuantidadeAnimais() {
        setText(binding.textoValorQuantidade, String.format(Locale.getDefault(), "%d", quantidade));
    }

    private void atualizarPesoMedioAnimais() {
        setText(binding.textoValorPeso, String.format(Locale.getDefault(), "%.2f", peso));
    }

    private void atualizarListaRacas(@NonNull List<RacaState> racas) {
        racaAdapter.submitList(racas);
    }

    private void atualizarListaCategorias(@NonNull List<CategoriaState> categorias) {
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
        preencherCamposEditaveis(cotacaoState.getValorPorCabeca(), cotacaoState.getValorPorKg());
        exibirValorEtapaCotado(formatCurrency(cotacaoState.getValorPorCabeca()));
        exibirDescricaoEtapaCotado(formatCurrency(cotacaoState.getValorPorKg()));
    }

    private void atualizarValoresPedido(@Nullable PropostaState propostaState) {
        if (isPropostaSemEstado(propostaState) || !isFreteDescontado(propostaState)) {
            restaurarPlaceholderProposta();
            return;
        }
        preencherCamposEditaveis(propostaState.getValorPorCabeca(), propostaState.getValorPorKg());
        exibirValorEtapaPedido(formatCurrency(propostaState.getValorPorCabeca()));
        exibirDescricaoEtapaPedido(formatCurrency(propostaState.getValorPorKg()));
        exibirBadgeFrete(formatCurrency(propostaState.getFretePorKg()));
        exibirValorFornecedor(formatCurrency(propostaState.getValorTotal()));
    }

    private void atualizarValoresFechamento(@Nullable FechamentoState fechamentoState) {
        if (isFechamentoSemEstado(fechamentoState) || !isComissaoAplicada(fechamentoState)) {
            restaurarPlaceholderFechamento();
            return;
        }
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

    private void atualizarEstadoCorretor(@NonNull CorretorState corretorState) {
        exibirNomeCorretor(corretorState.getNome());
        exibirDescricaoCorretor(formatarDescricaoCorretor(corretorState));
    }

    private void restaurarEstadoAnimal(@Nullable AnimalState animalState) {
        if (animalState == null) return;
        restaurarEspecificacao(animalState.getEspecificacao());
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

    private void processarCotacaoSeNecessario() {
        if (!negociacaoViewModel.isCotacaoCalculada()) {
            processarCotacao();
        }
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

    private void limparFreteEDependencias() {
        limparHelperTextFrete();
        limparValores();
        limparVariacao();
        limparCardFrete();
        limparSelecaoCorretor();
        limparCardCorretor();
    }

    private void limparValores() {
        negociacaoViewModel.limpar();
    }

    private void limparVariacao() {
        negociacaoViewModel.limparVariacao();
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

    private void restaurarPlaceholderProposta() {
        exibirPlaceholderValorEtapaPedido();
        exibirPlaceholderDescricaoEtapaPedido();
        exibirPlaceholderBadgeEtapaFrete();
        exibirPlaceholderValorFornecedor();
    }

    private void restaurarPlaceholderFechamento() {
        exibirPlaceholderValorEtapaFinal();
        exibirPlaceholderDescricaoEtapaFinal();
        exibirPlaceholderBadgeEtapaCorretor();
        exibirPlaceholderValorTotal();
    }

    private void exibirBottomSheetEmpresa() {
        new EmpresaBottomSheetDialogFragment().show(getChildFragmentManager(), null);
    }

    private void exibirBottomSheetCorretor() {
        new CorretorBottomSheetDialogFragment().show(getChildFragmentManager(), null);
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
        setText(binding.textoBadgeEtapaFrete, String.format(Locale.getDefault(), "+R$ %s/kg", valor));
    }

    private void exibirBadgeCorretor(@NonNull String valor) {
        setText(binding.textoBadgeEtapaCorretor, String.format(Locale.getDefault(), "+R$ %s/kg", valor));
    }

    private void exibirPlaceholderValorEtapaPedido() {
        setText(binding.textoValorEtapaPedido, getString(R.string.placeholder_valor_monetario));
    }

    private void exibirPlaceholderDescricaoEtapaPedido() {
        setText(binding.textoDescricaoEtapaPedido, getString(R.string.placeholder_valor_por_kg_default));
    }

    private void exibirPlaceholderBadgeEtapaFrete() {
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

    private void exibirPlaceholderBadgeEtapaCorretor() {
        setText(binding.textoBadgeEtapaCorretor, getString(R.string.placeholder_badge_diferenca_default));
    }

    private void exibirPlaceholderValorTotal() {
        setText(binding.textoValorTotal, getString(R.string.placeholder_valor_total));
    }

    private void exibirValorFornecedor(@NonNull String valor) {
        setText(binding.textoValorFornecedor, valor);
    }

    private void exibirValorTotal(@NonNull String valor) {
        setText(binding.textoValorTotal, valor);
    }

    private void exibirVariacaoPercentual(@NonNull Double variacao) {
        setText(binding.textoValorVariacao, String.format(Locale.getDefault(), "%.2f%%", variacao));
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

    private void exibirHelperTextFrete(@NonNull String incidencia) {
        setHelperText(binding.campoFreteLayout, getString(R.string.helper_frete, incidencia));
    }

    private void exibirErroDeCategoriaParaFrete() {
        AlertHelper.showSnackBarErro(binding.getRoot(), getString(R.string.aviso_selecione_categoria_frete));
    }

    private void exibirErroFreteObrigatorio() {
        AlertHelper.showSnackBarErro(binding.getRoot(), getString(R.string.aviso_informe_frete_antes_corretor));
    }

    private void exibirErroCamposObrigatorios() {
        AlertHelper.showSnackBarErro(binding.getRoot(), getString(R.string.aviso_preencha_campos_obrigatorios));
    }

    private void preencherCamposEditaveis(BigDecimal valorPorCabeca, BigDecimal valorPorKg) {
        setTextSafely(binding.campoValorCabecaEntrada, formatCurrency(valorPorCabeca), valorCabecaTextWatcher);
        setTextSafely(binding.campoValorKgEntrada, formatCurrency(valorPorKg), valorKgTextWatcher);
    }

    private void preencherCampoValorFrete(@NonNull PrecificacaoFreteState freteState) {
        if (isFreteManualEmEdicao(freteState)) return;
        setText(binding.campoFreteEntrada, formatCurrency(freteState.getValorTotal()));
    }

    @NonNull
    private String formatarDescricaoCorretor(@NonNull CorretorState corretorState) {
        return String.format(Locale.getDefault(), "R$ %s/%s -> R$ %s/t",
                formatCurrency(corretorState.getComissao()), corretorState.getTipoComissao(),
                formatCurrency(corretorState.getComissao().multiply(BigDecimal.valueOf(quantidade))));
    }

    @NonNull
    private String formatarTituloValorFrete(@NonNull PrecificacaoFreteState freteState) {
        return String.format(Locale.getDefault(), "R$ %s/c", formatCurrency(freteState.getValorTotal()));
    }

    @NonNull
    private String formatarDescricaoValorFrete(@NonNull PrecificacaoFreteState freteState) {
        return String.format(Locale.getDefault(), "R$ %s/kg", formatCurrency(freteState.getValorParcial()));
    }


    @Nullable
    private Integer obterTextoIdade() {
        return isNotEmpty(binding.campoIdadeEntrada) ? getInt(binding.campoIdadeEntrada) : null;
    }

    @Nullable
    private String obterValorSexoSelecionado() {
        return getCheckedChipText(binding.listaSexos);
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

    private boolean isProntoParaFinalizar() {
        return isIdadePreenchida()
                && isSexoSelecionado()
                && !isCategoriaInvalidaParaFrete()
                && !isRacaNaoSelecionada(racaAtual);
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

    private boolean isCampoValorCabecaVazio() {
        return !isNotEmpty(binding.campoValorCabecaEntrada);
    }

    private boolean isCampoValorKgVazio() {
        return !isNotEmpty(binding.campoValorKgEntrada);
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

    private boolean isCorretorNaoSelecionado(@Nullable CorretorState corretorState) {
        return corretorState == null;
    }

    private boolean isEmpresaNaoSelecionada(@Nullable EmpresaState empresaState) {
        return empresaState == null;
    }

    private boolean isCategoriaNaoSelecionada(@Nullable CategoriaState categoriaState) {
        return categoriaState == null;
    }

    private boolean isRacaNaoSelecionada(@Nullable RacaState racaState) {
        return racaState == null;
    }

    private boolean isCotacaoSemEstado(@Nullable CotacaoState cotacaoState) {
        return cotacaoState == null;
    }

    private boolean isPropostaSemEstado(@Nullable PropostaState propostaState) {
        return propostaState == null;
    }

    private boolean isFechamentoSemEstado(@Nullable FechamentoState fechamentoState) {
        return fechamentoState == null;
    }

    private boolean isNegociacaoSemEstado(@Nullable NegociacaoState negociacaoState) {
        return negociacaoState == null;
    }

    private boolean isFreteDescontado(@NonNull PropostaState propostaState) {
        return propostaState.isFreteDescontado();
    }

    private boolean isComissaoAplicada(@NonNull FechamentoState fechamentoState) {
        return fechamentoState.isComissaoAplicada();
    }

    private boolean isFreteSemEstado(@Nullable PrecificacaoFreteState freteState) {
        return freteState == null;
    }


    private void navegarParaSimulacaoDeFrete() {
        if (isCategoriaInvalidaParaFrete()) {
            exibirErroDeCategoriaParaFrete();
            return;
        }
        executarNavegacaoSimulacaoFrete();
    }

    private void executarNavegacaoSimulacaoFrete() {
        NegociacaoFragmentDirections.ActionNegociacaoFragmentToSimulacaoFreteeFragment directions =
                NegociacaoFragmentDirections.actionNegociacaoFragmentToSimulacaoFreteeFragment()
                        .setCargaTotal(quantidade)
                        .setPesoMedio((float) peso);
        NavHostFragment.findNavController(this).navigate(directions);
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

    private void finalizar() {
        if (!isProntoParaFinalizar()) {
            exibirErroCamposObrigatorios();
        }
    }
}