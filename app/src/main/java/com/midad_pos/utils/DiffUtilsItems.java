package com.midad_pos.utils;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.midad_pos.model.ItemModel;

import java.util.List;

public class DiffUtilsItems extends DiffUtil.Callback {
    private List<ItemModel> oldList;
    private List<ItemModel> newList;

    public DiffUtilsItems(List<ItemModel> oldList, List<ItemModel> newList) {
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
        return  newList.get(newItemPosition)==oldList.get(oldItemPosition);

    }

}
