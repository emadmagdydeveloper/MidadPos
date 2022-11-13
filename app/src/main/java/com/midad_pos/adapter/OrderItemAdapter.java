package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.OrderItemRowBinding;
import com.midad_pos.databinding.OrderPaymentRowBinding;
import com.midad_pos.model.OrderModel;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<OrderModel.Detail> list;
    private Context context;

    public OrderItemAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OrderItemRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.order_item_row, parent, false);
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
        private OrderItemRowBinding binding;

        public Holder(@NonNull OrderItemRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<OrderModel.Detail> list){
        this.list = list;
        notifyDataSetChanged();
    }
}
