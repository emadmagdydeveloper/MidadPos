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
import com.midad_pos.databinding.PrinterRowBinding;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.PrinterModel;
import com.midad_pos.uis.activity_items.ItemsActivity;
import com.midad_pos.utils.DiffUtilsCategories;
import com.midad_pos.utils.DiffUtilsPrinters;

import java.util.ArrayList;
import java.util.List;

public class PrinterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PrinterModel> list = new ArrayList<>();
    private Context context;

    public PrinterAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PrinterRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.printer_row, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        myHolder.binding.setModel(list.get(position));

    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private PrinterRowBinding binding;

        public Holder(@NonNull PrinterRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<PrinterModel> list){
        this.list = list;
        notifyDataSetChanged();

    }
}
