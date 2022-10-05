package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.CartItemRowBinding;
import com.midad_pos.databinding.CategoryItemBinding;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.ItemModel;
import com.midad_pos.model.cart.CartList;
import com.midad_pos.uis.activity_items.ItemsActivity;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ItemModel> list;
    private Context context;

    public CartAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CartItemRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.cart_item_row, parent, false);
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
        private CartItemRowBinding binding;

        public Holder(@NonNull CartItemRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<ItemModel> list){
        this.list = list;
        notifyDataSetChanged();
    }
}
