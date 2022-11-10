package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.ChargeRowBinding;
import com.midad_pos.databinding.ShiftRowBinding;
import com.midad_pos.model.ChargeModel;
import com.midad_pos.model.ShiftModel;
import com.midad_pos.uis.activity_charge.ChargeActivity;
import com.midad_pos.uis.activity_shift.ShiftActivity;
import com.midad_pos.utils.DiffUtilsCategories;
import com.midad_pos.utils.DiffUtilsShifts;

import java.util.List;

public class ShiftsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ShiftModel> list;
    private Context context;
    private String lang;

    public ShiftsAdapter(Context context,String lang) {
        this.context = context;
        this.lang = lang;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ShiftRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.shift_row, parent, false);
        return new Holder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        myHolder.binding.setLang(lang);
        myHolder.binding.setModel(list.get(position));

        myHolder.itemView.setOnClickListener(v -> {
            if (context instanceof ShiftActivity){
                ShiftActivity activity = (ShiftActivity) context;
                activity.setShiftData(list.get(myHolder.getAdapterPosition()));
            }
        });

    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }



    public static class Holder extends RecyclerView.ViewHolder {
        public ShiftRowBinding binding;

        public Holder(@NonNull ShiftRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


    public void updateList(List<ShiftModel> list) {
        DiffUtilsShifts callback = new DiffUtilsShifts(this.list,list);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        this.list = list;
        diffResult.dispatchUpdatesTo(this);
    }


}
