package com.midad_pos.uis.activity_add_item;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.midad_pos.R;
import com.midad_pos.databinding.ActivityAddItemBinding;
import com.midad_pos.model.AddItemModel;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.mvvm.AddItemMvvm;
import com.midad_pos.uis.activity_base.BaseActivity;

import java.util.List;
import java.util.Objects;

public class AddItemActivity extends BaseActivity {
    private AddItemMvvm mvvm;
    private ActivityAddItemBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_item);
        initView();
    }

    private void initView() {
        mvvm = ViewModelProviders.of(this).get(AddItemMvvm.class);
        binding.setLang(getLang());

        mvvm.getAddItemModel().observe(this, model -> {
            binding.setModel(model);

        });
        mvvm.getIsValid().observe(this, isLoading -> {
            if (isLoading) {
                binding.loader.setVisibility(View.VISIBLE);
                binding.scrollView.setVisibility(View.GONE);
            } else {
                binding.loader.setVisibility(View.GONE);
                binding.scrollView.setVisibility(View.VISIBLE);
            }
        });
        mvvm.getHomeData().observe(this, homeData -> {

        });


        updateCategoryCheckedColor(mvvm.getSelectedColorPos().getValue()!=null?mvvm.getSelectedColorPos().getValue():0);

        updateShapes(mvvm.getSelectedShapePos().getValue()!=null?mvvm.getSelectedShapePos().getValue():0);

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



        binding.shape1.setOnClickListener(v -> {
            updateShapes(0);
        });
        binding.shape2.setOnClickListener(v -> {
            updateShapes(1);
        });
        binding.shape3.setOnClickListener(v -> {
            updateShapes(2);
        });
        binding.shape4.setOnClickListener(v -> {
            updateShapes(3);
        });
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
            } else if (item.getItemId() == 1) {
                navigateToAddCategory();
            } else {
                if (mvvm.getCategories().getValue() != null) {
                    AddItemModel model = mvvm.getAddItemModel().getValue();
                    Objects.requireNonNull(model).setCategoryModel(mvvm.getCategories().getValue().get(item.getItemId()));
                    mvvm.getAddItemModel().setValue(model);

                }
            }
            return true;
        });

        popupMenu.show();
    }

    private void navigateToAddCategory() {


    }

    @SuppressLint("ResourceType")
    private void updateCategoryCheckedColor(int pos) {
        Log.e("pos",pos+"");
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
        mvvm.getSelectedColorPos().setValue(pos);
        AddItemModel model = mvvm.getAddItemModel().getValue();
        Objects.requireNonNull(model).setColor(color);
        mvvm.getAddItemModel().setValue(model);

    }

    @SuppressLint("ResourceType")
    private void updateShapes(int pos) {
        String shape = "";
        switch (pos) {
            case 1:
                shape ="2";

                binding.shapeCheck1.setVisibility(View.GONE);
                binding.shapeCheck2.setVisibility(View.VISIBLE);
                binding.shapeCheck3.setVisibility(View.GONE);
                binding.shapeCheck4.setVisibility(View.GONE);


                break;
            case 2:
                shape ="3";
                binding.shapeCheck1.setVisibility(View.GONE);
                binding.shapeCheck2.setVisibility(View.GONE);
                binding.shapeCheck3.setVisibility(View.VISIBLE);
                binding.shapeCheck4.setVisibility(View.GONE);

                break;
            case 3:
                shape ="4";
                binding.shapeCheck1.setVisibility(View.GONE);
                binding.shapeCheck2.setVisibility(View.GONE);
                binding.shapeCheck3.setVisibility(View.GONE);
                binding.shapeCheck4.setVisibility(View.VISIBLE);

                break;


            default:
                shape = "1";
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);

    }
}