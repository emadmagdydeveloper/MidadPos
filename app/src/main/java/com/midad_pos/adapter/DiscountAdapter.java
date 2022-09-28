package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.DiscountBinding;
import com.midad_pos.databinding.DiscountRowBinding;
import com.midad_pos.databinding.DiscountsLayoutBinding;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.DiscountModel;
import com.midad_pos.uis.activity_items.ItemsActivity;

import java.util.List;

public class DiscountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DiscountModel> list;
    private Context context;

    public DiscountAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DiscountBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.discount, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        myHolder.binding.setModel(list.get(position));
        myHolder.binding.getRoot().setOnLongClickListener(v -> {
            if (context instanceof ItemsActivity) {
                ItemsActivity activity = (ItemsActivity) context;
                DiscountModel discountModel = list.get(myHolder.getAdapterPosition());
                discountModel.setSelected(true);
                activity.updateDiscountDeleteModel(myHolder.getAdapterPosition());
            }
            return true;
        });
        myHolder.binding.getRoot().setOnClickListener(v -> {
            if (context instanceof ItemsActivity) {
                ItemsActivity activity = (ItemsActivity) context;
                DiscountModel discountModel = list.get(myHolder.getAdapterPosition());
                activity.selectDeleteDiscount(myHolder.getAdapterPosition(), discountModel);
            }
        });

    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private DiscountBinding binding;

        public Holder(@NonNull DiscountBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<DiscountModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}