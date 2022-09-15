package com.midad_pos.mvvm;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.R;
import com.midad_pos.model.AddItemModel;
import com.midad_pos.model.CategoryDataModel;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.HomeIndexModel;
import com.midad_pos.model.UserModel;
import com.midad_pos.preferences.Preferences;
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

public class AddItemMvvm extends AndroidViewModel {
    private final String TAG = AddItemMvvm.class.getName();
    private MutableLiveData<AddItemModel> addItemModel;
    private MutableLiveData<List<CategoryModel>> categories;
    private MutableLiveData<HomeIndexModel.Data> homeData;
    private MutableLiveData<Integer> selectedColorPos;
    private MutableLiveData<Integer> selectedShapePos;


    private MutableLiveData<Boolean> isValid;
    private MutableLiveData<String> onError;
    private MutableLiveData<Boolean> isLoading;
    private CompositeDisposable disposable = new CompositeDisposable();
    private UserModel userModel;

    public AddItemMvvm(@NonNull Application application) {
        super(application);
        userModel = Preferences.getInstance().getUserData(application.getApplicationContext());
        getCategoryData(userModel.getData().getUser().getId());


    }
    public MutableLiveData<AddItemModel> getAddItemModel(){
        if (addItemModel==null){
            addItemModel = new MutableLiveData<>();
            AddItemModel model = new AddItemModel(getIsValid());
            model.setCategoryModel(new CategoryModel(-1,getApplication().getApplicationContext().getString(R.string.no_category)));

            addItemModel.setValue(model);
        }
        return addItemModel;
    }

    public MutableLiveData<Boolean> getIsValid(){
        if (isValid==null){
            isValid = new MutableLiveData<>();
            isValid.setValue(false);
        }

        return isValid;
    }

    public MutableLiveData<HomeIndexModel.Data> getHomeData(){
        if (homeData==null){
            homeData = new MutableLiveData<>();
        }

        return homeData;
    }

    public MutableLiveData<String> getOnError() {
        if (onError == null) {
            onError = new MutableLiveData<>();
        }

        return onError;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        if (isLoading == null) {
            isLoading = new MutableLiveData<>();
        }

        return isLoading;
    }

    public MutableLiveData<List<CategoryModel>> getCategories() {
        if (categories == null) {
            categories = new MutableLiveData<>();
        }

        return categories;
    }

    public MutableLiveData<Integer> getSelectedColorPos(){
        if (selectedColorPos==null){
            selectedColorPos = new MutableLiveData<>();
            selectedColorPos.setValue(0);
        }
        return selectedColorPos;
    }

    public MutableLiveData<Integer> getSelectedShapePos(){
        if (selectedShapePos==null){
            selectedShapePos = new MutableLiveData<>();
            selectedShapePos.setValue(0);
        }
        return selectedShapePos;
    }

    public void getCategoryData(String user_id) {
        getIsLoading().setValue(true);
        List<CategoryModel> list = new ArrayList<>();

        list.add(new CategoryModel(-1, getApplication().getApplicationContext().getString(R.string.no_category)));
        list.add(new CategoryModel(-2, getApplication().getApplicationContext().getString(R.string.create_category)));
        getCategories().setValue(list);

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
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    list.clear();
                                    list.add(new CategoryModel(-1, getApplication().getApplicationContext().getString(R.string.no_category)));
                                    list.add(new CategoryModel(-2, getApplication().getApplicationContext().getString(R.string.create_category)));
                                    list.addAll(response.body().getData());
                                    getCategories().setValue(list);
                                    getHomeData(user_id);
                                } else {
                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));
                                }
                            } else {

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                        } else {
                            getIsLoading().setValue(false);
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
                        getIsLoading().setValue(false);

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                    }
                });
    }

    public void getHomeData(String user_id) {

        Api.getService(Tags.base_url)
                .homeIndex(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<HomeIndexModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<HomeIndexModel> response) {
                        getIsLoading().setValue(false);

                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {

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
                        getIsLoading().setValue(false);

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
