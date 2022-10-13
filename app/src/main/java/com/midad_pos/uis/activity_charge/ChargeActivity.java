package com.midad_pos.uis.activity_charge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityChargeBinding;
import com.midad_pos.uis.activity_base.BaseActivity;

public class ChargeActivity extends BaseActivity {
    private ActivityChargeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_charge);
        initView();
    }

    private void initView() {
        binding.layout.setLang(getLang());
    }
}