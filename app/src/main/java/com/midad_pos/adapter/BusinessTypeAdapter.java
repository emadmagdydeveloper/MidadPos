package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.BusinessTypeRowBinding;
import com.midad_pos.model.BusinessTypeModel;

import java.util.List;

public class BusinessTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BusinessTypeModel> list;
    private Context context;
    private String lang;

    public BusinessTypeAdapter(Context context, String lang) {
        this.context = context;
        this.lang = lang;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BusinessTypeRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.business_type_row, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        myHolder.binding.setLang(lang);
        myHolder.binding.setModel(list.get(position));
    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private BusinessTypeRowBinding binding;

        public Holder(@NonNull BusinessTypeRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<BusinessTypeModel> list){
        this.list = list;
        notifyDataSetChanged();
    }
}
