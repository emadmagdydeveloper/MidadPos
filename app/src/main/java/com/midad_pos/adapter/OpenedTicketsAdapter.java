package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.MyTicketRowBinding;
import com.midad_pos.databinding.OrderDiscountRowBinding;
import com.midad_pos.model.OrderModel;
import com.midad_pos.utils.DiffUtilsOpenedTickets;
import com.midad_pos.utils.DiffUtilsOrderDiscount;

import java.util.ArrayList;
import java.util.List;

public class OpenedTicketsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<OrderModel.Sale> list;
    private Context context;

    public OpenedTicketsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyTicketRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.my_ticket_row, parent, false);
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
        private MyTicketRowBinding binding;

        public Holder(@NonNull MyTicketRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<OrderModel.Sale> list) {
        DiffUtilsOpenedTickets callback = new DiffUtilsOpenedTickets(this.list,list);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        this.list = list;
        diffResult.dispatchUpdatesTo(this);

    }
}
