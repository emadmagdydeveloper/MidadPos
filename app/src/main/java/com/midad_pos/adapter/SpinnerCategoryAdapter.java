package com.midad_pos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.midad_pos.R;
import com.midad_pos.databinding.BusinessSpinnerRowBinding;
import com.midad_pos.databinding.SpinnerRowBinding;
import com.midad_pos.model.BusinessTypeModel;
import com.midad_pos.model.CategoryModel;

import java.util.List;

public class SpinnerCategoryAdapter extends BaseAdapter {
    private List<CategoryModel> list;
    private LayoutInflater inflater;
    private String lang;
    public SpinnerCategoryAdapter(Context context, String lang) {
        inflater = LayoutInflater.from(context);
        this.lang = lang;
    }

    @Override
    public int getCount() {
        return list!=null?list.size():0;
    }

    @Override
    public Object getItem(int i) {
        return list!=null?list.get(i):null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") SpinnerRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.spinner_row,viewGroup,false);
        binding.setTitle(list.get(i).getTitle());
        return binding.getRoot();
    }

    public void updateList(List<CategoryModel> list){
        this.list = list;
        notifyDataSetChanged();
    }
}
