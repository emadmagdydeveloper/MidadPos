package com.midad_pos.uis.activity_refund;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.os.Handler;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityReceiptsBinding;
import com.midad_pos.databinding.ActivityRefundBinding;
import com.midad_pos.mvvm.RefundMvvm;
import com.midad_pos.uis.activity_base.BaseActivity;

public class RefundActivity extends BaseActivity {
    private RefundMvvm mvvm;
    private ActivityRefundBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_refund);
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        mvvm = ViewModelProviders.of(this).get(RefundMvvm.class);
        binding.setLang(getLang());
        baseMvvm.getOnPinSuccess().observe(this,aBoolean -> {
            mvvm.showPin = false;
        });

        binding.arrowBack.setOnClickListener(view -> {
            finish();
            overridePendingTransition(0,0);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mvvm.showPin){
            showPinCodeView();
        }else {
            hidePinCodeView();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        mvvm.showPin = true;



    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("pin",mvvm.showPin);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mvvm.showPin = savedInstanceState.getBoolean("pin");
        try {
            super.onRestoreInstanceState(savedInstanceState);

        } catch (Exception e) {
        }
    }



}