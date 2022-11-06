package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.CategoryItemBinding;
import com.midad_pos.databinding.ShiftPaymentRowBinding;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.ShiftModel;
import com.midad_pos.uis.activity_items.ItemsActivity;
import com.midad_pos.utils.DiffUtilsCategories;

import java.util.List;

public class ShiftPaymentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ShiftModel.Payment> list;
    private Context context;

    public ShiftPaymentAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ShiftPaymentRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.shift_payment_row, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        myHolder.binding.setModel(list.get(position));


    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private ShiftPaymentRowBinding binding;

        public Holder(@NonNull ShiftPaymentRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<ShiftModel.Payment> list){
        this.list = list;
        notifyDataSetChanged();
    }
}
