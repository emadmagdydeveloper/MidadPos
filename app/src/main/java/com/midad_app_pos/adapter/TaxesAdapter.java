package com.midad_app_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_app_pos.R;
import com.midad_app_pos.databinding.TaxesRowBinding;
import com.midad_app_pos.databinding.UnitRowBinding;
import com.midad_app_pos.model.TaxModel;
import com.midad_app_pos.uis.activity_add_item.AddItemActivity;

import java.util.List;

public class TaxesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TaxModel> list;
    private Context context;
    private Holder oldHolder;

    public TaxesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TaxesRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.taxes_row, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        TaxModel taxModel = list.get(position);
        myHolder.binding.setModel(taxModel);
        if (taxModel.isChecked()) {
            if (oldHolder == null) {
                oldHolder = myHolder;
            }

        }

        myHolder.itemView.setOnClickListener(v -> {
            TaxModel model = list.get(myHolder.getAdapterPosition());

            if (oldHolder != null) {
                TaxModel oldModel = list.get(oldHolder.getAdapterPosition());
                oldModel.setChecked(false);
                list.set(oldHolder.getAdapterPosition(), oldModel);
                oldHolder.binding.setModel(oldModel);
            }

            model.setChecked(true);
            list.set(myHolder.getAdapterPosition(), model);
            myHolder.binding.setModel(model);
            oldHolder = myHolder;

            if (context instanceof AddItemActivity){
                AddItemActivity activity = (AddItemActivity) context;
                activity.selectTaxes(model);
            }


        });
    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private TaxesRowBinding binding;

        public Holder(@NonNull TaxesRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<TaxModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
