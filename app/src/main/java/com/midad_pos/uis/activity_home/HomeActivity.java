package com.midad_pos.uis.activity_home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.budiyev.android.codescanner.CodeScanner;
import com.midad_pos.R;
import com.midad_pos.adapter.CartAdapter;
import com.midad_pos.adapter.CartDiscountAdapter;
import com.midad_pos.adapter.CustomersAdapter;
import com.midad_pos.adapter.HomeDiscountAdapter;
import com.midad_pos.adapter.HomeItemAdapter;
import com.midad_pos.adapter.ItemDiscountAdapter;
import com.midad_pos.adapter.ItemMainModifierAdapter;
import com.midad_pos.adapter.ItemVariantAdapter;
import com.midad_pos.adapter.SpinnerCountryAdapter;
import com.midad_pos.databinding.ActivityHomeBinding;
import com.midad_pos.databinding.DialogClearTicketLayoutBinding;
import com.midad_pos.model.AddCustomerModel;
import com.midad_pos.model.AppSettingModel;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.CountryModel;
import com.midad_pos.model.CustomerModel;
import com.midad_pos.model.DeliveryModel;
import com.midad_pos.model.DiscountModel;
import com.midad_pos.model.ItemModel;
import com.midad_pos.model.ModifierModel;
import com.midad_pos.model.VariantModel;
import com.midad_pos.model.cart.CartList;
import com.midad_pos.model.cart.ManageCartModel;
import com.midad_pos.mvvm.HomeMvvm;
import com.midad_pos.share.App;
import com.midad_pos.uis.activity_charge.ChargeActivity;
import com.midad_pos.uis.activity_drawer_base.DrawerBaseActivity;
import com.midad_pos.uis.activity_items.ItemsActivity;
import com.midad_pos.uis.activity_shift.ShiftActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends DrawerBaseActivity {
    private HomeMvvm mvvm;
    private ActivityHomeBinding binding;
    private SpinnerCountryAdapter spinnerCountryAdapter;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private CodeScanner codeScanner;
    private ActivityResultLauncher<String> permissions;
    private HomeItemAdapter adapter;
    private HomeDiscountAdapter homeDiscountAdapter;
    private ItemMainModifierAdapter itemMainModifierAdapter;
    private ItemDiscountAdapter itemDiscountAdapter;
    private ItemVariantAdapter itemVariantAdapter;
    private CartAdapter cartAdapter;
    private CartDiscountAdapter cartDiscountAdapter;
    private CustomersAdapter customersAdapter;


    @Override
    protected void onStart() {
        super.onStart();
        if (binding.barcode != null) {
            if (getAppSetting() != null && getAppSetting().isScan()) {
                binding.barcode.setVisibility(View.VISIBLE);

            } else {
                binding.barcode.setVisibility(View.GONE);

            }


        }

        invalidateOptionsMenu();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_home, null, false);
        setContentView(binding.getRoot());
        setUpDrawer(binding.toolBarHomeLayout.toolBarHome, true);
        initView();
    }




    private void initView() {
        mvvm = ViewModelProviders.of(this).get(HomeMvvm.class);
        codeScanner = new CodeScanner(this, binding.scanCode);

        adapter = new HomeItemAdapter(this);
        adapter.updateType(1);

        homeDiscountAdapter = new HomeDiscountAdapter(this, getLang());

        permissions = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                if (mvvm.getCamera().getValue() != null) {
                    initCodeScanner(mvvm.getCamera().getValue());

                }
            }
        });

        baseMvvm.getOnUserRefreshed().observe(this,aBoolean -> {
            mvvm.loadHomeData();
        });
        binding.toolBarHomeLayout.setLang(getLang());

        spinnerCountryAdapter = new SpinnerCountryAdapter(this, getLang());
        binding.addCustomerDialog.addCustomerLayout.spinnerCountry.setAdapter(spinnerCountryAdapter);
        binding.addCustomerDialog.addCustomerLayout.spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        mvvm.getAppSettingModel().observe(this, this::updateShiftData);
        mvvm.getIsCustomerLoading().observe(this,isLoading-> binding.addCustomerDialog.searchDialog.loader.setVisibility(isLoading?View.VISIBLE:View.GONE));

        binding.addCustomerDialog.searchDialog.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.addCustomerDialog.searchDialog.recView.setHasFixedSize(true);
        customersAdapter = new CustomersAdapter(this);
        binding.addCustomerDialog.searchDialog.recView.setAdapter(customersAdapter);
        binding.addCustomerDialog.addCustomerLayout.btnSave.setOnClickListener(v -> mvvm.addCustomer(this));

        mvvm.getCustomersInstance().observe(this,customers->{
            binding.addCustomerDialog.searchDialog.tvNoCustomer.setVisibility(customers.size()>0?View.GONE:View.VISIBLE);
            if (customersAdapter!=null){
                customersAdapter.updateList(customers);
            }
        });



        if (mvvm.getIsScanOpened().getValue() != null && mvvm.getIsScanOpened().getValue() && mvvm.getCamera().getValue() != null) {
            initCodeScanner(mvvm.getCamera().getValue());
        }



        mvvm.getSelectedCategory().observe(this, selectedCategory -> {

            if (binding.toolBarHomeLayout.toolbarTitle != null) {
                binding.toolBarHomeLayout.toolbarTitle.setTitle(selectedCategory.getName());

            }

            if (binding.llSpinnerFilter != null) {
                binding.setTitle(selectedCategory.getName());
            }

            if (binding.recView != null) {
                binding.recView.setLayoutManager(new GridLayoutManager(this, 3));
                binding.recView.setHasFixedSize(true);
                if (selectedCategory.getId() == -2) {
                    binding.recView.setAdapter(homeDiscountAdapter);
                } else {
                    binding.recView.setAdapter(adapter);

                }
            }

            if (binding.recViewPort != null) {
                binding.recViewPort.setLayoutManager(new GridLayoutManager(this, 5));
                binding.recViewPort.setHasFixedSize(true);
                if (selectedCategory.getId() == -2) {
                    binding.recViewPort.setAdapter(homeDiscountAdapter);

                } else {
                    binding.recViewPort.setAdapter(adapter);

                }
            }

            if (binding.recViewLand != null) {
                binding.recViewLand.setLayoutManager(new GridLayoutManager(this, 5));
                binding.recViewLand.setHasFixedSize(true);
                if (selectedCategory.getId() == -2) {
                    binding.recViewLand.setAdapter(homeDiscountAdapter);

                } else {
                    binding.recViewLand.setAdapter(adapter);

                }
            }


        });

        mvvm.getSelectedDeliveryOptions().observe(this, deliveryOption ->{
            binding.setDeliveryOption(deliveryOption);

        } );

        mvvm.getTicketCount().observe(this, ticketCount -> {
            if (binding.toolBarHomeLayout.tvTicketCount != null) {
                binding.toolBarHomeLayout.setTicketCount(ticketCount);
            }
        });

        mvvm.getAddCustomerModel().observe(this, model -> binding.addCustomerDialog.addCustomerLayout.setModel(model));

        mvvm.getCountriesData().observe(this, countries -> {
            if (spinnerCountryAdapter != null) {
                spinnerCountryAdapter.updateList(countries);
            }
        });

        mvvm.getCountryPos().observe(this, pos -> binding.addCustomerDialog.addCustomerLayout.spinnerCountry.setSelection(pos));

        mvvm.getIsLoading().observe(this, isLoading -> {

            if (getAppSetting().getIsShiftOpen()==0){
                binding.llNoItems.setVisibility(View.GONE);
                binding.loader.setVisibility(View.GONE);

            }else {
                if (isLoading) {
                    binding.llNoItems.setVisibility(View.GONE);
                    binding.loader.setVisibility(View.VISIBLE);
                } else {
                    binding.loader.setVisibility(View.GONE);

                }
            }

        });

        mvvm.getItems().observe(this, items -> {

            if (adapter != null) {
                adapter.updateList(items);
            }
            if (items.size() > 0) {
                binding.llNoItems.setVisibility(View.GONE);
            } else {
                if (mvvm.getIsLoading().getValue() != null && !mvvm.getIsLoading().getValue()) {
                    if (getAppSetting().getIsShiftOpen()==0){
                        binding.llNoItems.setVisibility(View.GONE);

                    }else {
                        binding.llNoItems.setVisibility(View.VISIBLE);

                    }

                }
            }
        });

        mvvm.getDiscounts().observe(this, discounts -> {
            if (homeDiscountAdapter != null) {
                homeDiscountAdapter.updateList(discounts);
            }
        });

        mvvm.mainItemDiscountList.observe(this, discounts -> {
            if (itemDiscountAdapter != null && discounts != null) {
                List<DiscountModel> list = new ArrayList<>();

                for (DiscountModel discountModel : discounts) {
                    if (mvvm.getItemForPrice().getValue() != null) {
                        for (DiscountModel model : mvvm.getItemForPrice().getValue().getDiscounts()) {
                            if (discountModel.getId().equals(model.getId())) {
                                discountModel.setSelected(true);
                                break;
                            } else {
                                discountModel.setSelected(false);

                            }
                        }
                    } else {
                        discountModel.setSelected(false);

                    }
                    list.add(discountModel);
                }
                itemDiscountAdapter.updateList(list);
                if (list.size() == 0) {
                    binding.dialogItemExtras.llDiscount.setVisibility(View.GONE);
                }
            }
        });

        if (mvvm.getSearchQuery().getValue() != null && !mvvm.getSearchQuery().getValue().isEmpty()) {
            if (binding.llSearch != null && binding.edtSearch != null) {
                binding.llSearch.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> binding.edtSearch.setText(mvvm.getSearchQuery().getValue()), 100);
            }


        } else {
            if (binding.llSearch != null && binding.edtSearch != null) {
                binding.llSearch.setVisibility(View.GONE);
                binding.edtSearch.setText(null);


            }


        }

        mvvm.getPrice().observe(this, price -> binding.dialogAddPrice.setPrice(price));

        mvvm.getIsDialogPriceOpened().observe(this, isOpened -> {
            if (isOpened) {
                binding.flDialogAddPrice.setVisibility(View.VISIBLE);
            } else {
                mvvm.getPrice().setValue("0.00");
                binding.flDialogAddPrice.setVisibility(View.GONE);

            }
        });

        mvvm.getIsDialogDiscountsOpened().observe(this, isOpened -> {
            if (isOpened) {
                binding.flDialogCartDiscount.setVisibility(View.VISIBLE);
            } else {
                binding.flDialogCartDiscount.setVisibility(View.GONE);

            }
        });


        mvvm.getIsDialogExtrasOpened().observe(this, isOpened -> {
            if (isOpened) {
                if (mvvm.getItemForPrice().getValue() != null) {
                    binding.dialogItemExtras.setItem(mvvm.getItemForPrice().getValue());
                    itemMainModifierAdapter.updateList(mvvm.getItemForPrice().getValue().getModifiers());
                    binding.dialogItemExtras.edtComment.setText(mvvm.getItemForPrice().getValue().getComment());

                }
                if (itemDiscountAdapter != null && mvvm.mainItemDiscountList.getValue() != null) {
                    List<DiscountModel> list = new ArrayList<>(mvvm.mainItemDiscountList.getValue());
                    itemDiscountAdapter.updateList(list);
                    if (list.size() == 0) {
                        binding.dialogItemExtras.llDiscount.setVisibility(View.GONE);
                    } else {
                        binding.dialogItemExtras.llDiscount.setVisibility(View.VISIBLE);

                    }
                }

                if (itemVariantAdapter != null) {
                    itemVariantAdapter.updateList(mvvm.getItemForPrice().getValue().getVariants());
                }

                if (mvvm.getItemForPrice().getValue().getVariants().size() == 0) {
                    binding.dialogItemExtras.llVariants.setVisibility(View.GONE);
                } else {
                    binding.dialogItemExtras.llVariants.setVisibility(View.VISIBLE);

                }

                binding.flDialogItemExtras.setVisibility(View.VISIBLE);

            } else {
                binding.flDialogItemExtras.setVisibility(View.GONE);

            }
        });

        mvvm.getItemForPricePos().observe(this, pos -> {
            if (pos != -1 && mvvm.getItems().getValue() != null) {
                binding.dialogAddPrice.setItem(mvvm.getItems().getValue().get(pos));
            }
        });

        mvvm.getOnCustomerUpdatedSuccess().observe(this,aBoolean -> {
            binding.addCustomerDialog.flAddCustomerLayout.setVisibility(View.GONE);

        });
        binding.addCustomerDialog.addCustomerLayout.setLang(getLang());

        if (mvvm.getSearchQueryCustomer().getValue() != null) {
            binding.addCustomerDialog.searchDialog.llSearch.setVisibility(View.VISIBLE);
            binding.addCustomerDialog.searchDialog.edtSearch.setText(mvvm.getSearchQueryCustomer().getValue());

        }

        if (mvvm.getIsOpenedCustomerDialog().getValue() != null && mvvm.getIsOpenedCustomerDialog().getValue()) {
            binding.dialogCustomer.setVisibility(View.VISIBLE);
            if (mvvm.getIsAddCustomerDialogShow().getValue() != null && mvvm.getIsAddCustomerDialogShow().getValue()) {

                binding.addCustomerDialog.flAddCustomerLayout.setVisibility(View.VISIBLE);

            } else {
                binding.addCustomerDialog.flAddCustomerLayout.setVisibility(View.GONE);

            }
        }


        binding.addCustomerDialog.addCustomerLayout.arrow.setOnClickListener(view -> onBackPressed());

        binding.addCustomerDialog.searchDialog.addCustomer.setOnClickListener(view -> {
            binding.addCustomerDialog.flAddCustomerLayout.setVisibility(View.VISIBLE);
            mvvm.getAddCustomerModel().setValue(new AddCustomerModel());
            mvvm.getIsAddCustomerDialogShow().setValue(true);

        });

        if (binding.openSearch != null && binding.llSearch != null) {
            binding.openSearch.setOnClickListener(view -> binding.llSearch.setVisibility(View.VISIBLE));
        }


        if (binding.closeSearch != null && binding.edtSearch != null && binding.llSearch != null) {
            binding.closeSearch.setOnClickListener(view -> {
                binding.edtSearch.setText(null);
                binding.llSearch.setVisibility(View.GONE);


            });
        }

        if (binding.barcode != null) {
            if (getAppSetting() != null && getAppSetting().isScan()) {
                binding.barcode.setVisibility(View.VISIBLE);
            } else {
                binding.barcode.setVisibility(View.GONE);

            }
        }

        if (binding.toolBarHomeLayout.toolbarTitle != null && binding.toolBarHomeLayout.toolbarTitle.llSpinnerPort != null) {
            binding.toolBarHomeLayout.toolbarTitle.llSpinnerPort.setOnClickListener(view -> {
                if (mvvm.getCategories().getValue() != null && mvvm.getCategories().getValue().size() > 0) {
                    createFilterPopupMenu(view, mvvm.getCategories().getValue());

                }
            });
        }

        if (binding.toolBarHomeLayout.toolbarTitle != null && binding.toolBarHomeLayout.toolbarTitle.llSpinnerLand != null) {
            binding.toolBarHomeLayout.toolbarTitle.llSpinnerLand.setOnClickListener(view -> {
                if (mvvm.getCategories().getValue() != null && mvvm.getCategories().getValue().size() > 0) {
                    createFilterPopupMenu(view, mvvm.getCategories().getValue());

                }
            });
        }

        if (binding.toolBarHomeLand != null) {
            binding.setTitleLand(getString(R.string.ticket));
        }

        if (binding.toolbarLandMenu != null) {
            binding.toolbarLandMenu.setOnClickListener(this::createTicketOptionPopupMenu);
        }

        binding.llDelivery.setOnClickListener(view -> createDeliveryOptionPopupMenu(binding.tvDelivery));


        if (binding.llOpenDeliveryOptionPort != null && binding.llDeliveryPort != null && binding.arrowDownPort != null && binding.viewBg != null) {
            binding.llOpenDeliveryOptionPort.setOnClickListener(view -> {
                if (binding.llDeliveryPort.getVisibility() == View.VISIBLE) {
                    binding.llDeliveryPort.setVisibility(View.GONE);
                    binding.viewBg.setVisibility(View.GONE);
                    binding.arrowDownPort.animate().rotation(0).setDuration(100).start();
                } else {
                    binding.llDeliveryPort.setVisibility(View.VISIBLE);
                    binding.viewBg.setVisibility(View.VISIBLE);
                    binding.arrowDownPort.animate().rotation(180).setDuration(100).start();

                }
            });
        }

        if (binding.llSpinnerFilter != null && binding.llSpinner != null) {
            binding.llSpinnerFilter.setOnClickListener(view -> createFilterPopupMenu(binding.llSpinner, Objects.requireNonNull(mvvm.getCategories().getValue())));
        }

        if (binding.edtSearch != null) {
            Disposable d = Observable.create((ObservableOnSubscribe<String>) emitter -> binding.edtSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            emitter.onNext(editable.toString());
                        }
                    })).debounce(1, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((query -> mvvm.search(query)));
            disposable.add(d);
        }

        if (binding.toolbarLandAddUser != null) {
            binding.toolbarLandAddUser.setOnClickListener(view -> {
                mvvm.getIsOpenedCustomerDialog().setValue(true);
                binding.dialogCustomer.setVisibility(View.VISIBLE);
            });
        }

        itemMainModifierAdapter = new ItemMainModifierAdapter(this);
        itemDiscountAdapter = new ItemDiscountAdapter(this);
        itemVariantAdapter = new ItemVariantAdapter(this);

        binding.dialogItemExtras.recViewModifier.setLayoutManager(new LinearLayoutManager(this));
        binding.dialogItemExtras.recViewModifier.setHasFixedSize(true);
        binding.dialogItemExtras.recViewModifier.setAdapter(itemMainModifierAdapter);

        if (binding.dialogItemExtras.recViewDiscounts != null) {
            binding.dialogItemExtras.recViewDiscounts.setLayoutManager(new LinearLayoutManager(this));
            binding.dialogItemExtras.recViewDiscounts.setHasFixedSize(true);
            binding.dialogItemExtras.recViewDiscounts.setAdapter(itemDiscountAdapter);
        } else if (binding.dialogItemExtras.recViewDiscountsLand != null) {
            binding.dialogItemExtras.recViewDiscountsLand.setLayoutManager(new GridLayoutManager(this, 2));
            binding.dialogItemExtras.recViewDiscountsLand.setHasFixedSize(true);
            binding.dialogItemExtras.recViewDiscountsLand.setAdapter(itemDiscountAdapter);
        }

        if (binding.dialogItemExtras.recViewVariants != null) {
            binding.dialogItemExtras.recViewVariants.setLayoutManager(new LinearLayoutManager(this));
            binding.dialogItemExtras.recViewVariants.setHasFixedSize(true);
            binding.dialogItemExtras.recViewVariants.setAdapter(itemVariantAdapter);
        } else if (binding.dialogItemExtras.recViewVariantsLand != null) {
            binding.dialogItemExtras.recViewVariantsLand.setLayoutManager(new GridLayoutManager(this, 2));
            binding.dialogItemExtras.recViewVariantsLand.setHasFixedSize(true);
            binding.dialogItemExtras.recViewVariantsLand.setAdapter(itemVariantAdapter);
        }


        if (mvvm.getItemForPricePos().getValue() != null && mvvm.getItems().getValue() != null && mvvm.getItemForPricePos().getValue() != -1) {
            itemMainModifierAdapter.updateList(mvvm.getItems().getValue().get(mvvm.getItemForPricePos().getValue()).getModifiers());
        }


        binding.addCustomerDialog.searchDialog.closeCustomerDialog.setOnClickListener(view -> {
            binding.dialogCustomer.setVisibility(View.GONE);
            mvvm.getIsOpenedCustomerDialog().setValue(false);
        });

        binding.addCustomerDialog.searchDialog.tvOpenSearch.setOnClickListener(view -> binding.addCustomerDialog.searchDialog.llSearch.setVisibility(View.VISIBLE));

        binding.addCustomerDialog.searchDialog.closeSearch.setOnClickListener(view -> {
            if (mvvm.getSearchQueryCustomer().getValue() == null) {
                binding.addCustomerDialog.searchDialog.llSearch.setVisibility(View.GONE);

            } else {
                binding.addCustomerDialog.searchDialog.edtSearch.setText(null);

            }


        });

        binding.addCustomerDialog.searchDialog.edtSearch.addTextChangedListener(new TextWatcher() {
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

        binding.btnGoToItems.setOnClickListener(view -> navigation(ItemsActivity.class));

        binding.openShift.setOnClickListener(view -> navigation(ShiftActivity.class));

        if (binding.barcode != null && binding.llData != null) {
            binding.barcode.setOnClickListener(v -> {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                    if (mvvm.getCamera().getValue() != null) {
                        initCodeScanner(mvvm.getCamera().getValue());

                    }
                } else {
                    permissions.launch(Manifest.permission.CAMERA);
                }
            });


        }


        binding.flDialogAddPrice.setVisibility(View.VISIBLE);


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
            if (mvvm.getPrice().getValue() != null) {
                if (!mvvm.getPrice().getValue().isEmpty() && !mvvm.getPrice().getValue().equals("0") && !mvvm.getPrice().getValue().equals("0.00")) {
                    if (mvvm.getItems().getValue() != null && mvvm.getItemForPricePos().getValue() != null) {
                        ItemModel itemModel = mvvm.getItems().getValue().get(mvvm.getItemForPricePos().getValue());
                        itemModel.setPrice(mvvm.getPrice().getValue());
                        mvvm.getItemForPrice().setValue(itemModel);
                        adapter.notifyItemChanged(mvvm.getItemForPricePos().getValue());
                        mvvm.getIsDialogPriceOpened().setValue(false);


                        if (itemModel.getModifiers().size() > 0) {
                            mvvm.getIsDialogExtrasOpened().setValue(true);
                            binding.dialogItemExtras.setItem(itemModel);

                        } else if (itemModel.getVariants().size() > 0) {
                            mvvm.getIsDialogExtrasOpened().setValue(true);
                            binding.dialogItemExtras.setItem(itemModel);
                        } else {
                            mvvm.addCartItem(1);
                            scrollToLastItemCart();
                        }

                        mvvm.getPrice().setValue("0.00");
                        mvvm.getItemForPricePos().setValue(-1);

                    }

                }
            }

        });

        binding.dialogAddPrice.clear.setOnClickListener(v -> mvvm.removeLastPriceIndex());

        binding.dialogAddPrice.close.setOnClickListener(v -> {
            mvvm.getItemForPricePos().setValue(-1);
            mvvm.getIsDialogPriceOpened().setValue(false);
        });

        binding.dialogItemExtras.close.setOnClickListener(v -> {
            mvvm.getIsDialogExtrasOpened().setValue(false);
            mvvm.getItemForPricePos().setValue(-1);
            mvvm.getItemForPrice().setValue(null);
        });

        binding.dialogItemExtras.increase.setOnClickListener(v -> {
            ItemModel itemModel = mvvm.getItemForPrice().getValue();
            if (itemModel != null) {
                int amount = itemModel.getAmount();
                amount++;
                itemModel.setAmount(amount);
                itemModel.calculateTotal();
                mvvm.getItemForPrice().setValue(itemModel);
                binding.dialogItemExtras.setItem(itemModel);

            }
        });

        binding.dialogItemExtras.decrease.setOnClickListener(v -> {
            ItemModel itemModel = mvvm.getItemForPrice().getValue();
            if (itemModel != null) {
                int amount = itemModel.getAmount();
                if (amount > 0) {
                    amount--;
                    itemModel.setAmount(amount);
                    itemModel.calculateTotal();
                    mvvm.getItemForPrice().setValue(itemModel);
                    binding.dialogItemExtras.setItem(itemModel);
                }

            }
        });

        binding.dialogItemExtras.edtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ItemModel itemModel = mvvm.getItemForPrice().getValue();

                if (s.toString().isEmpty()) {
                    if (itemModel != null) {
                        itemModel.setAmount(0);
                        mvvm.getItemForPrice().setValue(itemModel);
                        binding.dialogItemExtras.setItem(itemModel);

                    }
                } else {
                    if (itemModel != null) {
                        try {
                            itemModel.setAmount(Integer.parseInt(s.toString()));
                            mvvm.getItemForPrice().setValue(itemModel);
                            binding.dialogItemExtras.setItem(itemModel);
                        } catch (NumberFormatException e) {
                            itemModel.setAmount(0);
                            mvvm.getItemForPrice().setValue(itemModel);
                            binding.dialogItemExtras.setItem(itemModel);
                        }
                    }

                }
            }
        });

        if (binding.toolBarHomeLayout.tvTicketCount != null && binding.toolBarHomeLayout.tv != null && binding.toolBarHomeLayout.arrowCloseTicket != null) {

            if (binding.llCartList != null && binding.llData != null) {
                if (mvvm.getIsTicketModel().getValue() != null) {
                    if (mvvm.getIsTicketModel().getValue()) {
                        binding.llCartList.setVisibility(View.VISIBLE);
                        binding.llData.setVisibility(View.GONE);
                        binding.toolBarHomeLayout.arrowCloseTicket.setVisibility(View.VISIBLE);
                        setUpDrawer(binding.toolBarHomeLayout.toolBarHome, false);
                    } else {
                        binding.llCartList.setVisibility(View.GONE);
                        binding.llData.setVisibility(View.VISIBLE);
                        binding.toolBarHomeLayout.arrowCloseTicket.setVisibility(View.GONE);

                        setUpDrawer(binding.toolBarHomeLayout.toolBarHome, true);
                    }
                }


            }
        }

        binding.recViewCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this);
        binding.recViewCart.setAdapter(cartAdapter);
        mvvm.getCart().observe(this, cartList -> {
            invalidateOptionsMenu();
            binding.toolBarHomeLayout.setTicketCount(cartList.getItemCounts());
            binding.setTotal(cartList.getTotalPrice());
            binding.setTotalDiscounts(cartList.getTotalDiscountValue());
            binding.setTotalVat(cartList.getTotalTaxPrice());
            if (binding.tvTicketCountPort != null) {
                binding.setTicketCount(cartList.getItemCounts());
            }

            if (cartAdapter != null) {
                cartAdapter.updateList(cartList.getItems());
            }

            if (binding.toolbarLandAddUser!=null){
                if (cartList.getCustomerModel()==null){
                    binding.toolbarLandAddUser.setImageResource(R.drawable.ic_add_user);

                }else {
                    binding.toolbarLandAddUser.setImageResource(R.drawable.ic_checked_user);

                }
            }


        });

        binding.dialogCartDiscount.recViewDiscounts.setLayoutManager(new LinearLayoutManager(this));
        binding.dialogCartDiscount.recViewDiscounts.setHasFixedSize(true);
        cartDiscountAdapter = new CartDiscountAdapter(this);
        binding.dialogCartDiscount.recViewDiscounts.setAdapter(cartDiscountAdapter);

        mvvm.getCartDiscounts().observe(this,discounts ->{
            if (cartDiscountAdapter!=null){
                cartDiscountAdapter.updateList(discounts);
            }
        } );


        if (binding.toolBarHomeLayout.tvTicketCount != null && binding.toolBarHomeLayout.tv != null && binding.toolBarHomeLayout.arrowCloseTicket != null) {
            binding.toolBarHomeLayout.tvTicketCount.setOnClickListener(v -> {
                if (binding.llCartList != null && binding.llData != null) {
                    if (binding.llCartList.getVisibility() == View.GONE && binding.llData.getVisibility() == View.VISIBLE) {
                        binding.llCartList.setVisibility(View.VISIBLE);
                        binding.llData.setVisibility(View.GONE);
                        binding.toolBarHomeLayout.arrowCloseTicket.setVisibility(View.VISIBLE);
                        setUpDrawer(binding.toolBarHomeLayout.toolBarHome, false);
                        mvvm.getIsTicketModel().setValue(true);
                    } else {
                        binding.llCartList.setVisibility(View.GONE);
                        binding.llData.setVisibility(View.VISIBLE);
                        binding.toolBarHomeLayout.arrowCloseTicket.setVisibility(View.GONE);

                        setUpDrawer(binding.toolBarHomeLayout.toolBarHome, true);
                        mvvm.getIsTicketModel().setValue(false);

                    }


                }
            });

            binding.toolBarHomeLayout.tv.setOnClickListener(v -> {
                if (binding.llCartList != null && binding.llData != null) {
                    if (binding.llCartList.getVisibility() == View.GONE && binding.llData.getVisibility() == View.VISIBLE) {
                        binding.llCartList.setVisibility(View.VISIBLE);
                        binding.llData.setVisibility(View.GONE);
                        binding.toolBarHomeLayout.arrowCloseTicket.setVisibility(View.VISIBLE);
                        setUpDrawer(binding.toolBarHomeLayout.toolBarHome, false);
                        mvvm.getIsTicketModel().setValue(true);
                    } else {
                        binding.llCartList.setVisibility(View.GONE);
                        binding.llData.setVisibility(View.VISIBLE);
                        binding.toolBarHomeLayout.arrowCloseTicket.setVisibility(View.GONE);

                        setUpDrawer(binding.toolBarHomeLayout.toolBarHome, true);
                        mvvm.getIsTicketModel().setValue(false);

                    }


                }
            });

            binding.toolBarHomeLayout.arrowCloseTicket.setOnClickListener(v -> {
                if (binding.llCartList != null && binding.llData != null) {
                    binding.llCartList.setVisibility(View.GONE);
                    binding.llData.setVisibility(View.VISIBLE);
                    binding.toolBarHomeLayout.arrowCloseTicket.setVisibility(View.GONE);

                    setUpDrawer(binding.toolBarHomeLayout.toolBarHome, true);
                    mvvm.getIsTicketModel().setValue(false);

                }
            });
        }

        binding.dialogItemExtras.save.setOnClickListener(v -> {
            String comment = binding.dialogItemExtras.edtComment.getText().toString().trim();

            if (mvvm.getItemForPrice().getValue()!=null){
                mvvm.getItemForPrice().getValue().setComment(comment);
            }

            mvvm.getIsDialogExtrasOpened().setValue(false);

            mvvm.getDiscounts().setValue(mvvm.mainItemDiscountList.getValue());
            mvvm.addCartItem(mvvm.isItemForUpdate ? 2 : 1);
            scrollToLastItemCart();
        });

        binding.dialogItemExtras.llVat.setOnClickListener(v -> {
            if (mvvm.getItemForPrice().getValue() != null) {
                mvvm.getItemForPrice().getValue().getTax().setSingleChecked(!mvvm.getItemForPrice().getValue().getTax().isSingleChecked());
                mvvm.getItemForPrice().setValue(mvvm.getItemForPrice().getValue());
                binding.dialogItemExtras.setItem(mvvm.getItemForPrice().getValue());
                if (mvvm.getCart().getValue() != null) {
                    CartList cartList = mvvm.getCart().getValue();
                    binding.toolBarHomeLayout.setTicketCount(cartList.getItemCounts());
                    binding.setTotal(cartList.getTotalPrice());

                    binding.setTotalDiscounts(cartList.getTotalDiscountValue());
                    binding.setTotalVat(cartList.getTotalTaxPrice());
                    if (binding.tvTicketCountPort != null) {
                        binding.setTicketCount(cartList.getItemCounts());
                    }
                }

            }
        });

        binding.llDiscount.setOnClickListener(v -> {
            if (mvvm.getCart().getValue() != null && mvvm.getCart().getValue().getDiscounts().size() > 0) {
                mvvm.getIsDialogDiscountsOpened().setValue(true);
                mvvm.getCartDiscount();

            }
        });

        binding.dialogCartDiscount.save.setOnClickListener(v -> {
            mvvm.deleteGeneralDiscountFromCart();
            mvvm.getIsDialogDiscountsOpened().setValue(false);
        });
        binding.dialogCartDiscount.close.setOnClickListener(v -> mvvm.getIsDialogDiscountsOpened().setValue(false));


        if (binding.llCharge!=null){
            binding.llCharge.setOnClickListener(v -> {

                if (mvvm.getCart().getValue() != null) {
                    CartList cartList = mvvm.getCart().getValue();
                    if (cartList.getTotalPrice()>0){
                        navigateToChargeActivity();
                    }
                }
            });
        }
        if (binding.charge!=null){
            binding.charge.setOnClickListener(v -> {
                if (mvvm.getCart().getValue() != null) {
                    CartList cartList = mvvm.getCart().getValue();
                    if (cartList.getTotalPrice()>0){
                        navigateToChargeActivity();
                    }
                }
            });
        }

    }

    private void updateShiftData(AppSettingModel appSetting) {

        if (getAppSetting().getIsShiftOpen()==0){
            binding.llOpenShift.setVisibility(View.VISIBLE);
            mvvm.clearCart();
            if (binding.recView!=null){
                binding.recView.setVisibility(View.GONE);
                binding.llNoItems.setVisibility(View.GONE);
            }

            if (binding.recViewLand!=null){
                binding.recViewLand.setVisibility(View.GONE);
                binding.llNoItems.setVisibility(View.GONE);

            }

            if (binding.recViewPort!=null){
                binding.recViewPort.setVisibility(View.GONE);
                binding.llNoItems.setVisibility(View.GONE);

            }
        }else if (getAppSetting().getIsShiftOpen()==1){
            binding.llOpenShift.setVisibility(View.GONE);
            if (binding.recView!=null){
                binding.recView.setVisibility(View.VISIBLE);

            }

            if (binding.recViewLand!=null){
                binding.recViewLand.setVisibility(View.VISIBLE);
            }

            if (binding.recViewPort!=null){
                binding.recViewPort.setVisibility(View.VISIBLE);
            }
        }
    }

    private void navigateToChargeActivity() {

        Intent intent = new Intent(this, ChargeActivity.class);
        startActivity(intent);

        if (getLang().equals("ar")) {
            overridePendingTransition(R.anim.from_right, R.anim.to_left);


        } else {
            overridePendingTransition(R.anim.from_left, R.anim.to_right);


        }
        mvvm.forNavigation = true;
        mvvm.showPin = false;
    }

    private void scrollToLastItemCart() {
        if (binding.nestedScrollViewLand != null) {
            binding.nestedScrollViewLand.getParent().requestChildFocus(binding.nestedScrollViewLand, binding.nestedScrollViewLand);
            binding.nestedScrollViewLand.scrollTo(0, binding.nestedScrollViewLand.getBottom() + 300);
            binding.nestedScrollViewLand.fling(binding.nestedScrollViewLand.getBottom() + 300);
            binding.nestedScrollViewLand.fullScroll(View.FOCUS_DOWN);
        }

    }

    private void initCodeScanner(int camera) {
        binding.flScanCode.setVisibility(View.VISIBLE);
        if (binding.llData != null) {
            binding.llData.setVisibility(View.GONE);

        }

        if (binding.toolBarHomeLayout.toolbarTitle != null && binding.toolBarHomeLayout.toolbarTitle.llSpinnerPort != null && binding.toolBarHomeLayout.tvScanTitle != null) {
            binding.toolBarHomeLayout.toolbarTitle.llSpinnerPort.setVisibility(View.GONE);
            binding.toolBarHomeLayout.tvScanTitle.setVisibility(View.VISIBLE);
        }

        if (binding.toolBarHomeLayout.toolbarTitle != null && binding.toolBarHomeLayout.toolbarTitle.llSpinnerLand != null && binding.toolBarHomeLayout.tvScanTitle != null) {
            binding.toolBarHomeLayout.toolbarTitle.llSpinnerLand.setVisibility(View.GONE);
            binding.toolBarHomeLayout.tvScanTitle.setVisibility(View.VISIBLE);
        }

        binding.toolBarHomeLayout.arrowCloseScan.setVisibility(View.VISIBLE);
        mvvm.getIsScanOpened().setValue(true);
        mvvm.getCamera().setValue(camera);
        invalidateOptionsMenu();
        binding.toolBarHomeLayout.arrowCloseScan.setOnClickListener(v -> {
            if (codeScanner != null) {
                binding.toolBarHomeLayout.arrowCloseScan.setVisibility(View.GONE);
                codeScanner.releaseResources();
                binding.flScanCode.setVisibility(View.GONE);
                if (binding.llData != null) {
                    binding.llData.setVisibility(View.VISIBLE);

                }
                if (binding.toolBarHomeLayout.toolbarTitle != null && binding.toolBarHomeLayout.toolbarTitle.llSpinnerPort != null && binding.toolBarHomeLayout.tvScanTitle != null) {
                    binding.toolBarHomeLayout.toolbarTitle.llSpinnerPort.setVisibility(View.VISIBLE);
                    binding.toolBarHomeLayout.tvScanTitle.setVisibility(View.GONE);
                }
                if (binding.toolBarHomeLayout.toolbarTitle != null && binding.toolBarHomeLayout.toolbarTitle.llSpinnerLand != null && binding.toolBarHomeLayout.tvScanTitle != null) {
                    binding.toolBarHomeLayout.toolbarTitle.llSpinnerLand.setVisibility(View.VISIBLE);
                    binding.toolBarHomeLayout.tvScanTitle.setVisibility(View.GONE);
                }
                setUpDrawer(binding.toolBarHomeLayout.toolBarHome, true);
                mvvm.getIsScanOpened().setValue(false);
                invalidateOptionsMenu();


            }
        });
        setUpDrawer(binding.toolBarHomeLayout.toolBarHome, false);

        codeScanner.setCamera(camera);
        codeScanner.setDecodeCallback(result -> runOnUiThread(() -> {

        }));

        codeScanner.startPreview();


    }

    private void createFilterPopupMenu(View view, List<CategoryModel> categories) {
        int pos = 0;
        PopupMenu popupMenu = new PopupMenu(this, view);
        for (CategoryModel categoryModel : categories) {
            popupMenu.getMenu().add(1, pos, 1, categoryModel.getName());
            pos += 1;
        }


        popupMenu.setOnMenuItemClickListener(item -> {
            mvvm.getSelectedCategory().setValue(Objects.requireNonNull(mvvm.getCategories().getValue()).get(item.getItemId()));
            mvvm.search(mvvm.getSearchQuery().getValue());
            return true;
        });

        popupMenu.show();
    }


    private void createDeliveryOptionPopupMenu(View view) {
        if (mvvm.getDeliveryOptions().getValue()!=null){
            PopupMenu popupMenu = new PopupMenu(this, view);
            int pos = 0;

            for (DeliveryModel deliveryModel : mvvm.getDeliveryOptions().getValue()) {
                popupMenu.getMenu().add(1, pos, 1, deliveryModel.getName());
                pos++;
            }

            popupMenu.setOnMenuItemClickListener(item -> {
               if (mvvm.getDeliveryOptions().getValue()!=null){
                   DeliveryModel deliveryModel = mvvm.getDeliveryOptions().getValue().get(item.getItemId());
                   mvvm.getSelectedDeliveryOptions().setValue(deliveryModel);
                   mvvm.addDeliveryOption(deliveryModel.getId(),deliveryModel.getName());
               }
                return true;
            });
            popupMenu.show();
        }


    }

    @SuppressLint("RestrictedApi")
    private void createTicketOptionPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.home_ticket_options_menu);
        MenuPopupHelper menuHelper = new MenuPopupHelper(this, (MenuBuilder) popupMenu.getMenu(), view);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();

        if (mvvm.getCart().getValue() != null && mvvm.getCart().getValue().getItems().size() > 0) {
            popupMenu.getMenu().findItem(R.id.clear).setEnabled(true);

        } else {
            popupMenu.getMenu().findItem(R.id.clear).setEnabled(false);

        }
        popupMenu.getMenu().findItem(R.id.edit).setEnabled(false);
        popupMenu.getMenu().findItem(R.id.assign).setEnabled(false);
        popupMenu.getMenu().findItem(R.id.merge).setEnabled(false);
        popupMenu.getMenu().findItem(R.id.split).setEnabled(false);

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.clear) {
                showDialogClearCart();
            }
            return true;
        });


    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_search_menu, menu);

        MenuItem menuItemMenu1 = menu.findItem(R.id.clear);
        MenuItem menuItemMenu2 = menu.findItem(R.id.edit);
        MenuItem menuItemMenu3 = menu.findItem(R.id.assign);
        MenuItem menuItemMenu4 = menu.findItem(R.id.merge);
        MenuItem menuItemMenu5 = menu.findItem(R.id.split);

        menuItemMenu2.setEnabled(false);
        menuItemMenu3.setEnabled(false);
        menuItemMenu4.setEnabled(false);
        menuItemMenu5.setEnabled(false);

        if (mvvm.getCart().getValue() != null && mvvm.getCart().getValue().getItems().size() > 0) {
            menuItemMenu1.setEnabled(true);
        } else {
            menuItemMenu1.setEnabled(false);

        }


        for (int index = 0; index < menu.size(); index++) {
            MenuItem item = menu.getItem(index);
            if (item.getItemId() != R.id.search && item.getItemId() != R.id.addUser && item.getItemId() != R.id.barcode && item.getItemId() != R.id.cameraSwitch) {

                if (item.isEnabled()) {
                    SpannableString spannable = new SpannableString(item.getTitle());
                    spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, spannable.length(), 0);
                    item.setTitle(spannable);
                    item.getIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);

                } else {

                    SpannableString spannable = new SpannableString(item.getTitle());
                    spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.grey4)), 0, spannable.length(), 0);
                    item.setTitle(spannable);
                    item.getIcon().setColorFilter(getResources().getColor(R.color.grey4), PorterDuff.Mode.SRC_IN);

                }

            }
        }

        if (menu instanceof MenuBuilder) {
            MenuBuilder menuBuilder = (MenuBuilder) menu;
            menuBuilder.setOptionalIconsVisible(true);
        }
        MenuItem menuItemSearch = menu.findItem(R.id.search);
        MenuItem menuItemAddUser = menu.findItem(R.id.addUser);
        MenuItem menuItemBarCode = menu.findItem(R.id.barcode);
        MenuItem menuItemCameraSwitch = menu.findItem(R.id.cameraSwitch);
        menuItemCameraSwitch.setVisible(false);

        if(mvvm.getCart().getValue()!=null){
            if (mvvm.getCart().getValue().getCustomerModel()!=null){

                menuItemAddUser.setIcon(R.drawable.ic_checked_user);

            }else {

                menuItemAddUser.setIcon(R.drawable.ic_add_user);

            }

        }


        menuItemSearch.getIcon().setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        SearchView searchView = (SearchView) menuItemSearch.getActionView();
        searchView.setQueryHint(getString(R.string.search));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextFocusChangeListener((view, isOpened) -> {


            if (isOpened) {
                menuItemMenu1.setVisible(false);
                menuItemMenu2.setVisible(false);
                menuItemMenu3.setVisible(false);
                menuItemMenu4.setVisible(false);
                menuItemMenu5.setVisible(false);
                menuItemAddUser.setVisible(false);
                menuItemBarCode.setVisible(false);

                if (binding.toolBarHomeLayout.toolbarTitle != null && binding.toolBarHomeLayout.toolbarTitle.llSpinnerLand != null) {
                    binding.toolBarHomeLayout.toolbarTitle.llSpinnerLand.setVisibility(View.GONE);

                }


            } else {

                if (binding.toolBarHomeLayout.toolbarTitle != null && binding.toolBarHomeLayout.toolbarTitle.llSpinnerPort != null) {
                    menuItemMenu1.setVisible(true);
                    menuItemMenu2.setVisible(true);
                    menuItemMenu3.setVisible(true);
                    menuItemMenu4.setVisible(true);
                    menuItemMenu5.setVisible(true);
                    menuItemAddUser.setVisible(true);
                    menuItemSearch.setVisible(true);
                    if (getAppSetting() != null && getAppSetting().isScan()) {
                        menuItemBarCode.setVisible(true);
                    }


                } else if (binding.toolBarHomeLayout.toolbarTitle != null && binding.toolBarHomeLayout.toolbarTitle.llSpinnerLand != null) {
                    menuItemMenu1.setVisible(false);
                    menuItemMenu2.setVisible(false);
                    menuItemMenu3.setVisible(false);
                    menuItemMenu4.setVisible(false);
                    menuItemMenu5.setVisible(false);

                    menuItemAddUser.setVisible(false);
                    menuItemSearch.setVisible(true);
                    if (getAppSetting() != null && getAppSetting().isScan()) {
                        menuItemBarCode.setVisible(true);
                    }
                    binding.toolBarHomeLayout.toolbarTitle.llSpinnerLand.setVisibility(View.VISIBLE);

                } else {
                    menuItemMenu1.setVisible(true);
                    menuItemMenu2.setVisible(true);
                    menuItemMenu3.setVisible(true);
                    menuItemMenu4.setVisible(true);
                    menuItemMenu5.setVisible(true);
                    menuItemAddUser.setVisible(true);
                }

            }
        });
        searchView.setQuery(mvvm.getSearchQuery().getValue(), false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (binding.toolBarHomeLayout.toolbarTitle != null) {
                    mvvm.search(newText);

                }

                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            mvvm.search("");
            return false;
        });


        searchView.setIconified(mvvm.getSearchQuery().getValue() != null && mvvm.getSearchQuery().getValue().isEmpty());


        if (mvvm.getIsScanOpened().getValue() != null && mvvm.getIsScanOpened().getValue()) {
            menuItemCameraSwitch.setVisible(true);
            if (binding.toolBarHomeLayout.toolbarTitle != null) {

                if (binding.toolBarHomeLayout.toolbarTitle.llSpinnerPort != null) {
                    menuItemAddUser.setVisible(true);
                    menuItemBarCode.setVisible(false);
                    menuItemSearch.setVisible(false);
                } else if (binding.toolBarHomeLayout.toolbarTitle.llSpinnerLand != null) {
                    menuItemAddUser.setVisible(false);
                    menuItemBarCode.setVisible(false);
                    menuItemSearch.setVisible(false);
                    menuItemMenu1.setVisible(false);
                    menuItemMenu2.setVisible(false);
                    menuItemMenu3.setVisible(false);
                    menuItemMenu4.setVisible(false);
                    menuItemMenu5.setVisible(false);
                }
            } else {
                menuItemAddUser.setVisible(false);
                menuItemSearch.setVisible(false);
                menuItemBarCode.setVisible(false);

            }


        } else {
            menuItemCameraSwitch.setVisible(false);

            if (binding.toolBarHomeLayout.toolbarTitle != null) {

                if (binding.toolBarHomeLayout.toolbarTitle.llSpinnerPort != null) {
                    menuItemMenu1.setVisible(true);
                    menuItemMenu2.setVisible(true);
                    menuItemMenu3.setVisible(true);
                    menuItemMenu4.setVisible(true);
                    menuItemMenu5.setVisible(true);

                    menuItemAddUser.setVisible(true);
                    menuItemSearch.setVisible(true);
                    menuItemBarCode.setVisible(getAppSetting() != null && getAppSetting().isScan());

                } else if (binding.toolBarHomeLayout.toolbarTitle.llSpinnerLand != null) {
                    menuItemMenu1.setVisible(false);
                    menuItemMenu2.setVisible(false);
                    menuItemMenu3.setVisible(false);
                    menuItemMenu4.setVisible(false);
                    menuItemMenu5.setVisible(false);
                    menuItemAddUser.setVisible(false);
                    menuItemBarCode.setVisible(getAppSetting() != null && getAppSetting().isScan());

                } else {
                    menuItemBarCode.setVisible(false);
                }


            } else {
                menuItemMenu1.setVisible(true);
                menuItemMenu2.setVisible(true);
                menuItemMenu3.setVisible(true);
                menuItemMenu4.setVisible(true);
                menuItemMenu5.setVisible(true);
                menuItemAddUser.setVisible(true);
                menuItemSearch.setVisible(false);
                menuItemBarCode.setVisible(false);


            }
        }


        return true;

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.clear) {
            showDialogClearCart();

            return true;
        } else if (item.getItemId() == R.id.addUser) {
            binding.dialogCustomer.setVisibility(View.VISIBLE);
            mvvm.getIsOpenedCustomerDialog().setValue(true);

            return true;
        } else if (item.getItemId() == R.id.cameraSwitch) {
            if (mvvm.getCamera().getValue() != null) {
                if (mvvm.getCamera().getValue() == CodeScanner.CAMERA_FRONT) {
                    initCodeScanner(CodeScanner.CAMERA_BACK);

                } else {
                    initCodeScanner(CodeScanner.CAMERA_FRONT);

                }
            }
            return true;
        } else if (item.getItemId() == R.id.barcode) {
            if (mvvm.getCamera().getValue() != null) {
                initCodeScanner(mvvm.getCamera().getValue());

            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

    public void setItemData(ItemModel model, int adapterPosition) {
        mvvm.isItemForUpdate = false;
        List<VariantModel> variants = new ArrayList<>();
        List<ModifierModel> modifierList = new ArrayList<>();
        for (VariantModel variantModel : model.getVariants()) {
            variantModel.setSelected(false);
            variants.add(variantModel);
        }
        if (variants.size() > 0) {
            variants.get(0).setSelected(true);

        }
        for (ModifierModel modifierModel : model.getModifiers()) {
            List<ModifierModel.Data> dataa = new ArrayList<>();
            for (ModifierModel.Data data : modifierModel.getModifiers_data()) {
                data.setSelected(false);
                dataa.add(data);
            }
            modifierModel.setModifiers_data(dataa);
            modifierList.add(modifierModel);
        }
        ItemModel itemModel = new ItemModel(model.getId(), model.getType(), model.getName(), model.getImage(), model.getImage_type(), model.getColor(), model.getShape(), model.getPrice(), model.getCost(), model.getCode(), model.getBarcode_symbology(), model.getBrand_id(), model.getBrand(), model.getCategory_id(), model.getCategory(), model.isIs_variant(), model.getQty(), variants, modifierList, model.getTax(), model.isSelected(), 0.0, null, new ArrayList<>(), 1, new ArrayList<>());
        if (variants.size() > 0) {
            itemModel.setSelectedVariant(variants.get(0));
        }

        if (itemModel.getVariants().size() > 0) {
            itemModel.calculateTotal();
            mvvm.mainItemDiscountList.setValue(mvvm.mainItemDiscountList.getValue());
            mvvm.getItemForPrice().setValue(itemModel);
            mvvm.getItemForPricePos().setValue(adapterPosition);
            mvvm.getIsDialogExtrasOpened().setValue(true);

        } else if (itemModel.getModifiers().size() > 0) {
            if (itemModel.getPrice().isEmpty() || itemModel.getPrice().equals("0") || itemModel.getPrice().equals("0.00")) {
                itemModel.calculateTotal();
                mvvm.mainItemDiscountList.setValue(mvvm.mainItemDiscountList.getValue());
                mvvm.getItemForPrice().setValue(itemModel);
                mvvm.getItemForPricePos().setValue(adapterPosition);

                mvvm.getIsDialogPriceOpened().setValue(true);

            } else {
                itemModel.calculateTotal();
                mvvm.mainItemDiscountList.setValue(mvvm.mainItemDiscountList.getValue());
                mvvm.getItemForPrice().setValue(itemModel);
                mvvm.getItemForPricePos().setValue(adapterPosition);
                mvvm.getIsDialogExtrasOpened().setValue(true);

            }
        } else if (itemModel.getPrice().isEmpty() || itemModel.getPrice().equals("0") || itemModel.getPrice().equals("0.00")) {
            itemModel.calculateTotal();
            mvvm.mainItemDiscountList.setValue(mvvm.mainItemDiscountList.getValue());
            mvvm.getItemForPrice().setValue(itemModel);
            mvvm.getItemForPricePos().setValue(adapterPosition);
            mvvm.getIsDialogPriceOpened().setValue(true);

        } else {

            mvvm.mainItemDiscountList.setValue(mvvm.mainItemDiscountList.getValue());
            itemModel.calculateTotal();
            mvvm.getItemForPrice().setValue(itemModel);
            mvvm.getItemForPricePos().setValue(adapterPosition);
            mvvm.addCartItem(1);
            scrollToLastItemCart();
        }


    }

    public void setItemModifier(int mainPos, int adapterPosition, ModifierModel.Data data) {
        ItemModel itemModel = mvvm.getItemForPrice().getValue();
        if (itemModel != null) {
            List<ModifierModel> modifiers = itemModel.getModifiers();
            List<ModifierModel.Data> modifiers_data = modifiers.get(mainPos).getModifiers_data();
            modifiers_data.set(adapterPosition, data);
            itemModel.setModifiers(modifiers);


            if (data.isSelected()) {
                itemModel.addModifier(data);
            } else {
                itemModel.removeModifier(data);
            }
            binding.dialogItemExtras.setItem(itemModel);
            mvvm.getItemForPrice().setValue(itemModel);


        }

    }

    public void setItemVariant(VariantModel model) {
        ItemModel itemModel = mvvm.getItemForPrice().getValue();
        if (itemModel != null) {
            itemModel.setSelectedVariant(model);
            itemModel.setPrice(model.getPrice());
            itemModel.calculateTotal();
            mvvm.getItemForPrice().setValue(itemModel);
            binding.dialogItemExtras.setItem(mvvm.getItemForPrice().getValue());
        }
    }

    public void addDiscountToCart(DiscountModel model) {
        ItemModel itemModel = mvvm.getItemForPrice().getValue();
        if (itemModel != null) {
            itemModel.addDiscount(model);
        }
        mvvm.getItemForPrice().setValue(itemModel);
    }

    public void deleteItemFromCart(int adapterPosition) {
        mvvm.removeItemFromCart(adapterPosition);
        cartAdapter.notifyItemRemoved(adapterPosition);
        invalidateOptionsMenu();

    }

    public void addDiscountForAll(DiscountModel model, int pos) {
        if (mvvm.getCart().getValue() != null) {
            if (mvvm.getDiscounts().getValue() != null) {
                mvvm.getDiscounts().getValue().get(pos).setSelected(model.isSelected());
            }
            CartList cartList = mvvm.getCart().getValue();
            cartList.addDiscountForAll(model);

            mvvm.addDiscountForAllTicket(model);
            mvvm.getCart().setValue(cartList);
        }
    }

    public void updateItemCart(ItemModel model) {
        mvvm.getItemForPrice().setValue(model);
        mvvm.getIsDialogExtrasOpened().setValue(true);
        mvvm.isItemForUpdate = true;
        mvvm.mainItemDiscountList.setValue(mvvm.mainItemDiscountList.getValue());


    }

    private void showDialogClearCart(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        DialogClearTicketLayoutBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this),R.layout.dialog_clear_ticket_layout,null,false);
        dialog.setView(dialogBinding.getRoot());
        dialogBinding.cancel.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.clear.setOnClickListener(v -> {
            mvvm.clearCart();
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (binding.dialogCustomer.getVisibility() == View.VISIBLE) {
            if (binding.addCustomerDialog.flAddCustomerLayout.getVisibility() == View.VISIBLE) {
                binding.addCustomerDialog.flAddCustomerLayout.setVisibility(View.GONE);
                mvvm.getIsAddCustomerDialogShow().setValue(false);
            } else {
                binding.dialogCustomer.setVisibility(View.GONE);
                mvvm.getIsOpenedCustomerDialog().setValue(false);
            }


        } else if (binding.flDialogAddPrice.getVisibility() == View.VISIBLE) {

            mvvm.getIsDialogPriceOpened().setValue(false);
        } else if (binding.flDialogItemExtras.getVisibility() == View.VISIBLE) {
            mvvm.getIsDialogExtrasOpened().setValue(false);
        } else if (binding.flDialogCartDiscount.getVisibility() == View.VISIBLE) {
            mvvm.getIsDialogDiscountsOpened().setValue(false);
        } else {
            super.onBackPressed();
        }

    }

    public void deleteCartDiscountItem(int adapterPosition) {
        mvvm.deleteCartDiscountItem(adapterPosition);
    }

    public void assignCustomerToCart(CustomerModel customerModel) {
        mvvm.assignCustomerToCart(customerModel);
        binding.dialogCustomer.setVisibility(View.GONE);
        mvvm.getIsOpenedCustomerDialog().setValue(false);
        invalidateOptionsMenu();
        if (binding.toolbarLandAddUser!=null){
            binding.toolbarLandAddUser.setImageResource(R.drawable.ic_checked_user);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSelectedPos(0);
        updateShiftData(getAppSetting());

        if (adapter != null) {
            adapter.updateType(getAppSetting().getHome_layout_type());
        }

        if (getAppSetting().getHome_layout_type() == 1) {
            if (binding.recView != null) {
                binding.recView.setLayoutManager(new GridLayoutManager(this, 3));
            }

            if (binding.recViewPort != null) {
                binding.recViewPort.setLayoutManager(new GridLayoutManager(this, 5));

            }

            if (binding.recViewLand != null) {
                binding.recViewLand.setLayoutManager(new GridLayoutManager(this, 5));

            }
        }
        else {
            if (binding.recView != null) {
                binding.recView.setLayoutManager(new LinearLayoutManager(this));
            }

            if (binding.recViewPort != null) {
                binding.recViewPort.setLayoutManager(new LinearLayoutManager(this));

            }

            if (binding.recViewLand != null) {
                binding.recViewLand.setLayoutManager(new LinearLayoutManager(this));

            }
        }
        if (mvvm.getIsScanOpened().getValue() != null && mvvm.getIsScanOpened().getValue() && mvvm.getCamera().getValue() != null) {
            initCodeScanner(mvvm.getCamera().getValue());
        }

        if (App.showHomePin){
            showPinCodeView();

        }else {
            if (App.saleSelected){
                hidePinCodeView();


            }else {
                if (mvvm.showPin) {
                    showPinCodeView();


                } else {
                    hidePinCodeView();
                }
            }

        }

        mvvm.loadHomeData();
        App.showHomePin = true;


    }


    @Override
    protected void onPause() {
        if (codeScanner != null) {
            codeScanner.releaseResources();
        }

        super.onPause();


    }

    @Override
    protected void onStop() {
        super.onStop();
        mvvm.showPin = true;

    }




    @Override
    protected void onDestroy() {
        mvvm.showPin = false;
        super.onDestroy();
        disposable.clear();

    }



}