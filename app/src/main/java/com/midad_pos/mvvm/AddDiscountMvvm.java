package com.midad_pos.mvvm;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.midad_pos.R;
import com.midad_pos.model.CategoryModel;
import com.midad_pos.model.DiscountModel;
import com.midad_pos.model.ItemModel;
import com.midad_pos.model.ItemsDataModel;
import com.midad_pos.model.SingleCategoryData;
import com.midad_pos.model.StatusResponse;
import com.midad_pos.model.UserModel;
import com.midad_pos.preferences.Preferences;
import com.midad_pos.remote.Api;
import com.midad_pos.share.Common;
import com.midad_pos.tags.Tags;

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

public class AddDiscountMvvm extends AndroidViewModel {
    private final String TAG = AddDiscountMvvm.class.getName();
    private MutableLiveData<String> type;
    private DiscountModel discountModel;
    private MutableLiveData<String> discountName;
    private MutableLiveData<String> discountValue;

    private MutableLiveData<Boolean> addedSuccess;
    private MutableLiveData<Boolean> deletedSuccess;

    private MutableLiveData<String> onError;
    private UserModel userModel;



    private CompositeDisposable disposable = new CompositeDisposable();


    public AddDiscountMvvm(@NonNull Application application, DiscountModel discountModel) {
        super(application);
        this.discountModel = discountModel;
        if (discountModel!=null){
            getDiscountName().setValue(discountModel.getTitle());
            getDiscountValue().setValue(discountModel.getValue());
            getType().setValue(discountModel.getType());

        }
        userModel = Preferences.getInstance().getUserData(application.getApplicationContext());
    }


    public MutableLiveData<String> getType() {
        if (type == null) {
            type = new MutableLiveData<>();
            type.setValue("val");
        }

        return type;
    }

    public MutableLiveData<String> getDiscountName() {
        if (discountName == null) {
            discountName = new MutableLiveData<>();
            discountName.setValue("");
        }
        return discountName;
    }

    public MutableLiveData<String> getDiscountValue() {
        if (discountValue == null) {
            discountValue = new MutableLiveData<>();
            discountValue.setValue("");
        }
        return discountValue;
    }


    public MutableLiveData<Boolean> getDeletedSuccess() {
        if (deletedSuccess == null) {
            deletedSuccess = new MutableLiveData<>();
        }
        return deletedSuccess;
    }



    public MutableLiveData<String> getOnError() {
        if (onError == null) {
            onError = new MutableLiveData<>();
        }
        return onError;
    }

    public MutableLiveData<Boolean> getAddedSuccess() {
        if (addedSuccess == null) {
            addedSuccess = new MutableLiveData<>();
        }
        return addedSuccess;
    }







    public void addDiscount(Context context, String user_id) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getString(R.string.creating_cat));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        String value = "0";
        if (getDiscountValue().getValue()!=null&&!getDiscountValue().getValue().isEmpty()){
            value = getDiscountValue().getValue();;
        }
        Api.getService(Tags.base_url)
                .addDiscount(user_id, discountName.getValue(),getType().getValue(),value,"1")
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
                                    getAddedSuccess().setValue(true);

                                } else {
                                    Log.e("error",response.body().getStatus()+"__"+response.body().getMessage().toString());
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


    public void updateDiscount(Context context, String user_id) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getString(R.string.updating));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        String value = "0";
        if (getDiscountValue().getValue()!=null&&!getDiscountValue().getValue().isEmpty()){
            value = getDiscountValue().getValue();;
        }
        Api.getService(Tags.base_url)
                .updateDiscount(user_id,discountModel.getId(),getDiscountName().getValue(),getType().getValue(),value,"1")
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
                                    discountModel.setTitle(getDiscountName().getValue());
                                    getAddedSuccess().setValue(true);

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


    public void deleteDiscounts(Context context) {
        ProgressDialog dialog = Common.createProgressDialog(context, context.getString(R.string.deleting));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        List<Integer> ids = new ArrayList<>();
        ids.add(Integer.valueOf(discountModel.getId()));
        Api.getService(Tags.base_url)
                .deleteDiscounts(userModel.getData().getSelectedUser().getId(), ids)
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
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                    }
                });
    }







    public static class MyViewModelFactory implements ViewModelProvider.Factory {
        private Application mApplication;
        private DiscountModel discountModel;


        public MyViewModelFactory(Application application, DiscountModel discountModel) {
            mApplication = application;
            this.discountModel = discountModel;
        }


        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new AddDiscountMvvm(mApplication, discountModel);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
