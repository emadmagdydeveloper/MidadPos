package com.midad_pos.uis.activity_cash_management;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityCashManagementBinding;
import com.midad_pos.uis.activity_base.BaseActivity;

public class CashManagementActivity extends BaseActivity {
    private ActivityCashManagementBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_cash_management);
        initView();
    }

    private void initView() {
        binding.setLang(getLang());

        binding.arrow.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0,0);
        });

        binding.edtAmount.requestFocus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);

    }


}