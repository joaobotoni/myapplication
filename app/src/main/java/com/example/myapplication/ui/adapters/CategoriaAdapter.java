package com.example.myapplication.ui.adapters;


import static com.example.myapplication.ui.helpers.ViewHelper.setText;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemCategoriaBinding;
import com.example.myapplication.ui.state.animal.CategoriaState;

import java.util.Objects;

public class CategoriaAdapter extends ListAdapter<CategoriaState, CategoriaAdapter.ViewHolder> {
    public interface OnClickListener {
        void onClick(CategoriaState categoria);
    }

    private final OnClickListener listener;

    public CategoriaAdapter(OnClickListener listener) {
        super(new DiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCategoriaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoriaBinding binding;
        private CategoriaState item;

        public ViewHolder(ItemCategoriaBinding binding, OnClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.cardOpcao.setOnClickListener(v -> {
                if (item != null) listener.onClick(item);
            });
        }

        protected void bind(CategoriaState estado) {
            this.item = estado;
            setText(binding.textoOpcao, estado.getDescricao());
            binding.cardOpcao.setChecked(estado.isSelected());
        }
    }

    private static class DiffCallback extends DiffUtil.ItemCallback<CategoriaState> {
        @Override
        public boolean areItemsTheSame(@NonNull CategoriaState oldItem, @NonNull CategoriaState newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CategoriaState oldItem, @NonNull CategoriaState newItem) {
            return oldItem.getId() == newItem.getId()
                    && Objects.equals(oldItem.getDescricao(), newItem.getDescricao())
                    && oldItem.isSelected() == newItem.isSelected();
        }
    }
}
