package com.midad_pos.uis.activity_add_category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.midad_pos.R;
import com.midad_pos.adapter.ItemAssignAdapter;
import com.midad_pos.databinding.ActivityAddCategoryBinding;
import com.midad_pos.databinding.AlertDeleteDialogBinding;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.ItemModel;
import com.midad_pos.mvvm.AddCategoryMvvm;
import com.midad_pos.mvvm.BaseMvvm;
import com.midad_pos.uis.activity_add_item.AddItemActivity;
import com.midad_pos.uis.activity_base.BaseActivity;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddCategoryActivity extends BaseActivity {
    private AddCategoryMvvm mvvm;
    private ActivityAddCategoryBinding binding;
    private CategoryModel categoryModel;
    private ItemAssignAdapter itemAssignAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_category);
        setContentView(binding.getRoot());
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        categoryModel = (CategoryModel) intent.getSerializableExtra("data");
    }

    private void initView() {
        mvvm = ViewModelProviders.of(this, new AddCategoryMvvm.MyViewModelFactory(getApplication(), categoryModel)).get(AddCategoryMvvm.class);
        binding.setLang(getLang());

        if (categoryModel == null) {
            binding.setTitle(getString(R.string.create_category));
            binding.cardDelete.setVisibility(View.GONE);

        } else {
            binding.setTitle(getString(R.string.edit_category));
            mvvm.getCategoryName().setValue(categoryModel.getName());
            binding.setEnable(!categoryModel.getName().isEmpty());
            if (mvvm.getPos().getValue() != null) {
                mvvm.getPos().setValue(getPos(categoryModel.getColor()));
            }
            binding.cardDelete.setVisibility(View.VISIBLE);

        }


        if (mvvm.getPos().getValue() != null) {
            updateCategoryCheckedColor(mvvm.getPos().getValue());
        }

        if (mvvm.getCategoryName().getValue() != null) {
            binding.edtCategoryName.setText(mvvm.getCategoryName().getValue());
        }

        mvvm.getOnCategoryAdded().observe(this, categoryModel -> {
            this.categoryModel = categoryModel;
        });

        mvvm.getIsEnableToAssign().observe(this, enabled -> {
            binding.assignDialog.setEnabled(enabled);
        });


        mvvm.getAddedSuccess().observe(this, action -> {

            if (action.isEmpty()) {
                finish();

            } else if (action.equals("assignItems")) {

                /*if (categoryModel == null) {
                    mvvm.getCategoryName().setValue("");
                    binding.edtCategoryName.setText(null);
                }*/


            } else {
                if (categoryModel == null) {
                    mvvm.getCategoryName().setValue("");
                    binding.edtCategoryName.setText(null);
                }

                Intent intent = new Intent(this, AddItemActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);

            }
        });

        mvvm.getDeletedSuccess().observe(this, aBoolean -> {
            finish();
            overridePendingTransition(0, 0);
        });

        mvvm.getOnError().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

        mvvm.getIsDialogShow().observe(this, show -> {
            if (show) {
                mvvm.getItemsData();
                binding.flDialog.setVisibility(View.VISIBLE);

            } else {
                binding.flDialog.setVisibility(View.GONE);

            }
        });

        mvvm.getIsItemsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.assignDialog.loader.setVisibility(View.VISIBLE);
                if (itemAssignAdapter != null) {
                    itemAssignAdapter.updateList(new ArrayList<>());
                }
            } else {
                binding.assignDialog.loader.setVisibility(View.GONE);

            }
        });

        mvvm.getOnAssignSuccess().observe(this, aBoolean -> {
            mvvm.getIsDialogShow().setValue(false);
        });


        if (mvvm.getQuery().getValue() != null) {
            if (mvvm.getQuery().getValue().isEmpty()) {
                binding.assignDialog.llSearch.setVisibility(View.GONE);
            } else {
                binding.assignDialog.llSearch.setVisibility(View.VISIBLE);

            }
        }


        mvvm.getSearchItems().observe(this, data -> {
            if (itemAssignAdapter != null) {
                itemAssignAdapter.updateList(data);
            }
            if (data.size() > 0) {
                binding.assignDialog.tvNoItems.setVisibility(View.GONE);
            } else {
                binding.assignDialog.tvNoItems.setVisibility(View.VISIBLE);

            }
        });


        binding.assignDialog.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.assignDialog.recView.setHasFixedSize(true);
        itemAssignAdapter = new ItemAssignAdapter(this);
        binding.assignDialog.recView.setAdapter(itemAssignAdapter);


        binding.card1.setOnClickListener(v -> {
            updateCategoryCheckedColor(0);
        });
        binding.card2.setOnClickListener(v -> {
            updateCategoryCheckedColor(1);
        });
        binding.card3.setOnClickListener(v -> {
            updateCategoryCheckedColor(2);
        });
        binding.card4.setOnClickListener(v -> {
            updateCategoryCheckedColor(3);
        });
        binding.card5.setOnClickListener(v -> {
            updateCategoryCheckedColor(4);
        });
        binding.card6.setOnClickListener(v -> {
            updateCategoryCheckedColor(5);
        });
        binding.card7.setOnClickListener(v -> {
            updateCategoryCheckedColor(6);
        });

        binding.card8.setOnClickListener(v -> {
            updateCategoryCheckedColor(7);
        });


        binding.btnAssignItem.setOnClickListener(v -> {
            if (!binding.edtCategoryName.getText().toString().isEmpty()) {
                binding.txtInput.setError(null);
                if (categoryModel == null) {
                    mvvm.addCategory(this, getUserModel().getData().getSelectedUser().getId(), "assignItems");

                } else {
                    if (mvvm.getCategoryName().getValue() != null && !mvvm.getCategoryName().getValue().isEmpty() && !mvvm.getCategoryName().getValue().equals(categoryModel.getName())) {
                        mvvm.updateCategory(this, getUserModel().getData().getSelectedUser().getId(), categoryModel, "assignItems");

                    } else if (mvvm.getCategoryName().getValue() != null && !mvvm.getCategoryName().getValue().isEmpty() && mvvm.getCategoryName().getValue().equals(categoryModel.getName())) {
                        mvvm.getIsDialogShow().setValue(true);
                    }
                }
            } else {
                binding.txtInput.setError(getString(R.string.empty_field));

            }
        });

        binding.btnCreateItem.setOnClickListener(v -> {
            if (!binding.edtCategoryName.getText().toString().isEmpty()) {
                binding.txtInput.setError(null);
                if (categoryModel == null) {
                    mvvm.addCategory(this, getUserModel().getData().getSelectedUser().getId(), "addItems");

                } else {
                    if (mvvm.getCategoryName().getValue() != null && !mvvm.getCategoryName().getValue().isEmpty() && !mvvm.getCategoryName().getValue().equals(categoryModel.getName())) {
                        mvvm.updateCategory(this, getUserModel().getData().getSelectedUser().getId(), categoryModel, "addItems");

                    }


                }

            } else {
                binding.txtInput.setError(getString(R.string.empty_field));

            }
        });

        binding.btnSave.setOnClickListener(v -> {
            if (binding.edtCategoryName.getText().toString().isEmpty()) {
                binding.txtInput.setError(null);
            } else {
                binding.txtInput.setError(getString(R.string.empty_field));

            }

            if (categoryModel == null) {
                mvvm.addCategory(this, getUserModel().getData().getSelectedUser().getId(), "");

            } else {
                if (mvvm.getCategoryName().getValue() != null && !mvvm.getCategoryName().getValue().isEmpty() && !mvvm.getCategoryName().getValue().equals(categoryModel.getName())) {
                    mvvm.updateCategory(this, getUserModel().getData().getSelectedUser().getId(), categoryModel, "");
                }


            }

        });

        binding.edtCategoryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString();
                binding.setEnable(name.length() > 0);
                mvvm.getCategoryName().setValue(name);
                binding.txtInput.setError(null);
            }
        });

        binding.arrowBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        binding.assignDialog.closeDialog.setOnClickListener(v -> {
            mvvm.getIsDialogShow().setValue(false);
            mvvm.refreshItems();
            binding.assignDialog.edtSearch.setText("");
            mvvm.search("");

        });
        binding.assignDialog.tvOpenSearch.setOnClickListener(v -> {
            binding.assignDialog.llSearch.setVisibility(View.VISIBLE);
            binding.assignDialog.edtSearch.requestFocus();
        });
        binding.assignDialog.closeSearch.setOnClickListener(v -> {
            binding.assignDialog.llSearch.setVisibility(View.GONE);
            binding.assignDialog.edtSearch.setText("");
        });

        binding.assignDialog.btnSave.setOnClickListener(v -> mvvm.assignItems(this));
        binding.cardDelete.setOnClickListener(v -> createAlertDialog());

        setUpSearch();

    }

    @SuppressLint("CheckResult")
    private void setUpSearch() {
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
                    binding.assignDialog.edtSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            emitter.onNext(s.toString());
                        }
                    });
                })
                .debounce(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(query -> mvvm.search(query));
    }

    @SuppressLint("ResourceType")
    private void updateCategoryCheckedColor(int pos) {
        String color = "";
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
        mvvm.getPos().setValue(pos);
        mvvm.getColor().setValue(color);
    }

    private int getPos(String color) {
        switch (color) {
            case "#fff44336":
                return 1;
            case "#ffe91e63":

                return 2;
            case "#ffff9800":

                return 3;
            case "#ffcddc39":

                return 4;
            case "#ff4caf50":

                return 5;
            case "#ff2196f3":

                return 7;

            default:
                return 0;

        }
    }

    private void createAlertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        AlertDeleteDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.alert_delete_dialog, null, false);
        binding.setTitle(getString(R.string.delete_category));
        binding.setContent(getString(R.string.are_sure_delete_cat));
        dialog.setView(binding.getRoot());
        binding.btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        binding.btnDelete.setOnClickListener(v -> {
            dialog.dismiss();
            mvvm.deleteCategory(this, getUserModel().getData().getSelectedUser().getId(), categoryModel.getId());
        });
        dialog.show();
    }

    public void assignItem(ItemModel itemModel) {
        mvvm.addRemoveAssignItem(itemModel);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mvvm.showPin = true;


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mvvm.showPin){
            showPinCodeView();
        }else {
            hidePinCodeView();
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.flDialog.getVisibility() == View.VISIBLE) {
            binding.flDialog.setVisibility(View.GONE);
            mvvm.search("");

        } else {
            super.onBackPressed();
            overridePendingTransition(0, 0);
        }


    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);

        }catch (Exception e){}
    }


}