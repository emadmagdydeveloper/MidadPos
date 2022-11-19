package com.midad_pos.uis.activity_add_printer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityAddDiscountBinding;
import com.midad_pos.databinding.ActivityAddPrinterBinding;
import com.midad_pos.model.DiscountModel;
import com.midad_pos.mvvm.AddDiscountMvvm;
import com.midad_pos.mvvm.AddPrinterMvvm;
import com.midad_pos.print_utils.PrintUtils;
import com.midad_pos.uis.activity_base.BaseActivity;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AddPrinterActivity extends BaseActivity {
    private AddPrinterMvvm mvvm;
    private ActivityAddPrinterBinding binding;
    private ActivityResultLauncher<String[]> permissions,permissions2;
    private PrintUtils printUtils = new PrintUtils();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_printer);
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        mvvm = ViewModelProviders.of(this).get(AddPrinterMvvm.class);
        binding.setLang(getLang());
        baseMvvm.getOnPinSuccess().observe(this, aBoolean -> {
            mvvm.showPin = false;
        });

        mvvm.getSelectedPrinterPos().observe(this, pos -> {
            if (pos == 0) {
                binding.setTitle(getString(R.string.no_printers));
                binding.llPaperWidth.setVisibility(View.GONE);
                binding.llInterface.setVisibility(View.GONE);
                mvvm.getSelectedInterfacePos().setValue(-1);


            } else if (pos == 1) {
                binding.setTitle("Sunmi");
                binding.llPaperWidth.setVisibility(View.VISIBLE);
                binding.llInterface.setVisibility(View.GONE);
                mvvm.getSelectedInterfacePos().setValue(-1);


            } else {
                binding.setTitle(getString(R.string.other_model));

                binding.llPaperWidth.setVisibility(View.VISIBLE);
                binding.llInterface.setVisibility(View.VISIBLE);
                mvvm.getSelectedInterfacePos().setValue(0);

            }
        });

        mvvm.getSelectedPaperPos().observe(this, pos -> {
            if (pos == 0) {
                binding.setWidth(getString(R.string.mm80));
            } else {
                binding.setWidth(getString(R.string.mm58));

            }
        });

        mvvm.getSelectedInterfacePos().observe(this, pos -> {
            if (pos == 0) {
                binding.setInterfacePrinter(getString(R.string.bluetooth));
                binding.llBluetoothPrinter.setVisibility(View.VISIBLE);
            } else {
                binding.llBluetoothPrinter.setVisibility(View.GONE);

            }

        });

        if (mvvm.getName().getValue() != null) {
            binding.edtName.setText(mvvm.getName().getValue());
        }
        if (mvvm.getCanPrint().getValue() != null) {
            binding.btnSwitch.setChecked(mvvm.getCanPrint().getValue());
        }

        binding.edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.edtName.removeTextChangedListener(this);
                mvvm.getName().setValue(s.toString().trim());
                binding.edtName.addTextChangedListener(this);
            }
        });


        binding.arrowBack.setOnClickListener(v -> onBackPressed());

        binding.btnSave.setOnClickListener(v -> {
            String name = binding.edtName.getText().toString().trim();
            if (!name.isEmpty()) {
                binding.txtInput.setError(null);

            } else {
                binding.txtInput.setError(getString(R.string.empty_field));
            }
        });

        binding.btnSwitch.setOnClickListener(v -> {
            mvvm.getCanPrint().setValue(binding.btnSwitch.isChecked());
        });


        binding.llModel.setOnClickListener(v -> showPopupPrinters());

        binding.llPaperWidth.setOnClickListener(v -> showPopupPaperWidth());

        binding.llSpinnerInterface.setOnClickListener(v -> showPopupInterface());

        binding.btnSearch.setOnClickListener(v -> {
            checkBluetoothPermission();
        });


        permissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted->{
            if (!isGranted.containsValue(false)){
                printUtils.findBluetoothDevice(this);
            }else {
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
            }
        });

        permissions2 = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),isGranted->{
            if (!isGranted.containsValue(false)){
                printUtils.findBluetoothDevice(this);

            }else {
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void checkBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
                    printUtils.findBluetoothDevice(this);
                }else {
                    permissions2.launch(new String[]{Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT});

                }
            }else {
                printUtils.findBluetoothDevice(this);
            }

        }else {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
                permissions2.launch(new String[]{Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT});

            }else {
                permissions.launch(new String[]{Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN});

            }
        }

    }


    private void showPopupPrinters() {
        PowerMenuItem item1 = new PowerMenuItem(getString(R.string.no_printers), false);
        PowerMenuItem item2 = new PowerMenuItem("Sunmi", false);
        PowerMenuItem item3 = new PowerMenuItem(getString(R.string.other_model), false);

        PowerMenu powerMenu = new PowerMenu.Builder(this)
                .addItem(item1) // add an item.
                .addItem(item2)
                .addItem(item3)
                .setAnimation(MenuAnimation.DROP_DOWN) // Animation start point (TOP | LEFT).
                .setMenuRadius(2f)
                .setMenuShadow(4f)
                .setSelectedEffect(true)
                .setSelectedMenuColorResource(R.color.grey1)// sets the shadow.
                .setTextColor(ContextCompat.getColor(this, R.color.black))
                .setTextGravity(Gravity.START)
                .setSelectedTextColor(Color.BLACK)
                .setMenuColor(Color.WHITE)
                .setTextSize(14)
                .setShowBackground(false)
                .setLifecycleOwner(this)
                .setWidth(binding.llModel.getMeasuredWidth())
                .build();


        powerMenu.setOnMenuItemClickListener((position, item) -> {
            item.setIsSelected(true);
            mvvm.getSelectedPrinterPos().setValue(position);
            powerMenu.dismiss();
        });

        if (mvvm.getSelectedPrinterPos().getValue() != null) {
            powerMenu.setSelectedPosition(mvvm.getSelectedPrinterPos().getValue());
        } else {
            powerMenu.setSelectedPosition(0);

        }

        powerMenu.showAsDropDown(binding.icon, binding.llModel.getMeasuredWidth() - 48, -48);
    }

    private void showPopupPaperWidth() {
        PowerMenuItem item1 = new PowerMenuItem(getString(R.string.mm80), false);
        PowerMenuItem item2 = new PowerMenuItem(getString(R.string.mm58), false);

        PowerMenu powerMenu = new PowerMenu.Builder(this)
                .addItem(item1) // add an item.
                .addItem(item2)
                .setAnimation(MenuAnimation.DROP_DOWN) // Animation start point (TOP | LEFT).
                .setMenuRadius(2f)
                .setMenuShadow(4f)
                .setSelectedEffect(true)
                .setSelectedMenuColorResource(R.color.grey1)// sets the shadow.
                .setTextColor(ContextCompat.getColor(this, R.color.black))
                .setTextGravity(Gravity.START)
                .setSelectedTextColor(Color.BLACK)
                .setTextSize(14)
                .setMenuColor(Color.WHITE)
                .setShowBackground(false)
                .setLifecycleOwner(this)
                .setWidth(binding.llSpinnerWidth.getMeasuredWidth())
                .build();


        powerMenu.setOnMenuItemClickListener((position, item) -> {
            item.setIsSelected(true);
            mvvm.getSelectedPaperPos().setValue(position);
            powerMenu.dismiss();
        });

        if (mvvm.getSelectedPaperPos().getValue() != null) {
            powerMenu.setSelectedPosition(mvvm.getSelectedPaperPos().getValue());
        } else {
            powerMenu.setSelectedPosition(0);

        }

        powerMenu.showAsDropDown(binding.icon2, binding.llSpinnerWidth.getMeasuredWidth() - 48, -48);
    }

    private void showPopupInterface() {
        PowerMenuItem item1 = new PowerMenuItem(getString(R.string.bluetooth), false);

        PowerMenu powerMenu = new PowerMenu.Builder(this)
                .addItem(item1) // add an item.
                .setAnimation(MenuAnimation.DROP_DOWN) // Animation start point (TOP | LEFT).
                .setMenuRadius(2f)
                .setMenuShadow(4f)
                .setSelectedEffect(true)
                .setSelectedMenuColorResource(R.color.grey1)// sets the shadow.
                .setTextColor(ContextCompat.getColor(this, R.color.black))
                .setTextGravity(Gravity.START)
                .setSelectedTextColor(Color.BLACK)
                .setTextSize(14)
                .setMenuColor(Color.WHITE)
                .setShowBackground(false)
                .setLifecycleOwner(this)
                .setWidth(binding.llSpinnerInterface.getMeasuredWidth())
                .build();


        powerMenu.setOnMenuItemClickListener((position, item) -> {
            item.setIsSelected(true);
            mvvm.getSelectedInterfacePos().setValue(position);
            powerMenu.dismiss();
        });

        if (mvvm.getSelectedInterfacePos().getValue() != null) {
            powerMenu.setSelectedPosition(mvvm.getSelectedInterfacePos().getValue());
        } else {
            powerMenu.setSelectedPosition(0);

        }

        powerMenu.showAsDropDown(binding.icon3, binding.llSpinnerInterface.getMeasuredWidth() - 48, -48);
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

        mvvm.showPin = true;


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("pin", mvvm.showPin);
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
        super.onBackPressed();
        finish();
        overridePendingTransition(0, 0);
    }
}