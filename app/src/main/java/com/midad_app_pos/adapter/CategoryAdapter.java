package com.midad_app_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_app_pos.R;
import com.midad_app_pos.databinding.BusinessTypeRowBinding;
import com.midad_app_pos.databinding.CategoryItemBinding;
import com.midad_app_pos.model.CategoryModel;
import com.midad_app_pos.uis.activity_items.ItemsActivity;
import com.midad_app_pos.utils.DiffUtilsCategories;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CategoryModel> list;
    private Context context;

    public CategoryAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategoryItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.category_item, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        myHolder.binding.setModel(list.get(position));
        myHolder.binding.getRoot().setOnLongClickListener(v ->{
            if (context instanceof ItemsActivity){
                ItemsActivity activity = (ItemsActivity) context;
                CategoryModel categoryModel = list.get(myHolder.getAdapterPosition());
                categoryModel.setSelected(true);
                activity.updateDeleteModel(myHolder.getAdapterPosition());
            }
            return  true;
        });
        myHolder.binding.getRoot().setOnClickListener(v -> {
            if (context instanceof ItemsActivity){
                ItemsActivity activity = (ItemsActivity) context;
                CategoryModel categoryModel = list.get(myHolder.getAdapterPosition());
                activity.selectDeleteCategory(myHolder.getAdapterPosition(),categoryModel);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private CategoryItemBinding binding;

        public Holder(@NonNull CategoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<CategoryModel> list){
        DiffUtilsCategories callback = new DiffUtilsCategories(this.list,list);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        this.list = list;
        diffResult.dispatchUpdatesTo(this);

    }
}
