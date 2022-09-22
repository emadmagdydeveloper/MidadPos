package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.ItemBinding;
import com.midad_pos.databinding.ItemHomeImageRowBinding;
import com.midad_pos.databinding.ItemHomeShapeRowBinding;
import com.midad_pos.model.ItemModel;
import com.midad_pos.uis.activity_items.ItemsActivity;

import java.util.List;

public class HomeItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ItemModel> list;
    private Context context;

    public HomeItemAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==1){
            ItemHomeShapeRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_home_shape_row, parent, false);
            return new HolderColor(binding);
        }else {
            ItemHomeImageRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_home_image_row, parent, false);
            return new HolderImage(binding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HolderColor){
            HolderColor holderColor = (HolderColor) holder;
            holderColor.binding.setModel(list.get(holderColor.getAdapterPosition()));

        }else if (holder instanceof HolderImage){
            HolderImage holderImage = (HolderImage) holder;
            holderImage.binding.setModel(list.get(holderImage.getAdapterPosition()));

        }

    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class HolderColor extends RecyclerView.ViewHolder {
        private ItemHomeShapeRowBinding binding;

        public HolderColor(@NonNull ItemHomeShapeRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class HolderImage extends RecyclerView.ViewHolder {
        private ItemHomeImageRowBinding binding;

        public HolderImage(@NonNull ItemHomeImageRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<ItemModel> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getImage_type().equals("color")){
            return 1;
        }else {
            return 2;
        }
    }
}
