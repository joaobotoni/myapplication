package com.example.myapplication.ui.adapters;

import static com.example.myapplication.ui.helpers.ViewHelper.setText;
import static com.example.myapplication.ui.helpers.ViewHelper.setVisible;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemEmpresaBinding;
import com.example.myapplication.ui.state.empresa.EmpresaState;

import java.util.Objects;

public class EmpresaAdapter extends ListAdapter<EmpresaState, EmpresaAdapter.ViewHolder> {

    public interface OnClickListener {
        void onClick(EmpresaState empresaUiState);
    }

    private final OnClickListener clickListener;

    public EmpresaAdapter(OnClickListener clickListener) {
        super(new DiffCallback());
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemEmpresaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemEmpresaBinding binding;
        private EmpresaState item;

        public ViewHolder(@NonNull ItemEmpresaBinding binding, OnClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                if (item != null) listener.onClick(item);
            });
        }

        protected void bind(EmpresaState empresa) {
            this.item = empresa;
            setText(binding.textoNomeEmpresa, empresa.getNome());
            setVisible(empresa.isSelected(), binding.checkImage);
        }
    }

    private static class DiffCallback extends DiffUtil.ItemCallback<EmpresaState> {

        @Override
        public boolean areItemsTheSame(@NonNull EmpresaState oldItem, @NonNull EmpresaState newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull EmpresaState oldItem, @NonNull EmpresaState newItem) {
            return oldItem.getId() == newItem.getId()
                    && Objects.equals(oldItem.getNome(), newItem.getNome())
                    && oldItem.isSelected() == newItem.isSelected();
        }
    }
}
