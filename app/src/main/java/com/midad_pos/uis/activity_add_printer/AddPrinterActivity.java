package com.midad_pos.uis.activity_add_printer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.midad_pos.R;
import com.midad_pos.adapter.DevicesBluetoothAdapter;
import com.midad_pos.databinding.ActivityAddDiscountBinding;
import com.midad_pos.databinding.ActivityAddPrinterBinding;
import com.midad_pos.databinding.DialogBluetoothDevicesBinding;
import com.midad_pos.model.DiscountModel;
import com.midad_pos.mvvm.AddDiscountMvvm;
import com.midad_pos.mvvm.AddPrinterMvvm;
import com.midad_pos.print_utils.PrintUtils;
import com.midad_pos.share.Common;
import com.midad_pos.uis.activity_base.BaseActivity;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class AddPrinterActivity extends BaseActivity implements PrintUtils.PrintResponse {
    private AddPrinterMvvm mvvm;
    private ActivityAddPrinterBinding binding;
    private ActivityResultLauncher<String[]> permissions, permissions2, permissions3;
    private PrintUtils printUtils = new PrintUtils(this);
    private DevicesBluetoothAdapter adapter = new DevicesBluetoothAdapter(this);
    private AlertDialog dialog;
    private ActivityResultLauncher<Intent> launcher;

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
                binding.llBluetoothPrinter.setVisibility(View.GONE);
                binding.viewPrintReceiptBillUi.setVisibility(View.VISIBLE);
                binding.llPrintReceiptBillUi.setVisibility(View.VISIBLE);
                binding.llKitchenPrinter.setVisibility(View.GONE);


            } else if (pos == 1) {
                binding.setTitle("Sunmi");
                binding.llPaperWidth.setVisibility(View.VISIBLE);
                binding.llInterface.setVisibility(View.GONE);
                mvvm.getSelectedInterfacePos().setValue(-1);
                binding.llBluetoothPrinter.setVisibility(View.GONE);
                binding.viewPrintReceiptBillUi.setVisibility(View.VISIBLE);
                binding.llPrintReceiptBillUi.setVisibility(View.VISIBLE);
                binding.llKitchenPrinter.setVisibility(View.GONE);


            } else if (pos == 2) {
                binding.setTitle(getString(R.string.kitchen_display));
                binding.llPaperWidth.setVisibility(View.GONE);
                binding.llInterface.setVisibility(View.GONE);
                binding.llBluetoothPrinter.setVisibility(View.GONE);
                binding.viewPrintReceiptBillUi.setVisibility(View.GONE);
                binding.llPrintReceiptBillUi.setVisibility(View.GONE);
                binding.llKitchenPrinter.setVisibility(View.VISIBLE);

                mvvm.getSelectedInterfacePos().setValue(-1);


            } else {
                binding.setTitle(getString(R.string.other_model));

                binding.llPaperWidth.setVisibility(View.VISIBLE);
                binding.llInterface.setVisibility(View.VISIBLE);
                mvvm.getSelectedInterfacePos().setValue(0);
                binding.llBluetoothPrinter.setVisibility(View.VISIBLE);
                binding.viewPrintReceiptBillUi.setVisibility(View.VISIBLE);
                binding.llPrintReceiptBillUi.setVisibility(View.VISIBLE);
                binding.llKitchenPrinter.setVisibility(View.GONE);


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

        mvvm.getOnAddPrinterSuccess().observe(this, model -> {
            onBackPressed();
        });

        mvvm.getOnAddedError().observe(this, error -> Common.createErrorDialog(this, error));


        if (mvvm.getName().getValue() != null) {
            binding.edtName.setText(mvvm.getName().getValue());
        }
        if (mvvm.getCanPrint().getValue() != null) {
            binding.btnSwitch.setChecked(mvvm.getCanPrint().getValue());
            binding.llPrintAutomatic.setVisibility(mvvm.getCanPrint().getValue() ? View.VISIBLE : View.GONE);
        }

        if (mvvm.getAutomaticPrint().getValue() != null) {
            binding.btnSwitchPrintAutomatic.setChecked(mvvm.getAutomaticPrint().getValue());
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
                mvvm.addPrinter(false,this,getLang(),null);

            } else {
                binding.txtInput.setError(getString(R.string.empty_field));
            }
        });

        binding.btnSwitch.setOnClickListener(v -> {
            mvvm.getCanPrint().setValue(binding.btnSwitch.isChecked());
            binding.llPrintAutomatic.setVisibility(binding.btnSwitch.isChecked() ? View.VISIBLE : View.GONE);
            mvvm.getAutomaticPrint().setValue(false);
            binding.btnSwitchPrintAutomatic.setChecked(false);
        });

        binding.btnSwitchPrintAutomatic.setOnClickListener(v -> {
            mvvm.getAutomaticPrint().setValue(binding.btnSwitchPrintAutomatic.isChecked());

        });


        binding.llModel.setOnClickListener(v -> showPopupPrinters());

        binding.llPaperWidth.setOnClickListener(v -> showPopupPaperWidth());

        binding.llSpinnerInterface.setOnClickListener(v -> showPopupInterface());

        binding.btnSearch.setOnClickListener(v -> {
            checkBluetoothPermission();


        });


        permissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            if (!isGranted.containsValue(false)) {

                printUtils.findBluetoothDevice(this);
                createBluetoothDeviceDialog();

                /*Intent intent = new Intent(this,ScanningActivity.class);
                launcher.launch(intent);*/


            } else {
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
            }
        });

        permissions2 = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            if (!isGranted.containsValue(false)) {
                printUtils.findBluetoothDevice(this);
                createBluetoothDeviceDialog();

                Intent intent = new Intent(this,ScanningActivity.class);
                launcher.launch(intent);



            } else {
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
            }
        });

        permissions3 = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            if (!isGranted.containsValue(false)) {


            } else {
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
            }
        });


        binding.flPrint.setOnClickListener(v -> {
            mvvm.addPrinter(true,this,getLang(),binding);

        });


        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Log.e("ready", "ready");
            }
        });

    }



    private void checkBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
                    createBluetoothDeviceDialog();

                    printUtils.findBluetoothDevice(this);




                } else {
                    permissions2.launch(new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_ADVERTISE});

                }
            } else {
                createBluetoothDeviceDialog();
                printUtils.findBluetoothDevice(this);




            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissions2.launch(new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_ADVERTISE});

            } else {
                permissions.launch(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.BLUETOOTH_ADVERTISE});

            }
        }

    }

    private void showPopupPrinters() {
        PowerMenuItem item1 = new PowerMenuItem(getString(R.string.no_printers), false);
        PowerMenuItem item2 = new PowerMenuItem("Sunmi", false);
        PowerMenuItem item3 = new PowerMenuItem(getString(R.string.kitchen_display), false);
        PowerMenuItem item4 = new PowerMenuItem(getString(R.string.other_model), false);

        PowerMenu powerMenu = new PowerMenu.Builder(this)
                .addItem(item1) // add an item.
                .addItem(item2)
                .addItem(item3)
                .addItem(item4)
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

    private void createBluetoothDeviceDialog() {
        DialogBluetoothDevicesBinding devicesBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_bluetooth_devices, null, false);
        dialog = new AlertDialog.Builder(this)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setView(devicesBinding.getRoot());

        devicesBinding.recView.setLayoutManager(new LinearLayoutManager(this));
        devicesBinding.recView.setHasFixedSize(true);
        devicesBinding.recView.setAdapter(adapter);
        devicesBinding.cancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void onDevices(List<BluetoothDevice> list) {
        if (adapter != null) {
            adapter.updateList(list);
        }
    }

    @SuppressLint("MissingPermission")
    public void selectBluetoothDevice(BluetoothDevice bluetoothDevice) {
        mvvm.getBluetoothDevice().setValue(bluetoothDevice);
        binding.setPrinterBluetoothName(bluetoothDevice.getName());
        if (dialog != null) {
            dialog.dismiss();
        }

    }

    @Override
    public void onStartIntent() {
        mvvm.forNavigation = true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            printUtils.findBluetoothDevice(this);
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
        if (dialog != null) {
            dialog.dismiss();
        }
        if (mvvm.forNavigation) {
            mvvm.showPin = false;
            mvvm.forNavigation = false;

        } else {
            mvvm.showPin = true;

        }

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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}