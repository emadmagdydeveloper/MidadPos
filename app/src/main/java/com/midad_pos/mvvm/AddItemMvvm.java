package com.midad_pos.mvvm;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.R;
import com.midad_pos.model.AddItemModel;
import com.midad_pos.model.CategoryDataModel;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.HomeIndexModel;
import com.midad_pos.model.StatusResponse;
import com.midad_pos.model.UnitModel;
import com.midad_pos.model.UserModel;
import com.midad_pos.preferences.Preferences;
import com.midad_pos.remote.Api;
import com.midad_pos.share.Common;
import com.midad_pos.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class AddItemMvvm extends AndroidViewModel {
    private final String TAG = AddItemMvvm.class.getName();
    private MutableLiveData<AddItemModel> addItemModel;
    private MutableLiveData<List<CategoryModel>> categories;
    private MutableLiveData<HomeIndexModel.Data> homeData;
    private MutableLiveData<Integer> selectedColorPos;
    private MutableLiveData<Integer> selectedShapePos;
    private MutableLiveData<String> onError;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<Boolean> onAddedSuccess;
    private MutableLiveData<Boolean> deletedSuccess;


    private MutableLiveData<List<String>> barcodeTypes;
    private CompositeDisposable disposable = new CompositeDisposable();
    private UserModel userModel;
    public boolean showPin =false;
    public boolean forNavigation = false;

    public AddItemMvvm(@NonNull Application application) {
        super(application);
        userModel = Preferences.getInstance().getUserData(application.getApplicationContext());

        List<String> types = getBarcodeTypes().getValue();
        if (types!=null){
            types.add("C128");
            types.add("C39");
            types.add("UPCA");
            types.add("UPCE");
            types.add("EAN8");
            types.add("EAN13");

        }
        getBarcodeTypes().setValue(types);


    }
    public MutableLiveData<AddItemModel> getAddItemModel(){
        if (addItemModel==null){
            addItemModel = new MutableLiveData<>();
            AddItemModel model = new AddItemModel();
            model.setCategoryModel(new CategoryModel(-1,getApplication().getApplicationContext().getString(R.string.no_category)));

            addItemModel.setValue(model);
        }
        return addItemModel;
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

    public MutableLiveData<Boolean> getOnAddedSuccess() {
        if (onAddedSuccess == null) {
            onAddedSuccess = new MutableLiveData<>();
        }

        return onAddedSuccess;
    }

    public MutableLiveData<List<CategoryModel>> getCategories() {
        if (categories == null) {
            categories = new MutableLiveData<>();
        }

        return categories;
    }

    public MutableLiveData<List<String>> getBarcodeTypes() {
        if (barcodeTypes == null) {
            barcodeTypes = new MutableLiveData<>();
            barcodeTypes.setValue(new ArrayList<>());
        }

        return barcodeTypes;
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
    public MutableLiveData<Boolean> getDeletedSuccess() {
        if (deletedSuccess == null) {
            deletedSuccess = new MutableLiveData<>();
        }
        return deletedSuccess;
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
                                   if (response.body().getData()!=null&&response.body().getData().getUnits()!=null&&response.body().getData().getUnits().size()>0){
                                       List<UnitModel> units = response.body().getData().getUnits();
                                       UnitModel unitModel = units.get(0);
                                       unitModel.setChecked(true);
                                       units.set(0,unitModel);
                                       response.body().getData().setUnits(units);
                                       Objects.requireNonNull(getAddItemModel().getValue()).setUnit_id(unitModel.getId());

                                   }
                                    getHomeData().setValue(response.body().getData());
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

    public void addItem(Context context){
        userModel = Preferences.getInstance().getUserData(getApplication().getApplicationContext());
        if (getAddItemModel().getValue()!=null){
            AddItemModel model = getAddItemModel().getValue();
            ProgressDialog dialog = Common.createProgressDialog(context,context.getString(R.string.creating_item));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();

            RequestBody user_id = Common.getRequestBodyText(userModel.getData().getSelectedUser().getId());
            RequestBody name = Common.getRequestBodyText(model.getName());
            RequestBody category_id = null;
            if (model.getCategoryModel()!=null&&model.getCategoryModel().getId()!=-1&&model.getCategoryModel().getId()!=-2){
                category_id = Common.getRequestBodyText(model.getCategoryModel().getId()+"");

            }
            RequestBody barcode = Common.getRequestBodyText(model.getBarcode());
            RequestBody barcode_type = Common.getRequestBodyText(model.getBarcode_type());
            RequestBody warehouse_id = Common.getRequestBodyText(userModel.getData().getSelectedWereHouse().getId());

            RequestBody price = null;
            if (model.getPrice().isEmpty()){
                price = Common.getRequestBodyText("0.00");
            }else {
                price = Common.getRequestBodyText(model.getPrice().replace(",",""));

            }
            RequestBody cost = Common.getRequestBodyText(model.getCost().replace(",",""));
            RequestBody unit_id = Common.getRequestBodyText(model.getUnit_id());
            RequestBody imageType;
            RequestBody color = null;
            RequestBody shape = null;
            RequestBody tax_id = null;
            MultipartBody.Part image;

            List<MultipartBody.Part> images = new ArrayList<>();
            List<RequestBody> modifiers = new ArrayList<>();

            for (String modifier:model.getModifiers()){
                RequestBody requestBody = Common.getRequestBodyText(modifier);
                modifiers.add(requestBody);
            }
            if (model.isShape()){
                imageType = Common.getRequestBodyText("color");
                color = Common.getRequestBodyText(model.getColor());
                shape = Common.getRequestBodyText(model.getShapes());


            }else {
                imageType = Common.getRequestBodyText("image");
                image = Common.getMultiPartFromPath(model.getImage(),"images[]");
                images.add(image);
            }

            if (!model.getTax_id().isEmpty()){
                tax_id = Common.getRequestBodyText(model.getTax_id());
            }


            Api.getService(Tags.base_url)
                    .addItem(user_id,name,category_id,barcode,price,cost,unit_id,tax_id,barcode_type,imageType,color,shape,warehouse_id,modifiers,images)
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
                                        getOnAddedSuccess().setValue(true);
                                    }else {
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

    }

    public void deleteItem(Context context, String user_id, String category_id) {
        ProgressDialog dialog = Common.createProgressDialog(context,context.getString(R.string.deleting));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        List<Integer> ids = new ArrayList<>();
        ids.add(Integer.valueOf(category_id));
        Api.getService(Tags.base_url)
                .deleteItems(user_id,ids)
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

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
