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
import com.midad_pos.databinding.ItemVariantBinding;
import com.midad_pos.model.ModifierModel;
import com.midad_pos.model.VariantModel;
import com.midad_pos.uis.activity_home.HomeActivity;

import java.util.List;

public class ItemVariantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<VariantModel> list;
    private Context context;
    private Holder oldHolder;

    public ItemVariantAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVariantBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_variant, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        VariantModel variantModel = list.get(position);
        myHolder.binding.setModel(variantModel);
        if (variantModel.isSelected()){
            oldHolder = myHolder;

        }

        myHolder.itemView.setOnClickListener(v -> {
            if (list.size()>1){
                VariantModel model = list.get(myHolder.getAdapterPosition());

                if (oldHolder!=null){
                    VariantModel oldModel = list.get(oldHolder.getAdapterPosition());
                    oldModel.setSelected(false);
                    list.set(oldHolder.getAdapterPosition(),oldModel);
                    oldHolder.binding.setModel(oldModel);

                }

                model.setSelected(true);
                list.set(myHolder.getAdapterPosition(), model);
                myHolder.binding.setModel(model);

                oldHolder = myHolder;

                if (context instanceof HomeActivity){
                    HomeActivity activity = (HomeActivity) context;
                    activity.setItemVariant(myHolder.getAdapterPosition(),model);
                }
            }



        });
    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private ItemVariantBinding binding;

        public Holder(@NonNull ItemVariantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<VariantModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
