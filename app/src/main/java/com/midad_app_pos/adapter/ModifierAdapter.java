package com.midad_app_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_app_pos.R;
import com.midad_app_pos.databinding.ModifierRowBinding;
import com.midad_app_pos.databinding.UnitRowBinding;
import com.midad_app_pos.model.ModifierModel;
import com.midad_app_pos.uis.activity_add_item.AddItemActivity;

import java.util.List;

public class ModifierAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ModifierModel> list;
    private Context context;

    public ModifierAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ModifierRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.modifier_row, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        ModifierModel modifierModel = list.get(position);
        myHolder.binding.setModel(modifierModel);


        myHolder.itemView.setOnClickListener(v -> {
            ModifierModel model = list.get(myHolder.getAdapterPosition());

            model.setChecked(!model.isChecked());
            list.set(myHolder.getAdapterPosition(), model);
            myHolder.binding.setModel(model);

            if (context instanceof AddItemActivity){
                AddItemActivity activity = (AddItemActivity) context;
                activity.selectExtraData(model);
            }


        });
    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private ModifierRowBinding binding;

        public Holder(@NonNull ModifierRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<ModifierModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
