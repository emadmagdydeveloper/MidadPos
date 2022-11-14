package com.midad_pos.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.midad_pos.model.DiscountModel;
import com.midad_pos.model.OrderModel;

import java.util.List;

public class DiffUtilsOrderDiscount extends DiffUtil.Callback {
    private List<OrderModel.OrderDiscount> oldList;
    private List<OrderModel.OrderDiscount> newList;

    public DiffUtilsOrderDiscount(List<OrderModel.OrderDiscount> oldList, List<OrderModel.OrderDiscount> newList) {
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
