package com.midad_pos.mvvm;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.R;
import com.midad_pos.model.CategoryDataModel;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.ItemModel;
import com.midad_pos.model.ItemsDataModel;
import com.midad_pos.model.StatusResponse;
import com.midad_pos.model.UserModel;
import com.midad_pos.preferences.Preferences;
import com.midad_pos.remote.Api;
import com.midad_pos.share.Common;
import com.midad_pos.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class ItemsMvvm extends AndroidViewModel {
    private final String TAG = ItemsMvvm.class.getName();
    private MutableLiveData<Integer> positions;
    private MutableLiveData<String> queryItems;
    private MutableLiveData<String> queryCategory;
    private MutableLiveData<String> queryDiscounts;
    private MutableLiveData<Boolean> isCategoryLoading;
    private MutableLiveData<List<CategoryModel>> categories;
    private MutableLiveData<String> onError;
    private MutableLiveData<Boolean> isDeleteMode;
    private MutableLiveData<List<Integer>> deletedCategoryIds;
    private List<CategoryModel> categoriesList = new ArrayList<>();
    private UserModel userModel;
    ///////////////////////////////////////////////////////////////
    private MutableLiveData<Boolean> isItemsLoading;
    private MutableLiveData<List<ItemModel>> items;
    private MutableLiveData<List<ItemModel>> mainItemsList;

    private MutableLiveData<Boolean> isItemsDeleteMode;
    private MutableLiveData<List<Integer>> deletedItemsIds;
    private MutableLiveData<List<CategoryModel>> itemCategories;

    private MutableLiveData<CategoryModel> selectedItemCategory;

    private CompositeDisposable disposable = new CompositeDisposable();


    public ItemsMvvm(@NonNull Application application) {
        super(application);
        userModel = Preferences.getInstance().getUserData(application.getApplicationContext());


    }

    public MutableLiveData<Integer> getPositions() {
        if (positions == null) {
            positions = new MutableLiveData<>();
            positions.setValue(-1);
        }

        return positions;
    }

    public MutableLiveData<String> getQueryItems() {
        if (queryItems == null) {
            queryItems = new MutableLiveData<>();
            queryItems.setValue("");
        }

        return queryItems;
    }

    public MutableLiveData<String> getQueryCategory() {
        if (queryCategory == null) {
            queryCategory = new MutableLiveData<>();
            queryCategory.setValue("");
        }

        return queryCategory;
    }

    public MutableLiveData<CategoryModel> getSelectedItemCategory() {
        if (selectedItemCategory == null) {
            selectedItemCategory = new MutableLiveData<>();
            selectedItemCategory.setValue(new CategoryModel(-1, getApplication().getApplicationContext().getString(R.string.all_items)));
        }

        return selectedItemCategory;
    }

    public MutableLiveData<String> getQueryDiscounts() {
        if (queryDiscounts == null) {
            queryDiscounts = new MutableLiveData<>();
            queryDiscounts.setValue("");
        }

        return queryDiscounts;
    }

    public MutableLiveData<List<CategoryModel>> getCategories() {
        if (categories == null) {
            categories = new MutableLiveData<>();
        }

        return categories;
    }

    public MutableLiveData<List<CategoryModel>> getItemCategories() {
        if (itemCategories == null) {
            itemCategories = new MutableLiveData<>();
            List<CategoryModel> itemsCat = new ArrayList<>();
            itemsCat.add(new CategoryModel(-1, getApplication().getApplicationContext().getString(R.string.all_items)));

            itemCategories.setValue(itemsCat);
        }

        return itemCategories;
    }

    public MutableLiveData<Boolean> getIsCategoryLoading() {
        if (isCategoryLoading == null) {
            isCategoryLoading = new MutableLiveData<>();
        }

        return isCategoryLoading;
    }

    public MutableLiveData<Boolean> getIsDeleteMode() {
        if (isDeleteMode == null) {
            isDeleteMode = new MutableLiveData<>();
        }

        return isDeleteMode;
    }

    public MutableLiveData<Boolean> getIsItemsDeleteMode() {
        if (isItemsDeleteMode == null) {
            isItemsDeleteMode = new MutableLiveData<>();
        }

        return isItemsDeleteMode;
    }

    public MutableLiveData<Boolean> getIsItemsLoading() {
        if (isItemsLoading == null) {
            isItemsLoading = new MutableLiveData<>();
        }

        return isItemsLoading;
    }

    public MutableLiveData<List<ItemModel>> getItems() {
        if (items == null) {
            items = new MutableLiveData<>();
        }

        return items;
    }

    public MutableLiveData<List<ItemModel>> getMainItemsList() {
        if (mainItemsList == null) {
            mainItemsList = new MutableLiveData<>();
        }

        return mainItemsList;
    }


    public MutableLiveData<String> getOnError() {
        if (onError == null) {
            onError = new MutableLiveData<>();
        }

        return onError;
    }

    public MutableLiveData<List<Integer>> getDeletedCategoryIds() {
        if (deletedCategoryIds == null) {
            deletedCategoryIds = new MutableLiveData<>();
            deletedCategoryIds.setValue(new ArrayList<>());
        }

        return deletedCategoryIds;
    }

    public MutableLiveData<List<Integer>> getDeletedItemsIds() {
        if (deletedItemsIds == null) {
            deletedItemsIds = new MutableLiveData<>();
            deletedItemsIds.setValue(new ArrayList<>());
        }

        return deletedItemsIds;
    }


    public void searchItems(String query) {
        getQueryItems().setValue(query);
        if (query.isEmpty()) {
            if (getSelectedItemCategory().getValue() != null) {
                if (getSelectedItemCategory().getValue().getId() == -1) {
                    getItems().setValue(getMainItemsList().getValue());

                } else {

                    if (getMainItemsList().getValue() != null) {
                        List<ItemModel> list = new ArrayList<>();
                        for (ItemModel itemModel : getMainItemsList().getValue()) {
                            if (itemModel.getCategory() != null) {
                                if (itemModel.getCategory().getId() == getSelectedItemCategory().getValue().getId()) {
                                    list.add(itemModel);

                                }
                            } else {
                                list.add(itemModel);

                            }
                        }

                        getItems().setValue(list);
                    }

                }
            }
        } else {
            if (getMainItemsList().getValue() != null) {
                List<ItemModel> list = new ArrayList<>();
                for (ItemModel itemModel : getMainItemsList().getValue()) {
                    if (itemModel.getCategory() != null) {
                        if (getSelectedItemCategory().getValue() != null && getSelectedItemCategory().getValue().getId() == -1) {
                            if (itemModel.getName().startsWith(query)) {
                                list.add(itemModel);
                            }
                        } else {
                            if (itemModel.getName().startsWith(query) && itemModel.getCategory().getId() == getSelectedItemCategory().getValue().getId()) {
                                list.add(itemModel);
                            }
                        }
                    } else {
                        if (itemModel.getName().startsWith(query)) {
                            list.add(itemModel);
                        }
                    }
                }

                getItems().setValue(list);
            }

        }

    }

    public void searchCategories(String query) {
        if (getCategories().getValue() != null) {

            if (query.isEmpty()) {
                getCategories().setValue(categoriesList);

            } else {
                List<CategoryModel> list = new ArrayList<>();
                for (CategoryModel categoryModel : getCategories().getValue()) {
                    if (categoryModel.getName().toLowerCase().startsWith(query) || categoryModel.getName().toLowerCase().equals(query)) {
                        list.add(categoryModel);
                    }
                }
                getCategories().setValue(list);

            }


        }

        getQueryCategory().setValue(query);
    }

    public void searchDiscount(String query) {

        getQueryDiscounts().setValue(query);
    }

    public void getCategoriesData(String user_id) {
        if (categoriesList.size()>0){
            getIsCategoryLoading().setValue(false);

        }else {
            getIsCategoryLoading().setValue(true);

        }
        Api.getService(Tags.base_url)
                .categories(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<CategoryDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<CategoryDataModel> response) {
                        getIsCategoryLoading().setValue(false);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    List<CategoryModel> list = new ArrayList<>(response.body().getData());
                                    categoriesList.clear();
                                    categoriesList.addAll(response.body().getData());
                                    List<CategoryModel> itemsCat = new ArrayList<>();
                                    itemsCat.add(new CategoryModel(-1, getApplication().getApplicationContext().getString(R.string.all_items)));
                                    itemsCat.addAll(list);
                                    getItemCategories().setValue(itemsCat);

                                    updateCategoryList(list);

                                } else {
                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));
                                }
                            } else {

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                        } else {

                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        getIsCategoryLoading().setValue(false);

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                    }
                });
    }

    private void updateCategoryList(List<CategoryModel> list) {
        if (getDeletedCategoryIds().getValue() != null) {
            for (CategoryModel categoryModel : list) {
                for (int id : getDeletedCategoryIds().getValue()) {
                    if (categoryModel.getId() == id) {
                        categoryModel.setSelected(true);
                    }
                }
            }

            if (getQueryCategory().getValue() != null) {
                if (getQueryCategory().getValue().isEmpty()) {
                    getCategories().setValue(list);

                } else {
                    searchCategories(getQueryCategory().getValue());
                }
            } else {
                getCategories().setValue(list);

            }
        }


    }

    public void clearDeletedCategoryIds() {
        if (deletedCategoryIds.getValue() != null) {
            deletedCategoryIds.getValue().clear();
            deletedCategoryIds.setValue(deletedCategoryIds.getValue());
            getIsDeleteMode().setValue(false);
            if (getCategories().getValue() != null) {
                for (CategoryModel categoryModel : getCategories().getValue()) {
                    if (categoryModel.isSelected()) {
                        categoryModel.setSelected(false);
                    }
                }

                getCategories().setValue(getCategories().getValue());
            }

        }
    }


    public void addCategoryIdsToDelete(int pos) {
        if (getDeletedCategoryIds().getValue() != null && getCategories().getValue() != null) {
            getDeletedCategoryIds().getValue().add(getCategories().getValue().get(pos).getId());
            getDeletedCategoryIds().setValue(getDeletedCategoryIds().getValue());
            CategoryModel categoryModel = getCategories().getValue().get(pos);
            categoryModel.setSelected(true);

        }

    }

    public void addItemsIdsToDelete(int pos) {
        if (getDeletedItemsIds().getValue() != null && getItems().getValue() != null) {
            getDeletedItemsIds().getValue().add(Integer.parseInt(getItems().getValue().get(pos).getId()));
            getDeletedItemsIds().setValue(getDeletedItemsIds().getValue());
            ItemModel itemModel = getItems().getValue().get(pos);
            itemModel.setSelected(true);

        }

    }

    public void removeCategoryIdFromDeletedList(int pos) {
        if (getDeletedCategoryIds().getValue() != null && getCategories().getValue() != null) {
            if (getDeletedCategoryIds().getValue().size() > 0) {
                CategoryModel categoryModel = getCategories().getValue().get(pos);
                int index = getDeletedCategoryPos(categoryModel.getId());
                if (index != -1) {
                    getDeletedCategoryIds().getValue().remove(index);
                    getDeletedCategoryIds().setValue(getDeletedCategoryIds().getValue());

                }
                if (getDeletedCategoryIds().getValue().size() == 0) {
                    getIsDeleteMode().setValue(false);
                }
            }
        }


    }

    public void removeItemIdFromDeletedList(int pos) {
        if (getDeletedItemsIds().getValue() != null && getItems().getValue() != null) {
            if (getDeletedItemsIds().getValue().size() > 0) {
                ItemModel itemModel = getItems().getValue().get(pos);
                int index = getDeletedItemPos(Integer.parseInt(itemModel.getId()));
                if (index != -1) {
                    getDeletedItemsIds().getValue().remove(index);
                    getDeletedItemsIds().setValue(getDeletedItemsIds().getValue());

                }
                if (getDeletedItemsIds().getValue().size() == 0) {
                    getIsItemsDeleteMode().setValue(false);
                }
            }
        }


    }

    private int getDeletedCategoryPos(int id) {
        if (getDeletedCategoryIds().getValue() != null) {
            for (int index = 0; index < getDeletedCategoryIds().getValue().size(); index++) {
                if (getDeletedCategoryIds().getValue().get(index) == id) {
                    return index;
                }
            }
        }
        return -1;
    }

    private int getDeletedItemPos(int id) {
        if (getDeletedItemsIds().getValue() != null) {
            for (int index = 0; index < getDeletedItemsIds().getValue().size(); index++) {
                if (getDeletedItemsIds().getValue().get(index) == id) {
                    return index;
                }
            }
        }
        return -1;
    }

    public void clearDeletedItemsIds() {
        if (getDeletedItemsIds().getValue() != null) {
            getDeletedItemsIds().getValue().clear();
            getDeletedItemsIds().setValue(getDeletedItemsIds().getValue());
            getIsItemsDeleteMode().setValue(false);
            if (getItems().getValue() != null) {
                for (ItemModel itemModel : getItems().getValue()) {
                    if (itemModel.isSelected()) {
                        itemModel.setSelected(false);
                    }
                }

                getItems().setValue(getItems().getValue());
            }

        }
    }


    public void getItemsData() {
        if (getMainItemsList().getValue()!=null&&getMainItemsList().getValue().size()>0){
            getIsItemsLoading().setValue(false);

        }else {
            getIsItemsLoading().setValue(true);

        }
        Api.getService(Tags.base_url)
                .items(userModel.getData().getSelectedUser().getId(), userModel.getData().getSelectedWereHouse().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ItemsDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<ItemsDataModel> response) {
                        getIsItemsLoading().setValue(false);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    getMainItemsList().setValue(response.body().getData());
                                    getItems().setValue(response.body().getData());

                                } else {
                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));
                                }
                            } else {

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                        } else {

                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        getIsItemsLoading().setValue(false);

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                    }
                });
    }

    public void deleteCategories(Context context) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getString(R.string.deleting));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .deleteCategories(userModel.getData().getSelectedUser().getId(), getDeletedCategoryIds().getValue())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<StatusResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (getCategories().getValue() != null && getDeletedCategoryIds().getValue() != null) {
                                        List<CategoryModel> categories = new ArrayList<>();
                                        for (int index = 0; index < getCategories().getValue().size(); index++) {
                                            CategoryModel categoryModel = getCategories().getValue().get(index);
                                            int pos = getDeletedCategoryPos(categoryModel.getId());
                                            if (pos == -1) {
                                                categories.add(categoryModel);
                                            }
                                        }
                                        getDeletedCategoryIds().getValue().clear();
                                        getCategories().setValue(categories);
                                        getIsDeleteMode().setValue(false);
                                    }

                                } else {
                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));
                                }
                            } else {

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                        } else {

                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                    }
                });
    }

    public void deleteItems(Context context) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getString(R.string.deleting));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Log.e("size",getDeletedItemsIds().getValue().size()+"");
        Api.getService(Tags.base_url)
                .deleteItems(userModel.getData().getSelectedUser().getId(), getDeletedItemsIds().getValue())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<StatusResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (getMainItemsList().getValue() != null && getDeletedItemsIds().getValue() != null) {
                                        List<ItemModel> items = new ArrayList<>();
                                        for (int index = 0; index < getMainItemsList().getValue().size(); index++) {
                                            ItemModel itemModel = getMainItemsList().getValue().get(index);
                                            int pos = getDeletedItemPos(Integer.parseInt(itemModel.getId()));
                                            if (pos == -1) {
                                                items.add(itemModel);
                                            }
                                        }
                                        getDeletedItemsIds().getValue().clear();
                                        getMainItemsList().setValue(items);
                                        searchItems(getQueryItems().getValue());
                                        getIsItemsDeleteMode().setValue(false);
                                    }

                                } else {
                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));
                                }
                            } else {

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                        } else {

                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
