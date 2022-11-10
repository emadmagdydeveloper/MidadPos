package com.midad_pos.uis.activity_choose_signin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityChooseSignInBinding;
import com.midad_pos.uis.activity_base.BaseActivity;
import com.midad_pos.uis.activity_login.LoginActivity;
import com.midad_pos.uis.activity_sign_up.SignUpActivity;

public class ChooseSignInActivity extends BaseActivity {
    private ActivityChooseSignInBinding binding;
    private ActivityResultLauncher<Intent> launcher;
    private int req ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_choose_sign_in);
        initView();
    }

    private void initView() {
        binding.btnLogin.setOnClickListener(view -> {
            navigation(LoginActivity.class);
        });

        binding.btnRegister.setOnClickListener(view -> {
            navigation(SignUpActivity.class);
        });

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {

        });
    }

    private void navigation(Class<?> activityClass){
        Intent intent = new Intent(this,activityClass);
        launcher.launch(intent);
        overridePendingTransition(0,0);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);

        }catch (Exception e){}
    }
}