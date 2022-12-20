package com.midad_app_pos.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.midad_app_pos.model.ShiftModel;

import java.util.List;

public class DiffUtilsPayInOut extends DiffUtil.Callback {
    private List<ShiftModel.PayInOutModel> oldList;
    private List<ShiftModel.PayInOutModel> newList;

    public DiffUtilsPayInOut(List<ShiftModel.PayInOutModel> oldList, List<ShiftModel.PayInOutModel> newList) {
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
