package com.midad_pos.uis.activity_home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.budiyev.android.codescanner.CodeScanner;
import com.midad_pos.R;
import com.midad_pos.adapter.SpinnerCountryAdapter;
import com.midad_pos.databinding.ActivityHomeBinding;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.CountryModel;
import com.midad_pos.mvvm.BaseMvvm;
import com.midad_pos.mvvm.HomeMvvm;
import com.midad_pos.uis.activity_drawer_base.DrawerBaseActivity;
import com.midad_pos.uis.activity_items.ItemsActivity;

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
    private BaseMvvm baseMvvm;
    private ActivityHomeBinding binding;
    private SpinnerCountryAdapter spinnerCountryAdapter;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private CodeScanner codeScanner;
    private ActivityResultLauncher<String> permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_home, null, false);
        setContentView(binding.getRoot());
        setUpDrawer(binding.toolBarHomeLayout.toolBarHome, true);
        updateSelectedPos(0);
        showPinCodeView();
        initView();
    }

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


    private void initView() {
        baseMvvm = ViewModelProviders.of(this).get(BaseMvvm.class);
        mvvm = ViewModelProviders.of(this).get(HomeMvvm.class);
        codeScanner = new CodeScanner(this, binding.scanCode);


        permissions = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                if (mvvm.getCamera().getValue() != null) {
                    initCodeScanner(mvvm.getCamera().getValue());

                }
            }
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

        if (mvvm.getIsScanOpened().getValue() != null && mvvm.getIsScanOpened().getValue() && mvvm.getCamera().getValue() != null) {
            initCodeScanner(mvvm.getCamera().getValue());
        }
        baseMvvm.getOnUserRefreshed().observe(this,aBoolean -> {

        });
        mvvm.getSelectedCategory().observe(this, selectedCategory -> {
            if (binding.toolBarHomeLayout.toolbarTitle != null) {
                binding.toolBarHomeLayout.toolbarTitle.setTitle(selectedCategory.getName());

            }

            if (binding.llSpinnerFilter != null) {
                binding.setTitle(selectedCategory.getName());
            }
        });

        mvvm.getSelectedDeliveryOptions().observe(this, deliveryOption -> binding.setDeliveryOption(deliveryOption));

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

        if (binding.llDelivery != null && binding.tvDelivery != null) {
            binding.llDelivery.setOnClickListener(view -> createDeliveryOptionPopupMenu(binding.tvDelivery));
        }

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
                    .distinct()
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
                } else {
                    mvvm.getSearchQueryCustomer().setValue(editable.toString());
                }
            }
        });

        binding.btnGoToItems.setOnClickListener(view -> navigation(ItemsActivity.class));
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

        mvvm.getCategoryData(getUserModel().getData().getSelectedUser().getId());
    }

    private void initCodeScanner(int camera) {
        binding.flScanCode.setVisibility(View.VISIBLE);
        if (binding.llData != null ){
            binding.llData.setVisibility(View.GONE);

        }

        if (binding.toolBarHomeLayout.toolbarTitle!=null&&binding.toolBarHomeLayout.toolbarTitle.llSpinnerPort!=null&&binding.toolBarHomeLayout.tvScanTitle!=null){
            binding.toolBarHomeLayout.toolbarTitle.llSpinnerPort.setVisibility(View.GONE);
            binding.toolBarHomeLayout.tvScanTitle.setVisibility(View.VISIBLE);
        }

        if (binding.toolBarHomeLayout.toolbarTitle!=null&&binding.toolBarHomeLayout.toolbarTitle.llSpinnerLand!=null&&binding.toolBarHomeLayout.tvScanTitle!=null){
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
                if (binding.llData != null ){
                    binding.llData.setVisibility(View.VISIBLE);

                }
                if (binding.toolBarHomeLayout.toolbarTitle!=null&&binding.toolBarHomeLayout.toolbarTitle.llSpinnerPort!=null&&binding.toolBarHomeLayout.tvScanTitle!=null){
                    binding.toolBarHomeLayout.toolbarTitle.llSpinnerPort.setVisibility(View.VISIBLE);
                    binding.toolBarHomeLayout.tvScanTitle.setVisibility(View.GONE);
                }
                if (binding.toolBarHomeLayout.toolbarTitle!=null&&binding.toolBarHomeLayout.toolbarTitle.llSpinnerLand!=null&&binding.toolBarHomeLayout.tvScanTitle!=null){
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
            if (item.getItemId() == 0) {
                mvvm.getSelectedCategory().setValue(Objects.requireNonNull(mvvm.getCategories().getValue()).get(0));
            } else if (item.getItemId() == 1) {
                mvvm.getSelectedCategory().setValue(Objects.requireNonNull(mvvm.getCategories().getValue()).get(1));

            }else {
                mvvm.getSelectedCategory().setValue(Objects.requireNonNull(mvvm.getCategories().getValue()).get(item.getItemId()));
            }
            return true;
        });

        popupMenu.show();
    }


    private void createDeliveryOptionPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.home_delivery_options_menu);
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.inPlace) {
                mvvm.getSelectedDeliveryOptions().setValue(getString(R.string.in_place));
            } else if (item.getItemId() == R.id.takeAway) {
                mvvm.getSelectedDeliveryOptions().setValue(getString(R.string.takeaway));

            } else if (item.getItemId() == R.id.delivery) {
                mvvm.getSelectedDeliveryOptions().setValue(getString(R.string.delivery));

            }
            return true;
        });
        popupMenu.show();

    }

    @SuppressLint("RestrictedApi")
    private void createTicketOptionPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.home_ticket_options_menu);
        MenuPopupHelper menuHelper = new MenuPopupHelper(this, (MenuBuilder) popupMenu.getMenu(), view);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();

        popupMenu.getMenu().findItem(R.id.clear).setEnabled(false);
        popupMenu.getMenu().findItem(R.id.edit).setEnabled(false);
        popupMenu.getMenu().findItem(R.id.assign).setEnabled(false);
        popupMenu.getMenu().findItem(R.id.merge).setEnabled(false);
        popupMenu.getMenu().findItem(R.id.split).setEnabled(false);


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


        for (int index = 0; index < menu.size(); index++) {
            MenuItem item = menu.getItem(index);
            if (item.getItemId() != R.id.search && item.getItemId() != R.id.addUser && item.getItemId() != R.id.barcode && item.getItemId() != R.id.cameraSwitch) {
                SpannableString spannable = new SpannableString(item.getTitle());
                spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.grey4)), 0, spannable.length(), 0);
                item.setTitle(spannable);
                item.getIcon().setColorFilter(getResources().getColor(R.color.grey4), PorterDuff.Mode.SRC_IN);

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
                }else if (binding.toolBarHomeLayout.toolbarTitle.llSpinnerLand != null){
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
            return true;
        } else if (item.getItemId() == R.id.addUser) {
            binding.dialogCustomer.setVisibility(View.VISIBLE);
            mvvm.getIsOpenedCustomerDialog().setValue(true);

            return true;
        }else if (item.getItemId() == R.id.cameraSwitch) {
            if (mvvm.getCamera().getValue()!=null){
                if (mvvm.getCamera().getValue()==CodeScanner.CAMERA_FRONT){
                    initCodeScanner(CodeScanner.CAMERA_BACK);

                }else {
                    initCodeScanner(CodeScanner.CAMERA_FRONT);

                }
            }
            return true;
        }else if (item.getItemId() == R.id.barcode) {
            if (mvvm.getCamera().getValue()!=null){
                initCodeScanner(mvvm.getCamera().getValue());

            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        updateSelectedPos(0);
        if (mvvm.getIsScanOpened().getValue() != null && mvvm.getIsScanOpened().getValue() && mvvm.getCamera().getValue() != null) {
            initCodeScanner(mvvm.getCamera().getValue());
        }
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


        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onPause() {
        if (codeScanner != null) {
            codeScanner.releaseResources();
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }


}