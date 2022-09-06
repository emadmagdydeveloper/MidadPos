package com.midad_pos.uis.activity_login;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityLoginBinding;
import com.midad_pos.model.UserModel;
import com.midad_pos.mvvm.LoginMvvm;
import com.midad_pos.share.Common;
import com.midad_pos.uis.activity_base.BaseActivity;
import com.midad_pos.uis.activity_home.HomeActivity;
import com.midad_pos.uis.activity_store.StoreActivity;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;
    private ActivityResultLauncher<Intent> launcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initView();
    }

    private void initView() {
        LoginMvvm mvvm = ViewModelProviders.of(this).get(LoginMvvm.class);
        setUpToolbar(binding.toolbarLogin, getString(R.string.sign_in), R.color.colorPrimary, R.color.white);
        mvvm.getLoginModel().observe(this, model -> binding.setModel(model));

        binding.btnLogin.setOnClickListener(view -> {
            mvvm.login();
        });

        mvvm.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.loader.setVisibility(View.VISIBLE);
                if (binding.scrollView!=null){
                    binding.scrollView.setVisibility(View.GONE);
                }
                if (binding.cardData!=null){
                    binding.cardData.setVisibility(View.GONE);
                }
            } else {
                binding.loader.setVisibility(View.GONE);
                new Handler().postDelayed(()->{
                    if (binding.scrollView!=null){
                        binding.scrollView.setVisibility(View.VISIBLE);
                    }
                    if (binding.cardData!=null){
                        binding.cardData.setVisibility(View.VISIBLE);
                    }
                },2000);


            }
        });

        mvvm.getOnError().observe(this, error -> {
            Common.createAlertDialog(this, error,false);
        });

        mvvm.getOnUserLogin().observe(this, userModel -> {
            if (userModel.getData().getUser().getWarehouses().size()>0){
                if (userModel.getData().getUser().getWarehouses().size()>1){
                    if (mvvm.getIsLoginDone().getValue()){
                        mvvm.getIsLoginDone().setValue(false);
                        navigateToWereHouses(userModel);

                    }else {

                    }

                }else {

                }
            }else {

            }
        });

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {

        });
    }

    private void navigateToWereHouses(UserModel userModel) {

        Intent intent = new Intent(this, StoreActivity.class);
        intent.putExtra("data",userModel);
        launcher.launch(intent);
        overridePendingTransition(0, 0);

    }


    private void navigateToHomeActivity() {

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }


}