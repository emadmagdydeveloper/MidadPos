package com.midad_pos.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.midad_pos.model.OrderModel;

import java.util.List;

public class DiffUtilsOpenedTickets extends DiffUtil.Callback {
    private List<OrderModel.Sale> oldList;
    private List<OrderModel.Sale> newList;

    public DiffUtilsOpenedTickets(List<OrderModel.Sale> oldList, List<OrderModel.Sale> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList!=null?oldList.size():0;
    }

    @Override
    public int getNewListSize() {
        return newList!=null?newList.size():0;

    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItemPosition == newItemPosition;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition)==newList.get(newItemPosition);
    }
}
