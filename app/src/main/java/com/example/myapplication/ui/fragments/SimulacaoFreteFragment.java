package com.example.myapplication.ui.fragments;

import static com.example.myapplication.ui.helpers.AlertHelper.showDialog;
import static com.example.myapplication.ui.helpers.FormatHelper.formatCurrency;
import static com.example.myapplication.ui.helpers.NavigationHelper.voltar;
import static com.example.myapplication.ui.helpers.PermissionHelper.hasPermissions;
import static com.example.myapplication.ui.helpers.PermissionHelper.register;
import static com.example.myapplication.ui.helpers.PermissionHelper.request;
import static com.example.myapplication.ui.helpers.RecyclerViewHelper.setupHorizontalRecyclerView;
import static com.example.myapplication.ui.helpers.TextWatcherHelper.simpleTextWatcher;
import static com.example.myapplication.ui.helpers.ViewHelper.clearText;
import static com.example.myapplication.ui.helpers.ViewHelper.isEmpty;
import static com.example.myapplication.ui.helpers.ViewHelper.isNotEmpty;
import static com.example.myapplication.ui.helpers.ViewHelper.isNotNull;
import static com.example.myapplication.ui.helpers.ViewHelper.isNull;
import static com.example.myapplication.ui.helpers.ViewHelper.orElse;
import static com.example.myapplication.ui.helpers.ViewHelper.parseDouble;
import static com.example.myapplication.ui.helpers.ViewHelper.setText;
import static com.example.myapplication.ui.helpers.ViewHelper.setVisible;

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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.data.models.Transporte;
import com.example.myapplication.databinding.FragmentSimulacaoFreteBinding;
import com.example.myapplication.ui.adapters.TransporteAdapter;
import com.example.myapplication.ui.state.animal.CategoriaState;
import com.example.myapplication.ui.state.frete.FreteState;
import com.example.myapplication.ui.state.frete.RotaState;
import com.example.myapplication.ui.state.frete.TransporteState;
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

    private static final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final String TAG_BOTTOM_SHEET_LOCATION_SEARCH = "BuscaLocalizacaoBottomSheet";

    @Inject
    TransporteMapper transporteMapper;

    private FragmentSimulacaoFreteBinding binding;
    private TransporteAdapter transporteAdapter;

    private PrecificacaoFreteViewModel precificacaoFreteViewModel;
    private RotaViewModel rotaViewModel;
    private TransporteViewModel transporteViewModel;
    private CategoriaViewModel categoriaViewModel;

    private TextWatcher distanciaManualWatcher;
    private ActivityResultLauncher<String[]> permissionLauncher;

    private int cargaTotalDoLote;
    private double pesoMedio;

    private RotaState rotaAtual;
    private List<TransporteState> transportesAtuais;
    private FreteState freteAtual;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registrarPermissionLauncher();
        registrarBackPressedCallback();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSimulacaoFreteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializar();
    }

    @Override
    public void onStart() {
        super.onStart();
        solicitarPermissoesSeNecessario();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        sincronizarVisibilidade();
    }

    @Override
    public void onDestroyView() {
        removerDistanciaWatcher();
        super.onDestroyView();
        binding = null;
    }
    private void inicializar() {
        extrairArgumentos();
        inicializarViewModels();
        configurarComportamentosDeTela();
        configurarEventosDeClique();
        observarViewModels();
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

    private void configurarComportamentosDeTela() {
        configurarRecyclerViewTransportes();
        inicializarDistanciaWatcher();
        anexarDistanciaWatcher();
    }

    private void configurarRecyclerViewTransportes() {
        transporteAdapter = new TransporteAdapter();
        setupHorizontalRecyclerView(binding.listTransport, transporteAdapter, requireContext());
    }

    private void inicializarDistanciaWatcher() {
        distanciaManualWatcher = simpleTextWatcher(this::aoAlterarDistanciaManual);
    }

    private void anexarDistanciaWatcher() {
        binding.entradaTextoDistancia.addTextChangedListener(distanciaManualWatcher);
    }

    private void removerDistanciaWatcher() {
        if (binding == null || distanciaManualWatcher == null) return;
        binding.entradaTextoDistancia.removeTextChangedListener(distanciaManualWatcher);
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
        binding.toolbar.setOnClickListener(v -> voltar(this));
    }

    private void configurarCliqueContinuar() {
        binding.botaoContinuar.setOnClickListener(v -> confirmarFrete());
    }

    private void observarViewModels() {
        observarRota();
        observarTransportes();
        observarCategoria();
        observarFrete();
        observarDistancia();
    }

    private void observarRota() {
        rotaViewModel.getState().observe(getViewLifecycleOwner(), this::onRotaAtualizada);
    }

    private void observarTransportes() {
        transporteViewModel.getState().observe(getViewLifecycleOwner(), this::onTransportesAtualizados);
    }

    private void observarCategoria() {
        categoriaViewModel.getCategoriaSelecionada().observe(getViewLifecycleOwner(), this::onCategoriaAtualizada);
    }

    private void observarFrete() {
        precificacaoFreteViewModel.getState().observe(getViewLifecycleOwner(), this::onFreteAtualizado);
    }

    private void observarDistancia() {
        precificacaoFreteViewModel.getDistancia().observe(getViewLifecycleOwner(), this::onDistanciaSalvaAtualizada);
    }

    private void onRotaAtualizada(RotaState rota) {
        rotaAtual = rota;
        if (isDistanciaManualComPrioridadeSobreRota()) {
            rotaViewModel.limpar();
            return;
        }
        if (isRotaDefinida()) limparDistanciaManual();
        sincronizarEstado();
    }

    private void onTransportesAtualizados(List<TransporteState> transportes) {
        transportesAtuais = transportes;
        exibirListaDeTransportes(transportes);
        sincronizarEstado();
    }

    private void onCategoriaAtualizada(CategoriaState categoria) {
        if (isNull(categoria)) return;
        transporteViewModel.recomendar(categoria.getId(), cargaTotalDoLote);
    }

    private void onFreteAtualizado(FreteState frete) {
        freteAtual = frete;
        setVisible(isFreteCalculado(), binding.containerResumoValores);
        if (isNull(frete)) return;
        exibirResumoFrete(frete);
    }

    private void onDistanciaSalvaAtualizada(Double distancia) {
        if (isDistanciaManualPreenchida()) return;
        if (isDistanciaValida(distancia)) exibirDistanciaManual(distancia);
    }

    private void aoAlterarDistanciaManual() {
        precificacaoFreteViewModel.setDistancia(lerDistanciaManual());
        if (isDistanciaManualPreenchida()) rotaViewModel.limpar();
        sincronizarEstado();
    }

    private void confirmarFrete() {
        if (isFreteInvalidoParaConfirmar()) return;
        voltar(this);
    }

    private void sincronizarEstado() {
        sincronizarVisibilidade();
        recalcularFrete();
    }

    private void sincronizarVisibilidade() {
        setVisible(isContainerRotaVisivel(), binding.containerRota);
        setVisible(isContainerTransporteVisivel(), binding.containerTransporte);
        if (isRotaDefinida()) exibirDadosDaRota(rotaAtual);
    }

    private void recalcularFrete() {
        if (isDadosInsuficientesParaCalcular()) {
            limparResultadoDaSimulacao();
            return;
        }
        precificacaoFreteViewModel.calcularFrete(
                mapearTransportesDisponiveis(),
                resolverDistanciaAtiva(),
                cargaTotalDoLote,
                BigDecimal.valueOf(pesoMedio));
    }

    private void exibirDadosDaRota(@NonNull RotaState rota) {
        atualizarCidadeOrigem(rota);
        atualizarCidadeDestino(rota);
        atualizarDistancia(rota);
    }

    private void atualizarCidadeOrigem(@NonNull RotaState rota) {
        attachCidadeOrigem(rota.getCidadeOrigem());
        attachEstadoOrigem(rota.getEstadoOrigem());
    }

    private void atualizarCidadeDestino(@NonNull RotaState rota) {
        attachCidadeDestino(rota.getCidadeDestino());
        attachEstadoDestino(rota.getEstadoDestino());
    }

    private void atualizarDistancia(@NonNull RotaState rota) {
        attachDistancia(String.valueOf(rota.getDistancia()));
    }

    private void exibirResumoFrete(@NonNull FreteState frete) {
        atualizarValorTotal(frete);
        atualizarValorParcial(frete);
    }

    private void atualizarValorTotal(@NonNull FreteState frete) {
        attachValorTotal(formatCurrency(frete.getValorTotal()));
    }

    private void atualizarValorParcial(@NonNull FreteState frete) {
        attachValorParcial(formatCurrency(frete.getValorParcial()));
    }

    private void attachCidadeOrigem(@NonNull String valor) {
        setText(binding.textoCidadeOrigem, valor);
    }

    private void attachEstadoOrigem(@NonNull String valor) {
        setText(binding.textoEstadoOrigem, valor);
    }

    private void attachCidadeDestino(@NonNull String valor) {
        setText(binding.textoCidadeDestino, valor);
    }

    private void attachEstadoDestino(@NonNull String valor) {
        setText(binding.textoEstadoDestino, valor);
    }

    private void attachDistancia(@NonNull String valor) {
        setText(binding.textoValorDistancia, valor);
    }

    private void attachValorTotal(@NonNull String valor) {
        setText(binding.textoValorPrincipal, valor);
    }

    private void attachValorParcial(@NonNull String valor) {
        setText(binding.textoValorSecundario, valor);
    }

    private void exibirListaDeTransportes(List<TransporteState> transportes) {
        transporteAdapter.submitList(isNotEmpty(transportes) ? transportes : Collections.emptyList());
    }

    private void exibirDistanciaManual(Double distancia) {
        setText(binding.entradaTextoDistancia, String.valueOf(distancia));
    }

    private void limparResultadoDaSimulacao() {
        precificacaoFreteViewModel.limpar();
        setVisible(false, binding.containerResumoValores);
    }

    private void limparDistanciaManual() {
        if (isDistanciaManualVazia()) return;
        removerDistanciaWatcher();
        clearText(binding.entradaTextoDistancia);
        precificacaoFreteViewModel.setDistancia(0);
        anexarDistanciaWatcher();
    }

    private void registrarPermissionLauncher() {
        permissionLauncher = register(this, (concedida, result) -> {
            if (!concedida) exibirDialogoPermissao();
        });
    }

    private void registrarBackPressedCallback() {
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                voltar(SimulacaoFreteFragment.this);
            }
        });
    }

    private void solicitarPermissoesSeNecessario() {
        if (isPermissoesDeLocalizacaoConcedidas()) return;
        request(requireContext(), permissionLauncher, LOCATION_PERMISSIONS);
    }

    private void exibirDialogoPermissao() {
        showDialog(
                requireContext(),
                getString(R.string.dialogo_titulo_permissao_localizacao),
                getString(R.string.dialogo_mensagem_permissao_localizacao),
                (dialogo, qual) -> abrirConfiguracoesDoApp(),
                (dialogo, qual) -> requireActivity().finish()
        );
    }

    private void abrirBuscaDeLocalizacao() {
        FragmentManager fm = getChildFragmentManager();
        if (isNotNull(fm.findFragmentByTag(TAG_BOTTOM_SHEET_LOCATION_SEARCH))) return;
        new BuscaLocalizacaoBottomSheetDialogFragment().show(fm, TAG_BOTTOM_SHEET_LOCATION_SEARCH);
    }

    private void abrirConfiguracoesDoApp() {
        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", requireContext().getPackageName(), null)));
    }

    private double resolverDistanciaAtiva() {
        if (isDistanciaManualPreenchida()) return lerDistanciaManual();
        return isRotaDefinida() ? rotaAtual.getDistancia() : 0.0;
    }

    private double lerDistanciaManual() {
        return parseDouble(binding.entradaTextoDistancia);
    }

    private List<Transporte> mapearTransportesDisponiveis() {
        return transporteMapper.mapTo(orElse(transportesAtuais, Collections.emptyList()));
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

    private boolean isFreteInvalidoParaConfirmar() {
        return isEmpty(freteAtual) || isEmpty(freteAtual.getValorTotal());
    }

    private boolean isDistanciaValida(Double distancia) {
        return isNotEmpty(distancia) && distancia > 0;
    }

    private boolean isPermissoesDeLocalizacaoConcedidas() {
        return hasPermissions(requireContext(), LOCATION_PERMISSIONS);
    }
}