package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.ItemBinding;
import com.midad_pos.databinding.MainReceiptRowBinding;
import com.midad_pos.model.ItemModel;
import com.midad_pos.model.OrderModel;
import com.midad_pos.uis.activity_items.ItemsActivity;
import com.midad_pos.utils.DiffUtilsItems;
import com.midad_pos.utils.DiffUtilsOrders;

import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<OrderModel> list;
    private Context context;
    private String lang;

    public ReceiptAdapter(Context context,String lang) {
        this.context = context;
        this.lang = lang;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MainReceiptRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.main_receipt_row, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        myHolder.binding.setLang(lang);
        myHolder.binding.setDate(list.get(position).getDate());
        myHolder.binding.recView.setLayoutManager(new LinearLayoutManager(context));
        myHolder.binding.recView.setHasFixedSize(true);
        ReceiptSaleAdapter adapter = new ReceiptSaleAdapter(context,lang);
        adapter.updateList(list.get(position).getSales());
        myHolder.binding.recView.setAdapter(adapter);

    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private MainReceiptRowBinding binding;

        public Holder(@NonNull MainReceiptRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<OrderModel> list){
        DiffUtilsOrders callback = new DiffUtilsOrders(this.list,list);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        this.list = list;
        diffResult.dispatchUpdatesTo(this);

    }
}
