package com.midad_pos.uis.activity_forgot_password;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityForgotPasswordBinding;
import com.midad_pos.databinding.ErrorDialog2Binding;
import com.midad_pos.databinding.ErrorDialogBinding;
import com.midad_pos.mvvm.ForgotPasswordMvvm;
import com.midad_pos.uis.activity_base.BaseActivity;

public class ForgotPasswordActivity extends BaseActivity {
    private ForgotPasswordMvvm mvvm;
    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        initView();
    }

    private void initView() {
        mvvm = ViewModelProviders.of(this).get(ForgotPasswordMvvm.class);
        setUpToolbar(binding.toolbarForgotPassword, getString(R.string.forgot_password), R.color.colorPrimary, R.color.white);

        mvvm.getIsLoading().observe(this, isLoading -> {
            binding.loader.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (binding.scrollView != null) {
                binding.scrollView.setVisibility(isLoading ? View.GONE : View.VISIBLE);

            }

            if (binding.cardData != null) {
                binding.cardData.setVisibility(isLoading ? View.GONE : View.VISIBLE);

            }
        });
        mvvm.getOnSuccess().observe(this, success -> createAlertDialog(getString(R.string.email_sent),getString(R.string.check_email),true));
        mvvm.getOnError().observe(this, error -> createAlertDialog(getString(R.string.opps),error,false));
        if (mvvm.getEmail().getValue() != null) {
            binding.edtEmail.setText(mvvm.getEmail().getValue());
        }


        binding.edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mvvm.getEmail().setValue(s.toString());
            }
        });


        binding.send.setOnClickListener(v -> {
            String email = binding.edtEmail.getText().toString();
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.txtInputEmail.setError(null);
                mvvm.forgotPassword();
            } else {
                if (email.isEmpty()) {
                    binding.txtInputEmail.setError(getString(R.string.empty_field));

                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.txtInputEmail.setError(getString(R.string.inv_email));

                }
            }

        });

    }

    private void createAlertDialog(String title,String content,boolean isSuccess) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        ErrorDialog2Binding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.error_dialog2, null, false);
        binding.setTitle(title);
        binding.setContent(content);
        dialog.setView(binding.getRoot());
        binding.cancel.setOnClickListener(v -> {
            dialog.dismiss();
            if (isSuccess){
                finish();
                overridePendingTransition(0, 0);

            }
        });
        dialog.show();
    }
}