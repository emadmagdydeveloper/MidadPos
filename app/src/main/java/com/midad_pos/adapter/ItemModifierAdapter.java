package com.midad_pos.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.ItemModifierRowBinding;
import com.midad_pos.model.ModifierModel;
import com.midad_pos.uis.activity_home.HomeActivity;

import java.util.List;

public class ItemModifierAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ModifierModel.Data> list;
    private Context context;
    private int mainPos ;

    public ItemModifierAdapter(Context context,int mainPos) {
        this.context = context;
        this.mainPos = mainPos;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemModifierRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_modifier_row, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        ModifierModel.Data modifierModel = list.get(position);
        myHolder.binding.setModel(modifierModel);
        Log.e("ddd","dd");


        myHolder.itemView.setOnClickListener(v -> {
            ModifierModel.Data model = list.get(myHolder.getAdapterPosition());

            model.setSelected(!model.isSelected());
            list.set(myHolder.getAdapterPosition(), model);
            myHolder.binding.setModel(model);

            if (context instanceof HomeActivity){
                HomeActivity activity = (HomeActivity) context;
                activity.setItemModifier(mainPos,myHolder.getAdapterPosition(),model);
            }


        });
    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private ItemModifierRowBinding binding;

        public Holder(@NonNull ItemModifierRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<ModifierModel.Data> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
