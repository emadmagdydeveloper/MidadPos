package com.midad_pos.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.ChargeCartItemRowBinding;
import com.midad_pos.databinding.ChargeRowBinding;
import com.midad_pos.model.ChargeModel;
import com.midad_pos.model.ItemModel;
import com.midad_pos.uis.activity_charge.ChargeActivity;

import java.util.List;
import java.util.Locale;

public class SplitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChargeModel> list;
    private Context context;
    private double remaining = 0.0;

    public SplitAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChargeRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.charge_row, parent, false);
        return new Holder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        myHolder.binding.setModel(list.get(position));
        myHolder.binding.delete.setOnClickListener(v -> {
            if (list.size()>1){
                ChargeActivity activity = (ChargeActivity) context;
                activity.deleteSplit();
            }
        });





    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public ChargeRowBinding binding;

        public Holder(@NonNull ChargeRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
    }



    public void updateList(List<ChargeModel> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public void updateRemaining(double remaining){
        this.remaining = remaining;
        notifyDataSetChanged();

    }


}
