package com.midad_app_pos.uis.activity_settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.midad_app_pos.R;
import com.midad_app_pos.adapter.PrinterAdapter;
import com.midad_app_pos.databinding.ActivitySettingsBinding;
import com.midad_app_pos.databinding.DialogHomeItemLayoutBinding;
import com.midad_app_pos.model.AppSettingModel;
import com.midad_app_pos.model.PrinterModel;
import com.midad_app_pos.mvvm.SettingsMvvm;
import com.midad_app_pos.uis.activity_add_printer.AddPrinterActivity;
import com.midad_app_pos.uis.activity_drawer_base.DrawerBaseActivity;
import com.midad_app_pos.uis.activity_splash.SplashActivity;

import io.reactivex.disposables.CompositeDisposable;

public class SettingsActivity extends DrawerBaseActivity {
    private SettingsMvvm mvvm;
    private ActivitySettingsBinding binding;
    private PrinterAdapter printerAdapter;
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_settings, null, false);
        setContentView(binding.getRoot());
        setUpDrawer(binding.toolbarSettings, true);
        updateSelectedPos(4);
        initView();

    }

    private void initView() {
        mvvm = ViewModelProviders.of(this).get(SettingsMvvm.class);
        binding.setModel(getUserModel());

        printerAdapter = new PrinterAdapter(this);

        if (mvvm.getPositions().getValue() != null) {
            updateSelections(mvvm.getPositions().getValue());

        }

        if (mvvm.getIsDialogTypeVisible().getValue() != null) {
            if (mvvm.getIsDialogTypeVisible().getValue()) {
                showLayoutDialog();
            }
        }

        mvvm.getIsDeleteMode().observe(this, aBoolean -> {
            if (aBoolean) {
                if (binding.printersLayout != null) {
                    binding.printersLayout.toolbarDeleteModel.setVisibility(View.VISIBLE);
                }

                if (binding.printersDetailsLayout != null) {
                    binding.printersDetailsLayout.toolbarDeleteModel.setVisibility(View.VISIBLE);
                }
            } else {
                if (binding.printersLayout != null) {
                    binding.printersLayout.toolbarDeleteModel.setVisibility(View.GONE);
                }

                if (binding.printersDetailsLayout != null) {
                    binding.printersDetailsLayout.toolbarDeleteModel.setVisibility(View.GONE);
                }
            }

        });


        mvvm.getDeletedPrinters().observe(this, list -> {
            if (binding.printersLayout != null) {
                binding.printersLayout.setDeleteCount(list.size() + "");
            }

            if (binding.printersDetailsLayout != null) {
                binding.printersDetailsLayout.setDeleteCount(list.size() + "");
            }
        });

        mvvm.getPrintersInstance().observe(this, list -> {
            if (printerAdapter != null) {
                printerAdapter.updateList(list);
            }
            if (binding.printersLayout!=null){
                binding.printersLayout.llNoPrinters.setVisibility(list.size()>0?View.GONE:View.VISIBLE);

            }

            if (binding.printersDetailsLayout!=null){
                binding.printersDetailsLayout.llNoPrinters.setVisibility(list.size()>0?View.GONE:View.VISIBLE);

            }
        });

        mvvm.getOnLogoutSuccess().observe(this,success->{
            if (success){
                clearUserModel();
                Intent intent = new Intent(this, SplashActivity.class);
                startActivity(intent);
                finishAffinity();
                overridePendingTransition(0,0);
            }
        });

        if (binding.printersLayout!=null)
        {
            binding.printersLayout.recView.setLayoutManager(new LinearLayoutManager(this));
            binding.printersLayout.recView.setHasFixedSize(true);
            binding.printersLayout.recView.setAdapter(printerAdapter);
        }

        if (binding.printersDetailsLayout!=null)
        {
            binding.printersDetailsLayout.recView.setLayoutManager(new LinearLayoutManager(this));
            binding.printersDetailsLayout.recView.setHasFixedSize(true);
            binding.printersDetailsLayout.recView.setAdapter(printerAdapter);
        }

        if (binding.flPrintersLayout != null && binding.printersLayout != null && binding.printersLayout.arrowBack != null) {
            binding.printersLayout.setLang(getLang());
            binding.printersLayout.arrowBack.setOnClickListener(view -> binding.flPrintersLayout.setVisibility(View.GONE));

        }



        if (binding.flCustomerDisplayLayout != null && binding.customerDisplayLayout != null && binding.customerDisplayLayout.arrowBack != null) {
            binding.customerDisplayLayout.setLang(getLang());
            binding.customerDisplayLayout.arrowBack.setOnClickListener(view -> binding.flCustomerDisplayLayout.setVisibility(View.GONE));
        }

        if (binding.flGeneralSettingsLayout != null && binding.generalSettingsLayout != null && binding.generalSettingsLayout.arrowBack != null) {
            binding.generalSettingsLayout.setLang(getLang());
            binding.generalSettingsLayout.arrowBack.setOnClickListener(view -> binding.flGeneralSettingsLayout.setVisibility(View.GONE));
        }

        if (binding.flPrintersDetailsLand != null) {
            mvvm.getPositions().setValue(mvvm.getPositions().getValue() != -1 ? mvvm.getPositions().getValue() : 0);
            updateSelections(mvvm.getPositions().getValue() != -1 ? mvvm.getPositions().getValue() : 0);
        }

        if (binding.generalSettingsLayout != null) {
            binding.generalSettingsLayout.btnSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                AppSettingModel appSetting = getAppSetting();
                if (appSetting == null) {
                    appSetting = new AppSettingModel();
                }
                appSetting.setScan(isChecked);
                setAppSettingModel(appSetting);
            });


            binding.generalSettingsLayout.layoutType.setOnClickListener(v -> {
                showLayoutDialog();
            });

        }

        if (binding.generalSettingsDetailsLayout != null) {
            binding.generalSettingsDetailsLayout.btnSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                AppSettingModel appSetting = getAppSetting();
                if (appSetting == null) {
                    appSetting = new AppSettingModel();
                }
                appSetting.setScan(isChecked);
                setAppSettingModel(appSetting);
            });

            binding.generalSettingsDetailsLayout.layoutType.setOnClickListener(v -> {
                showLayoutDialog();
            });


        }

        if (getAppSetting() != null) {
            if (binding.generalSettingsLayout != null) {
                binding.generalSettingsLayout.btnSwitch.setChecked(getAppSetting().isScan());


            }

            if (binding.generalSettingsDetailsLayout != null) {
                binding.generalSettingsDetailsLayout.btnSwitch.setChecked(getAppSetting().isScan());

            }

            if (getAppSetting().getHome_layout_type() == 1) {
                if (binding.generalSettingsLayout != null) {
                    binding.generalSettingsLayout.setLayout(getString(R.string.grid));
                }

                if (binding.generalSettingsDetailsLayout != null) {
                    binding.generalSettingsDetailsLayout.setLayout(getString(R.string.grid));
                }


            } else {
                if (binding.generalSettingsLayout != null) {
                    binding.generalSettingsLayout.setLayout(getString(R.string.list));
                }

                if (binding.generalSettingsDetailsLayout != null) {
                    binding.generalSettingsDetailsLayout.setLayout(getString(R.string.list));
                }
            }

        }


        binding.printers.setOnClickListener(view -> {
            mvvm.getPositions().setValue(0);
            updateSelections(0);
        });

        binding.customerDisplays.setOnClickListener(view -> {
            mvvm.getPositions().setValue(1);
            updateSelections(1);
        });

        binding.generalSettings.setOnClickListener(view -> {
            mvvm.getPositions().setValue(2);
            updateSelections(2);
        });

        if (binding.printersLayout != null) {
            binding.printersLayout.fab.setOnClickListener(v -> {
                mvvm.forNavigation = true;
                Intent intent = new Intent(this, AddPrinterActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }


        if (binding.printersDetailsLayout != null) {
            binding.printersDetailsLayout.fab.setOnClickListener(v -> {
                mvvm.forNavigation = true;

                Intent intent = new Intent(this, AddPrinterActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        binding.logout.setOnClickListener(v -> mvvm.logout(this));

        if (binding.printersLayout!=null){
            binding.printersLayout.imageNormalMode.setOnClickListener(v -> mvvm.clearDeletedPrinters());
        }

        if (binding.printersDetailsLayout!=null){
            binding.printersDetailsLayout.imageNormalMode.setOnClickListener(v -> mvvm.clearDeletedPrinters());
        }

        if (binding.printersLayout!=null){
            binding.printersLayout.imageDelete.setOnClickListener(v -> mvvm.deletePrinters());
        }

        if (binding.printersDetailsLayout!=null){
            binding.printersDetailsLayout.imageDelete.setOnClickListener(v -> mvvm.deletePrinters());
        }

    }

    private void showLayoutDialog() {
        mvvm.getIsDialogTypeVisible().setValue(true);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        DialogHomeItemLayoutBinding layoutBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_home_item_layout, null, false);
        dialog.setView(layoutBinding.getRoot());

        if (getAppSetting().getHome_layout_type() == 1)
        {
            if (layoutBinding.img1 != null) {
                layoutBinding.img1.setImageResource(R.drawable.selected_grid);

            }
            if (layoutBinding.img2 != null) {
                layoutBinding.img2.setImageResource(R.drawable.unselected_linear);

            }

            if (layoutBinding.imgLand1 != null) {
                layoutBinding.imgLand1.setImageResource(R.drawable.tablet_selected_grid);

            }
            if (layoutBinding.imgLand2 != null) {
                layoutBinding.imgLand2.setImageResource(R.drawable.tablet_unselected_linear);

            }
            layoutBinding.rbList.setChecked(false);
            layoutBinding.rbGrid.setChecked(true);

            if (binding.generalSettingsLayout != null) {
                binding.generalSettingsLayout.setLayout(getString(R.string.grid));
            }

            if (binding.generalSettingsDetailsLayout != null) {
                binding.generalSettingsDetailsLayout.setLayout(getString(R.string.grid));
            }


        }
        else {
            if (layoutBinding.img1 != null) {
                layoutBinding.img1.setImageResource(R.drawable.selected_linear);

            }
            if (layoutBinding.img2 != null) {
                layoutBinding.img2.setImageResource(R.drawable.unselected_grid);

            }

            if (layoutBinding.imgLand1 != null) {
                layoutBinding.imgLand1.setImageResource(R.drawable.tablet_unselected_linear);

            }
            if (layoutBinding.imgLand2 != null) {
                layoutBinding.imgLand2.setImageResource(R.drawable.tablet_selected_grid);

            }

            if (binding.generalSettingsLayout != null) {
                binding.generalSettingsLayout.setLayout(getString(R.string.list));
            }

            if (binding.generalSettingsDetailsLayout != null) {
                binding.generalSettingsDetailsLayout.setLayout(getString(R.string.list));
            }

            layoutBinding.rbList.setChecked(true);
            layoutBinding.rbGrid.setChecked(false);

        }

        layoutBinding.rbGrid.setOnClickListener(v -> {
            if (layoutBinding.img1 != null) {
                layoutBinding.img1.setImageResource(R.drawable.selected_grid);

            }
            if (layoutBinding.img2 != null) {
                layoutBinding.img2.setImageResource(R.drawable.unselected_linear);

            }

            if (layoutBinding.imgLand1 != null) {
                layoutBinding.imgLand1.setImageResource(R.drawable.tablet_selected_grid);

            }
            if (layoutBinding.imgLand2 != null) {
                layoutBinding.imgLand2.setImageResource(R.drawable.tablet_unselected_linear);

            }
            layoutBinding.rbList.setChecked(false);


        });

        layoutBinding.rbList.setOnClickListener(v -> {
            if (layoutBinding.img1 != null) {
                layoutBinding.img1.setImageResource(R.drawable.unselected_grid);

            }
            if (layoutBinding.img2 != null) {
                layoutBinding.img2.setImageResource(R.drawable.selected_linear);

            }

            if (layoutBinding.imgLand1 != null) {
                layoutBinding.imgLand1.setImageResource(R.drawable.tablet_unselected_grid);

            }
            if (layoutBinding.imgLand2 != null) {
                layoutBinding.imgLand2.setImageResource(R.drawable.tabled_selected_linear);

            }
            layoutBinding.rbGrid.setChecked(false);

        });


        if (layoutBinding.img1 != null) {
            layoutBinding.img1.setOnClickListener(v -> {
                layoutBinding.img1.setImageResource(R.drawable.selected_grid);

                if (layoutBinding.img2 != null) {
                    layoutBinding.img2.setImageResource(R.drawable.unselected_linear);

                }

                if (layoutBinding.imgLand1 != null) {
                    layoutBinding.imgLand1.setImageResource(R.drawable.tablet_selected_grid);

                }
                if (layoutBinding.imgLand2 != null) {
                    layoutBinding.imgLand2.setImageResource(R.drawable.tablet_unselected_linear);

                }
                layoutBinding.rbList.setChecked(false);
                layoutBinding.rbGrid.setChecked(true);


            });

        }

        if (layoutBinding.imgLand1 != null) {
            layoutBinding.imgLand1.setOnClickListener(v -> {
                if (layoutBinding.img1 != null) {
                    layoutBinding.img1.setImageResource(R.drawable.selected_grid);

                }
                if (layoutBinding.img2 != null) {
                    layoutBinding.img2.setImageResource(R.drawable.unselected_linear);

                }

                layoutBinding.imgLand1.setImageResource(R.drawable.tablet_selected_grid);

                if (layoutBinding.imgLand2 != null) {
                    layoutBinding.imgLand2.setImageResource(R.drawable.tablet_unselected_linear);

                }
                layoutBinding.rbList.setChecked(false);
                layoutBinding.rbGrid.setChecked(true);


            });

        }

        if (layoutBinding.img2 != null) {
            layoutBinding.img2.setOnClickListener(v -> {
                if (layoutBinding.img1 != null) {
                    layoutBinding.img1.setImageResource(R.drawable.unselected_grid);

                }
                layoutBinding.img2.setImageResource(R.drawable.selected_linear);

                if (layoutBinding.imgLand1 != null) {
                    layoutBinding.imgLand1.setImageResource(R.drawable.tablet_unselected_grid);

                }
                if (layoutBinding.imgLand2 != null) {
                    layoutBinding.imgLand2.setImageResource(R.drawable.tabled_selected_linear);

                }
                layoutBinding.rbGrid.setChecked(false);
                layoutBinding.rbList.setChecked(true);


            });

        }

        if (layoutBinding.imgLand2 != null) {
            layoutBinding.imgLand2.setOnClickListener(v -> {
                if (layoutBinding.img1 != null) {
                    layoutBinding.img1.setImageResource(R.drawable.unselected_grid);

                }
                if (layoutBinding.img2 != null) {
                    layoutBinding.img2.setImageResource(R.drawable.selected_linear);

                }

                if (layoutBinding.imgLand1 != null) {
                    layoutBinding.imgLand1.setImageResource(R.drawable.tablet_unselected_grid);

                }
                layoutBinding.imgLand2.setImageResource(R.drawable.tabled_selected_linear);

                layoutBinding.rbGrid.setChecked(false);
                layoutBinding.rbList.setChecked(true);

            });

        }


        layoutBinding.cancel.setOnClickListener(v -> {
            dialog.dismiss();
            mvvm.getIsDialogTypeVisible().setValue(false);
        });
        layoutBinding.ok.setOnClickListener(v -> {
            if (layoutBinding.rbGrid.isChecked()) {
                AppSettingModel appSettingModel = getAppSetting();
                appSettingModel.setHome_layout_type(1);
                setAppSettingModel(appSettingModel);
                if (binding.generalSettingsLayout != null) {
                    binding.generalSettingsLayout.setLayout(getString(R.string.grid));
                }

                if (binding.generalSettingsDetailsLayout != null) {
                    binding.generalSettingsDetailsLayout.setLayout(getString(R.string.grid));
                }
                dialog.dismiss();
            }

            if (layoutBinding.rbList.isChecked()) {
                AppSettingModel appSettingModel = getAppSetting();
                appSettingModel.setHome_layout_type(2);
                setAppSettingModel(appSettingModel);
                if (binding.generalSettingsLayout != null) {
                    binding.generalSettingsLayout.setLayout(getString(R.string.list));
                }

                if (binding.generalSettingsDetailsLayout != null) {
                    binding.generalSettingsDetailsLayout.setLayout(getString(R.string.list));
                }
                dialog.dismiss();
            }
            mvvm.getIsDialogTypeVisible().setValue(false);

        });

        dialog.show();

    }

    private void updateSelections(int pos) {
        if (pos == 0) {
            binding.printers.setBackgroundResource(R.color.grey1);
            binding.customerDisplays.setBackgroundResource(R.color.transparent);
            binding.generalSettings.setBackgroundResource(R.color.transparent);


            if (binding.flPrintersLayout != null && binding.flCustomerDisplayLayout != null && binding.flGeneralSettingsLayout != null) {
                binding.flPrintersLayout.setVisibility(View.VISIBLE);
                binding.flCustomerDisplayLayout.setVisibility(View.GONE);
                binding.flGeneralSettingsLayout.setVisibility(View.GONE);

                binding.printers.setBackgroundResource(R.color.transparent);
                binding.customerDisplays.setBackgroundResource(R.color.transparent);
                binding.generalSettings.setBackgroundResource(R.color.transparent);

            } else if (binding.flPrintersDetailsLand != null && binding.flCustomerDisplaysDetailsLand != null && binding.flGeneralSettingsDetailsLand != null) {
                binding.flPrintersDetailsLand.setVisibility(View.VISIBLE);
                binding.flCustomerDisplaysDetailsLand.setVisibility(View.GONE);
                binding.flGeneralSettingsDetailsLand.setVisibility(View.GONE);

                if (binding.iconPrinter != null && binding.iconDisplay != null && binding.iconSetting != null && binding.tvPrinter != null && binding.tvDisplay != null && binding.tvGeneral != null) {
                    binding.iconPrinter.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    binding.iconDisplay.setColorFilter(getResources().getColor(R.color.grey4));
                    binding.iconSetting.setColorFilter(getResources().getColor(R.color.grey4));

                    binding.tvPrinter.setTextColor(getResources().getColor(R.color.colorPrimary));
                    binding.tvDisplay.setTextColor(getResources().getColor(R.color.black));
                    binding.tvGeneral.setTextColor(getResources().getColor(R.color.black));

                }
            }

        } else if (pos == 1) {
            binding.printers.setBackgroundResource(R.color.transparent);
            binding.customerDisplays.setBackgroundResource(R.color.grey1);
            binding.generalSettings.setBackgroundResource(R.color.transparent);

            if (binding.flPrintersLayout != null && binding.flCustomerDisplayLayout != null && binding.flGeneralSettingsLayout != null) {
                binding.flPrintersLayout.setVisibility(View.GONE);
                binding.flCustomerDisplayLayout.setVisibility(View.VISIBLE);
                binding.flGeneralSettingsLayout.setVisibility(View.GONE);

                binding.printers.setBackgroundResource(R.color.transparent);
                binding.customerDisplays.setBackgroundResource(R.color.transparent);
                binding.generalSettings.setBackgroundResource(R.color.transparent);

            } else if (binding.flPrintersDetailsLand != null && binding.flCustomerDisplaysDetailsLand != null && binding.flGeneralSettingsDetailsLand != null) {
                binding.flPrintersDetailsLand.setVisibility(View.GONE);
                binding.flCustomerDisplaysDetailsLand.setVisibility(View.VISIBLE);
                binding.flGeneralSettingsDetailsLand.setVisibility(View.GONE);

                if (binding.iconPrinter != null && binding.iconDisplay != null && binding.iconSetting != null && binding.tvPrinter != null && binding.tvDisplay != null && binding.tvGeneral != null) {
                    binding.iconPrinter.setColorFilter(getResources().getColor(R.color.grey4));
                    binding.iconDisplay.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    binding.iconSetting.setColorFilter(getResources().getColor(R.color.grey4));

                    binding.tvPrinter.setTextColor(getResources().getColor(R.color.black));
                    binding.tvDisplay.setTextColor(getResources().getColor(R.color.colorPrimary));
                    binding.tvGeneral.setTextColor(getResources().getColor(R.color.black));

                }
            }


        } else if (pos == 2) {
            binding.printers.setBackgroundResource(R.color.transparent);
            binding.customerDisplays.setBackgroundResource(R.color.transparent);
            binding.generalSettings.setBackgroundResource(R.color.grey1);

            if (binding.flPrintersLayout != null && binding.flCustomerDisplayLayout != null && binding.flGeneralSettingsLayout != null) {
                binding.flPrintersLayout.setVisibility(View.GONE);
                binding.flCustomerDisplayLayout.setVisibility(View.GONE);
                binding.flGeneralSettingsLayout.setVisibility(View.VISIBLE);

                binding.printers.setBackgroundResource(R.color.transparent);
                binding.customerDisplays.setBackgroundResource(R.color.transparent);
                binding.generalSettings.setBackgroundResource(R.color.transparent);

            } else if (binding.flPrintersDetailsLand != null && binding.flCustomerDisplaysDetailsLand != null && binding.flGeneralSettingsDetailsLand != null) {
                binding.flPrintersDetailsLand.setVisibility(View.GONE);
                binding.flCustomerDisplaysDetailsLand.setVisibility(View.GONE);
                binding.flGeneralSettingsDetailsLand.setVisibility(View.VISIBLE);

                if (binding.iconPrinter != null && binding.iconDisplay != null && binding.iconSetting != null && binding.tvPrinter != null && binding.tvDisplay != null && binding.tvGeneral != null) {
                    binding.iconPrinter.setColorFilter(getResources().getColor(R.color.grey4));
                    binding.iconDisplay.setColorFilter(getResources().getColor(R.color.grey4));
                    binding.iconSetting.setColorFilter(getResources().getColor(R.color.colorPrimary));

                    binding.tvPrinter.setTextColor(getResources().getColor(R.color.black));
                    binding.tvDisplay.setTextColor(getResources().getColor(R.color.black));
                    binding.tvGeneral.setTextColor(getResources().getColor(R.color.colorPrimary));

                }
            }
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
        mvvm.getPrinters();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

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
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public void onBackPressed() {
        if (binding.flPrintersLayout != null && binding.flPrintersLayout.getVisibility() == View.VISIBLE) {
            binding.flPrintersLayout.setVisibility(View.GONE);
        } else if (binding.flCustomerDisplayLayout != null && binding.flCustomerDisplayLayout.getVisibility() == View.VISIBLE) {
            binding.flCustomerDisplayLayout.setVisibility(View.GONE);
        } else if (binding.flGeneralSettingsLayout != null && binding.flGeneralSettingsLayout.getVisibility() == View.VISIBLE) {
            binding.flGeneralSettingsLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }


    }


    public void updateDeleteModel(int adapterPosition) {
        if (mvvm.getDeletedPrinters().getValue() != null && mvvm.getDeletedPrinters().getValue().size() == 0) {
            mvvm.getIsDeleteMode().setValue(true);
            mvvm.addPrinterToDelete(adapterPosition);
            printerAdapter.notifyItemChanged(adapterPosition);

        }
    }


    public void selectDeletePrinter(int adapterPosition, PrinterModel printerModel) {
        if (mvvm.getIsDeleteMode().getValue() != null && mvvm.getIsDeleteMode().getValue()) {
            if (printerModel.isSelected()) {
                printerModel.setSelected(false);
                mvvm.removePrinterFromDeletedList(adapterPosition);
            } else {
                mvvm.addPrinterToDelete(adapterPosition);

            }
            printerAdapter.notifyItemChanged(adapterPosition);
        }
    }
}