package com.midad_app_pos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.midad_app_pos.R;
import com.midad_app_pos.databinding.SpinnerCountryRowBinding;
import com.midad_app_pos.databinding.SpinnerRowBinding;
import com.midad_app_pos.model.CountryModel;

import java.util.List;

public class SpinnerCountryAdapter extends BaseAdapter {
    private List<CountryModel> list;
    private LayoutInflater inflater;
    private String lang;
    public SpinnerCountryAdapter(Context context, String lang) {
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
        @SuppressLint("ViewHolder") SpinnerCountryRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.spinner_country_row,viewGroup,false);
        binding.setTitle(list.get(i).getName());
        return binding.getRoot();
    }

    public void updateList(List<CountryModel> list){
        this.list = list;
        notifyDataSetChanged();
    }
}
