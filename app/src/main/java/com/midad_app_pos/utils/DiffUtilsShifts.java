package com.midad_app_pos.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.midad_app_pos.model.ShiftModel;

import java.util.List;

public class DiffUtilsShifts extends DiffUtil.Callback {
    private List<ShiftModel> oldList;
    private List<ShiftModel> newList;

    public DiffUtilsShifts(List<ShiftModel> oldList, List<ShiftModel> newList) {
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
