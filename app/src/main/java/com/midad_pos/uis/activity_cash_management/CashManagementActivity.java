package com.midad_pos.uis.activity_cash_management;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.midad_pos.R;
import com.midad_pos.adapter.PayInOutAdapter;
import com.midad_pos.databinding.ActivityCashManagementBinding;
import com.midad_pos.model.ShiftModel;
import com.midad_pos.mvvm.CashManagementMvvm;
import com.midad_pos.uis.activity_base.BaseActivity;

import java.util.List;
import java.util.Locale;

public class CashManagementActivity extends BaseActivity {
    private CashManagementMvvm mvvm;
    private ActivityCashManagementBinding binding;
    private List<ShiftModel.PayInOutModel> list;
    private PayInOutAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cash_management);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        list = (List<ShiftModel.PayInOutModel>) intent.getSerializableExtra("data");

    }

    private void initView() {
        mvvm = ViewModelProviders.of(this).get(CashManagementMvvm.class);
        binding.setLang(getLang());

        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recView.setHasFixedSize(true);
        binding.recView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new PayInOutAdapter(this);
        adapter.updateList(list);
        binding.recView.setAdapter(adapter);

        binding.arrow.setOnClickListener(v -> onBackPressed());

        if (mvvm.getAmount().getValue() != null) {
            binding.edtAmount.setText(mvvm.getAmount().getValue());
        }
        if (mvvm.getComment().getValue() != null) {
            binding.edtComment.setText(mvvm.getComment().getValue());
        }

        mvvm.getPayInOut().observe(this, payInOutModel -> {
            adapter.addItemList(payInOutModel);
            binding.recView.post(()->binding.recView.smoothScrollToPosition(0));
            binding.edtAmount.setText(null);
            binding.edtComment.setText(null);
        });


        binding.edtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.edtAmount.removeTextChangedListener(this);
                String num = editable.toString().trim();
                if (num.equals("0.00") || num.isEmpty()) {
                    binding.edtAmount.setText("0.00");
                    binding.edtAmount.setSelection(binding.edtAmount.getText().length());


                } else {
                    num = num.replace(".", "");
                    num = num.replace(",", "");
                    float pr = Float.parseFloat(num) / 100.0f;
                    num = String.format(Locale.US, "%.2f", pr);
                    if (num.length() >= 9) {
                        num = "999,999.99";
                        binding.edtAmount.setText(num);

                    } else if (num.length() == 7 || num.length() == 8) {
                        StringBuilder builder = new StringBuilder(num);
                        builder.insert(num.length() - 6, ",");
                        num = builder.toString();
                        binding.edtAmount.setText(num);

                    } else {
                        binding.edtAmount.setText(num);

                    }


                }

                mvvm.getAmount().setValue(num);
                binding.setEnabled(num.length() > 0);
                binding.edtAmount.setSelection(binding.edtAmount.getText().length());
                binding.edtAmount.addTextChangedListener(this);

            }
        });

        binding.edtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.edtComment.removeTextChangedListener(this);
                mvvm.getComment().setValue(s.toString());
                binding.edtAmount.addTextChangedListener(this);
            }
        });

        binding.edtAmount.requestFocus();

        binding.btnPayIn.setOnClickListener(v -> mvvm.payment(this, "in"));
        binding.btnPayOut.setOnClickListener(v -> mvvm.payment(this, "out"));

    }

    @Override
    public void onBackPressed() {
        if (mvvm.getIsDataChanged().getValue() != null && mvvm.getIsDataChanged().getValue()) {
            setResult(RESULT_OK);
        }
        finish();

        overridePendingTransition(0, 0);


    }


}