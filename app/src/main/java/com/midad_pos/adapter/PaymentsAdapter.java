package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.CustomerRowBinding;
import com.midad_pos.databinding.PaymentRowBinding;
import com.midad_pos.model.CustomerModel;
import com.midad_pos.model.PaymentModel;
import com.midad_pos.uis.activity_charge.ChargeActivity;
import com.midad_pos.uis.activity_home.HomeActivity;

import java.util.List;

public class PaymentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PaymentModel> list;
    private Context context;

    public PaymentsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PaymentRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.payment_row, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        myHolder.binding.setModel(list.get(position));
        myHolder.itemView.setOnClickListener(v -> {
            if (context instanceof ChargeActivity){
                ChargeActivity activity = (ChargeActivity) context;
                activity.choosePayment(list.get(myHolder.getAdapterPosition()));
            }
        });


    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private PaymentRowBinding binding;

        public Holder(@NonNull PaymentRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<PaymentModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
