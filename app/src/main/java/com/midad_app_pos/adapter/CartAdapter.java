package com.midad_app_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_app_pos.R;
import com.midad_app_pos.databinding.CartItemRow2Binding;
import com.midad_app_pos.databinding.CartItemRow3Binding;
import com.midad_app_pos.databinding.CartItemRowBinding;
import com.midad_app_pos.databinding.CategoryItemBinding;
import com.midad_app_pos.model.ItemModel;
import com.midad_app_pos.uis.activity_home.HomeActivity;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ItemModel> list;
    private Context context;

    public CartAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==1){
            CartItemRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.cart_item_row, parent, false);
            return new Holder1(binding);
        }else if (viewType == 2){
            CartItemRow2Binding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.cart_item_row2, parent, false);
            return new Holder2(binding);
        }else {
            CartItemRow3Binding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.cart_item_row3, parent, false);
            return new Holder3(binding);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof Holder1){
            Holder1 holder1 = (Holder1) holder;
            holder1.binding.setModel(list.get(position));
            holder1.binding.llRoot.setOnClickListener(v -> {
                HomeActivity activity = (HomeActivity) context;
                activity.updateItemCart(list.get(holder1.getAdapterPosition()));
            });
            holder1.binding.delete.setOnClickListener(v -> {
                holder1.binding.swipeDelete.close(true);
                HomeActivity activity = (HomeActivity) context;
                activity.deleteItemFromCart(holder1.getAdapterPosition());

            });
        }else if (holder instanceof  Holder2){
            Holder2 holder2 = (Holder2) holder;
            holder2.binding.swipeDelete.close(true);
            holder2.binding.setModel(list.get(position));

            holder2.binding.llRoot.setOnClickListener(v -> {

                HomeActivity activity = (HomeActivity) context;
                activity.updateItemCart(list.get(holder2.getAdapterPosition()));
            });

            holder2.binding.delete.setOnClickListener(v -> {
                HomeActivity activity = (HomeActivity) context;
                activity.deleteItemFromCart(holder2.getAdapterPosition());
            });
        }else if (holder instanceof Holder3){
            Holder3 holder3 = (Holder3) holder;
            holder3.binding.swipeDelete.close(true);
            holder3.binding.setModel(list.get(position));
            holder3.binding.llRoot.setOnClickListener(v -> {
                HomeActivity activity = (HomeActivity) context;
                activity.updateItemCart(list.get(holder3.getAdapterPosition()));
            });
            holder3.binding.delete.setOnClickListener(v -> {
                HomeActivity activity = (HomeActivity) context;
                activity.deleteItemFromCart(holder3.getAdapterPosition());
            });
        }

    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder1 extends RecyclerView.ViewHolder {
        public CartItemRowBinding binding;

        public Holder1(@NonNull CartItemRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public CartItemRowBinding getBinding(){
            return binding;

        }
    }

    public static class Holder2 extends RecyclerView.ViewHolder {
        public CartItemRow2Binding binding;

        public Holder2(@NonNull CartItemRow2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


    }
    public static class Holder3 extends RecyclerView.ViewHolder {
        public CartItemRow3Binding binding;

        public Holder3(@NonNull CartItemRow3Binding binding) {
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
        ItemModel itemModel = list.get(position);
        if (itemModel.getSelectedVariant()!=null&&itemModel.getSelectedModifiers().size()>0){
            return 3;
        }else if (itemModel.getSelectedVariant()==null&&itemModel.getSelectedModifiers().size()==0){
            return 1;
        }else {
            return 2;
        }
    }
}
