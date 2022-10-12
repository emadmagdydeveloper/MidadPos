package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.CategoryItemBinding;
import com.midad_pos.databinding.DiscountRowBinding;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.DiscountModel;
import com.midad_pos.uis.activity_home.HomeActivity;
import com.midad_pos.uis.activity_items.ItemsActivity;

import java.util.List;

public class HomeDiscountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DiscountModel> list;
    private Context context;
    private String lang;

    public HomeDiscountAdapter(Context context,String lang) {
        this.lang = lang;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DiscountRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.discount_row, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        myHolder.binding.setLang(lang);
        myHolder.binding.setModel(list.get(position));
        myHolder.itemView.setOnClickListener(v -> {
            DiscountModel model = list.get(myHolder.getAdapterPosition());
            if (!model.isSelected()){
                model.setSelected(true);
                myHolder.binding.setModel(model);
                list.set(myHolder.getAdapterPosition(),model);
                HomeActivity activity = (HomeActivity) context;
                activity.addDiscountForAll(model,myHolder.getAdapterPosition());
            }

        });


    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private DiscountRowBinding binding;

        public Holder(@NonNull DiscountRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<DiscountModel> list){
        this.list = list;
        notifyDataSetChanged();
    }
}
