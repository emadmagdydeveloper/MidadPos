package com.midad_pos.uis.activity_shift;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.midad_pos.R;
import com.midad_pos.adapter.ShiftPaymentAdapter;
import com.midad_pos.databinding.ActivityShiftBinding;
import com.midad_pos.mvvm.ShiftMvvm;
import com.midad_pos.uis.activity_cash_management.CashManagementActivity;
import com.midad_pos.uis.activity_drawer_base.DrawerBaseActivity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


public class ShiftActivity extends DrawerBaseActivity {
    private ShiftMvvm mvvm;
    private ActivityShiftBinding binding;
    private ShiftPaymentAdapter paymentAdapter;
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_shift, null, false);
        setContentView(binding.getRoot());
        setUpDrawer(binding.toolBarShift, true);
        updateSelectedPos(2);
        initView();
    }

    private void initView() {

        mvvm = ViewModelProviders.of(this).get(ShiftMvvm.class);

        binding.shiftReportLayout.setLang(getLang());

        binding.recViewShiftPayment.setLayoutManager(new LinearLayoutManager(this));
        binding.recViewShiftPayment.setHasFixedSize(true);
        paymentAdapter = new ShiftPaymentAdapter(this);
        binding.recViewShiftPayment.setAdapter(paymentAdapter);

        mvvm.getShift().observe(this,shiftModel -> {
            binding.setModel(shiftModel);
            if (paymentAdapter!=null){
                paymentAdapter.updateList(shiftModel.getPayments());
            }
            binding.flData.setVisibility(View.VISIBLE);
        });
        if (mvvm.getIsShiftsOpened().getValue() != null && mvvm.getIsShiftsOpened().getValue()) {
            binding.flDialog.setVisibility(View.VISIBLE);
            binding.flShift.setVisibility(View.VISIBLE);
            binding.flShiftReport.setVisibility(View.GONE);
        }

        if (mvvm.getIsShiftReportsOpened().getValue() != null && mvvm.getIsShiftReportsOpened().getValue()) {
            binding.flDialog.setVisibility(View.VISIBLE);
            binding.flShift.setVisibility(View.VISIBLE);
            binding.flShiftReport.setVisibility(View.VISIBLE);

        }

        if (getAppSetting().getIsShiftOpen()==1){
            binding.flData.setVisibility(View.VISIBLE);
            binding.flOpenShift.setVisibility(View.GONE);
        }else {
            binding.flData.setVisibility(View.GONE);
            binding.flOpenShift.setVisibility(View.VISIBLE);
        }

        mvvm.getIsOpenSuccess().observe(this, success -> {
            binding.flData.setVisibility(View.VISIBLE);
            binding.flOpenShift.setVisibility(View.GONE);
        });

        mvvm.getIsShiftsClosed().observe(this, success -> {
            binding.flData.setVisibility(View.GONE);
            binding.flOpenShift.setVisibility(View.VISIBLE);
        });

        mvvm.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.loader.setVisibility(View.VISIBLE);
                binding.flOpenShift.setVisibility(View.GONE);
                binding.flData.setVisibility(View.GONE);
            } else {
                binding.loader.setVisibility(View.GONE);

            }


        });
        binding.imageShift.setOnClickListener(v -> {
            binding.flDialog.setVisibility(View.VISIBLE);
            binding.flShift.setVisibility(View.VISIBLE);
            binding.flShiftReport.setVisibility(View.GONE);
            mvvm.getIsShiftsOpened().setValue(true);
        });

        binding.dialogOpenShiftLayout.closeCustomerDialog.setOnClickListener(v -> {
            binding.flShiftReport.setVisibility(View.VISIBLE);
            mvvm.getIsShiftReportsOpened().setValue(true);
        });

        binding.shiftReportLayout.closeCustomerDialog.setOnClickListener(v -> {
            binding.flShiftReport.setVisibility(View.GONE);
            mvvm.getIsShiftReportsOpened().setValue(false);

        });

        binding.openShiftLayout.edtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.openShiftLayout.edtAmount.removeTextChangedListener(this);
                String num = editable.toString().trim();
                if (num.equals("0.00") || num.isEmpty()) {
                    binding.openShiftLayout.edtAmount.setText("0.00");
                    binding.openShiftLayout.edtAmount.setSelection(binding.openShiftLayout.edtAmount.getText().length());


                } else {
                    num = num.replace(".", "");
                    num = num.replace(",", "");
                    float pr = Float.parseFloat(num) / 100.0f;
                    num = String.format(Locale.US, "%.2f", pr);
                    if (num.length() >= 9) {
                        binding.openShiftLayout.edtAmount.setText("999,999.99");

                    } else if (num.length() == 7 || num.length() == 8) {
                        StringBuilder builder = new StringBuilder(num);
                        builder.insert(num.length() - 6, ",");
                        num = builder.toString();
                        binding.openShiftLayout.edtAmount.setText(num);

                    } else {
                        binding.openShiftLayout.edtAmount.setText(num);

                    }


                }


                binding.openShiftLayout.edtAmount.setSelection(binding.openShiftLayout.edtAmount.getText().length());
                binding.openShiftLayout.edtAmount.addTextChangedListener(this);

            }
        });

        binding.openShiftLayout.edtAmount.setText(mvvm.getStartAmount().getValue());


        binding.openShiftLayout.btnOpenShift.setOnClickListener(v -> {
            binding.flOpenShift.setVisibility(View.GONE);
            binding.flData.setVisibility(View.VISIBLE);
        });

        binding.btnCashManagement.setOnClickListener(v -> {
            mvvm.getForNavigation().setValue(true);

            Intent intent = new Intent(this, CashManagementActivity.class);
            if (mvvm.getShift().getValue()!=null&&mvvm.getShift().getValue().getData()!=null){
                intent.putExtra("data", (Serializable) mvvm.getShift().getValue().getData());

            }
            launcher.launch(intent);
            Log.e("vv","tyuio");
            overridePendingTransition(0, 0);
        });

        binding.btnCloseShift.setOnClickListener(v -> {
            binding.dialogCloseShift.setVisibility(View.VISIBLE);
        });

        binding.dialogCloseShiftLayout.closeDialog.setOnClickListener(v -> {
            binding.dialogCloseShift.setVisibility(View.GONE);

        });
        binding.btnCloseShift.setOnClickListener(v -> mvvm.closeShift());
        binding.openShiftLayout.btnOpenShift.setOnClickListener(v -> mvvm.openShift());

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            if (result.getResultCode()==RESULT_OK){
                mvvm.getCurrentShift();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mvvm.getShowPin().getValue()!=null&&mvvm.getShowPin().getValue()){
            showPinCodeView();
        }else {
            hidePinCodeView();
        }
        mvvm.getForNavigation().setValue(false);
        mvvm.getShowPin().setValue(false);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mvvm.getForNavigation().getValue()!=null&&mvvm.getForNavigation().getValue()){
            mvvm.getShowPin().setValue(false);
        }else {
            mvvm.getShowPin().setValue(true);

        }

    }

    @Override
    public void onBackPressed() {
        if (binding.flShiftReport.getVisibility() == View.VISIBLE) {
            binding.flShiftReport.setVisibility(View.GONE);
            mvvm.getIsShiftReportsOpened().setValue(false);
        } else if (binding.flShift.getVisibility() == View.VISIBLE) {
            binding.flShift.setVisibility(View.GONE);
            binding.flDialog.setVisibility(View.GONE);
            mvvm.getIsShiftsOpened().setValue(false);
        } else if (binding.dialogCloseShift.getVisibility() == View.VISIBLE) {
            binding.dialogCloseShift.setVisibility(View.GONE);

        } else {
            super.onBackPressed();
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mvvm.getShowPin().setValue(false);
    }
}