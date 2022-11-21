package com.midad_pos.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.PrinterModel;

import java.util.List;

public class DiffUtilsPrinters extends DiffUtil.Callback {
    private List<PrinterModel> oldList;
    private List<PrinterModel> newList;

    public DiffUtilsPrinters(List<PrinterModel> oldList, List<PrinterModel> newList) {
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
