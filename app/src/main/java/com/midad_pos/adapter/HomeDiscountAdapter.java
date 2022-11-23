package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.CategoryItemBinding;
import com.midad_pos.databinding.DiscountRowBinding;
import com.midad_pos.databinding.HomeDiscountRow2Binding;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.DiscountModel;
import com.midad_pos.uis.activity_home.HomeActivity;
import com.midad_pos.uis.activity_items.ItemsActivity;
import com.midad_pos.utils.DiffUtilsDiscount;
import com.midad_pos.utils.DiffUtilsItems;

import java.util.ArrayList;
import java.util.List;

public class HomeDiscountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DiscountModel> list = new ArrayList<>();
    private Context context;
    private String lang;
    private int type = 1;

    public HomeDiscountAdapter(Context context,String lang) {
        this.lang = lang;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==1){
            DiscountRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.discount_row, parent, false);
            return new Holder(binding);
        }else {
            HomeDiscountRow2Binding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.home_discount_row2, parent, false);
            return new Holder2(binding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof Holder){
            Holder myHolder = (Holder) holder;
            myHolder.binding.setLang(lang);
            myHolder.binding.setModel(list.get(position));
            myHolder.itemView.setOnClickListener(v -> {
                DiscountModel model = list.get(myHolder.getAdapterPosition());
                if (!model.isSelected()){
                    model.setSelected(true);
                    // myHolder.binding.setModel(model);
                    //list.set(myHolder.getAdapterPosition(),model);
                    HomeActivity activity = (HomeActivity) context;
                    activity.addDiscountForAll(model,myHolder.getAdapterPosition());
                }

            });
        }else if (holder instanceof Holder2){
            Holder2 myHolder = (Holder2) holder;
            myHolder.binding.setModel(list.get(position));
            myHolder.itemView.setOnClickListener(v -> {
                DiscountModel model = list.get(myHolder.getAdapterPosition());
                if (!model.isSelected()){
                    model.setSelected(true);
                    // myHolder.binding.setModel(model);
                    //list.set(myHolder.getAdapterPosition(),model);
                    HomeActivity activity = (HomeActivity) context;
                    activity.addDiscountForAll(model,myHolder.getAdapterPosition());
                }

            });
        }



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

    public static class Holder2 extends RecyclerView.ViewHolder {
        private HomeDiscountRow2Binding binding;

        public Holder2(@NonNull HomeDiscountRow2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<DiscountModel> list){
        DiffUtilsDiscount callback = new DiffUtilsDiscount(this.list,list);
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
        return type;
    }
}
