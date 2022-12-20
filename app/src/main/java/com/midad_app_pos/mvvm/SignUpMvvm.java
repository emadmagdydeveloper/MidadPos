package com.midad_app_pos.mvvm;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_app_pos.R;
import com.midad_app_pos.model.SignUpModel;
import com.midad_app_pos.model.UserModel;
import com.midad_app_pos.remote.Api;
import com.midad_app_pos.tags.Tags;

import java.io.IOException;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class SignUpMvvm extends AndroidViewModel {
    private final String TAG = SignUpMvvm.class.getName();
    private SignUpModel signUpModel;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> onError;
    private MutableLiveData<UserModel> onSuccess;
    private MutableLiveData<SignUpModel> signUpModelMutableLiveData;
    private CompositeDisposable disposable = new CompositeDisposable();

    public SignUpMvvm(@NonNull Application application) {
        super(application);
        signUpModel = new SignUpModel();
        getSignUpModel().setValue(signUpModel);
    }

    public MutableLiveData<SignUpModel> getSignUpModel(){
        if (signUpModelMutableLiveData==null){
            signUpModelMutableLiveData = new MutableLiveData<>();
        }
        return signUpModelMutableLiveData;
    }

    public MutableLiveData<Boolean> getIsLoading(){
        if (isLoading==null){
            isLoading = new MutableLiveData<>();
        }
        return isLoading;
    }

    public MutableLiveData<String> getOnError(){
        if (onError==null){
            onError = new MutableLiveData<>();
        }
        return onError;
    }

    public MutableLiveData<UserModel> getOnSuccess(){
        if (onSuccess==null){
            onSuccess = new MutableLiveData<>();
        }
        return onSuccess;
    }

    public void signUp(){
        getIsLoading().setValue(true);
        Api.getService(Tags.base_url)
                .signUp(getSignUpModel().getValue().getEmail(),getSignUpModel().getValue().getPassword(),getSignUpModel().getValue().getPhone(),getSignUpModel().getValue().getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<UserModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<UserModel> response) {
                        getIsLoading().setValue(false);
                        if (response.isSuccessful()){
                            if (response.body()!=null){
                                Log.e("code",response.body().getStatus()+""+response.body().getMessage().toString());
                                if (response.body().getStatus()==200){
                                    getOnSuccess().setValue(response.body());
                                }else if (response.body().getStatus()==406){

                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.email_exist));

                                }else if (response.body().getStatus()==407){

                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.phone_exits));

                                }else {
                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));
                                }
                            }else {

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                        }else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            try {
                                Log.e(TAG,response.code()+"__"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getIsLoading().setValue(false);
                        if (e.getMessage()!=null&&(e.getMessage().contains("host")||e.getMessage().contains("connection"))){
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        }else {
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
