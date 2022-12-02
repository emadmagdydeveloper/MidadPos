package com.midad_pos.uis.activity_sign_up;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityChooseSignInBinding;
import com.midad_pos.databinding.ActivitySignUpBinding;
import com.midad_pos.mvvm.SignUpMvvm;
import com.midad_pos.share.Common;
import com.midad_pos.tags.Tags;
import com.midad_pos.uis.activity_base.BaseActivity;
import com.midad_pos.uis.activity_sign_up_2.SignUp2Activity;
import com.midad_pos.uis.activity_splash.SplashActivity;

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
        mvvm = ViewModelProviders.of(this).get(SignUpMvvm.class);
        setUpToolbar(binding.toolbarSignUp,getString(R.string.registration),R.color.colorPrimary,R.color.white);
        binding.tvTerms.setClickable(true);
        binding.tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.tvTerms.setText(Html.fromHtml(getString(R.string.termsLink), Html.FROM_HTML_MODE_COMPACT));
        } else {
            binding.tvTerms.setText(Html.fromHtml(getString(R.string.termsLink)));

        }
        mvvm.getIsLoading().observe(this,isLoading->{
            binding.loader.setVisibility(isLoading? View.VISIBLE:View.GONE);
            if (binding.scrollView!=null){
                binding.scrollView.setVisibility(isLoading? View.GONE:View.VISIBLE);

            }

            if (binding.cardData!=null){
                binding.cardData.setVisibility(isLoading? View.GONE:View.VISIBLE);

            }
        });

        mvvm.getOnError().observe(this,error->{
            Common.createErrorDialog(this,error);
        });

        mvvm.getOnSuccess().observe(this,userModel -> {
            userModel.getData().setSelectedUser(userModel.getData().getUser());
            if (userModel.getData().getUser().getWarehouses().size()>0){
                if (userModel.getData().getUser().getWarehouses().get(0).getPos().size()>0){
                    userModel.getData().setSelectedWereHouse(userModel.getData().getUser().getWarehouses().get(0));
                    userModel.getData().setSelectedPos(userModel.getData().getUser().getWarehouses().get(0).getPos().get(0));
                    setUserModel(userModel);
                    Intent intent = new Intent(this, SplashActivity.class);
                    startActivity(intent);
                    finishAffinity();
                    overridePendingTransition(0,0);
                }else {
                    String title = "<font color ='#000000'>"+getString(R.string.no_pos)+" "+"</font><a href='"+ Tags.base_url +"' color='9F56DB'>"+getString(R.string.back_office)+"</a>";

                    Common.createAlertDialog(this,title,true);
                }


            }else {
                String title = "<font color ='#000000'>"+getString(R.string.no_pos)+" "+"</font><a href='"+ Tags.base_url +"' color='9F56DB'>"+getString(R.string.back_office)+"</a>";

                Common.createAlertDialog(this,title,true);
            }

        });



        mvvm.getSignUpModel().observe(this,signUpModel -> {
            binding.setModel(signUpModel);
        });

        if (mvvm.getSignUpModel().getValue()!=null){
            binding.checkbox.setChecked(mvvm.getSignUpModel().getValue().isAccept_terms());
        }


        binding.checkbox.setOnClickListener(v -> {
            if (mvvm.getSignUpModel().getValue()!=null){
                mvvm.getSignUpModel().getValue().setAccept_terms(binding.checkbox.isChecked());
            }
        });
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {

        });
        binding.btnNext.setOnClickListener(view -> {
            mvvm.signUp();
           /* Intent intent = new Intent(this, SignUp2Activity.class);
            intent.putExtra("data",mvvm.getSignUpModel().getValue());
            launcher.launch(intent);
            overridePendingTransition(0,0);*/

        });


    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);

        }catch (Exception e){}
    }

}