package com.example.myapplication.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.databinding.FragmentBottomSheetCorretorBinding;
import com.example.myapplication.ui.adapters.CorretorAdapter;
import com.example.myapplication.ui.state.empresa.CorretorState;
import com.example.myapplication.ui.viewmodel.CorretorViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CorretorBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private FragmentBottomSheetCorretorBinding binding;
    private CorretorAdapter adapter;
    private CorretorViewModel corretorViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBottomSheetCorretorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iniciarSetup();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void iniciarSetup() {
        configurarViewModel();
        configurarRecyclerViewCorretor();
        configurarObservadores();
    }

    private void configurarViewModel() {
        corretorViewModel = new ViewModelProvider(requireActivity()).get(CorretorViewModel.class);
    }

    private void configurarObservadores() {
        observarCorretor();
    }

    private void observarCorretor() {
        corretorViewModel.getState().observe(getViewLifecycleOwner(), this::atualizarListaCorretor);
    }

    private void atualizarListaCorretor(List<CorretorState> corretores) {
        adapter.submitList(corretores);
    }

    private void configurarRecyclerViewCorretor() {
        adapter = new CorretorAdapter(this::aoSelecionarCorretorNaLista);
        binding.recyclerViewCorretores.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerViewCorretores.setAdapter(adapter);
    }

    private void aoSelecionarCorretorNaLista(CorretorState corretorState) {
        corretorViewModel.selecionarCorretor(corretorState);
        dismiss();
    }
}
