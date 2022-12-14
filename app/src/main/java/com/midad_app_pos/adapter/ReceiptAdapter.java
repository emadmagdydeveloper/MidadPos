package com.midad_app_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_app_pos.R;
import com.midad_app_pos.databinding.ItemBinding;
import com.midad_app_pos.databinding.MainReceiptRowBinding;
import com.midad_app_pos.model.OrderModel;
import com.midad_app_pos.utils.DiffUtilsOrders;

import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<OrderModel> list;
    private Context context;
    private String lang;
    private boolean showSelected;


    public ReceiptAdapter(Context context,String lang,boolean showSelected) {
        this.context = context;
        this.lang = lang;
        this.showSelected = showSelected;
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
        /*ReceiptSaleAdapter adapter = new ReceiptSaleAdapter(context,position,lang);
        adapter.updateSelected(showSelected);
        adapter.updateList(list.get(position).getSales());
        myHolder.binding.recView.setAdapter(adapter);*/

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
