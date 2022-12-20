package com.midad_app_pos.mvvm;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.midad_app_pos.R;
import com.midad_app_pos.model.CategoryModel;
import com.midad_app_pos.model.ItemModel;
import com.midad_app_pos.model.ItemsDataModel;
import com.midad_app_pos.model.SingleCategoryData;
import com.midad_app_pos.model.StatusResponse;
import com.midad_app_pos.model.UserModel;
import com.midad_app_pos.preferences.Preferences;
import com.midad_app_pos.remote.Api;
import com.midad_app_pos.share.Common;
import com.midad_app_pos.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class AddCategoryMvvm extends AndroidViewModel {
    private final String TAG = AddCategoryMvvm.class.getName();
    private MutableLiveData<Integer> pos;
    private MutableLiveData<String> categoryName;
    private MutableLiveData<String> color;
    private MutableLiveData<String> addedSuccess;
    private MutableLiveData<Boolean> deletedSuccess;
    private MutableLiveData<Boolean> onAssignSuccess;
    private MutableLiveData<CategoryModel> onCategoryAdded;


    private MutableLiveData<String> onError;
    private MutableLiveData<Boolean> isDialogShow;
    private List<ItemModel> items;
    private MutableLiveData<List<ItemModel>> searchItems;
    private MutableLiveData<Boolean> isItemsLoading;
    private MutableLiveData<Boolean> isEnableToAssign;
    private UserModel userModel;
    private MutableLiveData<String> query;
    private CategoryModel categoryModel;
    public List<String> selectedItemIds = new ArrayList<>();
    private List<ItemModel> itemModelList = new ArrayList<>();
    public boolean showPin =false;
    public boolean forNavigation = false;


    private CompositeDisposable disposable = new CompositeDisposable();


    public AddCategoryMvvm(@NonNull Application application, CategoryModel categoryModel) {
        super(application);
        this.categoryModel = categoryModel;
        items = new ArrayList<>();
        userModel = Preferences.getInstance().getUserData(application.getApplicationContext());
    }


    public MutableLiveData<Integer> getPos() {
        if (pos == null) {
            pos = new MutableLiveData<>();
            pos.setValue(0);
        }

        return pos;
    }

    public MutableLiveData<String> getCategoryName() {
        if (categoryName == null) {
            categoryName = new MutableLiveData<>();
            categoryName.setValue("");
        }
        return categoryName;
    }

    public MutableLiveData<String> getColor() {
        if (color == null) {
            color = new MutableLiveData<>();
            color.setValue("");
        }
        return color;
    }

    public MutableLiveData<Boolean> getDeletedSuccess() {
        if (deletedSuccess == null) {
            deletedSuccess = new MutableLiveData<>();
        }
        return deletedSuccess;
    }

    public MutableLiveData<Boolean> getOnAssignSuccess() {
        if (onAssignSuccess == null) {
            onAssignSuccess = new MutableLiveData<>();
        }
        return onAssignSuccess;
    }

    public MutableLiveData<Boolean> getIsEnableToAssign() {
        if (isEnableToAssign == null) {
            isEnableToAssign = new MutableLiveData<>();
        }
        return isEnableToAssign;
    }

    public MutableLiveData<String> getOnError() {
        if (onError == null) {
            onError = new MutableLiveData<>();
        }
        return onError;
    }

    public MutableLiveData<String> getAddedSuccess() {
        if (addedSuccess == null) {
            addedSuccess = new MutableLiveData<>();
        }
        return addedSuccess;
    }

    public MutableLiveData<CategoryModel> getOnCategoryAdded() {
        if (onCategoryAdded == null) {
            onCategoryAdded = new MutableLiveData<>();
        }
        return onCategoryAdded;
    }

    public MutableLiveData<Boolean> getIsDialogShow() {
        if (isDialogShow == null) {
            isDialogShow = new MutableLiveData<>();
            isDialogShow.setValue(false);
        }
        return isDialogShow;
    }

    public MutableLiveData<Boolean> getIsItemsLoading() {
        if (isItemsLoading == null) {
            isItemsLoading = new MutableLiveData<>();
        }
        return isItemsLoading;
    }

    public MutableLiveData<List<ItemModel>> getSearchItems() {
        if (searchItems == null) {
            searchItems = new MutableLiveData<>();
        }
        return searchItems;
    }

    public MutableLiveData<String> getQuery() {
        if (query == null) {
            query = new MutableLiveData<>();
            query.setValue("");
        }
        return query;
    }


    public void addCategory(Context context, String user_id, String action) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getString(R.string.creating_cat));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .addCategory(user_id, getCategoryName().getValue(), getColor().getValue())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<SingleCategoryData>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<SingleCategoryData> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    getAddedSuccess().setValue(action);
                                    getIsDialogShow().setValue(true);
                                    categoryModel = response.body().getData();
                                    getOnCategoryAdded().setValue(categoryModel);
                                    Log.e("catid2__",categoryModel.getId()+"");

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
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }

                    }
                });
    }

    public void deleteCategory(Context context, String user_id, int category_id) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getString(R.string.deleting));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        List<Integer> ids = new ArrayList<>();
        ids.add(category_id);
        Api.getService(Tags.base_url)
                .deleteCategories(user_id, ids)
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
                                    getDeletedSuccess().setValue(true);

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
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }

                    }
                });
    }

    public void updateCategory(Context context, String user_id, CategoryModel categoryModel, String action) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getString(R.string.updating));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .updateCategory(categoryModel.getId() + "", user_id, getCategoryName().getValue(), color.getValue())
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
                                    categoryModel.setName(getCategoryName().getValue());
                                    getAddedSuccess().setValue(action);
                                    getIsDialogShow().setValue(true);

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
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }

                    }
                });
    }

    public void getItemsData() {
        getIsItemsLoading().setValue(true);
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
                                    itemModelList.clear();
                                    itemModelList.addAll(response.body().getData());

                                    updateItemsData(response.body().getData());


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

    private void updateItemsData(List<ItemModel> data) {
        Observable.fromIterable(data)
                .filter(itemModel -> {
                    if (itemModel.getCategory() != null && itemModel.getCategory().getId() == categoryModel.getId()) {
                        itemModel.setSelected(true);
                        selectedItemIds.add(itemModel.getId());
                    } else {
                        itemModel.setSelected(false);
                    }
                    return true;
                }).toList()
                .toObservable()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ItemModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(List<ItemModel> list) {
                        items.clear();
                        items.addAll(list);
                        search("");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void refreshItems() {
        clearAssignIds();
        List<ItemModel> list = new ArrayList<>(itemModelList);
        updateItemsData(list);
    }

    public void search(String query) {
        getQuery().setValue(query);
        if (query.isEmpty()) {
            getSearchItems().setValue(items);
        } else {
            Observable.fromIterable(items)
                    .filter(item -> item.getName().startsWith(query)
                    ).toList().toObservable()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<ItemModel>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable.add(d);
                        }

                        @Override
                        public void onNext(List<ItemModel> list) {
                            getSearchItems().setValue(list);

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }


    }


    public void addRemoveAssignItem(ItemModel itemModel) {
        int pos = getAssignItemPos(itemModel);
        if (pos == -1) {
            if (itemModel.isSelected()) {
                selectedItemIds.add(itemModel.getId());
            }
            getIsEnableToAssign().setValue(true);
        } else {
            if (!itemModel.isSelected()) {
                selectedItemIds.remove(pos);
            }
            if (selectedItemIds.size() == 0) {
                getIsEnableToAssign().setValue(false);

            }
        }


    }

    private int getAssignItemPos(ItemModel itemModel) {
        for (int index = 0; index < selectedItemIds.size(); index++) {
            if (selectedItemIds.get(index).equals(itemModel.getId())) {
                return index;
            }
        }
        return -1;
    }

    private void clearAssignIds() {
        selectedItemIds.clear();

    }

    public void assignItems(Context context) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getString(R.string.assigning_items));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Log.e("cat_id",categoryModel.getId()+"");
        for (String id :selectedItemIds){
            Log.e("pod",id+"_");
        }
        Log.e("user_id",userModel.getData().getSelectedUser().getId()+"_");
        Api.getService(Tags.base_url)
                .assignItems(userModel.getData().getSelectedUser().getId(), categoryModel.getId() + "", selectedItemIds)
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
                                    getOnAssignSuccess().setValue(true);
                                } else {
                                    Log.e("code",response.body().getStatus()+"__"+response.body().getMessage().toString());
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
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }

                    }
                });
    }

    public static class MyViewModelFactory implements ViewModelProvider.Factory {
        private Application mApplication;
        private CategoryModel categoryModel;


        public MyViewModelFactory(Application application, CategoryModel categoryModel) {
            mApplication = application;
            this.categoryModel = categoryModel;
        }


        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new AddCategoryMvvm(mApplication, categoryModel);
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
