package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.ShiftPayInOutRowBinding;
import com.midad_pos.databinding.ShiftPaymentRowBinding;
import com.midad_pos.model.ShiftModel;
import com.midad_pos.utils.DiffUtilsItems;
import com.midad_pos.utils.DiffUtilsPayInOut;

import java.util.List;

public class PayInOutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ShiftModel.PayInOutModel> list;
    private Context context;

    public PayInOutAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ShiftPayInOutRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.shift_pay_in_out_row, parent, false);
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
        private ShiftPayInOutRowBinding binding;

        public Holder(@NonNull ShiftPayInOutRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<ShiftModel.PayInOutModel> list){
        DiffUtilsPayInOut callback = new DiffUtilsPayInOut(this.list,list);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        this.list = list;
        diffResult.dispatchUpdatesTo(this);
    }

    public void addItemList(ShiftModel.PayInOutModel model){
        this.list.add(0,model);
        notifyItemInserted(0);
    }
}
