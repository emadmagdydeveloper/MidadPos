package com.midad_app_pos.uis.activity_add_discount;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.midad_app_pos.R;
import com.midad_app_pos.databinding.ActivityAddDiscountBinding;
import com.midad_app_pos.model.DiscountModel;
import com.midad_app_pos.mvvm.AddDiscountMvvm;
import com.midad_app_pos.uis.activity_base.BaseActivity;

import java.util.Locale;

public class AddDiscountActivity extends BaseActivity {
    private ActivityAddDiscountBinding binding;
    private DiscountModel discountModel;
    private AddDiscountMvvm mvvm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_discount);
        setContentView(binding.getRoot());

        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        discountModel = (DiscountModel) intent.getSerializableExtra("data");
    }

    private void initView() {

        mvvm = ViewModelProviders.of(this, new AddDiscountMvvm.MyViewModelFactory(getApplication(), discountModel)).get(AddDiscountMvvm.class);
        if (discountModel == null) {
            binding.setTitle(getString(R.string.create_discount));
            binding.setEnable(false);

        } else {
            binding.setTitle(getString(R.string.edit_discount));
            binding.setEnable(true);
            binding.cardDelete.setVisibility(View.VISIBLE);


        }
        binding.setLang(getLang());

        baseMvvm.getOnPinSuccess().observe(this,aBoolean -> {
            mvvm.showPin = false;
        });

        if (mvvm.getDiscountName().getValue() != null) {
            binding.edtDiscountName.setText(mvvm.getDiscountName().getValue());
        }
        if (mvvm.getDiscountValue() != null) {
            binding.edtValue.setText(mvvm.getDiscountValue().getValue());
        }
        mvvm.getType().observe(this, this::updateTypeUi);

        mvvm.getOnError().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

        mvvm.getAddedSuccess().observe(this, aBoolean -> {
            onBackPressed();
        });

        mvvm.getDeletedSuccess().observe(this, aBoolean -> {
            onBackPressed();
        });

        binding.edtDiscountName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.setEnable(!s.toString().isEmpty());
                mvvm.getDiscountName().setValue(s.toString());

            }
        });

        binding.edtValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.edtValue.removeTextChangedListener(this);
                String num = s.toString().trim();
                if (!num.isEmpty()&&!num.equals("0.0")){
                    num = num.replace(".", "");
                    float newNum = (float) (Integer.parseInt(num)/100.0);
                    String val = String.format(Locale.US,"%.2f",newNum);
                    binding.edtValue.setText(val);
                    binding.edtValue.setSelection(binding.edtValue.getText().length());
                    mvvm.getDiscountValue().setValue(val);
                }else if (num.equals("0.0")){

                    binding.edtValue.setText("");
                    mvvm.getDiscountValue().setValue("");

                }else {
                    binding.edtValue.setText("");
                    mvvm.getDiscountValue().setValue("");
                }
                binding.edtValue.addTextChangedListener(this);


            }
        });






        binding.arrowBack.setOnClickListener(v -> onBackPressed());

        binding.flValue.setOnClickListener(v -> mvvm.getType().setValue("val"));

        binding.flPercentage.setOnClickListener(v -> mvvm.getType().setValue("per"));

        binding.btnSave.setOnClickListener(v -> {
            if (discountModel == null) {
                mvvm.addDiscount(this, getUserModel().getData().getSelectedUser().getId());
            } else {
                mvvm.updateDiscount(this, getUserModel().getData().getSelectedUser().getId());
            }
        });

        binding.cardDelete.setOnClickListener(v -> mvvm.deleteDiscounts(this));

    }

    private void updateTypeUi(String type) {
        if (type.equals("val")) {
            binding.flValue.setBackgroundResource(R.color.primary_trans2);
            binding.flPercentage.setBackgroundResource(R.color.transparent);
        } else {
            binding.flValue.setBackgroundResource(R.color.transparent);
            binding.flPercentage.setBackgroundResource(R.color.primary_trans2);
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        if (mvvm.forNavigation){
            mvvm.showPin = false;
            mvvm.forNavigation = false;

        }else {
            mvvm.showPin = true;

        }


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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

}