package com.midad_pos.uis.activity_items;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.midad_pos.R;
import com.midad_pos.adapter.CategoryAdapter;
import com.midad_pos.databinding.ActivityItemsBinding;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.mvvm.BaseMvvm;
import com.midad_pos.mvvm.ItemsMvvm;
import com.midad_pos.uis.activity_add_category.AddCategoryActivity;
import com.midad_pos.uis.activity_add_item.AddItemActivity;
import com.midad_pos.uis.activity_drawer_base.DrawerBaseActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ItemsActivity extends DrawerBaseActivity {
    private ItemsMvvm mvvm;
    private ActivityItemsBinding binding;
    private CategoryAdapter categoryAdapter;

    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_items, null, false);
        setContentView(binding.getRoot());
        setUpDrawer(binding.toolbarItems, true);
        updateSelectedPos(3);
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mvvm.getCategoriesData(getUserModel().getData().getSelectedUser().getId());

    }

    private void initView() {
        mvvm = ViewModelProviders.of(this).get(ItemsMvvm.class);

        if (mvvm.getPositions().getValue() != null) {
            updateSelections(mvvm.getPositions().getValue());

        }


        mvvm.getIsDeleteMode().observe(this, aBoolean -> {
            if (aBoolean){
                if (binding.categoryLayout != null) {
                    binding.categoryLayout.toolbarDeleteModel.setVisibility(View.VISIBLE);
                }

                if (binding.categoryDetailsLayout != null) {
                    binding.categoryDetailsLayout.toolbarDeleteModel.setVisibility(View.VISIBLE);
                }
            }else {
                if (binding.categoryLayout != null) {
                    binding.categoryLayout.toolbarDeleteModel.setVisibility(View.GONE);
                }

                if (binding.categoryDetailsLayout != null) {
                    binding.categoryDetailsLayout.toolbarDeleteModel.setVisibility(View.GONE);
                }
            }

        });

        mvvm.getDeletedCategoryIds().observe(this, list -> {
            if (binding.categoryLayout != null) {
                binding.categoryLayout.setDeleteCount(list.size()+"");
            }

            if (binding.categoryDetailsLayout != null) {
                binding.categoryDetailsLayout.setDeleteCount(list.size()+"");
            }
        });

        mvvm.getIsCategoryLoading().observe(this, isLoading -> {
            if (binding.categoryLayout != null) {

            }

            if (binding.categoryDetailsLayout != null) {

            }
        });

        mvvm.getCategories().observe(this, categories -> {
            if (categoryAdapter != null) {
                categoryAdapter.updateList(categories);
            }
            if (binding.categoryLayout != null) {
                if (categories.size() > 0) {

                    binding.categoryLayout.llNoCategories.setVisibility(View.GONE);

                } else {
                    binding.categoryLayout.llNoCategories.setVisibility(View.VISIBLE);

                }
            }

            if (binding.categoryDetailsLayout != null) {
                if (categories.size() > 0) {

                    binding.categoryDetailsLayout.llNoCategories.setVisibility(View.GONE);

                } else {
                    binding.categoryDetailsLayout.llNoCategories.setVisibility(View.VISIBLE);

                }

            }

        });


        categoryAdapter = new CategoryAdapter(this);

        if (binding.categoryLayout != null) {
            binding.categoryLayout.recView.setLayoutManager(new LinearLayoutManager(this));
            binding.categoryLayout.recView.setDrawingCacheEnabled(true);
            binding.categoryLayout.recView.setItemViewCacheSize(20);
            binding.categoryLayout.recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

            binding.categoryLayout.recView.setAdapter(categoryAdapter);

            binding.categoryLayout.imageNormalMode.setOnClickListener(v -> {
                mvvm.clearDeletedCategoryIds();
            });
        }

        if (binding.categoryDetailsLayout != null) {
            binding.categoryDetailsLayout.recView.setLayoutManager(new LinearLayoutManager(this));
            binding.categoryDetailsLayout.recView.setDrawingCacheEnabled(true);
            binding.categoryDetailsLayout.recView.setItemViewCacheSize(20);
            binding.categoryDetailsLayout.recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

            binding.categoryDetailsLayout.recView.setAdapter(categoryAdapter);

            binding.categoryDetailsLayout.imageNormalMode.setOnClickListener(v -> {
                mvvm.clearDeletedCategoryIds();
            });
        }


        if (binding.flItemDetailsLand != null) {
            mvvm.getPositions().setValue(mvvm.getPositions().getValue() != -1 ? mvvm.getPositions().getValue() : 0);
            updateSelections(mvvm.getPositions().getValue() != -1 ? mvvm.getPositions().getValue() : 0);
        }

        if (binding.itemsLayout != null && binding.itemsLayout.arrowBack != null) {
            binding.itemsLayout.setLang(getLang());
            if (mvvm.getQueryItems().getValue() != null && mvvm.getQueryItems().getValue().isEmpty()) {
                binding.itemsLayout.search.setVisibility(View.GONE);


            } else {
                binding.itemsLayout.search.setVisibility(View.VISIBLE);
                new Handler()
                        .postDelayed(() -> binding.itemsLayout.search.setText(mvvm.getQueryItems().getValue()), 100);

            }
            binding.itemsLayout.arrowBack.setOnClickListener(view -> {
                if (binding.flItemsLayout != null) {
                    if (binding.itemsLayout.search.getVisibility() == View.VISIBLE) {
                        if (mvvm.getQueryItems().getValue() != null && mvvm.getQueryItems().getValue().isEmpty()) {
                            binding.itemsLayout.search.setVisibility(View.GONE);

                        } else {
                            binding.itemsLayout.search.setText("");
                            mvvm.getQueryItems().setValue("");
                        }

                    } else {
                        binding.flItemsLayout.setVisibility(View.GONE);

                    }
                }
            });

            setUpItemSearch();
        }

        if (binding.categoryLayout != null && binding.categoryLayout.arrowBack != null) {
            binding.categoryLayout.setLang(getLang());
            if (mvvm.getQueryCategory().getValue() != null && mvvm.getQueryCategory().getValue().isEmpty()) {
                binding.categoryLayout.search.setVisibility(View.GONE);
            } else {
                binding.categoryLayout.search.setVisibility(View.VISIBLE);

                new Handler()
                        .postDelayed(() -> binding.categoryLayout.search.setText(mvvm.getQueryCategory().getValue()), 100);


            }
            binding.categoryLayout.arrowBack.setOnClickListener(view -> {


                if (binding.flCategoryLayout != null) {

                    if (binding.categoryLayout.search.getVisibility() == View.VISIBLE) {
                        if (mvvm.getQueryCategory().getValue() != null && mvvm.getQueryCategory().getValue().isEmpty()) {
                            binding.categoryLayout.search.setVisibility(View.GONE);

                        } else {
                            binding.categoryLayout.search.setText("");
                            mvvm.searchCategories("");
                        }

                    } else {
                        binding.flCategoryLayout.setVisibility(View.GONE);

                    }

                }
            });


            setUpCategorySearch();

        }

        if (binding.discountLayout != null && binding.discountLayout.arrowBack != null) {
            binding.discountLayout.setLang(getLang());
            if (mvvm.getQueryDiscounts().getValue() != null && mvvm.getQueryDiscounts().getValue().isEmpty()) {
                binding.discountLayout.search.setVisibility(View.GONE);
            } else {
                binding.discountLayout.search.setVisibility(View.VISIBLE);
                new Handler()
                        .postDelayed(() -> binding.discountLayout.search.setText(mvvm.getQueryDiscounts().getValue()), 100);

            }
            binding.discountLayout.arrowBack.setOnClickListener(view -> {
                if (binding.flDiscountLayout != null) {
                    if (binding.discountLayout.search.getVisibility() == View.VISIBLE) {
                        if (mvvm.getQueryDiscounts().getValue() != null && mvvm.getQueryDiscounts().getValue().isEmpty()) {
                            binding.discountLayout.search.setVisibility(View.GONE);

                        } else {
                            binding.discountLayout.search.setText("");
                            mvvm.searchDiscount("");
                        }

                    } else {
                        binding.flDiscountLayout.setVisibility(View.GONE);

                    }
                }
            });
            setUpDiscountSearch();
        }

        if (binding.itemsDetailsLayout != null && binding.itemsDetailsLayout.llSearch != null) {
            binding.itemsDetailsLayout.setLang(getLang());
            if (mvvm.getQueryItems().getValue() != null && mvvm.getQueryItems().getValue().isEmpty()) {
                binding.itemsDetailsLayout.llSearch.setVisibility(View.GONE);
            } else {
                binding.itemsDetailsLayout.llSearch.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> binding.itemsDetailsLayout.search.setText(mvvm.getQueryItems().getValue()), 100);

            }

            setUpItemSearch();
        }

        if (binding.categoryDetailsLayout != null && binding.categoryDetailsLayout.llSearch != null) {
            binding.categoryDetailsLayout.setLang(getLang());
            if (mvvm.getQueryCategory().getValue() != null && mvvm.getQueryCategory().getValue().isEmpty()) {
                binding.categoryDetailsLayout.llSearch.setVisibility(View.GONE);
            } else {
                binding.categoryDetailsLayout.llSearch.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> binding.categoryDetailsLayout.search.setText(mvvm.getQueryCategory().getValue()), 100);

            }

            setUpCategorySearch();
        }

        if (binding.discountDetailsLayout != null && binding.discountDetailsLayout.llSearch != null) {
            binding.discountDetailsLayout.setLang(getLang());
            if (mvvm.getQueryDiscounts().getValue() != null && mvvm.getQueryDiscounts().getValue().isEmpty()) {
                binding.discountDetailsLayout.llSearch.setVisibility(View.GONE);
            } else {
                binding.discountDetailsLayout.llSearch.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> binding.discountDetailsLayout.search.setText(mvvm.getQueryDiscounts().getValue()), 100);

            }

            setUpDiscountSearch();
        }

        if (binding.itemsLayout != null && binding.itemsLayout.searchIcon != null) {
            binding.itemsLayout.searchIcon.setOnClickListener(view -> {
                binding.itemsLayout.search.setVisibility(View.VISIBLE);
                binding.itemsLayout.search.setFocusable(true);
                binding.itemsLayout.search.requestFocus();
            });
        }

        if (binding.categoryLayout != null && binding.categoryLayout.searchIcon != null) {
            binding.categoryLayout.searchIcon.setOnClickListener(view -> {
                binding.categoryLayout.search.setVisibility(View.VISIBLE);
                binding.categoryLayout.search.setFocusable(true);
                binding.categoryLayout.search.requestFocus();

            });
        }

        if (binding.discountLayout != null && binding.discountLayout.searchIcon != null) {
            binding.discountLayout.searchIcon.setOnClickListener(view -> {
                binding.discountLayout.search.setVisibility(View.VISIBLE);
                binding.discountLayout.search.setFocusable(true);
                binding.discountLayout.search.requestFocus();

            });
        }

        if (binding.itemsDetailsLayout != null && binding.itemsDetailsLayout.searchIconLand != null && binding.itemsDetailsLayout.llSearch != null) {
            binding.itemsDetailsLayout.searchIconLand.setOnClickListener(view -> {
                binding.itemsDetailsLayout.llSearch.setVisibility(View.VISIBLE);
                binding.itemsDetailsLayout.search.setFocusable(true);
                binding.itemsDetailsLayout.search.requestFocus();

            });
        }

        if (binding.categoryDetailsLayout != null && binding.categoryDetailsLayout.searchIconLand != null && binding.categoryDetailsLayout.llSearch != null) {
            binding.categoryDetailsLayout.searchIconLand.setOnClickListener(view -> {
                binding.categoryDetailsLayout.llSearch.setVisibility(View.VISIBLE);
                binding.categoryDetailsLayout.search.setFocusable(true);
                binding.categoryDetailsLayout.search.requestFocus();

            });
        }

        if (binding.discountDetailsLayout != null && binding.discountDetailsLayout.searchIconLand != null && binding.discountDetailsLayout.llSearch != null) {
            binding.discountDetailsLayout.searchIconLand.setOnClickListener(view -> {
                binding.discountDetailsLayout.llSearch.setVisibility(View.VISIBLE);
                binding.discountDetailsLayout.search.setFocusable(true);
                binding.discountDetailsLayout.search.requestFocus();

            });
        }

        if (binding.itemsDetailsLayout != null && binding.itemsDetailsLayout.arrowBackLand != null && binding.itemsDetailsLayout.llSearch != null) {
            binding.itemsDetailsLayout.arrowBackLand.setOnClickListener(view -> {
                if (binding.itemsDetailsLayout.llSearch.getVisibility() == View.VISIBLE) {
                    if (mvvm.getQueryItems().getValue() != null && mvvm.getQueryItems().getValue().isEmpty()) {
                        binding.itemsDetailsLayout.llSearch.setVisibility(View.GONE);
                    } else {
                        binding.itemsDetailsLayout.search.setText("");
                        mvvm.searchItems("");

                    }

                }

            });
        }

        if (binding.categoryDetailsLayout != null && binding.categoryDetailsLayout.arrowBackLand != null && binding.categoryDetailsLayout.llSearch != null) {
            binding.categoryDetailsLayout.arrowBackLand.setOnClickListener(view -> {
                if (binding.categoryDetailsLayout.llSearch.getVisibility() == View.VISIBLE) {
                    if (mvvm.getQueryCategory().getValue() != null && mvvm.getQueryCategory().getValue().isEmpty()) {
                        binding.categoryDetailsLayout.llSearch.setVisibility(View.GONE);

                    } else {
                        binding.categoryDetailsLayout.search.setText("");
                        mvvm.searchCategories("");
                    }

                }
            });
        }

        if (binding.discountDetailsLayout != null && binding.discountDetailsLayout.arrowBackLand != null && binding.discountDetailsLayout.llSearch != null) {
            binding.discountDetailsLayout.arrowBackLand.setOnClickListener(view -> {
                if (binding.discountDetailsLayout.llSearch.getVisibility() == View.VISIBLE) {
                    if (mvvm.getQueryDiscounts().getValue() != null && mvvm.getQueryDiscounts().getValue().isEmpty()) {
                        binding.discountDetailsLayout.llSearch.setVisibility(View.GONE);

                    } else {
                        binding.discountDetailsLayout.search.setText("");
                        mvvm.searchDiscount("");
                    }

                }
            });
        }

        if (binding.categoryLayout != null) {
            binding.categoryLayout.categoryFab.setOnClickListener(v -> {
                navigateToAddCategoryActivity();
            });
        }

        if (binding.categoryDetailsLayout != null) {
            binding.categoryDetailsLayout.categoryFab.setOnClickListener(v -> {
                navigateToAddCategoryActivity();
            });
        }


        binding.items.setOnClickListener(view -> {
            mvvm.getPositions().setValue(0);
            updateSelections(0);
        });

        binding.categories.setOnClickListener(view -> {
            mvvm.getPositions().setValue(1);
            updateSelections(1);
        });

        binding.discounts.setOnClickListener(view -> {
            mvvm.getPositions().setValue(2);
            updateSelections(2);
        });

        if (binding.itemsLayout!=null){
            binding.itemsLayout.fabAddItem.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddItemActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            });
        }

        if (binding.itemsDetailsLayout!=null){
            binding.itemsDetailsLayout.fabAddItem.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddItemActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            });
        }



    }

    private void navigateToAddCategoryActivity() {
        Intent intent = new Intent(this, AddCategoryActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);

    }

    private void setUpItemSearch() {

        Disposable d = Observable.create((ObservableOnSubscribe<String>) emitter -> {
                    if (binding.itemsLayout != null) {
                        binding.itemsLayout.search.addTextChangedListener(new TextWatcher() {
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
                        });
                    } else if (binding.itemsDetailsLayout != null) {
                        binding.itemsDetailsLayout.search.addTextChangedListener(new TextWatcher() {
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
                        });

                    }


                }).debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((query) -> mvvm.searchItems(query));

        disposable.add(d);

    }

    private void setUpCategorySearch() {
        Disposable d = Observable.create((ObservableOnSubscribe<String>) emitter -> {
                    if (binding.categoryLayout != null) {
                        binding.categoryLayout.search.addTextChangedListener(new TextWatcher() {
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
                        });
                    } else if (binding.categoryDetailsLayout != null) {
                        binding.categoryDetailsLayout.search.addTextChangedListener(new TextWatcher() {
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
                        });

                    }


                }).debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((query) -> mvvm.searchCategories(query));

        disposable.add(d);
    }

    private void setUpDiscountSearch() {
        Disposable d = Observable.create((ObservableOnSubscribe<String>) emitter -> {
                    if (binding.discountLayout != null) {
                        binding.discountLayout.search.addTextChangedListener(new TextWatcher() {
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
                        });
                    } else if (binding.discountDetailsLayout != null) {
                        binding.discountDetailsLayout.search.addTextChangedListener(new TextWatcher() {
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
                        });

                    }


                }).debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((query) -> mvvm.searchDiscount(query));

        disposable.add(d);
    }


    private void updateSelections(int pos) {
        if (pos == 0) {
            binding.items.setBackgroundResource(R.color.grey1);
            binding.categories.setBackgroundResource(R.color.transparent);
            binding.discounts.setBackgroundResource(R.color.transparent);


            if (binding.flItemsLayout != null && binding.flCategoryLayout != null && binding.flDiscountLayout != null) {
                binding.flItemsLayout.setVisibility(View.VISIBLE);
                binding.flCategoryLayout.setVisibility(View.GONE);
                binding.flDiscountLayout.setVisibility(View.GONE);

                binding.items.setBackgroundResource(R.color.transparent);
                binding.categories.setBackgroundResource(R.color.transparent);
                binding.discounts.setBackgroundResource(R.color.transparent);

            } else if (binding.flItemDetailsLand != null && binding.flCategoryDetailsLand != null && binding.flDiscountsDetailsLand != null) {
                binding.flItemDetailsLand.setVisibility(View.VISIBLE);
                binding.flCategoryDetailsLand.setVisibility(View.GONE);
                binding.flDiscountsDetailsLand.setVisibility(View.GONE);
                if (binding.iconItem != null && binding.iconCategory != null && binding.iconDiscount != null && binding.tvItem != null && binding.tvCategory != null && binding.tvDiscount != null) {
                    binding.iconItem.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    binding.iconCategory.setColorFilter(getResources().getColor(R.color.grey4));
                    binding.iconDiscount.setColorFilter(getResources().getColor(R.color.grey4));

                    binding.tvItem.setTextColor(getResources().getColor(R.color.colorPrimary));
                    binding.tvCategory.setTextColor(getResources().getColor(R.color.black));
                    binding.tvDiscount.setTextColor(getResources().getColor(R.color.black));

                }
            }

        } else if (pos == 1) {
            binding.items.setBackgroundResource(R.color.transparent);
            binding.categories.setBackgroundResource(R.color.grey1);
            binding.discounts.setBackgroundResource(R.color.transparent);

            if (binding.flItemsLayout != null && binding.flCategoryLayout != null && binding.flDiscountLayout != null) {
                binding.flItemsLayout.setVisibility(View.GONE);
                binding.flCategoryLayout.setVisibility(View.VISIBLE);
                binding.flDiscountLayout.setVisibility(View.GONE);

                binding.items.setBackgroundResource(R.color.transparent);
                binding.categories.setBackgroundResource(R.color.transparent);
                binding.discounts.setBackgroundResource(R.color.transparent);

            } else if (binding.flItemDetailsLand != null && binding.flCategoryDetailsLand != null && binding.flDiscountsDetailsLand != null) {
                binding.flItemDetailsLand.setVisibility(View.GONE);
                binding.flCategoryDetailsLand.setVisibility(View.VISIBLE);
                binding.flDiscountsDetailsLand.setVisibility(View.GONE);

                if (binding.iconItem != null && binding.iconCategory != null && binding.iconDiscount != null && binding.tvItem != null && binding.tvCategory != null && binding.tvDiscount != null) {
                    binding.iconItem.setColorFilter(getResources().getColor(R.color.grey4));
                    binding.iconCategory.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    binding.iconDiscount.setColorFilter(getResources().getColor(R.color.grey4));

                    binding.tvItem.setTextColor(getResources().getColor(R.color.black));
                    binding.tvCategory.setTextColor(getResources().getColor(R.color.colorPrimary));
                    binding.tvDiscount.setTextColor(getResources().getColor(R.color.black));

                }
            }


        } else if (pos == 2) {
            binding.items.setBackgroundResource(R.color.transparent);
            binding.categories.setBackgroundResource(R.color.transparent);
            binding.discounts.setBackgroundResource(R.color.grey1);

            if (binding.flItemsLayout != null && binding.flCategoryLayout != null && binding.flDiscountLayout != null) {
                binding.flItemsLayout.setVisibility(View.GONE);
                binding.flCategoryLayout.setVisibility(View.GONE);
                binding.flDiscountLayout.setVisibility(View.VISIBLE);

                binding.items.setBackgroundResource(R.color.transparent);
                binding.categories.setBackgroundResource(R.color.transparent);
                binding.discounts.setBackgroundResource(R.color.transparent);

            } else if (binding.flItemDetailsLand != null && binding.flCategoryDetailsLand != null && binding.flDiscountsDetailsLand != null) {
                binding.flItemDetailsLand.setVisibility(View.GONE);
                binding.flCategoryDetailsLand.setVisibility(View.GONE);
                binding.flDiscountsDetailsLand.setVisibility(View.VISIBLE);

                if (binding.iconItem != null && binding.iconCategory != null && binding.iconDiscount != null && binding.tvItem != null && binding.tvCategory != null && binding.tvDiscount != null) {
                    binding.iconItem.setColorFilter(getResources().getColor(R.color.grey4));
                    binding.iconCategory.setColorFilter(getResources().getColor(R.color.grey4));
                    binding.iconDiscount.setColorFilter(getResources().getColor(R.color.colorPrimary));

                    binding.tvItem.setTextColor(getResources().getColor(R.color.black));
                    binding.tvCategory.setTextColor(getResources().getColor(R.color.black));
                    binding.tvDiscount.setTextColor(getResources().getColor(R.color.colorPrimary));

                }
            }
        }


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public void onBackPressed() {
        if (binding.flItemsLayout != null && binding.flItemsLayout.getVisibility() == View.VISIBLE) {
            binding.flItemsLayout.setVisibility(View.GONE);
        } else if (binding.flCategoryLayout != null && binding.flCategoryLayout.getVisibility() == View.VISIBLE) {
            binding.flCategoryLayout.setVisibility(View.GONE);
        } else if (binding.flDiscountLayout != null && binding.flDiscountLayout.getVisibility() == View.VISIBLE) {
            binding.flDiscountLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }


    }

    public void updateDeleteModel(int adapterPosition) {
        if (mvvm.getDeletedCategoryIds().getValue()!=null&&mvvm.getDeletedCategoryIds().getValue().size()==0){
            mvvm.getIsDeleteMode().setValue(true);
            mvvm.addCategoryIdsToDelete(adapterPosition);
            categoryAdapter.notifyItemChanged(adapterPosition);

        }


    }

    public void selectDeleteCategory(int adapterPosition,CategoryModel categoryModel) {
        if (mvvm.getIsDeleteMode().getValue()!=null&&mvvm.getIsDeleteMode().getValue()){
            if (categoryModel.isSelected()){
                categoryModel.setSelected(false);
                mvvm.removeCategoryIdFromDeletedList(adapterPosition);
            }else {
                mvvm.addCategoryIdsToDelete(adapterPosition);

            }
            categoryAdapter.notifyItemChanged(adapterPosition);
        }



    }
}