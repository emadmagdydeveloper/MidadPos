package com.midad_pos.uis.activity_refund;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityReceiptsBinding;
import com.midad_pos.databinding.ActivityRefundBinding;
import com.midad_pos.uis.activity_base.BaseActivity;

public class RefundActivity extends BaseActivity {
    private ActivityRefundBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_refund);
        initView();
    }

    private void initView() {
        binding.setLang(getLang());

        binding.arrowBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(0,0);
        });
    }



}