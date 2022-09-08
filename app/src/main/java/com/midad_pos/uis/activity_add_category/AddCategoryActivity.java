package com.midad_pos.uis.activity_add_category;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityAddCategoryBinding;
import com.midad_pos.mvvm.AddCategoryMvvm;
import com.midad_pos.mvvm.BaseMvvm;
import com.midad_pos.uis.activity_base.BaseActivity;

public class AddCategoryActivity extends BaseActivity {
    private AddCategoryMvvm mvvm;
    private ActivityAddCategoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_category);
        initView();
    }

    private void initView() {
        mvvm = ViewModelProviders.of(this).get(AddCategoryMvvm.class);
        binding.setLang(getLang());


        if (mvvm.getPos().getValue() != null) {
            updateCategoryCheckedColor(mvvm.getPos().getValue());
        }

        if (mvvm.getCategoryName().getValue() != null) {
            binding.edtCategoryName.setText(mvvm.getCategoryName().getValue());
        }

        mvvm.getAddedSuccess().observe(this, action -> {
            if (action.isEmpty()) {
                finish();

            } else {
                mvvm.getCategoryName().setValue("");
                binding.edtCategoryName.setText(null);

            }
        });


        binding.card1.setOnClickListener(v -> {
            updateCategoryCheckedColor(0);
        });
        binding.card2.setOnClickListener(v -> {
            updateCategoryCheckedColor(1);
        });
        binding.card3.setOnClickListener(v -> {
            updateCategoryCheckedColor(2);
        });
        binding.card4.setOnClickListener(v -> {
            updateCategoryCheckedColor(3);
        });
        binding.card5.setOnClickListener(v -> {
            updateCategoryCheckedColor(4);
        });
        binding.card6.setOnClickListener(v -> {
            updateCategoryCheckedColor(5);
        });
        binding.card7.setOnClickListener(v -> {
            updateCategoryCheckedColor(6);
        });
        binding.card8.setOnClickListener(v -> {
            updateCategoryCheckedColor(7);
        });


        binding.btnSave.setOnClickListener(v -> {
            if (binding.edtCategoryName.getText().toString().isEmpty()) {
                binding.txtInput.setError(null);
            } else {
                binding.txtInput.setError(getString(R.string.empty_field));

            }
        });

        binding.btnAssignItem.setOnClickListener(v -> {
            if (!binding.edtCategoryName.getText().toString().isEmpty()) {
                binding.txtInput.setError(null);
                mvvm.addCategory(getUserModel().getData().getSelectedUser().getId(), "assignItems");
            } else {
                binding.txtInput.setError(getString(R.string.empty_field));

            }
        });

        binding.btnCreateItem.setOnClickListener(v -> {
            if (!binding.edtCategoryName.getText().toString().isEmpty()) {
                binding.txtInput.setError(null);
                mvvm.addCategory(getUserModel().getData().getSelectedUser().getId(), "addItems");

            } else {
                binding.txtInput.setError(getString(R.string.empty_field));

            }
        });

        binding.btnSave.setOnClickListener(v -> {
            mvvm.addCategory(getUserModel().getData().getSelectedUser().getId(), "");

        });
        binding.edtCategoryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString();
                binding.setEnable(name.length() > 0);
                mvvm.getCategoryName().setValue(name);
                binding.txtInput.setError(null);
            }
        });

        binding.arrowBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });


    }

    @SuppressLint("ResourceType")
    private void updateCategoryCheckedColor(int pos) {
        String color = "";
        switch (pos) {
            case 1:
                color = getResources().getString(R.color.cat2);

                binding.check1.setVisibility(View.GONE);
                binding.check2.setVisibility(View.VISIBLE);
                binding.check3.setVisibility(View.GONE);
                binding.check4.setVisibility(View.GONE);
                binding.check5.setVisibility(View.GONE);
                binding.check6.setVisibility(View.GONE);
                binding.check7.setVisibility(View.GONE);
                binding.check8.setVisibility(View.GONE);

                break;
            case 2:
                color = getResources().getString(R.color.cat3);
                binding.check1.setVisibility(View.GONE);
                binding.check2.setVisibility(View.GONE);
                binding.check3.setVisibility(View.VISIBLE);
                binding.check4.setVisibility(View.GONE);
                binding.check5.setVisibility(View.GONE);
                binding.check6.setVisibility(View.GONE);
                binding.check7.setVisibility(View.GONE);
                binding.check8.setVisibility(View.GONE);
                break;
            case 3:
                color = getResources().getString(R.color.cat4);
                binding.check1.setVisibility(View.GONE);
                binding.check2.setVisibility(View.GONE);
                binding.check3.setVisibility(View.GONE);
                binding.check4.setVisibility(View.VISIBLE);
                binding.check5.setVisibility(View.GONE);
                binding.check6.setVisibility(View.GONE);
                binding.check7.setVisibility(View.GONE);
                binding.check8.setVisibility(View.GONE);
                break;
            case 4:
                color = getResources().getString(R.color.cat5);
                binding.check1.setVisibility(View.GONE);
                binding.check2.setVisibility(View.GONE);
                binding.check3.setVisibility(View.GONE);
                binding.check4.setVisibility(View.GONE);
                binding.check5.setVisibility(View.VISIBLE);
                binding.check6.setVisibility(View.GONE);
                binding.check7.setVisibility(View.GONE);
                binding.check8.setVisibility(View.GONE);
                break;
            case 5:
                color = getResources().getString(R.color.cat6);
                binding.check1.setVisibility(View.GONE);
                binding.check2.setVisibility(View.GONE);
                binding.check3.setVisibility(View.GONE);
                binding.check4.setVisibility(View.GONE);
                binding.check5.setVisibility(View.GONE);
                binding.check6.setVisibility(View.VISIBLE);
                binding.check7.setVisibility(View.GONE);
                binding.check8.setVisibility(View.GONE);
                break;
            case 6:
                color = getResources().getString(R.color.cat7);
                binding.check1.setVisibility(View.GONE);
                binding.check2.setVisibility(View.GONE);
                binding.check3.setVisibility(View.GONE);
                binding.check4.setVisibility(View.GONE);
                binding.check5.setVisibility(View.GONE);
                binding.check6.setVisibility(View.GONE);
                binding.check7.setVisibility(View.VISIBLE);
                binding.check8.setVisibility(View.GONE);
                break;
            case 7:
                color = getResources().getString(R.color.cat8);
                binding.check1.setVisibility(View.GONE);
                binding.check2.setVisibility(View.GONE);
                binding.check3.setVisibility(View.GONE);
                binding.check4.setVisibility(View.GONE);
                binding.check5.setVisibility(View.GONE);
                binding.check6.setVisibility(View.GONE);
                binding.check7.setVisibility(View.GONE);
                binding.check8.setVisibility(View.VISIBLE);
                break;
            default:
                color = getResources().getString(R.color.cat1);
                binding.check1.setVisibility(View.VISIBLE);
                binding.check2.setVisibility(View.GONE);
                binding.check3.setVisibility(View.GONE);
                binding.check4.setVisibility(View.GONE);
                binding.check5.setVisibility(View.GONE);
                binding.check6.setVisibility(View.GONE);
                binding.check7.setVisibility(View.GONE);
                binding.check8.setVisibility(View.GONE);
                break;

        }
        mvvm.getPos().setValue(pos);
        mvvm.getColor().setValue(color);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);

    }
}