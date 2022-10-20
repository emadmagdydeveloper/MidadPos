package com.midad_pos.uis.activity_charge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.midad_pos.R;
import com.midad_pos.adapter.ChargeCartAdapter;
import com.midad_pos.adapter.SplitAdapter;
import com.midad_pos.databinding.ActivityChargeBinding;
import com.midad_pos.databinding.ChargeRowBinding;
import com.midad_pos.model.ChargeModel;
import com.midad_pos.model.cart.ManageCartModel;
import com.midad_pos.mvvm.ChargeMvvm;
import com.midad_pos.uis.activity_base.BaseActivity;

import java.util.Locale;

public class ChargeActivity extends BaseActivity {
    private ChargeMvvm mvvm;
    private ActivityChargeBinding binding;
    private ChargeCartAdapter adapter;
    private SplitAdapter splitAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_charge);
        initView();
    }

    private void initView() {
        mvvm = ViewModelProviders.of(this).get(ChargeMvvm.class);
        binding.layout.setLang(getLang());
        binding.layout.ticketSplit.setLang(getLang());
        binding.layout.payment.setLang(getLang());

        if (binding.layout.recViewCart != null) {
            binding.layout.recViewCart.setLayoutManager(new LinearLayoutManager(this));
            binding.layout.recViewCart.setHasFixedSize(true);
            adapter = new ChargeCartAdapter(this);
            binding.layout.recViewCart.setAdapter(adapter);

        }

        binding.layout.ticketSplit.recViewPayment.setLayoutManager(new LinearLayoutManager(this));
        binding.layout.ticketSplit.recViewPayment.setHasFixedSize(true);
        splitAdapter = new SplitAdapter(this);
        binding.layout.ticketSplit.recViewPayment.setAdapter(splitAdapter);


        if (mvvm.getPaidAmount().getValue() != null) {

            if (mvvm.getCartListInstance().getValue() != null) {
                updateEnableIcons(Double.parseDouble(mvvm.getPaidAmount().getValue().replace(",","")) >= mvvm.getCartListInstance().getValue().getTotalPrice());
            }
            binding.layout.edtCash.setText(String.format(Locale.US,"%.2f",Double.parseDouble(mvvm.getPaidAmount().getValue())));
        }
        mvvm.getCartListInstance().observe(this, cartList -> {


            binding.layout.setDeliveryOption(cartList.getDelivery_name());
            binding.layout.setTotal(cartList.getTotalPrice());
            binding.layout.setTotalDiscounts(cartList.getTotalDiscountValue());
            binding.layout.setTotalVat(cartList.getTotalTaxPrice());

            if (binding.layout.recViewCart != null) {
                if (adapter != null) {
                    adapter.updateList(cartList.getItems());

                }
            }

            if (cartList.getCustomerModel()!=null){
            }


        });
        mvvm.getIsSplit().observe(this, isSplit -> {
            binding.layout.splitTicket.setVisibility(isSplit ? View.VISIBLE : View.GONE);
        });
        mvvm.getRemaining().observe(this, remaining -> {
            binding.layout.ticketSplit.setRemaining(String.format(Locale.US, "%.2f", remaining));
            binding.layout.payment.setChangeAmount(remaining+"");

        });
        mvvm.getSplitAmount().observe(this, splitAmount -> {
            binding.layout.ticketSplit.setCount(String.valueOf(splitAmount));
        });
        mvvm.getSplitList().observe(this, list -> {
            if (splitAdapter != null) {
                splitAdapter.updateList(list);
            }

        });

        mvvm.getIsPaidShown().observe(this ,isShown->{
            binding.layout.flPayment.setVisibility(isShown?View.VISIBLE:View.GONE);
        });

        mvvm.getPaidAmount().observe(this,paidAmount->{
            binding.layout.payment.setPaidAmount(paidAmount);
        });

        binding.layout.arrow.setOnClickListener(v -> onBackPressed());
        binding.layout.ticketSplit.arrow.setOnClickListener(v -> {
            mvvm.getIsSplit().setValue(false);
            mvvm.getSplitAmount().setValue(2);
            mvvm.createSplitList(2);

        });
        binding.layout.split.setOnClickListener(v -> mvvm.getIsSplit().setValue(true));
        binding.layout.ticketSplit.increase.setOnClickListener(v -> mvvm.increase_decrease(1));

        binding.layout.ticketSplit.decrease.setOnClickListener(v -> mvvm.increase_decrease(2));


        binding.layout.edtCash.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.layout.edtCash.removeTextChangedListener(this);
                String num = editable.toString().trim();
                if (num.equals("0.00") || num.isEmpty()) {
                    binding.layout.edtCash.setText("0.00");
                    binding.layout.edtCash.setSelection(binding.layout.edtCash.getText().length());


                } else {
                    num = num.replace(".", "");
                    num = num.replace(",", "");
                    float pr = Float.parseFloat(num) / 100.0f;
                    num = String.format(Locale.US, "%.2f", pr);
                    if (num.length() >= 9) {
                        binding.layout.edtCash.setText("999,999.99");

                    } else if (num.length() == 7 || num.length() == 8) {
                        StringBuilder builder = new StringBuilder(num);
                        builder.insert(num.length() - 6, ",");
                        num = builder.toString();
                        binding.layout.edtCash.setText(num);

                    } else {
                        binding.layout.edtCash.setText(num);

                    }


                }

                if (mvvm.getCartListInstance().getValue() != null) {
                    updateEnableIcons(Double.parseDouble(num.replace(",","")) >= mvvm.getCartListInstance().getValue().getTotalPrice());
                }

                mvvm.getPaidAmount().setValue(num);
                binding.layout.edtCash.setSelection(binding.layout.edtCash.getText().length());
                binding.layout.edtCash.addTextChangedListener(this);

            }
        });

        if (binding.layout.flCash!=null){
            binding.layout.flCash.setOnClickListener(v ->{
                mvvm.getIsPaidShown().setValue(true);
                if (mvvm.getCartListInstance().getValue()!=null&&mvvm.getPaidAmount().getValue()!=null){
                    double remaining = Double.parseDouble(mvvm.getPaidAmount().getValue())-mvvm.getCartListInstance().getValue().getTotalPrice();
                    mvvm.getRemaining().setValue(Double.parseDouble(String.format(Locale.US,"%.2f",remaining)));
                }

            });
        }
        binding.layout.flCard.setOnClickListener(v -> {
            mvvm.getIsPaidShown().setValue(true);
            if (mvvm.getCartListInstance().getValue()!=null&&mvvm.getPaidAmount().getValue()!=null){
                double remaining = Double.parseDouble(mvvm.getPaidAmount().getValue())-mvvm.getCartListInstance().getValue().getTotalPrice();
                mvvm.getRemaining().setValue(Double.parseDouble(String.format(Locale.US,"%.2f",remaining)));
            }
        });

        if (binding.layout.btnCharge!=null){
            binding.layout.btnCharge.setOnClickListener(v -> {
                mvvm.getIsPaidShown().setValue(true);
                if (mvvm.getCartListInstance().getValue()!=null&&mvvm.getPaidAmount().getValue()!=null){
                    double remaining = Double.parseDouble(mvvm.getPaidAmount().getValue())-mvvm.getCartListInstance().getValue().getTotalPrice();
                    mvvm.getRemaining().setValue(Double.parseDouble(String.format(Locale.US,"%.2f",remaining)));
                }
            });
        }


    }

    private void updateEnableIcons(boolean enable) {
        binding.layout.setEnabled(enable);
        if (binding.layout.flCash != null) {
            binding.layout.flCash.setEnabled(enable);
        }
        if (binding.layout.iconCash!=null){
            binding.layout.iconCash.setEnabled(enable);
        }
        binding.layout.flCard.setEnabled(enable);
        binding.layout.iconCard.setEnabled(enable);

    }

    public void deleteSplit() {
        mvvm.increase_decrease(2);
    }

    public void updatePriceChange(double price, int adapterPos) {
        mvvm.updateList(price, adapterPos);
    }

    @Override
    public void onBackPressed() {

        if (binding.layout.flPayment.getVisibility()!=View.VISIBLE){
            super.onBackPressed();
            if (getLang().equals("ar")) {
                overridePendingTransition(R.anim.from_left, R.anim.to_right);


            } else {
                overridePendingTransition(R.anim.from_right, R.anim.to_left);


            }
        }else
        {
         mvvm.getIsPaidShown().setValue(false);
        }


    }


}