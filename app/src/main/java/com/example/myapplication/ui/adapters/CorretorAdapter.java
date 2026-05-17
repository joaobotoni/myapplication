package com.example.myapplication.ui.adapters;

import static com.example.myapplication.ui.helpers.FormatHelper.formatCurrency;
import static com.example.myapplication.ui.helpers.ViewHelper.setText;
import static com.example.myapplication.ui.helpers.ViewHelper.setVisible;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemCorretorBinding;
import com.example.myapplication.ui.state.empresa.CorretorState;

import java.util.Objects;

public class CorretorAdapter extends ListAdapter<CorretorState, CorretorAdapter.ViewHolder> {
    public interface OnClickListener {
        void onClick(CorretorState corretorState);
    }

    private final OnClickListener listener;

    public CorretorAdapter(OnClickListener listener) {
        super(new DiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCorretorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCorretorBinding binding;
        private CorretorState item;

        public ViewHolder(@NonNull ItemCorretorBinding binding, OnClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                if (item != null) listener.onClick(item);
            });
        }

        protected void bind(CorretorState corretor) {
            this.item = corretor;
            setText(binding.textoNomeCorretor, corretor.getNome());
            setText(binding.textoComissao, formatCurrency(corretor.getComissao()));
            setVisible(corretor.isSelected(), binding.checkImage);
        }
    }


    private static class DiffCallback extends DiffUtil.ItemCallback<CorretorState> {
        @Override
        public boolean areItemsTheSame(@NonNull CorretorState oldItem, @NonNull CorretorState newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CorretorState oldItem, @NonNull CorretorState newItem) {
            return oldItem.getId() == newItem.getId()
                    && Objects.equals(oldItem.getNome(), newItem.getNome())
                    && Objects.equals(oldItem.getComissao(), newItem.getComissao())
                    && oldItem.isSelected() == newItem.isSelected();
        }
    }
}
