package com.midad_pos.uis.activity_login;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityLoginBinding;
import com.midad_pos.model.POSModel;
import com.midad_pos.model.UserModel;
import com.midad_pos.model.WereHouse;
import com.midad_pos.mvvm.LoginMvvm;
import com.midad_pos.share.Common;
import com.midad_pos.tags.Tags;
import com.midad_pos.uis.activity_base.BaseActivity;
import com.midad_pos.uis.activity_forgot_password.ForgotPasswordActivity;
import com.midad_pos.uis.activity_home.HomeActivity;
import com.midad_pos.uis.activity_pos.PosActivity;
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
                    if (mvvm.getIsLoginDone().getValue()!=null&&mvvm.getIsLoginDone().getValue()){
                        mvvm.getIsLoginDone().setValue(false);
                        navigateToWereHouses(userModel);

                    }else {
                    }

                }else {
                    WereHouse wereHouse = userModel.getData().getUser().getWarehouses().get(0);
                    userModel.getData().setSelectedWereHouse(wereHouse);
                    if (wereHouse.getPos().size()>1){
                        navigateToPosActivity(userModel);
                    }else {
                        POSModel posModel = wereHouse.getPos().get(0);
                        userModel.getData().setSelectedPos(posModel);
                        if (!userModel.getData().getUser().isAvailable()){
                            userModel.getData().setSelectedUser(userModel.getData().getUser());
                            setUserModel(userModel);
                        }
                        navigateToHomeActivity(userModel);


                    }

                }
            }else {
                String title = "<font color ='#000000'>"+getString(R.string.no_pos)+" "+"</font><a href='"+ Tags.base_url +"' color='9F56DB'>"+getString(R.string.back_office)+"</a>";

                Common.createAlertDialog(this,title,true);
            }
        });

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {

        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            navigateToForgetPasswordActivity();
        });
    }

    private void navigateToForgetPasswordActivity() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void navigateToWereHouses(UserModel userModel) {

        Intent intent = new Intent(this, StoreActivity.class);
        intent.putExtra("data",userModel);
        launcher.launch(intent);
        overridePendingTransition(0, 0);

    }


    private void navigateToHomeActivity(UserModel userModel) {

        setUserModel(userModel);
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finishAffinity();
        overridePendingTransition(0,0);
    }

    private void navigateToPosActivity(UserModel userModel) {
        Intent intent = new Intent(this, PosActivity.class);
        intent.putExtra("data",userModel);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);

        }catch (Exception e){}
    }

}