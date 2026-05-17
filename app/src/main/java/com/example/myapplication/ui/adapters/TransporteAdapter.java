package com.example.myapplication.ui.adapters;


import static com.example.myapplication.ui.helpers.ViewHelper.setPluralText;
import static com.example.myapplication.ui.helpers.ViewHelper.setText;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemTransporteBinding;
import com.example.myapplication.ui.state.frete.TransporteState;

import java.util.List;
import java.util.Objects;

public class TransporteAdapter extends ListAdapter<TransporteState, TransporteAdapter.ViewHolder> {

    public TransporteAdapter() {
        super(new DiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemTransporteBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), getItemCount() == 1);
    }

    @Override
    public void onCurrentListChanged(@NonNull List<TransporteState> previousList, @NonNull List<TransporteState> currentList) {
        super.onCurrentListChanged(previousList, currentList);
        boolean tamanhoMudou = (previousList.size() == 1) != (currentList.size() == 1);
        if (tamanhoMudou) notifyItemRangeChanged(0, currentList.size());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTransporteBinding binding;

        ViewHolder(ItemTransporteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TransporteState estado, boolean isSingleItem) {
            Context context = itemView.getContext();
            setText(binding.textoTipoVeiculo, estado.getNomeVeiculo());
            setPluralText(binding.textoQuantidadeVeiculos, context, R.plurals.plural_quantidade_veiculos, estado.getQuantidade());
            setText(binding.textoCapacidadeCabecas, context, R.string.formato_capacidade_cabecas, estado.getCapacidade());
            setText(binding.textoPorcentagemOcupada, context, R.string.formato_numero_percentual, estado.getOcupacao());
            aplicarLayoutPorModo(isSingleItem);
        }

        private void aplicarLayoutPorModo(boolean isSingleItem) {
            aplicarLarguraDoItem(isSingleItem);
            aplicarMargemFinalDoCard(isSingleItem, itemView.getContext());
        }

        private void aplicarLarguraDoItem(boolean isSingleItem) {
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.width = isSingleItem ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
            itemView.setLayoutParams(params);
        }

        private void aplicarMargemFinalDoCard(boolean isSingleItem, Context context) {
            ViewGroup.MarginLayoutParams cardParams = (ViewGroup.MarginLayoutParams) binding.getRoot().getLayoutParams();
            int margin = isSingleItem ? 0 : (int) (12 * context.getResources().getDisplayMetrics().density);
            cardParams.setMarginEnd(margin);
            binding.getRoot().setLayoutParams(cardParams);
        }
    }

    private static class DiffCallback extends DiffUtil.ItemCallback<TransporteState> {
        @Override
        public boolean areItemsTheSame(@NonNull TransporteState oldItem, @NonNull TransporteState newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull TransporteState oldItem, @NonNull TransporteState newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId())
                    && Objects.equals(oldItem.getNomeVeiculo(), newItem.getNomeVeiculo())
                    && Objects.equals(oldItem.getQuantidade(), newItem.getQuantidade())
                    && Objects.equals(oldItem.getCapacidade(), newItem.getCapacidade())
                    && Objects.equals(oldItem.getOcupacao(), newItem.getOcupacao());
        }
    }
}
