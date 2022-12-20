package com.midad_app_pos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.midad_app_pos.R;
import com.midad_app_pos.databinding.BusinessSpinnerRowBinding;
import com.midad_app_pos.model.BusinessTypeModel;

import java.util.List;

public class SpinnerBusinessType extends BaseAdapter {
    private List<BusinessTypeModel> list;
    private LayoutInflater inflater;
    private String lang;
    public SpinnerBusinessType(Context context,String lang) {
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
        @SuppressLint("ViewHolder") BusinessSpinnerRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.business_spinner_row,viewGroup,false);
        binding.setTitle(lang.equals("ar")?list.get(i).getTitle_ar():list.get(i).getTitle_en());
        binding.setIsSelected(list.get(i).isSelected());
        return binding.getRoot();
    }

    public void updateList(List<BusinessTypeModel> list){
        this.list = list;
        notifyDataSetChanged();
    }
}
