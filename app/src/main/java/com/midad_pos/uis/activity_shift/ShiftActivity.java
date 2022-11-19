package com.midad_pos.uis.activity_shift;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.midad_pos.R;
import com.midad_pos.adapter.ShiftPaymentAdapter;
import com.midad_pos.adapter.ShiftsAdapter;
import com.midad_pos.databinding.ActivityShiftBinding;
import com.midad_pos.model.ShiftModel;
import com.midad_pos.mvvm.ShiftMvvm;
import com.midad_pos.uis.activity_cash_management.CashManagementActivity;
import com.midad_pos.uis.activity_drawer_base.DrawerBaseActivity;

import java.io.Serializable;
import java.util.Locale;


public class ShiftActivity extends DrawerBaseActivity {
    private ShiftMvvm mvvm;
    private ActivityShiftBinding binding;
    private ShiftPaymentAdapter paymentAdapter,paymentAdapter2;
    private ActivityResultLauncher<Intent> launcher;
    private ShiftsAdapter adapter;

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
        binding.setLang(getLang());
        binding.shiftReportLayout.setLang(getLang());
        binding.recViewShiftPayment.setLayoutManager(new LinearLayoutManager(this));
        binding.recViewShiftPayment.setHasFixedSize(true);
        paymentAdapter = new ShiftPaymentAdapter(this);
        binding.recViewShiftPayment.setAdapter(paymentAdapter);

        binding.shiftReportLayout.recViewShiftPayment.setLayoutManager(new LinearLayoutManager(this));
        binding.shiftReportLayout.recViewShiftPayment.setHasFixedSize(true);
        paymentAdapter2 = new ShiftPaymentAdapter(this);
        binding.shiftReportLayout.recViewShiftPayment.setAdapter(paymentAdapter2);


        binding.dialogOpenShiftLayout.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.dialogOpenShiftLayout.recView.setHasFixedSize(true);
        adapter = new ShiftsAdapter(this,getLang());
        binding.dialogOpenShiftLayout.recView.setAdapter(adapter);
        binding.dialogOpenShiftLayout.recView.setAdapter(adapter);


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


        if (mvvm.getActualAmount().getValue() != null) {
            binding.dialogCloseShiftLayout.edtActualAmount.setText(mvvm.getActualAmount().getValue());
        }

        if (mvvm.getSelectedHistoryShift().getValue()!=null){
            binding.shiftReportLayout.setModel(mvvm.getSelectedHistoryShift().getValue());
            if (paymentAdapter2!=null){
                paymentAdapter2.updateList(mvvm.getSelectedHistoryShift().getValue().getPayments());
            }
        }
        if (getAppSetting().getIsShiftOpen() == 1) {
            binding.flData.setVisibility(View.VISIBLE);
            binding.flOpenShift.setVisibility(View.GONE);
        } else {
            binding.flData.setVisibility(View.GONE);
            binding.flOpenShift.setVisibility(View.VISIBLE);
        }


        mvvm.getShift().observe(this, shiftModel -> {
            binding.setModel(shiftModel);
            calculateDiff(shiftModel.getExpected_cash() + "");
            binding.dialogCloseShiftLayout.setTotal(String.format(Locale.US, "%.2f", shiftModel.getExpected_cash()));
            if (paymentAdapter != null) {
                paymentAdapter.updateList(shiftModel.getPayments());
            }
            binding.flData.setVisibility(View.VISIBLE);
        });

        mvvm.getShowDialogCloseShift().observe(this, show -> binding.dialogCloseShift.setVisibility(show ? View.VISIBLE : View.GONE));

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
                binding.dialogCloseShift.setVisibility(View.GONE);
            } else {
                binding.loader.setVisibility(View.GONE);

            }


        });

        mvvm.getIsLoadingShifts().observe(this, isLoading -> {
            if (isLoading) {
                binding.dialogOpenShiftLayout.loader.setVisibility(View.VISIBLE);

            } else {
                binding.dialogOpenShiftLayout.loader.setVisibility(View.GONE);

            }


        });

        mvvm.getShifts().observe(this,list->{
            if (adapter!=null){
                adapter.updateList(list);
            }
        });



        binding.dialogCloseShiftLayout.edtActualAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.dialogCloseShiftLayout.edtActualAmount.removeTextChangedListener(this);
                String num = editable.toString().trim();
                if (num.equals("0.00") || num.isEmpty()) {
                    num = "0.00";
                    binding.dialogCloseShiftLayout.edtActualAmount.setText(num);
                    binding.dialogCloseShiftLayout.edtActualAmount.setSelection(binding.dialogCloseShiftLayout.edtActualAmount.getText().length());


                } else {
                    num = num.replace(".", "");
                    num = num.replace(",", "");
                    float pr = Float.parseFloat(num) / 100.0f;
                    num = String.format(Locale.US, "%.2f", pr);
                    if (num.length() >= 9) {
                        num = "999,999.99";
                        binding.dialogCloseShiftLayout.edtActualAmount.setText(num);

                    } else if (num.length() == 7 || num.length() == 8) {
                        StringBuilder builder = new StringBuilder(num);
                        builder.insert(num.length() - 6, ",");
                        num = builder.toString();
                        binding.dialogCloseShiftLayout.edtActualAmount.setText(num);

                    } else {
                        binding.dialogCloseShiftLayout.edtActualAmount.setText(num);

                    }


                }

                mvvm.getActualAmount().setValue(num);
                calculateDiff(num);

                binding.dialogCloseShiftLayout.edtActualAmount.setSelection(binding.dialogCloseShiftLayout.edtActualAmount.getText().length());
                binding.dialogCloseShiftLayout.edtActualAmount.addTextChangedListener(this);

            }
        });



        binding.dialogOpenShiftLayout.closeOpenShiftDialog.setOnClickListener(v -> {
            binding.flDialog.setVisibility(View.GONE);
            binding.flShift.setVisibility(View.GONE);
            mvvm.getIsShiftsOpened().setValue(false);
        });

        binding.shiftReportLayout.closeReportDialog.setOnClickListener(v -> {
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
            mvvm.forNavigation = true;
            Intent intent = new Intent(this, CashManagementActivity.class);
            if (mvvm.getShift().getValue() != null && mvvm.getShift().getValue().getData() != null) {
                intent.putExtra("data", (Serializable) mvvm.getShift().getValue().getData());

            }
            launcher.launch(intent);
            overridePendingTransition(0, 0);
        });

        binding.dialogCloseShiftLayout.closeDialog.setOnClickListener(v -> {
            binding.dialogCloseShift.setVisibility(View.GONE);
            mvvm.getActualAmount().setValue("0.00");
            binding.dialogCloseShiftLayout.setDiff("0.00");
            binding.dialogCloseShiftLayout.edtActualAmount.setText("0.00");

        });

        binding.imageShift.setOnClickListener(v -> {
            binding.flDialog.setVisibility(View.VISIBLE);
            binding.flShift.setVisibility(View.VISIBLE);
            binding.flShiftReport.setVisibility(View.GONE);
            mvvm.getIsShiftsOpened().setValue(true);
        });

        binding.btnCloseShift.setOnClickListener(v -> mvvm.getShowDialogCloseShift().setValue(true));

        binding.openShiftLayout.btnOpenShift.setOnClickListener(v -> mvvm.openShift());

        binding.dialogCloseShiftLayout.btnCloseShift.setOnClickListener(v -> {
            mvvm.closeShift();
        });
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                mvvm.getCurrentShift();
            }
        });


    }

    private void calculateDiff(String num) {
        if (mvvm.getShift().getValue() != null) {
            double total = mvvm.getShift().getValue().getExpected_cash();
            double diff = total - Double.parseDouble(num.replace(",", ""));
            String format = String.format(Locale.US, "%.2f", diff);
            binding.dialogCloseShiftLayout.setDiff(format);
        }
    }

    public void setShiftData(ShiftModel model) {
        mvvm.getSelectedHistoryShift().setValue(model);
        mvvm.getIsShiftReportsOpened().setValue(true);
        binding.flDialog.setVisibility(View.VISIBLE);
        binding.flShift.setVisibility(View.VISIBLE);
        binding.flShiftReport.setVisibility(View.VISIBLE);
        binding.shiftReportLayout.setModel(model);
        if (paymentAdapter2!=null){
            paymentAdapter2.updateList(model.getPayments());
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mvvm.showPin) {
            showPinCodeView();
        } else {
            hidePinCodeView();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (mvvm.forNavigation){
            mvvm.showPin = false;

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
            mvvm.getActualAmount().setValue("0.00");
            binding.dialogCloseShiftLayout.setDiff("0.00");
            binding.dialogCloseShiftLayout.edtActualAmount.setText("0.00");

        } else {
            super.onBackPressed();
        }


    }


}