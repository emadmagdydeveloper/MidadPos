package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.DiscountBinding;
import com.midad_pos.databinding.OrderDiscountRowBinding;
import com.midad_pos.model.DiscountModel;
import com.midad_pos.model.OrderModel;
import com.midad_pos.uis.activity_items.ItemsActivity;
import com.midad_pos.utils.DiffUtilsDiscount;
import com.midad_pos.utils.DiffUtilsOrderDiscount;

import java.util.ArrayList;
import java.util.List;

public class OrderDiscountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<OrderModel.OrderDiscount> list = new ArrayList<>();
    private Context context;

    public OrderDiscountAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OrderDiscountRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.order_discount_row, parent, false);
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
        private OrderDiscountRowBinding binding;

        public Holder(@NonNull OrderDiscountRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<OrderModel.OrderDiscount> list) {
        DiffUtilsOrderDiscount callback = new DiffUtilsOrderDiscount(this.list,list);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        this.list = list;
        diffResult.dispatchUpdatesTo(this);

    }
}
