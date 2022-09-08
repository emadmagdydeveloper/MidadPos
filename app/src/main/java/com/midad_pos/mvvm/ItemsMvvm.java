package com.midad_pos.mvvm;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.R;
import com.midad_pos.model.CategoryDataModel;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.StatusResponse;
import com.midad_pos.remote.Api;
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


    private CompositeDisposable disposable = new CompositeDisposable();


    public ItemsMvvm(@NonNull Application application) {
        super(application);
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


    public void searchItems(String query) {

        getQueryItems().setValue(query);
    }

    public void searchCategories(String query) {
        if (getCategories().getValue()!=null){

            if (query.isEmpty()){
                getCategories().setValue(categoriesList);

            }else {
                List<CategoryModel> list = new ArrayList<>();
                for (CategoryModel categoryModel:getCategories().getValue())  {
                    if (categoryModel.getName().toLowerCase().startsWith(query)||categoryModel.getName().toLowerCase().equals(query)){
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
        getIsCategoryLoading().setValue(true);
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
                                    Log.e("cat_size",categoriesList.size()+"");
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
        if (getDeletedCategoryIds().getValue()!=null){
            for (CategoryModel categoryModel:list){
                for (int id:getDeletedCategoryIds().getValue()){
                    if (categoryModel.getId()==id){
                        categoryModel.setSelected(true);
                    }
                }
            }

            if (getQueryCategory().getValue()!=null){
                if (getQueryCategory().getValue().isEmpty()){
                    getCategories().setValue(list);

                }else {
                    searchCategories(getQueryCategory().getValue());
                }
            }else {
                getCategories().setValue(list);

            }
        }


    }

    public void clearDeletedCategoryIds(){
        if (deletedCategoryIds.getValue()!=null){
            deletedCategoryIds.getValue().clear();
            deletedCategoryIds.setValue(deletedCategoryIds.getValue());
            getIsDeleteMode().setValue(false);
            if (getCategories().getValue()!=null){
                for (CategoryModel categoryModel:getCategories().getValue()){
                    if (categoryModel.isSelected()){
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

    public void removeCategoryIdFromDeletedList(int pos) {
        if (getDeletedCategoryIds().getValue() != null && getCategories().getValue() != null) {
            if (getDeletedCategoryIds().getValue().size() > 0) {
                CategoryModel categoryModel = getCategories().getValue().get(pos);
                int index =getDeletedCategoryPos(categoryModel.getId());
                if (index!=-1){
                    getDeletedCategoryIds().getValue().remove(index);
                    getDeletedCategoryIds().setValue(getDeletedCategoryIds().getValue());

                }
                if (getDeletedCategoryIds().getValue().size() == 0) {
                    getIsDeleteMode().setValue(false);
                }
            }
        }


    }

    private int getDeletedCategoryPos(int id) {
        if (getDeletedCategoryIds().getValue()!=null){
            for (int index=0;index<getDeletedCategoryIds().getValue().size();index++){
                if (getDeletedCategoryIds().getValue().get(index)==id){
                    return index;
                }
            }
        }
        return -1;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
