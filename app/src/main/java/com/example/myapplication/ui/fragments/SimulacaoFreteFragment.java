package com.example.myapplication.ui.fragments;

import static com.example.myapplication.ui.helpers.AlertHelper.showDialog;
import static com.example.myapplication.ui.helpers.FormatHelper.formatCurrency;
import static com.example.myapplication.ui.helpers.PermissionHelper.hasPermissions;
import static com.example.myapplication.ui.helpers.PermissionHelper.register;
import static com.example.myapplication.ui.helpers.PermissionHelper.request;
import static com.example.myapplication.ui.helpers.TextWatcherHelper.simpleTextWatcher;
import static com.example.myapplication.ui.helpers.ViewHelper.clearText;
import static com.example.myapplication.ui.helpers.ViewHelper.isEmpty;
import static com.example.myapplication.ui.helpers.ViewHelper.isNotEmpty;
import static com.example.myapplication.ui.helpers.ViewHelper.orElse;
import static com.example.myapplication.ui.helpers.ViewHelper.parseDouble;
import static com.example.myapplication.ui.helpers.ViewHelper.setText;
import static com.example.myapplication.ui.helpers.ViewHelper.setVisible;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.ui.helpers.NavigationHelper;

import com.example.myapplication.R;
import com.example.myapplication.data.models.Transporte;
import com.example.myapplication.databinding.FragmentSimulacaoFreteBinding;
import com.example.myapplication.ui.adapters.TransporteAdapter;
import com.example.myapplication.ui.state.animal.CategoriaState;
import com.example.myapplication.ui.state.PrecificacaoFreteState;
import com.example.myapplication.ui.state.RotaState;
import com.example.myapplication.ui.state.TransporteState;
import com.example.myapplication.ui.viewmodel.CategoriaViewModel;
import com.example.myapplication.ui.viewmodel.PrecificacaoFreteViewModel;
import com.example.myapplication.ui.viewmodel.RotaViewModel;
import com.example.myapplication.ui.viewmodel.TransporteViewModel;
import com.example.myapplication.utils.mappers.domain.TransporteMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SimulacaoFreteFragment extends Fragment {

    private static final String[] PERMISSOES_LOCALIZACAO = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static final String TAG_BOTTOM_SHEET_BUSCA_LOCALIZACAO = "BuscaLocalizacaoBottomSheet";

    @Inject
    TransporteMapper transporteMapper;
    private FragmentSimulacaoFreteBinding binding;
    private TransporteAdapter transporteAdapter;
    private PrecificacaoFreteViewModel precificacaoFreteViewModel;
    private RotaViewModel rotaViewModel;
    private TransporteViewModel transporteViewModel;
    private CategoriaViewModel categoriaViewModel;
    private TextWatcher distanciaManualWatcher;
    private ActivityResultLauncher<String[]> solicitacaoDePermissaoLauncher;
    private int cargaTotalDoLote;
    private double pesoMedio;
    private RotaState rotaAtual;
    private List<TransporteState> transportesAtuais;
    private PrecificacaoFreteState freteAtual;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registrarLauncherDePermissao();
        registrarCallbackDeBotaoVoltar();
    }

    @Override
    public void onStart() {
        super.onStart();
        solicitarPermissoesDeLocalizacaoSeNecessario();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSimulacaoFreteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        extrairArgumentos();
        inicializarViewModels();
        configurarInterface();
        observarEstadosDosViewModels();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        atualizarVisibilidadeDosContainers();
    }

    @Override
    public void onDestroyView() {
        if (binding != null && distanciaManualWatcher != null) {
            binding.entradaTextoDistancia.removeTextChangedListener(distanciaManualWatcher);
        }
        super.onDestroyView();
        binding = null;
    }

    private void extrairArgumentos() {
        SimulacaoFreteFragmentArgs args = SimulacaoFreteFragmentArgs.fromBundle(requireArguments());
        cargaTotalDoLote = args.getCargaTotal();
        pesoMedio = args.getPesoMedio();
    }

    private void inicializarViewModels() {
        rotaViewModel = new ViewModelProvider(requireActivity()).get(RotaViewModel.class);
        transporteViewModel = new ViewModelProvider(requireActivity()).get(TransporteViewModel.class);
        precificacaoFreteViewModel = new ViewModelProvider(requireActivity()).get(PrecificacaoFreteViewModel.class);
        categoriaViewModel = new ViewModelProvider(requireActivity()).get(CategoriaViewModel.class);
    }

    private void configurarInterface() {
        configurarAdapterDeTransporte();
        configurarWatcherDeDistanciaManual();
        configurarEventosDeClique();
    }

    private void configurarAdapterDeTransporte() {
        transporteAdapter = new TransporteAdapter();
        transporteAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        binding.listTransport.setAdapter(transporteAdapter);
    }

    private void configurarWatcherDeDistanciaManual() {
        distanciaManualWatcher = simpleTextWatcher(this::aoAlterarDistanciaManual);
        binding.entradaTextoDistancia.addTextChangedListener(distanciaManualWatcher);
    }

    private void configurarEventosDeClique() {
        configurarCliqueExplorarRota();
        configurarCliqueVoltar();
        configurarCliqueContinuar();
    }

    private void configurarCliqueExplorarRota() {
        binding.cartaoExplorarRota.setOnClickListener(v -> abrirBuscaDeLocalizacao());
    }

    private void configurarCliqueVoltar() {
        binding.toolbar.setOnClickListener(v -> navegarDeVolta());
    }

    private void configurarCliqueContinuar() {
        binding.botaoContinuar.setOnClickListener(v -> confirmarFrete());
    }

    private void observarEstadosDosViewModels() {
        observarEstadoDaRota();
        observarEstadoDeTransporte();
        observarCategoriaSelecionada();
        observarResumoDoFrete();
        observarDistanciaSalva();
    }

    private void observarEstadoDaRota() {
        rotaViewModel.getState().observe(getViewLifecycleOwner(), this::aoAtualizarRota);
    }

    private void observarEstadoDeTransporte() {
        transporteViewModel.getState().observe(getViewLifecycleOwner(), this::aoAtualizarListaDeTransportes);
    }

    private void observarCategoriaSelecionada() {
        categoriaViewModel.getCategoriaSelecionada().observe(getViewLifecycleOwner(), this::aoAtualizarCategoria);
    }

    private void observarResumoDoFrete() {
        precificacaoFreteViewModel.getState().observe(getViewLifecycleOwner(), this::aoAtualizarResumoDoFrete);
    }

    private void observarDistanciaSalva() {
        precificacaoFreteViewModel.getDistancia().observe(getViewLifecycleOwner(), this::aoRestaurarDistanciaSalva);
    }

    private void aoAtualizarRota(RotaState rota) {
        rotaAtual = rota;
        if (isDistanciaManualComPrioridadeSobreRota()) {
            rotaViewModel.limpar();
            return;
        }
        if (isRotaDefinida()) limparDistanciaManual();
        sincronizarEstado();
    }

    private void aoAtualizarListaDeTransportes(List<TransporteState> transportes) {
        transportesAtuais = transportes;
        exibirListaDeTransportes(transportesAtuais);
        sincronizarEstado();
    }

    private void aoAtualizarCategoria(CategoriaState categoria) {
        if (isCategoriaNaoSelecionada(categoria)) return;
        transporteViewModel.recomendar(categoria.getId(), cargaTotalDoLote);
    }

    private void aoAlterarDistanciaManual() {
        double distancia = lerDistanciaManual();
        precificacaoFreteViewModel.setDistancia(distancia);
        if (isDistanciaManualPreenchida()) rotaViewModel.limpar();
        sincronizarEstado();
    }

    private void aoAtualizarResumoDoFrete(PrecificacaoFreteState estado) {
        freteAtual = estado;
        exibirContainerResumo(isFreteCalculado());
        if (isFreteSemEstado()) return;
        exibirValoresDoResumo(freteAtual);
    }

    private void aoRestaurarDistanciaSalva(Double distancia) {
        if (isDistanciaManualPreenchida()) return;
        if (isDistanciaValida(distancia)) exibirDistanciaManual(distancia);
    }

    private void sincronizarEstado() {
        atualizarVisibilidadeDosContainers();
        recalcularFrete();
    }

    private void recalcularFrete() {
        if (isDadosInsuficientesParaCalcular()) {
            limparResultadoDaSimulacao();
            return;
        }
        calcularFrete();
    }

    private void calcularFrete() {
        precificacaoFreteViewModel.calcularFrete(
                mapearTransportesDisponiveis(),
                resolverDistanciaAtiva(),
                cargaTotalDoLote,
                BigDecimal.valueOf(pesoMedio));
    }

    private void confirmarFrete() {
        if (isFreteInvalidoParaConfirmar()) return;
        navegarDeVolta();
    }

    private void atualizarVisibilidadeDosContainers() {
        exibirContainerDeRota();
        exibirContainerDeTransporte();
        if (isRotaDefinida()) exibirDadosDaRota();
    }

    private void exibirContainerDeRota() {
        setVisible(isContainerRotaVisivel(), binding.containerRota);
    }

    private void exibirContainerDeTransporte() {
        setVisible(isContainerTransporteVisivel(), binding.containerTransporte);
    }

    private void exibirContainerResumo(boolean visivel) {
        setVisible(visivel, binding.containerResumoValores);
    }

    private void exibirDadosDaRota() {
        exibirOrigem(rotaAtual);
        exibirDestino(rotaAtual);
        exibirDistanciaDaRota(rotaAtual);
    }

    private void exibirOrigem(@NonNull RotaState rota) {
        setText(binding.textoCidadeOrigem, rota.getCidadeOrigem());
        setText(binding.textoEstadoOrigem, rota.getEstadoOrigem());
    }

    private void exibirDestino(@NonNull RotaState rota) {
        setText(binding.textoCidadeDestino, rota.getCidadeDestino());
        setText(binding.textoEstadoDestino, rota.getEstadoDestino());
    }

    private void exibirDistanciaDaRota(@NonNull RotaState rota) {
        setText(binding.textoValorDistancia, String.valueOf(rota.getDistancia()));
    }

    private void exibirValoresDoResumo(@NonNull PrecificacaoFreteState estado) {
        setText(binding.textoValorPrincipal, formatCurrency(estado.getValorTotal()));
        setText(binding.textoValorSecundario, formatCurrency(estado.getValorParcial()));
    }

    private void exibirListaDeTransportes(List<TransporteState> transportes) {
        transporteAdapter.submitList(isNotEmpty(transportes) ? transportes : Collections.emptyList());
    }

    private void exibirDistanciaManual(Double distancia) {
        setText(binding.entradaTextoDistancia, String.valueOf(distancia));
    }

    private void limparResultadoDaSimulacao() {
        precificacaoFreteViewModel.limpar();
        exibirContainerResumo(false);
    }

    private void limparDistanciaManual() {
        if (isDistanciaManualVazia()) return;
        removerWatcher();
        limparCampoDeDistancia();
        adicionarWatcher();
    }

    private void limparCampoDeDistancia() {
        clearText(binding.entradaTextoDistancia);
        precificacaoFreteViewModel.setDistancia(0);
    }

    private void removerWatcher() {
        binding.entradaTextoDistancia.removeTextChangedListener(distanciaManualWatcher);
    }

    private void adicionarWatcher() {
        binding.entradaTextoDistancia.addTextChangedListener(distanciaManualWatcher);
    }

    private void registrarLauncherDePermissao() {
        solicitacaoDePermissaoLauncher = register(this, (concedida, result) -> {
            if (!concedida) exibirDialogoExplicandoNecessidadeDePermissao();
        });
    }

    private void solicitarPermissoesDeLocalizacaoSeNecessario() {
        if (isPermissoesDeLocalizacaoConcedidas()) return;
        request(requireContext(), solicitacaoDePermissaoLauncher, PERMISSOES_LOCALIZACAO);
    }

    private void exibirDialogoExplicandoNecessidadeDePermissao() {
        showDialog(
                requireContext(),
                getString(R.string.dialogo_titulo_permissao_localizacao),
                getString(R.string.dialogo_mensagem_permissao_localizacao),
                (dialogo, qual) -> abrirConfiguracoesDoPeloApp(),
                (dialogo, qual) -> encerrarActivity()
        );
    }

    private void abrirConfiguracoesDoPeloApp() {
        startActivity(new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", requireContext().getPackageName(), null)));
    }

    private void encerrarActivity() {
        requireActivity().finish();
    }

    private void registrarCallbackDeBotaoVoltar() {
        requireActivity().getOnBackPressedDispatcher()
                .addCallback(this, new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navegarDeVolta();
                    }
                });
    }

    private void abrirBuscaDeLocalizacao() {
        FragmentManager fm = getChildFragmentManager();
        if (fm.findFragmentByTag(TAG_BOTTOM_SHEET_BUSCA_LOCALIZACAO) != null) return;
        new BuscaLocalizacaoBottomSheetDialogFragment().show(fm, TAG_BOTTOM_SHEET_BUSCA_LOCALIZACAO);
    }

    private void navegarDeVolta() {
        NavigationHelper.voltar(this);
    }

    private double resolverDistanciaAtiva() {
        if (isDistanciaManualPreenchida()) return lerDistanciaManual();
        return lerDistanciaDefinidaPelaRota();
    }

    private double lerDistanciaDefinidaPelaRota() {
        return isRotaDefinida() ? rotaAtual.getDistancia() : 0.0;
    }

    private List<Transporte> mapearTransportesDisponiveis() {
        return transporteMapper.mapTo(orElse(transportesAtuais, Collections.emptyList()));
    }

    private double lerDistanciaManual() {
        return parseDouble(binding.entradaTextoDistancia);
    }

    private boolean isRotaDefinida() {
        return isNotEmpty(rotaAtual);
    }

    private boolean isDistanciaManualPreenchida() {
        return isNotEmpty(lerDistanciaManual());
    }

    private boolean isDistanciaManualVazia() {
        return isEmpty(lerDistanciaManual());
    }

    private boolean isDistanciaManualComPrioridadeSobreRota() {
        return isRotaDefinida() && isDistanciaManualPreenchida();
    }

    private boolean isContainerRotaVisivel() {
        return isRotaDefinida() && !isDistanciaManualPreenchida();
    }

    private boolean isContainerTransporteVisivel() {
        return isDistanciaAtiva() && isTransportesDisponiveis();
    }

    private boolean isDistanciaAtiva() {
        return isRotaDefinida() || isDistanciaManualPreenchida();
    }

    private boolean isTransportesDisponiveis() {
        return isNotEmpty(transportesAtuais);
    }

    private boolean isDadosInsuficientesParaCalcular() {
        return isEmpty(resolverDistanciaAtiva()) || !isTransportesDisponiveis();
    }

    private boolean isFreteCalculado() {
        return isNotEmpty(freteAtual);
    }

    private boolean isFreteSemEstado() {
        return isEmpty(freteAtual);
    }

    private boolean isFreteInvalidoParaConfirmar() {
        return isEmpty(freteAtual) || isEmpty(freteAtual.getValorTotal());
    }

    private boolean isDistanciaValida(Double distancia) {
        return isNotEmpty(distancia) && distancia > 0;
    }

    private boolean isCategoriaNaoSelecionada(CategoriaState categoria) {
        return categoria == null;
    }

    private boolean isPermissoesDeLocalizacaoConcedidas() {
        return hasPermissions(requireContext(), PERMISSOES_LOCALIZACAO);
    }
}