package com.midad_app_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_app_pos.R;
import com.midad_app_pos.databinding.CartItemRow2Binding;
import com.midad_app_pos.databinding.CartItemRow3Binding;
import com.midad_app_pos.databinding.CartItemRowBinding;
import com.midad_app_pos.databinding.ChargeCartItemRowBinding;
import com.midad_app_pos.model.ItemModel;

import java.util.List;

public class ChargeCartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ItemModel> list;
    private Context context;

    public ChargeCartAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChargeCartItemRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.charge_cart_item_row, parent, false);
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
        public ChargeCartItemRowBinding binding;

        public Holder(@NonNull ChargeCartItemRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
    }



    public void updateList(List<ItemModel> list){
        this.list = list;
        notifyDataSetChanged();
    }



}
