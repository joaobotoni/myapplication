package com.example.myapplication.ui.fragments;

import static com.example.myapplication.ui.helpers.AlertHelper.showSnackBarErro;
import static com.example.myapplication.ui.helpers.AlertHelper.showSnackBarSucesso;
import static com.example.myapplication.ui.helpers.FormatHelper.formatCurrency;
import static com.example.myapplication.ui.helpers.NavigationHelper.navegar;
import static com.example.myapplication.ui.helpers.RecyclerViewHelper.setupHorizontalRecyclerView;
import static com.example.myapplication.ui.helpers.ViewHelper.getCheckedChipText;
import static com.example.myapplication.ui.helpers.ViewHelper.isEmpty;
import static com.example.myapplication.ui.helpers.ViewHelper.isNotEmpty;
import static com.example.myapplication.ui.helpers.ViewHelper.isNotNull;
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

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentNegociacaoBinding;
import com.example.myapplication.ui.adapters.CategoriaAdapter;
import com.example.myapplication.ui.adapters.RacaAdapter;

import com.example.myapplication.ui.fragments.negociacao.NegociacaoFragmentArgs;
import com.example.myapplication.ui.fragments.negociacao.NegociacaoFragmentDirections;
import com.example.myapplication.ui.helpers.NavigationHelper;
import com.example.myapplication.ui.helpers.TextWatcherHelper;
import com.example.myapplication.ui.state.animal.AnimalEspecificacaoState;
import com.example.myapplication.ui.state.animal.AnimalState;
import com.example.myapplication.ui.state.animal.CategoriaState;
import com.example.myapplication.ui.state.animal.RacaState;
import com.example.myapplication.ui.state.empresa.CorretorState;
import com.example.myapplication.ui.state.empresa.EmpresaState;
import com.example.myapplication.ui.state.frete.FreteState;
import com.example.myapplication.ui.state.frete.StatusFrete;
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

    private CategoriaAdapter categoriaAdapter;
    private RacaAdapter racaAdapter;

    private CotacaoState cotacaoAtual;
    private PropostaState propostaAtual;
    private FechamentoState fechamentoAtual;
    private FreteState freteAtual;
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
        inicializarDependencias();
        configurarComportamentosDeTela();
        configurarEventosDeClique();
        observarEstadosDasViewModels();
        atualizarEstadoCardInfoAnimal();
        processarCotacao();
    }

    private void extrairArgumentosDeNavegacao() {
        NegociacaoFragmentArgs args = NegociacaoFragmentArgs.fromBundle(requireArguments());
        quantidade = args.getCargaTotal();
        peso = args.getPesoMedio();
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
        configurarRecyclerViewCategorias();
        configurarRecyclerViewRacas();
    }

    private void configurarRecyclerViewCategorias() {
        categoriaAdapter = new CategoriaAdapter(this::aoSelecionarCategoriaNaLista);
        setupHorizontalRecyclerView(binding.listaCategorias, categoriaAdapter, requireContext());
    }

    private void configurarRecyclerViewRacas() {
        racaAdapter = new RacaAdapter(this::aoSelecionarRacaNaLista);
        setupHorizontalRecyclerView(binding.listaRacas, racaAdapter, requireContext());
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
        observarRacas();
        observarCategorias();
        observarAnimal();
        observarCotacao();
        observarProposta();
        observarFechamento();
        observarFrete();
        observarVariacao();
        observarCorretor();
        observarEmpresa();
    }

    private void observarRacas() {
        racaViewModel.getState().observe(getViewLifecycleOwner(), this::attachListaRacas);
        racaViewModel.getRacaSelecionada().observe(getViewLifecycleOwner(), this::onRacaSelecionada);
    }

    private void observarCategorias() {
        categoriaViewModel.getState().observe(getViewLifecycleOwner(), this::attachListaCategorias);
        categoriaViewModel.getCategoriaSelecionada().observe(getViewLifecycleOwner(), this::onCategoriaSelecionada);
    }

    private void observarAnimal() {
        animalViewModel.getAnimalState().observe(getViewLifecycleOwner(), this::onAnimalAtualizado);
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
        negociacaoViewModel.getVariacao().observe(getViewLifecycleOwner(), this::attachVariacaoPercentual);
    }

    private void observarCorretor() {
        corretorViewModel.getCorretorSelecionado().observe(getViewLifecycleOwner(), this::onCorretorSelecionado);
    }

    private void observarEmpresa() {
        empresaViewModel.getEmpresaSelecionada().observe(getViewLifecycleOwner(), this::onEmpresaSelecionada);
    }

    private void onRacaSelecionada(@Nullable RacaState raca) {
        racaAtual = raca;
        if (!isRacaSelecionada(raca)) return;
        animalViewModel.atualizarRaca(raca);
        atualizarEstadoDoBotaoFinalizar();
    }

    private void onCategoriaSelecionada(@Nullable CategoriaState categoria) {
        categoriaAtual = categoria;
        if (!isCategoriaSelecionada(categoria)) return;
        animalViewModel.atualizarCategoria(categoria);
        transporteViewModel.recomendar(categoria.getId(), quantidade);
        atualizarEstadoDoBotaoFinalizar();
    }

    private void onAnimalAtualizado(@Nullable AnimalState animal) {
        if (!isAnimalValido(animal)) return;
        restaurarEspecificacao(animal.getEspecificacao());
    }

    private void onCotacaoAtualizada(@Nullable CotacaoState cotacao) {
        cotacaoAtual = cotacao;
        if (!isCotacaoValida(cotacao)) return;
        atualizarValoresCotadosNaTabela(cotacao);
        attachCampoValoresCotados(cotacao);
        atualizarEstadoDoBotaoFinalizar();
    }

    private void onPropostaAtualizada(@Nullable PropostaState proposta) {
        propostaAtual = proposta;
        atualizarValoresPedidoNaTabela(proposta);
        atualizarEstadoDoBotaoFinalizar();
    }

    private void onFechamentoAtualizado(@Nullable FechamentoState fechamento) {
        fechamentoAtual = fechamento;
        atualizarValoresFechamentoNaTabela(fechamento);
        atualizarEstadoDoBotaoFinalizar();
    }

    private void onFreteAtualizado(@Nullable FreteState frete) {
        freteAtual = frete;
        if (!isFreteValido(frete)) return;
        processarProposta(frete);
        atualizarCampoFrete(frete);
        atualizarEstadoCardFrete(frete);
    }

    private void onCorretorSelecionado(@Nullable CorretorState corretor) {
        corretorAtual = corretor;
        if (!isCorretorSelecionado(corretor)) {
            restaurarCardCorretor();
            return;
        }
        atualizarEstadoCardCorretor(corretor);
        processarFechamento(corretor);
    }

    private void onEmpresaSelecionada(@Nullable EmpresaState empresa) {
        if (!isEmpresaSelecionada(empresa)) return;
        attachNomeEmpresa(empresa);
    }


    private void aoSelecionarRacaNaLista(@NonNull RacaState raca) {
        racaViewModel.selecionarRaca(raca);
    }

    private void aoSelecionarCategoriaNaLista(@NonNull CategoriaState categoria) {
        categoriaViewModel.selecionarCategoria(categoria);
    }

    private void aoEspecificacaoAlterada() {
        animalViewModel.atualizarEspecificacao(obterSexoSelecionado(), obterIdade());
        atualizarEstadoDoBotaoFinalizar();
    }

    private void aoFreteManualAlterado() {
        if (hasCampoFreteVazio() || isValorZero(obterValorTotalFrete())) {
            restaurarFrete();
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

    private void processarCotacao() {
        simulacaoViewModel.processarCotacao(BigDecimal.valueOf(peso), quantidade);
    }

    private void processarProposta(@NonNull FreteState frete) {
        if (!isCotacaoValida(cotacaoAtual)) return;
        negociacaoViewModel.processarNegociacao(cotacaoAtual, BigDecimal.valueOf(peso), quantidade,
                frete.getValorTotal(), frete.getFreteState(), obterComissaoTotal());
    }

    private void processarFechamento(@NonNull CorretorState corretor) {
        if (!isCotacaoValida(cotacaoAtual) || !isFreteValido(freteAtual)) return;
        negociacaoViewModel.processarNegociacao(cotacaoAtual, BigDecimal.valueOf(peso), quantidade,
                freteAtual.getValorTotal(), freteAtual.getFreteState(), corretor.getComissao());
    }

    private void calcularIncidenciaFreteManual() {
        precificacaoFreteViewModel.calcularIncidencia(obterValorTotalFrete(), BigDecimal.valueOf(peso), quantidade);
    }

    private void recalcularPorCabeca(@NonNull BigDecimal valor, @Nullable CotacaoState cotacao,
                                     @Nullable FechamentoState fechamento, @Nullable FreteState frete) {
        if (isRecalculoInvalido(valor, cotacao, frete)) {
            restaurarPropostaEFechamento();
            setTextSafely(binding.campoValorKgEntrada, formatCurrency(BigDecimal.ZERO), valorKgTextWatcher);
            return;
        }
        negociacaoViewModel.recalcularPropostaPorCabeca(cotacao, fechamento, valor, BigDecimal.valueOf(peso),
                quantidade, obterValorParcialFrete(frete), obterFreteState(frete));
    }

    private void recalcularPorKg(@NonNull BigDecimal valor, @Nullable CotacaoState cotacao,
                                 @Nullable FechamentoState fechamento, @Nullable FreteState frete) {
        if (isRecalculoInvalido(valor, cotacao, frete)) {
            restaurarPropostaEFechamento();
            setTextSafely(binding.campoValorCabecaEntrada, formatCurrency(BigDecimal.ZERO), valorCabecaTextWatcher);
            return;
        }
        negociacaoViewModel.recalcularPropostaPorKg(cotacao, fechamento, valor, BigDecimal.valueOf(peso),
                quantidade, obterValorParcialFrete(frete), obterFreteState(frete));
    }

    private void atualizarEstadoCardInfoAnimal() {
        attachQuantidadeAnimais(quantidade);
        attachPesoMedioAnimais(peso);
    }

    private void atualizarCampoFrete(@NonNull FreteState frete) {
        if (isFreteManualEmEdicao(frete)) return;
        attachCampoFrete(frete);
        attachHelperTextFrete(frete.getValorParcial());
    }

    private void atualizarEstadoCardFrete(@NonNull FreteState frete) {
        if (isFreteManualEmEdicao(frete)) return;
        attachTituloFrete(frete);
        attachDescricaoFrete(frete);
    }

    private void atualizarEstadoCardCorretor(@NonNull CorretorState corretor) {
        attachNomeCorretor(corretor);
        attachDescricaoCorretor(corretor, quantidade);
    }

    private void atualizarValoresCotadosNaTabela(@NonNull CotacaoState cotacao) {
        attachValorCabecaEtapaCotado(cotacao);
        attachValorKgEtapaCotado(cotacao);
    }

    private void atualizarValoresPedidoNaTabela(@Nullable PropostaState proposta) {
        if (isPropostaIndisponivel(proposta)) {
            restaurarPlaceholderProposta();
            return;
        }
        attachCampoValoresProposta(proposta);
        attachValorCabecaEtapaPedido(proposta);
        attachValorKgEtapaPedido(proposta);
        attachBadgeFrete(proposta);
        attachValorFornecedor(proposta);
    }

    private void atualizarValoresFechamentoNaTabela(@Nullable FechamentoState fechamento) {
        if (isFechamentoIndisponivel(fechamento)) {
            restaurarPlaceholderFechamento();
            return;
        }
        attachValorCabecaEtapaFinal(fechamento);
        attachValorKgEtapaFinal(fechamento);
        attachBadgeCorretor(fechamento);
        attachValorTotal(fechamento);
    }

    private void atualizarEstadoDoBotaoFinalizar() {
        binding.botaoFinalizar.setEnabled(isProntoParaFinalizar(categoriaAtual, racaAtual, cotacaoAtual, propostaAtual, fechamentoAtual));
    }


    private void restaurarPlaceholderProposta() {
        attachPlaceholderValorCabecaEtapaPedido();
        attachPlaceholderValorKgEtapaPedido();
        attachPlaceholderBadgeFrete();
        attachPlaceholderValorFornecedor();
    }

    private void restaurarPlaceholderFechamento() {
        attachPlaceholderValorCabecaEtapaFinal();
        attachPlaceholderValorKgEtapaFinal();
        attachPlaceholderBadgeCorretor();
        attachPlaceholderValorTotal();
    }

    private void restaurarCardCorretor() {
        setText(binding.textoNomeCorretor, getString(R.string.texto_sem_corretor));
        setText(binding.textoDescricaoCorretor, getString(R.string.descricao_sem_comissao));
    }

    private void restaurarCardFrete() {
        setText(binding.textoTituloFrete, getString(R.string.titulo_frete));
        setText(binding.textoDescricaoFrete, getString(R.string.descricao_frete_vazio));
    }

    private void restaurarEspecificacao(@Nullable AnimalEspecificacaoState especificacao) {
        if (!isEspecificacaoValida(especificacao)) return;
        restaurarIdade(especificacao.getIdade());
        restaurarSexo(especificacao.getSexo());
    }

    private void restaurarIdade(@Nullable Integer idade) {
        if (!isIdadeValida(idade)) return;
        setTextSafely(binding.campoIdadeEntrada, String.valueOf(idade), especificacaoTextWatcher);
    }

    private void restaurarSexo(@Nullable String sexo) {
        if (!isSexoValido(sexo)) return;
        selectChip(binding.listaSexos, sexo);
    }

    private void restaurarFrete() {
        restaurarHelperTextFrete();
        restaurarPropostaEFechamento();
        restaurarCardFrete();
        restaurarCardCorretor();
        atualizarValoresCotadosNaTabela(cotacaoAtual);
        attachCampoValoresCotados(cotacaoAtual);
    }

    private void restaurarPropostaEFechamento() {
        restaurarPlaceholderProposta();
        restaurarPlaceholderFechamento();
    }

    private void restaurarHelperTextFrete() {
        attachHelperTextFrete(BigDecimal.ZERO);
    }

    private void attachListaCategorias(@NonNull List<CategoriaState> categorias) {
        categoriaAdapter.submitList(categorias);
    }

    private void attachListaRacas(@NonNull List<RacaState> racas) {
        racaAdapter.submitList(racas);
    }

    private void attachCampoFrete(@NonNull FreteState frete) {
        setTextSafely(binding.campoFreteEntrada, formatCurrency(frete.getValorTotal()), freteTextWatcher);
    }

    private void attachHelperTextFrete(@NonNull BigDecimal incidencia) {
        setHelperText(binding.campoFreteLayout, formatarHelperTextFrete(incidencia));
    }

    private void attachCampoValoresCotados(@NonNull CotacaoState cotacao) {
        setTextSafely(binding.campoValorCabecaEntrada, formatCurrency(cotacao.getValorPorCabeca()), valorCabecaTextWatcher);
        setTextSafely(binding.campoValorKgEntrada, formatCurrency(cotacao.getValorPorKg()), valorKgTextWatcher);
    }

    private void attachCampoValoresProposta(@NonNull PropostaState proposta) {
        setTextSafely(binding.campoValorCabecaEntrada, formatCurrency(proposta.getValorPorCabeca()), valorCabecaTextWatcher);
        setTextSafely(binding.campoValorKgEntrada, formatCurrency(proposta.getValorPorKg()), valorKgTextWatcher);
    }

    private void attachTituloFrete(@NonNull FreteState frete) {
        setText(binding.textoTituloFrete, formatarTituloValorFrete(frete));
    }

    private void attachDescricaoFrete(@NonNull FreteState frete) {
        setText(binding.textoDescricaoFrete, formatarDescricaoValorFrete(frete));
    }

    private void attachNomeCorretor(@NonNull CorretorState corretor) {
        setText(binding.textoNomeCorretor, corretor.getNome());
    }

    private void attachDescricaoCorretor(@NonNull CorretorState corretor, int quantidade) {
        setText(binding.textoDescricaoCorretor, formatarDescricaoCorretor(corretor, quantidade));
    }

    private void attachNomeEmpresa(@NonNull EmpresaState empresa) {
        setText(binding.textoNomeEmpresa, empresa.getNome());
    }

    private void attachQuantidadeAnimais(int quantidade) {
        setText(binding.textoValorQuantidade, formatarQuantidadeAnimais(quantidade));
    }

    private void attachPesoMedioAnimais(double peso) {
        setText(binding.textoValorPeso, formatarPesoMedioAnimais(peso));
    }

    private void attachValorCabecaEtapaCotado(@NonNull CotacaoState cotacao) {
        setText(binding.textoValorEtapaCotado, formatarValorEtapa(cotacao.getValorPorCabeca()));
    }

    private void attachValorKgEtapaCotado(@NonNull CotacaoState cotacao) {
        setText(binding.textoDescricaoEtapaCotado, formatarValorEtapa(cotacao.getValorPorKg()));
    }

    private void attachValorCabecaEtapaPedido(@NonNull PropostaState proposta) {
        setText(binding.textoValorEtapaPedido, formatarValorEtapa(proposta.getValorPorCabeca()));
    }

    private void attachValorKgEtapaPedido(@NonNull PropostaState proposta) {
        setText(binding.textoDescricaoEtapaPedido, formatarValorEtapa(proposta.getValorPorKg()));
    }

    private void attachBadgeFrete(@NonNull PropostaState proposta) {
        setText(binding.textoBadgeEtapaFrete, formatarBadgeAdicional(proposta.getFretePorKg()));
    }

    private void attachValorFornecedor(@NonNull PropostaState proposta) {
        setText(binding.textoValorFornecedor, formatCurrency(proposta.getValorTotal()));
    }

    private void attachValorCabecaEtapaFinal(@NonNull FechamentoState fechamento) {
        setText(binding.textoValorEtapaFinal, formatarValorEtapa(fechamento.getValorPorCabeca()));
    }

    private void attachValorKgEtapaFinal(@NonNull FechamentoState fechamento) {
        setText(binding.textoDescricaoEtapaFinal, formatarValorEtapa(fechamento.getValorPorKg()));
    }

    private void attachBadgeCorretor(@NonNull FechamentoState fechamento) {
        setText(binding.textoBadgeEtapaCorretor, formatarBadgeAdicional(fechamento.getComissaoPorKg()));
    }

    private void attachValorTotal(@NonNull FechamentoState fechamento) {
        setText(binding.textoValorTotal, formatCurrency(fechamento.getValorTotal()));
    }

    private void attachVariacaoPercentual(@NonNull Double variacao) {
        setText(binding.textoValorVariacao, formatarVariacaoPercentual(variacao));
    }

    private void attachPlaceholderValorCabecaEtapaPedido() {
        setText(binding.textoValorEtapaPedido, getString(R.string.placeholder_valor_monetario));
    }

    private void attachPlaceholderValorKgEtapaPedido() {
        setText(binding.textoDescricaoEtapaPedido, getString(R.string.placeholder_valor_por_kg_default));
    }

    private void attachPlaceholderBadgeFrete() {
        setText(binding.textoBadgeEtapaFrete, getString(R.string.placeholder_badge_diferenca_default));
    }

    private void attachPlaceholderValorFornecedor() {
        setText(binding.textoValorFornecedor, getString(R.string.placeholder_valor_total));
    }

    private void attachPlaceholderValorCabecaEtapaFinal() {
        setText(binding.textoValorEtapaFinal, getString(R.string.placeholder_valor_monetario));
    }

    private void attachPlaceholderValorKgEtapaFinal() {
        setText(binding.textoDescricaoEtapaFinal, getString(R.string.placeholder_valor_por_kg_default));
    }

    private void attachPlaceholderBadgeCorretor() {
        setText(binding.textoBadgeEtapaCorretor, getString(R.string.placeholder_badge_diferenca_default));
    }

    private void attachPlaceholderValorTotal() {
        setText(binding.textoValorTotal, getString(R.string.placeholder_valor_total));
    }


    @NonNull
    private String formatarHelperTextFrete(@NonNull BigDecimal incidencia) {
        return getString(R.string.helper_frete, formatCurrency(incidencia));
    }

    @NonNull
    private String formatarTituloValorFrete(@NonNull FreteState frete) {
        return String.format(Locale.getDefault(), "R$ %s/c", formatCurrency(frete.getValorTotal()));
    }

    @NonNull
    private String formatarDescricaoValorFrete(@NonNull FreteState frete) {
        return String.format(Locale.getDefault(), "R$ %s/kg", formatCurrency(frete.getValorParcial()));
    }

    @NonNull
    private String formatarBadgeAdicional(@NonNull BigDecimal valor) {
        return String.format(Locale.getDefault(), "+R$ %s/kg", formatCurrency(valor));
    }

    @NonNull
    private String formatarDescricaoCorretor(@NonNull CorretorState corretor, int quantidade) {
        return String.format(Locale.getDefault(), "R$ %s/%s - R$ %s/t",
                formatCurrency(corretor.getComissao()), corretor.getTipoComissao(),
                formatCurrency(corretor.getComissao().multiply(BigDecimal.valueOf(quantidade))));
    }

    @NonNull
    private String formatarQuantidadeAnimais(int quantidade) {
        return String.format(Locale.getDefault(), "%d", quantidade);
    }

    @NonNull
    private String formatarPesoMedioAnimais(double peso) {
        return String.format(Locale.getDefault(), "%.2f", peso);
    }

    @NonNull
    private String formatarValorEtapa(@NonNull BigDecimal valor) {
        return String.format(Locale.getDefault(), "R$ %s", formatCurrency(valor));
    }

    @NonNull
    private String formatarVariacaoPercentual(@NonNull Double variacao) {
        return String.format(Locale.getDefault(), "%.2f%%", variacao);
    }

    @NonNull
    private BigDecimal obterValorParcialFrete(@Nullable FreteState frete) {
        return frete != null ? frete.getValorParcial() : BigDecimal.ZERO;
    }

    @NonNull
    private StatusFrete obterFreteState(@Nullable FreteState frete) {
        return frete != null ? frete.getFreteState() : StatusFrete.NAO_SELECIONADO;
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
    private String obterSexoSelecionado() {
        return getCheckedChipText(binding.listaSexos);
    }

    @NonNull
    private BigDecimal obterComissaoTotal() {
        return !isCorretorSelecionado(corretorAtual) ? BigDecimal.ZERO : corretorAtual.getComissao();
    }

    @Nullable
    private Integer obterIdade() {
        return hasIdadePreenchida() ? parseInt(binding.campoIdadeEntrada) : null;
    }

    public boolean hasIdadePreenchida() {
        return isNotEmpty(binding.campoIdadeEntrada);
    }

    public boolean hasSexoSelecionado() {
        return isNotEmpty(getCheckedChipText(binding.listaSexos));
    }

    public boolean hasCampoFreteVazio() {
        return isEmpty(binding.campoFreteEntrada);
    }

    public boolean isAnimalValido(@Nullable AnimalState animal) {
        return isNotNull(animal);
    }

    public boolean isEspecificacaoValida(@Nullable AnimalEspecificacaoState especificacao) {
        return isNotNull(especificacao);
    }

    public boolean isIdadeValida(@Nullable Integer idade) {
        return isNotNull(idade);
    }

    public boolean isSexoValido(@Nullable String sexo) {
        return isNotNull(sexo);
    }

    public boolean isCotacaoValida(@Nullable CotacaoState cotacao) {
        return isNotNull(cotacao);
    }

    public boolean isPropostaValida(@Nullable PropostaState proposta) {
        return isNotNull(proposta);
    }

    public boolean isPropostaIndisponivel(@Nullable PropostaState proposta) {
        return !isPropostaValida(proposta) || isFreteNaoDescontado(proposta);
    }

    public boolean isFreteNaoDescontado(@NonNull PropostaState proposta) {
        return !proposta.isFreteDescontado();
    }

    public boolean isFechamentoValido(@Nullable FechamentoState fechamento) {
        return isNotNull(fechamento);
    }

    public boolean isFechamentoIndisponivel(@Nullable FechamentoState fechamento) {
        return !isFechamentoValido(fechamento) || isComissaoNaoAplicada(fechamento);
    }

    public boolean isComissaoNaoAplicada(@NonNull FechamentoState fechamento) {
        return !fechamento.isComissaoAplicada();
    }

    public boolean isFreteValido(@Nullable FreteState frete) {
        return isNotNull(frete);
    }

    public boolean isFreteManual(@NonNull FreteState frete) {
        return frete.getFreteState() == StatusFrete.MANUAL;
    }

    public boolean isFreteManualEmEdicao(@NonNull FreteState frete) {
        return isFreteManual(frete) && !hasCampoFreteVazio();
    }

    public boolean isCategoriaSelecionada(@Nullable CategoriaState categoria) {
        return isNotNull(categoria);
    }

    public boolean isRacaSelecionada(@Nullable RacaState raca) {
        return isNotNull(raca);
    }

    public boolean isCorretorSelecionado(@Nullable CorretorState corretor) {
        return isNotNull(corretor);
    }

    public boolean isEmpresaSelecionada(@Nullable EmpresaState empresa) {
        return isNotNull(empresa);
    }

    public boolean isRecalculoInvalido(@NonNull BigDecimal valor, @Nullable CotacaoState cotacao, @Nullable FreteState frete) {
        return isValorZero(valor) || !isCotacaoValida(cotacao) || !isFreteValido(frete);
    }

    public boolean isValorZero(@NonNull BigDecimal valor) {
        return isEmpty(valor);
    }

    public boolean isFormularioValido(@Nullable CategoriaState categoria, @Nullable RacaState raca) {
        return hasIdadePreenchida() && hasSexoSelecionado() && isCategoriaSelecionada(categoria) && isRacaSelecionada(raca);
    }

    public boolean isNegociacaoValida(@Nullable CotacaoState cotacao, @Nullable PropostaState proposta, @Nullable FechamentoState fechamento) {
        return isCotacaoValida(cotacao) && isPropostaValida(proposta) && isFechamentoValido(fechamento);
    }

    public boolean isProntoParaFinalizar(@Nullable CategoriaState categoria, @Nullable RacaState raca, @Nullable CotacaoState cotacao, @Nullable PropostaState proposta, @Nullable FechamentoState fechamento) {
        return isFormularioValido(categoria, raca) && isNegociacaoValida(cotacao, proposta, fechamento);
    }

    private void navegarParaSimulacaoDeFrete() {
        if (!isCategoriaSelecionada(categoriaAtual)) {
            exibirErroCategoriaParaFrete();
            return;
        }
        navegar(this, R.id.negociacaoFragment,
                NegociacaoFragmentDirections.actionNegociacaoFragmentToSimulacaoFreteeFragment()
                        .setCargaTotal(quantidade).setPesoMedio((float) peso));
    }

    private void abrirSeletorCorretor() {
        if (hasCampoFreteVazio()) {
            exibirErroFreteObrigatorio();
            return;
        }
        exibirBottomSheetCorretor();
    }

    private void finalizar() {
        if (!isProntoParaFinalizar(categoriaAtual, racaAtual, cotacaoAtual, propostaAtual, fechamentoAtual)) {
            exibirErroCamposObrigatorios();
            return;
        }
        exibirSucessoFinalizacao();
        NavigationHelper.voltar(this);
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
}