package com.midad_pos.uis.activity_sign_up;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityChooseSignInBinding;
import com.midad_pos.databinding.ActivitySignUpBinding;
import com.midad_pos.mvvm.SignUpMvvm;
import com.midad_pos.uis.activity_base.BaseActivity;
import com.midad_pos.uis.activity_sign_up_2.SignUp2Activity;

public class SignUpActivity extends BaseActivity {
    private ActivitySignUpBinding binding;
    private SignUpMvvm mvvm;
    private ActivityResultLauncher<Intent> launcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up);
        initView();
    }

    private void initView() {
        setUpToolbar(binding.toolbarSignUp,getString(R.string.registration),R.color.colorPrimary,R.color.white);
        mvvm = ViewModelProviders.of(this).get(SignUpMvvm.class);

        mvvm.getSignUpModel().observe(this,signUpModel -> {
            binding.setModel(signUpModel);
        });
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {

        });
        binding.btnNext.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUp2Activity.class);
            intent.putExtra("data",mvvm.getSignUpModel().getValue());
            launcher.launch(intent);
            overridePendingTransition(0,0);
        });


    }

}