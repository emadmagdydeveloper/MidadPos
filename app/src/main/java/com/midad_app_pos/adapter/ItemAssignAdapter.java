package com.midad_app_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_app_pos.R;
import com.midad_app_pos.databinding.ItemAssignBinding;
import com.midad_app_pos.databinding.ItemBinding;
import com.midad_app_pos.model.ItemModel;
import com.midad_app_pos.uis.activity_add_category.AddCategoryActivity;

import java.util.List;

public class ItemAssignAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ItemModel> list;
    private Context context;

    public ItemAssignAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAssignBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_assign, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        myHolder.binding.setModel(list.get(position));
        myHolder.itemView.setOnClickListener(v -> {
            ItemModel itemModel = list.get(myHolder.getAdapterPosition());
            itemModel.setSelected(!itemModel.isSelected());
            list.set(myHolder.getAdapterPosition(),itemModel);
            myHolder.binding.setModel(itemModel);
            if (context instanceof AddCategoryActivity){
                AddCategoryActivity activity = (AddCategoryActivity) context;
                activity.assignItem(itemModel);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private ItemAssignBinding binding;

        public Holder(@NonNull ItemAssignBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<ItemModel> list){
        this.list = list;
        notifyDataSetChanged();
    }
}
