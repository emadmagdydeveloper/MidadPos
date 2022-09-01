package com.midad_pos.uis.activity_login;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityLoginBinding;
import com.midad_pos.mvvm.LoginMvvm;
import com.midad_pos.uis.activity_base.BaseActivity;
import com.midad_pos.uis.activity_home.HomeActivity;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
       initView();
    }

    private void initView() {
        LoginMvvm mvvm = ViewModelProviders.of(this).get(LoginMvvm.class);
        setUpToolbar(binding.toolbarLogin,getString(R.string.sign_in),R.color.colorPrimary,R.color.white);
        mvvm.getLoginModel().observe(this, model-> binding.setModel(model));

        binding.btnLogin.setOnClickListener(view -> navigateToHomeActivity());
    }


    private void navigateToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
    }
}