package com.midad_pos.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.ItemModel;

import java.util.List;

public class DiffUtilsCategories extends DiffUtil.Callback {
    private List<CategoryModel> oldList;
    private List<CategoryModel> newList;

    public DiffUtilsCategories(List<CategoryModel> oldList, List<CategoryModel> newList) {
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
