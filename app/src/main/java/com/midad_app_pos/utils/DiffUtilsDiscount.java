package com.midad_app_pos.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.midad_app_pos.model.DiscountModel;

import java.util.List;

public class DiffUtilsDiscount extends DiffUtil.Callback {
    private List<DiscountModel> oldList;
    private List<DiscountModel> newList;

    public DiffUtilsDiscount(List<DiscountModel> oldList, List<DiscountModel> newList) {
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
        return oldList.get(oldItemPosition).isSelected()!=newList.get(newItemPosition).isSelected();
    }
}
