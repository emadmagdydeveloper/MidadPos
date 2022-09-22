package com.midad_pos.uis.activity_base;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityBaseLayoutBinding;
import com.midad_pos.databinding.ToolbarBinding;
import com.midad_pos.language.Language;
import com.midad_pos.model.AppSettingModel;
import com.midad_pos.model.User;
import com.midad_pos.model.UserModel;
import com.midad_pos.mvvm.BaseMvvm;
import com.midad_pos.preferences.Preferences;

import java.util.Objects;

import io.paperdb.Paper;


public class BaseActivity extends AppCompatActivity {
    private ActivityBaseLayoutBinding binding;
    private BaseMvvm mvvm;
    public static final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String WRITE_PERM = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String CAM_PERM = Manifest.permission.CAMERA;
    public static final String FINELOCPerm = Manifest.permission.ACCESS_FINE_LOCATION;


    @Override
    public void setContentView(View view) {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_base_layout, null, false);
        binding.container.addView(view);
        super.setContentView(binding.getRoot());
        initViews();
    }

    protected BaseMvvm getBaseMvvm(){
        if (mvvm==null){
            mvvm = ViewModelProviders.of(this).get(BaseMvvm.class);

        }
        return mvvm;
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initViews() {
        mvvm = ViewModelProviders.of(this).get(BaseMvvm.class);
        if (binding != null) {
            binding.pinView.setLang(getLang());
            binding.pinView.btn1.setOnClickListener(v -> updatePinView("1"));
            binding.pinView.btn2.setOnClickListener(v -> updatePinView("2"));
            binding.pinView.btn3.setOnClickListener(v -> updatePinView("3"));
            binding.pinView.btn4.setOnClickListener(v -> updatePinView("4"));
            binding.pinView.btn5.setOnClickListener(v -> updatePinView("5"));
            binding.pinView.btn6.setOnClickListener(v -> updatePinView("6"));
            binding.pinView.btn7.setOnClickListener(v -> updatePinView("7"));
            binding.pinView.btn8.setOnClickListener(v -> updatePinView("8"));
            binding.pinView.btn9.setOnClickListener(v -> updatePinView("9"));
            binding.pinView.btn0.setOnClickListener(v -> updatePinView("0"));
            binding.pinView.btnClear.setOnClickListener(v -> updatePinView(""));

            if (mvvm.getPinCode().getValue() != null) {
                updatePinView(mvvm.getPinCode().getValue());
            }
            if (mvvm.getPay_in_out().getValue() != null && mvvm.getPay_in_out().getValue()) {
                if (binding.pinView.timeClock != null) {
                    binding.pinView.timeClock.setVisibility(View.GONE);
                } else {
                    if (binding.pinView.imageAlarm != null) {
                        binding.pinView.imageAlarm.setVisibility(View.GONE);

                    }

                }
                binding.pinView.imageBack.setVisibility(View.VISIBLE);
                if (binding.pinView.logo != null) {
                    binding.pinView.logo.setVisibility(View.GONE);

                } else {
                    binding.pinView.checkIn.setVisibility(View.GONE);
                    binding.pinView.checkOut.setVisibility(View.GONE);
                }
                binding.pinView.alarmCheck.setVisibility(View.VISIBLE);
                updatePinView("");

            }
            if (binding.pinView.imageAlarm != null) {
                binding.pinView.imageAlarm.setOnClickListener(v -> {
                    if (binding.pinView.timeClock != null) {
                        binding.pinView.timeClock.setVisibility(View.GONE);
                    } else {
                        binding.pinView.imageAlarm.setVisibility(View.GONE);

                    }
                    binding.pinView.imageBack.setVisibility(View.VISIBLE);
                    if (binding.pinView.logo != null) {
                        binding.pinView.logo.setVisibility(View.GONE);

                    } else {
                        binding.pinView.checkIn.setVisibility(View.VISIBLE);
                        binding.pinView.checkOut.setVisibility(View.VISIBLE);
                    }

                    binding.pinView.alarmCheck.setVisibility(View.VISIBLE);
                    mvvm.getPay_in_out().setValue(true);
                    updatePinView("");
                });
            }

            binding.pinView.imageBack.setOnClickListener(v -> {

                if (binding.pinView.timeClock != null) {
                    binding.pinView.timeClock.setVisibility(View.VISIBLE);
                } else {
                    binding.pinView.imageAlarm.setVisibility(View.VISIBLE);

                }
                binding.pinView.imageBack.setVisibility(View.GONE);
                if (binding.pinView.logo != null) {
                    binding.pinView.logo.setVisibility(View.VISIBLE);
                    binding.pinView.alarmCheck.setVisibility(View.GONE);

                } else {
                    binding.pinView.checkIn.setVisibility(View.GONE);
                    binding.pinView.checkOut.setVisibility(View.GONE);
                    binding.pinView.alarmCheck.setVisibility(View.VISIBLE);

                }

                mvvm.getPay_in_out().setValue(false);

                updatePinView("");
            });


        }

    }

    protected void updatePinView(String number) {
        mvvm.getPinCode().setValue(number);
        if (number.isEmpty()) {
            binding.pinView.firstPinView.setText("");

        } else {

            String pinNumber = Objects.requireNonNull(binding.pinView.firstPinView.getText()).toString();
            if (pinNumber.length() < 5) {
                pinNumber += number;
                binding.pinView.firstPinView.setText(pinNumber);
                if (pinNumber.length() == 4) {
                    UserModel userModel = getUserModel();
                    User user = getUserByPin(pinNumber);
                    if (user==null){
                        binding.pinView.tvEnterPinCode.setTextColor(getResources().getColor(R.color.cancel));
                        binding.pinView.tvEnterPinCode.setText(R.string.wrong_pin_code);
                        binding.pinView.firstPinView.setItemBackgroundResources(R.drawable.circle_wrong_pin);
                        binding.pinView.firstPinView.setTextColor(getResources().getColor(R.color.cancel));
                        new Handler()
                                .postDelayed(()->{
                                    binding.pinView.tvEnterPinCode.setTextColor(getResources().getColor(R.color.black));
                                    binding.pinView.tvEnterPinCode.setText(R.string.enter_pin);
                                    binding.pinView.firstPinView.setItemBackgroundResources(R.drawable.circle_pin);
                                    binding.pinView.firstPinView.setTextColor(getResources().getColor(R.color.colorPrimary));

                                    updatePinView("");
                                },500);
                    }else {
                        userModel.getData().setSelectedUser(user);
                        setUserModel(userModel);
                        mvvm.getOnUserRefreshed().setValue(true);
                        binding.pinContainer.setVisibility(View.GONE);
                        binding.container.setVisibility(View.VISIBLE);
                        updatePinView("");

                    }


                }
            }
        }


    }

    protected void showPinCodeView(){
        if (binding!=null){
            binding.pinContainer.setVisibility(View.VISIBLE);
            binding.container.setVisibility(View.GONE);
            updatePinView("");
        }
    }

    protected void hidePinCodeView(){
        if (binding!=null){
            binding.pinContainer.setVisibility(View.GONE);
            binding.container.setVisibility(View.VISIBLE);
            updatePinView("");
        }
    }


    public User getUserByPin(String pinCode){
        UserModel userModel = getUserModel();
        if (userModel.getData().getAnotherUsers().size()>0){
            for (User user:userModel.getData().getAnotherUsers()){
                if (user.getPin_code().equals(pinCode)){
                    return user;
                }
            }
        }else {
            if (userModel.getData().getUser().getPin_code().equals(pinCode)){
                return userModel.getData().getUser();
            }
        }


        return null;
    }

    protected String getLang() {
        Paper.init(this);
        return Paper.book().read("lang", "ar");
    }

    protected UserModel getUserModel() {
        Preferences preferences = Preferences.getInstance();
        return preferences.getUserData(this);
    }

    protected void setUserModel(UserModel userModel) {
        Preferences preferences = Preferences.getInstance();
        preferences.createUpdateUserData(this, userModel);
    }

    protected AppSettingModel getAppSetting() {
        Preferences preferences = Preferences.getInstance();
        return preferences.getAppSetting(this);
    }

    protected void setAppSettingModel(AppSettingModel model) {
        Preferences preferences = Preferences.getInstance();
        preferences.createUpdateAppSetting(this, model);
    }


    protected void setUpToolbar(ToolbarBinding binding, String title, int background, int arrowTitleColor) {
        binding.setLang(getLang());
        binding.setTitle(title);
        binding.arrow.setColorFilter(ContextCompat.getColor(this, arrowTitleColor));
        binding.tvTitle.setTextColor(ContextCompat.getColor(this, arrowTitleColor));
        binding.toolbar.setBackgroundResource(background);
        binding.llBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);

    }


}