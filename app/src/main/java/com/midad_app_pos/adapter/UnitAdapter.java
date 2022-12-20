package com.midad_app_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_app_pos.R;
import com.midad_app_pos.databinding.CategoryItemBinding;
import com.midad_app_pos.databinding.UnitRowBinding;
import com.midad_app_pos.model.UnitModel;
import com.midad_app_pos.uis.activity_add_item.AddItemActivity;

import java.util.List;

public class UnitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UnitModel> list;
    private Context context;
    private Holder oldHolder;

    public UnitAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UnitRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.unit_row, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        UnitModel unitModel = list.get(position);
        myHolder.binding.setModel(unitModel);
        if (unitModel.isChecked()) {
            if (oldHolder == null) {
                oldHolder = myHolder;
            }

        }

        myHolder.itemView.setOnClickListener(v -> {
            UnitModel model = list.get(myHolder.getAdapterPosition());

            if (oldHolder != null) {
                UnitModel oldModel = list.get(oldHolder.getAdapterPosition());
                oldModel.setChecked(false);
                list.set(oldHolder.getAdapterPosition(), oldModel);
                oldHolder.binding.setModel(oldModel);
            }

            model.setChecked(true);
            list.set(myHolder.getAdapterPosition(), model);
            myHolder.binding.setModel(model);
            oldHolder = myHolder;

            if (context instanceof AddItemActivity){
                AddItemActivity activity = (AddItemActivity) context;
                activity.selectSoldBy(model);
            }


        });
    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private UnitRowBinding binding;

        public Holder(@NonNull UnitRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<UnitModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
