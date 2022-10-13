package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.CustomerRowBinding;
import com.midad_pos.databinding.DiscountBinding;
import com.midad_pos.model.CustomerModel;
import com.midad_pos.model.DiscountModel;
import com.midad_pos.uis.activity_items.ItemsActivity;

import java.util.List;

public class CustomersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CustomerModel> list;
    private Context context;

    public CustomersAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomerRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.customer_row, parent, false);
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
        private CustomerRowBinding binding;

        public Holder(@NonNull CustomerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<CustomerModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
