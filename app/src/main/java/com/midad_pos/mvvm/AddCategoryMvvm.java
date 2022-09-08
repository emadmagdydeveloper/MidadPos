package com.midad_pos.mvvm;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.R;
import com.midad_pos.model.StatusResponse;
import com.midad_pos.model.UserModel;
import com.midad_pos.remote.Api;
import com.midad_pos.tags.Tags;

import java.io.IOException;

import io.reactivex.Single;
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
    private MutableLiveData<String> onError;
    private CompositeDisposable disposable = new CompositeDisposable();


    public AddCategoryMvvm(@NonNull Application application) {
        super(application);
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


    public void addCategory(String user_id, String action) {
        if (disposable.size() > 0) {
            return;
        }
        Api.getService(Tags.base_url)
                .addCategory(user_id, getCategoryName().getValue(), getColor().getValue())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<StatusResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<StatusResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    getAddedSuccess().setValue(action);

                                } else {
                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                                }
                            } else {

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                            disposable.clear();
                        } else {

                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));
                            disposable.clear();

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                        }
                        disposable.clear();

                    }
                });
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
