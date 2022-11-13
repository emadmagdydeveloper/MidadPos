package com.midad_pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.midad_pos.R;
import com.midad_pos.databinding.RecieptRowBinding;
import com.midad_pos.model.OrderModel;
import com.midad_pos.uis.activity_receipts.ReceiptsActivity;
import com.midad_pos.utils.DiffUtilsOrderSale;

import java.util.List;

public class ReceiptSaleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<OrderModel.Sale> list;
    private Context context;
    private String lang;
    private boolean showSelected = false;
    private Holder oldHolder;

    public ReceiptSaleAdapter(Context context, String lang) {
        this.context = context;
        this.lang = lang;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecieptRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.reciept_row, parent, false);
        return new Holder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Holder myHolder = (Holder) holder;
        myHolder.binding.setLang(lang);
        myHolder.binding.setCanShowSelect(showSelected);
        myHolder.binding.setModel(list.get(position));
        if (list.get(position).isSelected()){
            oldHolder = myHolder;

        }


        myHolder.binding.getRoot().setOnClickListener(v -> {
            OrderModel.Sale model = list.get(myHolder.getAdapterPosition());
            if (!model.isSelected()){
                model.setSelected(true);
                myHolder.binding.setModel(model);
                if (oldHolder!=null){
                    try {
                        list.get(oldHolder.getAdapterPosition()).setSelected(false);
                        oldHolder.binding.setModel(list.get(oldHolder.getAdapterPosition()));

                    }catch (Exception e){

                    }
                   }
                ReceiptsActivity activity = (ReceiptsActivity) context;
                activity.setItemReceipt(model);
                oldHolder = myHolder;
            }







        });


    }


    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private RecieptRowBinding binding;

        public Holder(@NonNull RecieptRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateList(List<OrderModel.Sale> list) {
        DiffUtilsOrderSale callback = new DiffUtilsOrderSale(this.list, list);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        this.list = list;
        diffResult.dispatchUpdatesTo(this);

    }

    public void updateSelected(boolean showSelected) {
        this.showSelected = showSelected;
        notifyDataSetChanged();
    }
}
