package com.midad_app_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_app_pos.R;
import com.midad_app_pos.databinding.ItemModifierRowBinding;
import com.midad_app_pos.databinding.MainItemModifierRowBinding;
import com.midad_app_pos.databinding.ModifierRowBinding;
import com.midad_app_pos.model.ModifierModel;

import java.util.List;

public class ItemMainModifierAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ModifierModel> list;
    private Context context;

    public ItemMainModifierAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MainItemModifierRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.main_item_modifier_row, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        ModifierModel modifierModel = list.get(position);
        myHolder.binding.setModel(modifierModel);
        ItemModifierAdapter adapter= new ItemModifierAdapter(context,position);
        if (myHolder.binding.recView!=null){
            myHolder.binding.recView.setLayoutManager(new LinearLayoutManager(context));
            myHolder.binding.recView.setAdapter(adapter);
        }else if (myHolder.binding.recViewLand!=null){
            myHolder.binding.recViewLand.setLayoutManager(new GridLayoutManager(context,2));
            myHolder.binding.recViewLand.setAdapter(adapter);
        }
        adapter.updateList(modifierModel.getModifiers_data());

    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private MainItemModifierRowBinding binding;

        public Holder(@NonNull MainItemModifierRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<ModifierModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
