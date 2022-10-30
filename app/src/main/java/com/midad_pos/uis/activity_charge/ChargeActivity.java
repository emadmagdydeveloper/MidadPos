package com.midad_pos.uis.activity_charge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.midad_pos.R;
import com.midad_pos.adapter.ChargeCartAdapter;
import com.midad_pos.adapter.CustomersAdapter;
import com.midad_pos.adapter.PaymentsAdapter;
import com.midad_pos.adapter.SpinnerCountryAdapter;
import com.midad_pos.adapter.SplitAdapter;
import com.midad_pos.databinding.ActivityChargeBinding;
import com.midad_pos.databinding.ChargeRowBinding;
import com.midad_pos.model.AddCustomerModel;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.ChargeModel;
import com.midad_pos.model.CountryModel;
import com.midad_pos.model.CustomerModel;
import com.midad_pos.model.ItemModel;
import com.midad_pos.model.PaymentModel;
import com.midad_pos.model.cart.ManageCartModel;
import com.midad_pos.mvvm.ChargeMvvm;
import com.midad_pos.uis.activity_base.BaseActivity;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChargeActivity extends BaseActivity {
    private ChargeMvvm mvvm;
    private ActivityChargeBinding binding;
    private ChargeCartAdapter adapter;
    private SplitAdapter splitAdapter;
    private CustomersAdapter customersAdapter;
    private SpinnerCountryAdapter spinnerCountryAdapter;
    private PaymentsAdapter paymentsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_charge, null, false);
        setContentView(binding.getRoot());
        initView();
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

    private void initView() {
        mvvm = ViewModelProviders.of(this).get(ChargeMvvm.class);
        binding.layout.setLang(getLang());
        binding.layout.ticketSplit.setLang(getLang());
        binding.layout.payment.setLang(getLang());

        binding.layout.recViewPayment.setLayoutManager(new LinearLayoutManager(this));
        binding.layout.recViewPayment.setHasFixedSize(true);
        paymentsAdapter = new PaymentsAdapter(this);
        binding.layout.recViewPayment.setAdapter(paymentsAdapter);

        if (binding.layout.recViewCart != null) {
            binding.layout.recViewCart.setLayoutManager(new LinearLayoutManager(this));
            binding.layout.recViewCart.setHasFixedSize(true);
            adapter = new ChargeCartAdapter(this);
            binding.layout.recViewCart.setAdapter(adapter);

        }

        binding.layout.ticketSplit.recViewPayment.setLayoutManager(new LinearLayoutManager(this));
        binding.layout.ticketSplit.recViewPayment.setHasFixedSize(true);
        splitAdapter = new SplitAdapter(this);


        if (mvvm.getPaidAmount().getValue() != null) {

            if (mvvm.getCartListInstance().getValue() != null) {

                updateEnableIcons(Double.parseDouble(mvvm.getPaidAmount().getValue().replace(",", "")) >= mvvm.getCartListInstance().getValue().getTotalPrice());
            }
            binding.layout.edtCash.setText(String.format(Locale.US, "%.2f", Double.parseDouble(mvvm.getPaidAmount().getValue())));
        }

        baseMvvm.getPaymentsInstance().observe(this, payments -> {
            mvvm.updatePayments(payments, true);

        });

        if (mvvm.getItemSplitPrice().getValue()!=null){
            binding.layout.edtCashSplitItem.setText(mvvm.getItemSplitPrice().getValue().replace(",",""));

        }
        mvvm.getPayment().observe(this, payments -> {
            if (paymentsAdapter != null) {
                paymentsAdapter.updateList(payments.getAll());

            }

            binding.layout.setShowCash(payments.getCash() != null);

        });
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


            if (binding.layout.toolbarLandAddUser != null) {
                if (cartList.getCustomerModel() == null) {
                    binding.layout.toolbarLandAddUser.setImageResource(R.drawable.ic_add_user);

                } else {
                    binding.layout.toolbarLandAddUser.setImageResource(R.drawable.ic_checked_user);

                }
            }


        });
        mvvm.getIsSplit().observe(this, isSplit -> {
            binding.layout.splitTicket.setVisibility(isSplit ? View.VISIBLE : View.GONE);
        });
        mvvm.getRemaining().observe(this, remaining -> {
            binding.layout.ticketSplit.setRemaining(String.format(Locale.US, "%.2f", remaining));
            binding.layout.payment.setChangeAmount(remaining + "");
            Log.e("remainAmount", remaining + "");


        });
        mvvm.getSplitAmount().observe(this, splitAmount -> {
            binding.layout.ticketSplit.setCount(String.valueOf(splitAmount));
        });
        mvvm.getSplitList().observe(this,list -> {
            if (splitAdapter != null) {
                splitAdapter.updateList(list);
            }
            if (binding.layout.ticketSplit.llDone!=null){
                Log.e("rem",mvvm.getRemaining().getValue()+"");

                binding.layout.ticketSplit.llDone.setVisibility((mvvm.getRemaining().getValue()!=null&&mvvm.getRemaining().getValue()==0)?View.VISIBLE:View.GONE);
            }
            binding.layout.ticketSplit.setEnable(mvvm.getRemaining().getValue()!=null&&mvvm.getRemaining().getValue()==0);
        });

        mvvm.getIsPaidShown().observe(this, isShown -> {
            binding.layout.flPayment.setVisibility(isShown ? View.VISIBLE : View.GONE);
        });

        mvvm.getPaidAmount().observe(this, paidAmount -> {
            binding.layout.payment.setPaidAmount(paidAmount);
        });

        mvvm.getIsDialogPriceOpened().observe(this, isOpened -> {
            if (isOpened) {
                binding.flDialogAddPrice.setVisibility(View.VISIBLE);
            } else {
                mvvm.getSplitPrice().setValue("0.00");
                binding.flDialogAddPrice.setVisibility(View.GONE);

            }
        });





        mvvm.getSplitPrice().observe(this, price -> binding.dialogAddPrice.setPrice(price));

        mvvm.getShowItemPaid().observe(this,show -> {
            binding.layout.llSplitItemPayment.setVisibility(show?View.VISIBLE:View.GONE);
            binding.layout.setItemDueAmount(Double.parseDouble(mvvm.itemForPaid.getPrice().replace(",","")));
        });

        binding.layout.arrowSplitItemAmount.setOnClickListener(v -> {
            mvvm.getShowItemPaid().setValue(false);
        });

        binding.layout.arrow.setOnClickListener(v -> onBackPressed());
        binding.layout.ticketSplit.arrow.setOnClickListener(v -> {
            mvvm.getIsSplit().setValue(false);
            mvvm.getSplitAmount().setValue(2);
            mvvm.createSplitList(2);


        });
        binding.layout.split.setOnClickListener(v -> mvvm.getIsSplit().setValue(true));
        binding.layout.ticketSplit.increase.setOnClickListener(v -> {
            mvvm.increase_decrease(1);

        });

        binding.layout.ticketSplit.decrease.setOnClickListener(v -> {
            mvvm.increase_decrease(2);

        });


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
                    updateEnableIcons(Double.parseDouble(num.replace(",", "")) >= mvvm.getCartListInstance().getValue().getTotalPrice());
                }

                mvvm.getPaidAmount().setValue(num);
                binding.layout.edtCash.setSelection(binding.layout.edtCash.getText().length());
                binding.layout.edtCash.addTextChangedListener(this);

            }
        });

        binding.layout.edtCashSplitItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.layout.edtCashSplitItem.removeTextChangedListener(this);
                String num = editable.toString().trim();
                if (num.equals("0.00") || num.isEmpty()) {
                    binding.layout.edtCashSplitItem.setText("0.00");
                    binding.layout.edtCashSplitItem.setSelection(binding.layout.edtCashSplitItem.getText().length());


                } else {
                    num = num.replace(".", "");
                    num = num.replace(",", "");
                    float pr = Float.parseFloat(num) / 100.0f;
                    num = String.format(Locale.US, "%.2f", pr);
                    if (num.length() >= 9) {
                        binding.layout.edtCashSplitItem.setText("999,999.99");

                    } else if (num.length() == 7 || num.length() == 8) {
                        StringBuilder builder = new StringBuilder(num);
                        builder.insert(num.length() - 6, ",");
                        num = builder.toString();
                        binding.layout.edtCashSplitItem.setText(num);

                    } else {
                        binding.layout.edtCashSplitItem.setText(num);

                    }

                    binding.layout.setEnabledSplitItem(Double.parseDouble(num.replace(",",""))>=Double.parseDouble(mvvm.itemForPaid.getPrice()));

                }
                mvvm.getItemSplitPrice().setValue(num);


                binding.layout.edtCashSplitItem.setSelection(binding.layout.edtCashSplitItem.getText().length());
                binding.layout.edtCashSplitItem.addTextChangedListener(this);

            }
        });


        if (binding.layout.flCash != null) {
            binding.layout.flCash.setOnClickListener(v -> {
                if (mvvm.getPayment().getValue() != null) {
                    mvvm.getPaymentType().setValue(Integer.valueOf(mvvm.getPayment().getValue().getCash().getId()));
                    mvvm.getIsPaidShown().setValue(true);
                    if (mvvm.getCartListInstance().getValue() != null && mvvm.getPaidAmount().getValue() != null) {
                        double remaining = Double.parseDouble(mvvm.getPaidAmount().getValue()) - mvvm.getCartListInstance().getValue().getTotalPrice();
                        mvvm.getRemaining().setValue(Double.parseDouble(String.format(Locale.US, "%.2f", remaining)));
                    }

                }

            });
        }


        if (binding.layout.btnCharge != null) {

            binding.layout.btnCharge.setOnClickListener(v -> {
                mvvm.getPaymentType().setValue(1);
                mvvm.getIsPaidShown().setValue(true);
                if (mvvm.getCartListInstance().getValue() != null && mvvm.getPaidAmount().getValue() != null) {
                    double remaining = Double.parseDouble(mvvm.getPaidAmount().getValue()) - mvvm.getCartListInstance().getValue().getTotalPrice();
                    mvvm.getRemaining().setValue(Double.parseDouble(String.format(Locale.US, "%.2f", remaining)));
                }
            });
        }


        if (binding.layout.addCustomerDialog != null) {
            spinnerCountryAdapter = new SpinnerCountryAdapter(this, getLang());
            binding.layout.addCustomerDialog.addCustomerLayout.spinnerCountry.setAdapter(spinnerCountryAdapter);
            binding.layout.addCustomerDialog.addCustomerLayout.spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mvvm.getCountryPos().setValue(i);
                    CountryModel model = (CountryModel) adapterView.getSelectedItem();
                    if (i == 0) {
                        Objects.requireNonNull(mvvm.getAddCustomerModel().getValue()).setCountry("");

                    } else {
                        Objects.requireNonNull(mvvm.getAddCustomerModel().getValue()).setCountry(model.getName());

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            mvvm.getIsCustomerLoading().observe(this, isLoading -> binding.layout.addCustomerDialog.searchDialog.loader.setVisibility(isLoading ? View.VISIBLE : View.GONE));
            binding.layout.addCustomerDialog.searchDialog.recView.setLayoutManager(new LinearLayoutManager(this));
            binding.layout.addCustomerDialog.searchDialog.recView.setHasFixedSize(true);
            customersAdapter = new CustomersAdapter(this);
            binding.layout.addCustomerDialog.searchDialog.recView.setAdapter(customersAdapter);
            binding.layout.addCustomerDialog.addCustomerLayout.btnSave.setOnClickListener(v -> mvvm.addCustomer(this));

            mvvm.getCustomersInstance().observe(this, customers -> {
                binding.layout.addCustomerDialog.searchDialog.tvNoCustomer.setVisibility(customers.size() > 0 ? View.GONE : View.VISIBLE);
                if (customersAdapter != null) {
                    customersAdapter.updateList(customers);
                }
            });

            mvvm.getAddCustomerModel().observe(this, model -> binding.layout.addCustomerDialog.addCustomerLayout.setModel(model));

            mvvm.getCountriesData().observe(this, countries -> {
                if (spinnerCountryAdapter != null) {
                    spinnerCountryAdapter.updateList(countries);
                }
            });

            mvvm.getCountryPos().observe(this, pos -> binding.layout.addCustomerDialog.addCustomerLayout.spinnerCountry.setSelection(pos));

            mvvm.getOnCustomerUpdatedSuccess().observe(this, aBoolean -> {
                binding.layout.addCustomerDialog.flAddCustomerLayout.setVisibility(View.GONE);

            });
            binding.layout.addCustomerDialog.addCustomerLayout.setLang(getLang());

            if (mvvm.getSearchQueryCustomer().getValue() != null) {
                binding.layout.addCustomerDialog.searchDialog.llSearch.setVisibility(View.VISIBLE);
                binding.layout.addCustomerDialog.searchDialog.edtSearch.setText(mvvm.getSearchQueryCustomer().getValue());

            }

            if (mvvm.getIsOpenedCustomerDialog().getValue() != null && mvvm.getIsOpenedCustomerDialog().getValue()) {
                if (binding.layout.dialogCustomer != null) {
                    binding.layout.dialogCustomer.setVisibility(View.VISIBLE);

                }
                if (mvvm.getIsAddCustomerDialogShow().getValue() != null && mvvm.getIsAddCustomerDialogShow().getValue()) {

                    binding.layout.addCustomerDialog.flAddCustomerLayout.setVisibility(View.VISIBLE);

                } else {
                    binding.layout.addCustomerDialog.flAddCustomerLayout.setVisibility(View.GONE);

                }
            }




            binding.layout.addCustomerDialog.addCustomerLayout.arrow.setOnClickListener(view -> onBackPressed());

            binding.layout.addCustomerDialog.searchDialog.addCustomer.setOnClickListener(view -> {
                binding.layout.addCustomerDialog.flAddCustomerLayout.setVisibility(View.VISIBLE);
                mvvm.getAddCustomerModel().setValue(new AddCustomerModel());
                mvvm.getIsAddCustomerDialogShow().setValue(true);

            });

            if (binding.layout.toolbarLandAddUser != null) {
                binding.layout.toolbarLandAddUser.setOnClickListener(view -> {
                    mvvm.getIsOpenedCustomerDialog().setValue(true);
                    if (binding.layout.dialogCustomer != null) {
                        binding.layout.dialogCustomer.setVisibility(View.VISIBLE);

                    }
                });
            }

            binding.layout.addCustomerDialog.searchDialog.closeCustomerDialog.setOnClickListener(view -> {
                if (binding.layout.dialogCustomer != null) {
                    binding.layout.dialogCustomer.setVisibility(View.GONE);
                    mvvm.getIsOpenedCustomerDialog().setValue(false);
                }

            });

            binding.layout.addCustomerDialog.searchDialog.tvOpenSearch.setOnClickListener(view -> binding.layout.addCustomerDialog.searchDialog.llSearch.setVisibility(View.VISIBLE));

            binding.layout.addCustomerDialog.searchDialog.closeSearch.setOnClickListener(view -> {
                if (mvvm.getSearchQueryCustomer().getValue() == null) {
                    binding.layout.addCustomerDialog.searchDialog.llSearch.setVisibility(View.GONE);

                } else {
                    binding.layout.addCustomerDialog.searchDialog.edtSearch.setText(null);

                }


            });

            binding.layout.addCustomerDialog.searchDialog.edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().isEmpty()) {
                        mvvm.getSearchQueryCustomer().setValue(null);
                        mvvm.searchCustomer(null);
                    } else {
                        mvvm.getSearchQueryCustomer().setValue(editable.toString());
                        mvvm.searchCustomer(editable.toString());

                    }
                }
            });


        }


        binding.layout.ticketSplit.recViewPayment.setAdapter(splitAdapter);


        binding.dialogAddPrice.btn0.setOnClickListener(v -> mvvm.updatePrice("0"));
        binding.dialogAddPrice.btn1.setOnClickListener(v -> mvvm.updatePrice("1"));
        binding.dialogAddPrice.btn2.setOnClickListener(v -> mvvm.updatePrice("2"));
        binding.dialogAddPrice.btn3.setOnClickListener(v -> mvvm.updatePrice("3"));
        binding.dialogAddPrice.btn4.setOnClickListener(v -> mvvm.updatePrice("4"));
        binding.dialogAddPrice.btn5.setOnClickListener(v -> mvvm.updatePrice("5"));
        binding.dialogAddPrice.btn6.setOnClickListener(v -> mvvm.updatePrice("6"));
        binding.dialogAddPrice.btn7.setOnClickListener(v -> mvvm.updatePrice("7"));
        binding.dialogAddPrice.btn8.setOnClickListener(v -> mvvm.updatePrice("8"));
        binding.dialogAddPrice.btn9.setOnClickListener(v -> mvvm.updatePrice("9"));

        if (binding.dialogAddPrice.btn00 != null) {
            binding.dialogAddPrice.btn00.setOnClickListener(v -> mvvm.updatePrice("00"));
        }

        binding.dialogAddPrice.btnOk.setOnClickListener(v -> {
            if (mvvm.getSplitPrice().getValue() != null) {

                if (mvvm.getSplitPrice().getValue()!=null&&mvvm.splitPos!=-1){
                    mvvm.updateList(Double.parseDouble(mvvm.getSplitPrice().getValue().replace(",","")),mvvm.splitPos);

                }
                mvvm.getIsDialogPriceOpened().setValue(false);


            }

        });

        binding.dialogAddPrice.clear.setOnClickListener(v -> mvvm.removeLastPriceIndex());

        binding.dialogAddPrice.close.setOnClickListener(v -> {
            mvvm.getIsDialogPriceOpened().setValue(false);
        });

        if (binding.layout.btnChargeSplitItem!=null){
            binding.layout.btnChargeSplitItem.setOnClickListener(v -> {
                mvvm.itemForPaid.setPaid(true);
                mvvm.itemForPaid.setEdited(true);
                mvvm.getShowItemPaid().setValue(false);
                mvvm.getRemainingPrice();
                mvvm.getSplitList().setValue(mvvm.getSplitList().getValue());

            });
        }

        if (binding.layout.flCashSplitItem!=null){
            binding.layout.flCashSplitItem.setOnClickListener(v -> {
                mvvm.itemForPaid.setPaid(true);
                mvvm.itemForPaid.setEdited(true);
                mvvm.getShowItemPaid().setValue(false);
                mvvm.getSplitList().setValue(mvvm.getSplitList().getValue());

            });
        }


    }

    private void updateEnableIcons(boolean enable) {
        binding.layout.setEnabled(enable);
        if (binding.layout.flCash != null) {
            binding.layout.flCash.setEnabled(enable);
        }
        if (binding.layout.iconCash != null) {
            binding.layout.iconCash.setEnabled(enable);
        }
        if (mvvm.getPayment().getValue() != null) {
            mvvm.updatePayments(mvvm.getPayment().getValue(), enable);


        }


    }

    public void deleteSplit(int adapterPosition) {
        mvvm.removeAdditionalSplit(adapterPosition);

    }

    public void updatePriceChange(ChargeModel model ,int pos) {
        if (!model.isPaid()){
            mvvm.splitPos = pos;
            mvvm.getSplitPrice().setValue(model.getPrice());
            mvvm.getIsDialogPriceOpened().setValue(true);
        }


    }

    public void assignCustomerToCart(CustomerModel customerModel) {
        if (binding.layout.dialogCustomer != null) {
            mvvm.assignCustomerToCart(customerModel);
            binding.layout.dialogCustomer.setVisibility(View.GONE);
            mvvm.getIsOpenedCustomerDialog().setValue(false);
            invalidateOptionsMenu();
            if (binding.layout.toolbarLandAddUser != null) {
                binding.layout.toolbarLandAddUser.setImageResource(R.drawable.ic_checked_user);
            }
        }

    }


    @Override
    public void onBackPressed() {
        if (binding.layout.dialogCustomer != null && binding.layout.addCustomerDialog != null) {
            if (binding.layout.dialogCustomer.getVisibility() == View.VISIBLE) {
                if (binding.layout.addCustomerDialog.flAddCustomerLayout.getVisibility() == View.VISIBLE) {
                    binding.layout.addCustomerDialog.flAddCustomerLayout.setVisibility(View.GONE);
                    mvvm.getIsAddCustomerDialogShow().setValue(false);
                } else {
                    binding.layout.dialogCustomer.setVisibility(View.GONE);
                    mvvm.getIsOpenedCustomerDialog().setValue(false);
                }


            } else {
                super.onBackPressed();
                if (getLang().equals("ar")) {
                    overridePendingTransition(R.anim.from_left, R.anim.to_right);


                } else {
                    overridePendingTransition(R.anim.from_right, R.anim.to_left);


                }
            }
        } else if (binding.layout.flPayment.getVisibility() != View.VISIBLE) {

            super.onBackPressed();
            if (getLang().equals("ar")) {
                overridePendingTransition(R.anim.from_left, R.anim.to_right);


            } else {
                overridePendingTransition(R.anim.from_right, R.anim.to_left);


            }
        } else {

            mvvm.getIsPaidShown().setValue(false);
        }


    }


    public void choosePayment(PaymentModel paymentModel) {
        mvvm.getPaymentType().setValue(Integer.valueOf(paymentModel.getId()));

        mvvm.getIsPaidShown().setValue(true);
        if (mvvm.getCartListInstance().getValue() != null && mvvm.getPaidAmount().getValue() != null) {
            double remaining = Double.parseDouble(mvvm.getPaidAmount().getValue()) - mvvm.getCartListInstance().getValue().getTotalPrice();
            mvvm.getRemaining().setValue(Double.parseDouble(String.format(Locale.US, "%.2f", remaining)));
        }
    }

    public void chooseSplitPayment(ChargeModel chargeModel, int adapterPosition, View view) {
        if (!chargeModel.isPaid()){
            createMenu(view, chargeModel, adapterPosition);

        }
    }

    private void createMenu(View view, ChargeModel chargeModel, int adapterPos) {
        if (mvvm.getAllPayment().getValue() != null) {
            int pos = 0;
            PopupMenu popupMenu = new PopupMenu(this, view);
            for (PaymentModel paymentModel : mvvm.getAllPayment().getValue()) {
                popupMenu.getMenu().add(1, pos, 1, paymentModel.getName());
                pos += 1;
            }


            popupMenu.setOnMenuItemClickListener(item -> {
                chargeModel.setPaymentModel(mvvm.getAllPayment().getValue().get(item.getItemId()));
                splitAdapter.notifyItemChanged(adapterPos);
                return true;
            });

            popupMenu.show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mvvm.showPin = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mvvm.showPin = false;
    }

    public void itemSplitPay(ChargeModel chargeModel) {
        mvvm.itemForPaid = chargeModel;
        if (chargeModel.getPaymentModel().getType().equalsIgnoreCase("cash")){
            mvvm.getItemSplitPrice().setValue(chargeModel.getPrice());
            binding.layout.edtCashSplitItem.setText(chargeModel.getPrice().replace(",",""));

            binding.layout.setItemDueAmount(Double.parseDouble(chargeModel.getPrice().replace(",","")));
            mvvm.getShowItemPaid().setValue(true);
        }else {
            mvvm.itemForPaid.setPaid(true);
            mvvm.itemForPaid.setEdited(true);
            mvvm.getRemainingPrice();
            mvvm.getSplitList().setValue(mvvm.getSplitList().getValue());
        }

    }
}