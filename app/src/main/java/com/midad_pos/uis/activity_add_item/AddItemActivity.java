package com.midad_pos.uis.activity_add_item;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.midad_pos.R;
import com.midad_pos.adapter.ModifierAdapter;
import com.midad_pos.adapter.TaxesAdapter;
import com.midad_pos.adapter.UnitAdapter;
import com.midad_pos.databinding.ActivityAddItemBinding;
import com.midad_pos.databinding.AlertDeleteDialogBinding;
import com.midad_pos.model.AddItemModel;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.ItemModel;
import com.midad_pos.model.ModifierModel;
import com.midad_pos.model.TaxModel;
import com.midad_pos.model.UnitModel;
import com.midad_pos.mvvm.AddItemMvvm;
import com.midad_pos.share.Common;
import com.midad_pos.uis.activity_add_category.AddCategoryActivity;
import com.midad_pos.uis.activity_base.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddItemActivity extends BaseActivity {
    private AddItemMvvm mvvm;
    private ActivityAddItemBinding binding;
    private ActivityResultLauncher<String[]> permissions;
    private ActivityResultLauncher<Intent> launcher;
    private String imagePath = "";
    private Uri outPutUri;
    private int req;
    private UnitAdapter unitAdapter;
    private ModifierAdapter modifierAdapter;
    private TaxesAdapter taxesAdapter;
    private boolean forImageIntent = false;
    private ItemModel itemModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_item);
        setContentView(binding.getRoot());

        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        itemModel = (ItemModel) intent.getSerializableExtra("data");
    }

    private void initView() {
        mvvm = ViewModelProviders.of(this).get(AddItemMvvm.class);
        binding.setLang(getLang());
        baseMvvm.getOnPinSuccess().observe(this,aBoolean -> {
            mvvm.showPin = false;
        });
        if (itemModel==null){
            binding.setTitle(getString(R.string.create_item));
            binding.cardDelete.setVisibility(View.GONE);

        }else {
            binding.setTitle(getString(R.string.edit_item));
            binding.cardDelete.setVisibility(View.VISIBLE);

        }

        mvvm.getAddItemModel().observe(this, model -> {
            binding.setModel(model);
            if (!model.isShape() && !model.getImage().isEmpty()) {
                Glide.with(this)
                        .asBitmap()
                        .load(model.getImage())
                        .into(binding.image);
                binding.closeImage.setVisibility(View.VISIBLE);
                binding.icon.setVisibility(View.GONE);
                Objects.requireNonNull(mvvm.getAddItemModel().getValue()).setShape(false);
            }

        });
        mvvm.getIsLoading().observe(this, isLoading -> {

            if (isLoading) {
                binding.loader.setVisibility(View.VISIBLE);
                binding.scrollView.setVisibility(View.GONE);
            } else {
                binding.loader.setVisibility(View.GONE);
                binding.scrollView.setVisibility(View.VISIBLE);
            }
        });
        mvvm.getOnAddedSuccess().observe(this, aBoolean -> {
            finish();
            Toast.makeText(this, R.string.suc, Toast.LENGTH_SHORT).show();
        });
        mvvm.getHomeData().observe(this, homeData -> {
            binding.setShowModifier(homeData.getModifiers().size() > 0);
            binding.setShowTaxes(homeData.getTaxes().size() > 0);
            if (unitAdapter != null) {
                unitAdapter.updateList(homeData.getUnits());
            }

            if (modifierAdapter != null) {
                modifierAdapter.updateList(homeData.getModifiers());
            }

            if (taxesAdapter != null) {
                taxesAdapter.updateList(homeData.getTaxes());
            }
        });

        mvvm.getOnError().observe(this,error-> Toast.makeText(this, error, Toast.LENGTH_SHORT).show());

        updateCategoryCheckedColor(mvvm.getSelectedColorPos().getValue() != null ? mvvm.getSelectedColorPos().getValue() : 0);

        updateShapes(mvvm.getSelectedShapePos().getValue() != null ? mvvm.getSelectedShapePos().getValue() : 0);

        binding.arrow.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        binding.llCategory.setOnClickListener(this::createCategoryPopupMenu);

        binding.rbShapes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AddItemModel model = mvvm.getAddItemModel().getValue();
            Objects.requireNonNull(model).setImage("");
            binding.image.setImageBitmap(null);
            binding.image.setImageResource(R.color.grey1);
            binding.icon.setVisibility(View.VISIBLE);
            binding.closeImage.setVisibility(View.GONE);
            Objects.requireNonNull(model).setShape(isChecked);
            mvvm.getAddItemModel().setValue(model);
        });


        binding.card1.setOnClickListener(v -> updateCategoryCheckedColor(0));
        binding.card2.setOnClickListener(v -> updateCategoryCheckedColor(1));
        binding.card3.setOnClickListener(v -> updateCategoryCheckedColor(2));
        binding.card4.setOnClickListener(v -> updateCategoryCheckedColor(3));
        binding.card5.setOnClickListener(v -> updateCategoryCheckedColor(4));
        binding.card6.setOnClickListener(v -> updateCategoryCheckedColor(5));
        binding.card7.setOnClickListener(v -> updateCategoryCheckedColor(6));
        binding.card8.setOnClickListener(v -> updateCategoryCheckedColor(7));


        binding.shape1.setOnClickListener(v -> updateShapes(0));
        binding.shape2.setOnClickListener(v -> updateShapes(1));
        binding.shape3.setOnClickListener(v -> updateShapes(2));
        binding.shape4.setOnClickListener(v -> updateShapes(3));

        binding.closeImage.setOnClickListener(v -> {
            binding.closeImage.setVisibility(View.GONE);
            binding.image.setImageBitmap(null);
            binding.image.setImageResource(R.color.grey1);
            binding.icon.setVisibility(View.GONE);
            Objects.requireNonNull(mvvm.getAddItemModel().getValue()).setImage("");
            Objects.requireNonNull(mvvm.getAddItemModel().getValue()).setShape(true);
            mvvm.getAddItemModel().setValue(mvvm.getAddItemModel().getValue());

        });

        permissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
            if (req == 1) {
                if (permissions.containsValue(false)) {
                    Toast.makeText(this, R.string.perm_denied, Toast.LENGTH_SHORT).show();
                } else {
                    openCamera();
                }
            } else if (req == 2) {
                if (permissions.containsValue(false)) {
                    Toast.makeText(this, R.string.perm_denied, Toast.LENGTH_SHORT).show();
                } else {
                    openGallery();
                }
            }
        });

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            forImageIntent = true;

            if (req == 2 && result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                Objects.requireNonNull(mvvm.getAddItemModel().getValue()).setUri(uri.toString());

                Objects.requireNonNull(mvvm.getAddItemModel().getValue()).setImage(Common.getImagePath(this, uri));

                Glide.with(this)
                        .asBitmap()
                        .load(uri)
                        .into(binding.image);
                binding.closeImage.setVisibility(View.VISIBLE);
                binding.icon.setVisibility(View.GONE);
                mvvm.getAddItemModel().getValue().setShape(false);
            } else if (req == 1 && result.getResultCode() == RESULT_OK) {
                Objects.requireNonNull(mvvm.getAddItemModel().getValue()).setImage(imagePath);
                Objects.requireNonNull(mvvm.getAddItemModel().getValue()).setUri(outPutUri.toString());

                binding.closeImage.setVisibility(View.VISIBLE);
                binding.icon.setVisibility(View.GONE);
                Glide.with(this)
                        .asBitmap()
                        .load(imagePath)
                        .into(binding.image);
                mvvm.getAddItemModel().getValue().setShape(false);


            }
        });

        unitAdapter = new UnitAdapter(this);
        modifierAdapter = new ModifierAdapter(this);
        taxesAdapter = new TaxesAdapter(this);
        if (binding.recViewUnit != null) {
            binding.recViewUnit.setLayoutManager(new LinearLayoutManager(this));
            binding.recViewUnit.setAdapter(unitAdapter);

        } else if (binding.recViewUnitLand != null) {
            binding.recViewUnitLand.setLayoutManager(new GridLayoutManager(this, 2));
            binding.recViewUnitLand.setAdapter(unitAdapter);

        }

        if (binding.recViewModifiers != null) {
            binding.recViewModifiers.setLayoutManager(new LinearLayoutManager(this));
            binding.recViewModifiers.setAdapter(modifierAdapter);

        } else if (binding.recViewModifiersLand != null) {
            binding.recViewModifiersLand.setLayoutManager(new GridLayoutManager(this, 2));
            binding.recViewModifiersLand.setAdapter(modifierAdapter);

        }

        if (binding.recViewTaxes != null) {
            binding.recViewTaxes.setLayoutManager(new LinearLayoutManager(this));
            binding.recViewTaxes.setAdapter(taxesAdapter);

        } else if (binding.recViewTaxesLand != null) {
            binding.recViewTaxesLand.setLayoutManager(new GridLayoutManager(this, 2));
            binding.recViewTaxesLand.setAdapter(taxesAdapter);

        }

        binding.edtPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.edtPrice.setSelection(Objects.requireNonNull(binding.edtPrice.getText()).toString().length());
            }
        });

        binding.edtCost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.edtCost.setSelection(Objects.requireNonNull(binding.edtCost.getText()).toString().length());
            }
        });


        binding.llBarcodeType.setOnClickListener(this::createBarcodeTypePopupMenu);
        binding.llChooseImage.setOnClickListener(v -> checkPhotoPermission());

        binding.llTakeImage.setOnClickListener(v -> checkCameraPermission());

    }

    private void createCategoryPopupMenu(View view) {
        int pos = 0;
        PopupMenu popupMenu = new PopupMenu(this, view);
        if (mvvm.getCategories().getValue() != null) {
            for (CategoryModel categoryModel : mvvm.getCategories().getValue()) {
                popupMenu.getMenu().add(1, pos, 1, categoryModel.getName());
                pos += 1;
            }
        }


        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 0) {
                AddItemModel addItemModel = mvvm.getAddItemModel().getValue();
                if (addItemModel!=null){
                    addItemModel.setCategoryModel(null);
                    mvvm.getAddItemModel().setValue(addItemModel);
                }
            } else if (item.getItemId() == 1) {
                navigateToAddCategory();
            } else {
                if (mvvm.getCategories().getValue() != null) {
                    AddItemModel model = mvvm.getAddItemModel().getValue();
                    CategoryModel categoryModel = mvvm.getCategories().getValue().get(item.getItemId());
                    Objects.requireNonNull(model).setCategoryModel(categoryModel);
                    mvvm.getAddItemModel().setValue(model);

                }
            }
            return true;
        });

        popupMenu.show();
    }

    private void createBarcodeTypePopupMenu(View view) {
        int pos = 0;
        PopupMenu popupMenu = new PopupMenu(this, view);
        if (mvvm.getBarcodeTypes().getValue() != null) {
            for (String name : mvvm.getBarcodeTypes().getValue()) {
                popupMenu.getMenu().add(1, pos, 1, name);
                pos += 1;
            }
        }


        popupMenu.setOnMenuItemClickListener(item -> {
            if (mvvm.getCategories().getValue() != null) {
                AddItemModel model = mvvm.getAddItemModel().getValue();
                String name = mvvm.getBarcodeTypes().getValue().get(item.getItemId());
                Objects.requireNonNull(model).setBarcode_type(name);
                mvvm.getAddItemModel().setValue(model);

            }
            return true;
        });

        popupMenu.show();
    }


    private void navigateToAddCategory() {
        mvvm.forNavigation = true;
        Intent intent = new Intent(this, AddCategoryActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);

    }

    @SuppressLint("ResourceType")
    private void updateCategoryCheckedColor(int pos) {
        String color;
        switch (pos) {
            case 1:
                color = getResources().getString(R.color.cat2);

                binding.check1.setVisibility(View.GONE);
                binding.check2.setVisibility(View.VISIBLE);
                binding.check3.setVisibility(View.GONE);
                binding.check4.setVisibility(View.GONE);
                binding.check5.setVisibility(View.GONE);
                binding.check6.setVisibility(View.GONE);
                binding.check7.setVisibility(View.GONE);
                binding.check8.setVisibility(View.GONE);

                break;
            case 2:
                color = getResources().getString(R.color.cat3);
                binding.check1.setVisibility(View.GONE);
                binding.check2.setVisibility(View.GONE);
                binding.check3.setVisibility(View.VISIBLE);
                binding.check4.setVisibility(View.GONE);
                binding.check5.setVisibility(View.GONE);
                binding.check6.setVisibility(View.GONE);
                binding.check7.setVisibility(View.GONE);
                binding.check8.setVisibility(View.GONE);
                break;
            case 3:
                color = getResources().getString(R.color.cat4);
                binding.check1.setVisibility(View.GONE);
                binding.check2.setVisibility(View.GONE);
                binding.check3.setVisibility(View.GONE);
                binding.check4.setVisibility(View.VISIBLE);
                binding.check5.setVisibility(View.GONE);
                binding.check6.setVisibility(View.GONE);
                binding.check7.setVisibility(View.GONE);
                binding.check8.setVisibility(View.GONE);
                break;
            case 4:
                color = getResources().getString(R.color.cat5);
                binding.check1.setVisibility(View.GONE);
                binding.check2.setVisibility(View.GONE);
                binding.check3.setVisibility(View.GONE);
                binding.check4.setVisibility(View.GONE);
                binding.check5.setVisibility(View.VISIBLE);
                binding.check6.setVisibility(View.GONE);
                binding.check7.setVisibility(View.GONE);
                binding.check8.setVisibility(View.GONE);
                break;
            case 5:
                color = getResources().getString(R.color.cat6);
                binding.check1.setVisibility(View.GONE);
                binding.check2.setVisibility(View.GONE);
                binding.check3.setVisibility(View.GONE);
                binding.check4.setVisibility(View.GONE);
                binding.check5.setVisibility(View.GONE);
                binding.check6.setVisibility(View.VISIBLE);
                binding.check7.setVisibility(View.GONE);
                binding.check8.setVisibility(View.GONE);
                break;
            case 6:
                color = getResources().getString(R.color.cat7);
                binding.check1.setVisibility(View.GONE);
                binding.check2.setVisibility(View.GONE);
                binding.check3.setVisibility(View.GONE);
                binding.check4.setVisibility(View.GONE);
                binding.check5.setVisibility(View.GONE);
                binding.check6.setVisibility(View.GONE);
                binding.check7.setVisibility(View.VISIBLE);
                binding.check8.setVisibility(View.GONE);
                break;
            case 7:
                color = getResources().getString(R.color.cat8);
                binding.check1.setVisibility(View.GONE);
                binding.check2.setVisibility(View.GONE);
                binding.check3.setVisibility(View.GONE);
                binding.check4.setVisibility(View.GONE);
                binding.check5.setVisibility(View.GONE);
                binding.check6.setVisibility(View.GONE);
                binding.check7.setVisibility(View.GONE);
                binding.check8.setVisibility(View.VISIBLE);
                break;
            default:
                color = getResources().getString(R.color.cat1);
                binding.check1.setVisibility(View.VISIBLE);
                binding.check2.setVisibility(View.GONE);
                binding.check3.setVisibility(View.GONE);
                binding.check4.setVisibility(View.GONE);
                binding.check5.setVisibility(View.GONE);
                binding.check6.setVisibility(View.GONE);
                binding.check7.setVisibility(View.GONE);
                binding.check8.setVisibility(View.GONE);
                break;

        }
        mvvm.getSelectedColorPos().setValue(pos);
        AddItemModel model = mvvm.getAddItemModel().getValue();
        Objects.requireNonNull(model).setColor(color);
        mvvm.getAddItemModel().setValue(model);

        binding.btnSave.setOnClickListener(v -> mvvm.addItem(this));
        binding.cardDelete.setOnClickListener(v -> createAlertDialog());
    }

    @SuppressLint("ResourceType")
    private void updateShapes(int pos) {
        String shape;
        switch (pos) {
            case 1:
                shape = "3";

                binding.shapeCheck1.setVisibility(View.GONE);
                binding.shapeCheck2.setVisibility(View.VISIBLE);
                binding.shapeCheck3.setVisibility(View.GONE);
                binding.shapeCheck4.setVisibility(View.GONE);


                break;
            case 2:
                shape = "1";
                binding.shapeCheck1.setVisibility(View.GONE);
                binding.shapeCheck2.setVisibility(View.GONE);
                binding.shapeCheck3.setVisibility(View.VISIBLE);
                binding.shapeCheck4.setVisibility(View.GONE);

                break;
            case 3:
                shape = "4";
                binding.shapeCheck1.setVisibility(View.GONE);
                binding.shapeCheck2.setVisibility(View.GONE);
                binding.shapeCheck3.setVisibility(View.GONE);
                binding.shapeCheck4.setVisibility(View.VISIBLE);

                break;


            default:
                shape = "2";
                binding.shapeCheck1.setVisibility(View.VISIBLE);
                binding.shapeCheck2.setVisibility(View.GONE);
                binding.shapeCheck3.setVisibility(View.GONE);
                binding.shapeCheck4.setVisibility(View.GONE);

                break;

        }
        mvvm.getSelectedShapePos().setValue(pos);
        AddItemModel model = mvvm.getAddItemModel().getValue();
        Objects.requireNonNull(model).setShapes(shape);
        mvvm.getAddItemModel().setValue(model);
    }

    public void checkCameraPermission() {
        req = 1;
        String[] permissions = new String[]{WRITE_PERM, CAM_PERM};
        if (ContextCompat.checkSelfPermission(this, WRITE_PERM) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, CAM_PERM) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            this.permissions.launch(permissions);
        }
    }

    public void checkPhotoPermission() {
        req = 2;
        String[] permissions = new String[]{READ_PERM};
        if (ContextCompat.checkSelfPermission(this, READ_PERM) == PackageManager.PERMISSION_GRANTED

        ) {
            openGallery();
        } else {
            this.permissions.launch(permissions);
        }
    }

    private void openCamera() {
        mvvm.forNavigation = true;
        req = 1;
        String authority = getPackageName() + ".provider";
        outPutUri = FileProvider.getUriForFile(this, authority, getCameraOutPutFile());

        Intent intentCamera = new Intent();
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        launcher.launch(intentCamera);


    }

    private void openGallery() {
        mvvm.forNavigation = true;
        req = 2;
        Intent intentGallery = new Intent();
        intentGallery.setAction(Intent.ACTION_GET_CONTENT);
        intentGallery.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentGallery.setType("image/*");
        launcher.launch(Intent.createChooser(intentGallery, "Choose photos"));


    }

    private File getCameraOutPutFile() {
        File file = null;
        String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageName = "JPEG_" + stamp + "_";

        File appFile = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            file = File.createTempFile(imageName, ".jpg", appFile);
            imagePath = file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }



    public void selectSoldBy(UnitModel model) {
        Objects.requireNonNull(mvvm.getAddItemModel().getValue()).setUnit_id(model.getId());
        mvvm.getAddItemModel().setValue(mvvm.getAddItemModel().getValue());
    }

    public void selectExtraData(ModifierModel model) {
        Objects.requireNonNull(mvvm.getAddItemModel().getValue()).addModifier(model);
    }

    public void selectTaxes(TaxModel model) {
        Objects.requireNonNull(mvvm.getAddItemModel().getValue()).setTax_id(model.getId());

    }

    private void createAlertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        AlertDeleteDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.alert_delete_dialog, null, false);
        binding.setTitle(getString(R.string.delete_item));
        binding.setContent(getString(R.string.are_sure_delete_item));
        dialog.setView(binding.getRoot());
        binding.btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        binding.btnDelete.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mvvm.showPin){
            showPinCodeView();
        }else {
            hidePinCodeView();
        }

        if (!forImageIntent){
            mvvm.getCategoryData(getUserModel().getData().getUser().getId());
            forImageIntent = false;
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
        super.onBackPressed();
        overridePendingTransition(0, 0);

    }






}