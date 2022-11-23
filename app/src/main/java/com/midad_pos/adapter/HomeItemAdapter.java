package com.midad_pos.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.ItemHomeBinding;
import com.midad_pos.databinding.ItemHomeImageRowBinding;
import com.midad_pos.databinding.ItemHomeShapeRowBinding;
import com.midad_pos.model.ItemModel;
import com.midad_pos.uis.activity_home.HomeActivity;
import com.midad_pos.utils.DiffUtilsItems;

import java.util.ArrayList;
import java.util.List;

public class HomeItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ItemModel> list = new ArrayList<>();
    private Context context;
    private int type = 1;

    public HomeItemAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==1){
            ItemHomeShapeRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_home_shape_row, parent, false);
            return new HolderColor(binding);
        }else if (viewType ==2){
            ItemHomeImageRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_home_image_row, parent, false);
            return new HolderImage(binding);
        }else {
            ItemHomeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_home, parent, false);
            return new Holder(binding);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HolderColor){
            HolderColor holderColor = (HolderColor) holder;
            holderColor.binding.setModel(list.get(holderColor.getAdapterPosition()));
            holderColor.itemView.setOnClickListener(v -> {
                HomeActivity activity = (HomeActivity) context;
                activity.setItemData(list.get(holderColor.getAdapterPosition()),holderColor.getAdapterPosition());
            });

        }else if (holder instanceof HolderImage){
            HolderImage holderImage = (HolderImage) holder;
            holderImage.binding.setModel(list.get(holderImage.getAdapterPosition()));

            holderImage.itemView.setOnClickListener(v -> {
                HomeActivity activity = (HomeActivity) context;
                activity.setItemData(list.get(holderImage.getAdapterPosition()), holderImage.getAdapterPosition());
            });
        }else if (holder instanceof Holder){
            Holder holderItem = (Holder) holder;
            holderItem.binding.setModel(list.get(holderItem.getAdapterPosition()));

            holderItem.itemView.setOnClickListener(v -> {
                HomeActivity activity = (HomeActivity) context;
                activity.setItemData(list.get(holderItem.getAdapterPosition()), holderItem.getAdapterPosition());
            });
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

    public static class Holder extends RecyclerView.ViewHolder {
        private ItemHomeBinding binding;

        public Holder(@NonNull ItemHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<ItemModel> list){
        DiffUtilsItems callback = new DiffUtilsItems(this.list,list);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        diffResult.dispatchUpdatesTo(this);
        this.list.clear();
        this.list.addAll(list);



    }
    public void updateType(int type){
        this.type = type;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (type==1){
            if (list.get(position).getImage_type().equals("color")){
                return 1;
            }else {
                return 2;
            }
        }else {
            return 3;
        }

    }
}
