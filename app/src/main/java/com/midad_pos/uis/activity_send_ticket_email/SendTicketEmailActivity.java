package com.midad_pos.uis.activity_send_ticket_email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivitySendTicketEmailBinding;
import com.midad_pos.mvvm.SendTicketEmailMvvm;
import com.midad_pos.uis.activity_base.BaseActivity;

public class SendTicketEmailActivity extends BaseActivity {
    private SendTicketEmailMvvm mvvm;
    private ActivitySendTicketEmailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_send_ticket_email);
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        mvvm = ViewModelProviders.of(this).get(SendTicketEmailMvvm.class);
        baseMvvm.getOnPinSuccess().observe(this,aBoolean -> {
            mvvm.showPin = false;
        });

        if (mvvm.getEmail().getValue()!=null){
            binding.email.setText(mvvm.getEmail().getValue());
        }

        binding.close.setOnClickListener(view -> {
            finish();
            overridePendingTransition(0,0);
        });

        binding.email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String mail = editable.toString().trim();
                mvvm.getEmail().setValue(mail);
                binding.setIsValid(Patterns.EMAIL_ADDRESS.matcher(mail).matches());

            }
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