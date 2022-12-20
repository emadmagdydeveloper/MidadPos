package com.midad_app_pos.uis.activity_sign_up_2;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;

import com.midad_app_pos.R;
import com.midad_app_pos.adapter.SpinnerBusinessType;
import com.midad_app_pos.databinding.ActivitySignUp2Binding;
import com.midad_app_pos.databinding.BusinessTypeAlertDialogBinding;
import com.midad_app_pos.model.SignUpModel;
import com.midad_app_pos.mvvm.SignUp2Mvvm;
import com.midad_app_pos.uis.activity_base.BaseActivity;
import com.midad_app_pos.uis.activity_home.HomeActivity;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

public class SignUp2Activity extends BaseActivity implements OnCountryPickerListener {
    private ActivitySignUp2Binding binding;
    private SignUp2Mvvm mvvm;
    private SignUpModel model;
    private SpinnerBusinessType spinnerBusinessType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up2);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        model = (SignUpModel) getIntent().getSerializableExtra("data");

    }

    private void initView() {
        setUpToolbar(binding.toolbarSignUp, getString(R.string.registration), R.color.colorPrimary, R.color.white);

        binding.tvTerms.setClickable(true);
        binding.tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.tvTerms.setText(Html.fromHtml(getString(R.string.termsLink), Html.FROM_HTML_MODE_COMPACT));
        } else {
            binding.tvTerms.setText(Html.fromHtml(getString(R.string.termsLink)));

        }
        mvvm = ViewModelProviders.of(this).get(SignUp2Mvvm.class);
        mvvm.setSignUpModel(model);
        mvvm.getSignUpModel().observe(this, signUpModel -> binding.setModel(signUpModel));

        spinnerBusinessType = new SpinnerBusinessType(this, getLang());
        binding.spinner.setAdapter(spinnerBusinessType);

        mvvm.getBusinessType().observe(this, list -> {
            spinnerBusinessType.updateList(list);
        });

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mvvm.updateList(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Country country = getCountryBySim();
        if (country != null) {
            onSelectCountry(country);
        } else {

        }
        binding.llCountry.setOnClickListener(view -> showCountryPicker());

        binding.btnSignUp.setOnClickListener(view -> navigateToHomeActivity());
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    private Country getCountryBySim() {
        try {
            CountryPicker countryPicker = new CountryPicker.Builder().with(this).listener(this).build();
            return countryPicker.getCountryFromSIM();
        } catch (Exception e) {

        }

        return null;
    }

    private void showCountryPicker() {
        CountryPicker.Builder builder =
                new CountryPicker.Builder().with(this)
                        .listener(this);
        builder.theme(CountryPicker.THEME_NEW);
        builder.canSearch(true);
        builder.sortBy(CountryPicker.SORT_BY_NAME);
        CountryPicker countryPicker = builder.build();
        countryPicker.showDialog(this);

    }

    @Override
    public void onSelectCountry(Country country) {
        binding.flag.setImageResource(country.getFlag());
        model.setCountry_code(country.getDialCode());
        model.setCountry_name(country.getName());
        model.setFlag_id(country.getFlag());
        mvvm.setSignUpModel(model);
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);

        }catch (Exception e){}
    }

}