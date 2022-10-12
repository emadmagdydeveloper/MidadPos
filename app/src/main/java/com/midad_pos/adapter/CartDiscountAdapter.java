package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.CartDiscountRowBinding;
import com.midad_pos.databinding.ItemDiscountRowBinding;
import com.midad_pos.model.DiscountModel;
import com.midad_pos.uis.activity_home.HomeActivity;

import java.util.List;

public class CartDiscountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DiscountModel> list;
    private Context context;

    public CartDiscountAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CartDiscountRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.cart_discount_row, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        DiscountModel discountModel = list.get(position);
        myHolder.binding.setModel(discountModel);


        myHolder.binding.delete.setOnClickListener(v -> {
            HomeActivity activity = (HomeActivity) context;
            activity.deleteCartDiscountItem(myHolder.getAdapterPosition());
        });
    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private CartDiscountRowBinding binding;

        public Holder(@NonNull CartDiscountRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<DiscountModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
