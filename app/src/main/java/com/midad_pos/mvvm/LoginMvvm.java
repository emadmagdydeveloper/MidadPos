package com.midad_pos.mvvm;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.R;
import com.midad_pos.model.LoginModel;
import com.midad_pos.model.UserModel;
import com.midad_pos.remote.Api;
import com.midad_pos.tags.Tags;

import java.io.IOException;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class LoginMvvm extends AndroidViewModel {
    private final String TAG = LoginMvvm.class.getName();
    private LoginModel loginModel;
    private MutableLiveData<LoginModel> loginModelMutableLiveData;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<UserModel> onUserLogin;
    private MutableLiveData<String> onError;
    private MutableLiveData<Boolean> isLoginDone;
    private CompositeDisposable disposable = new CompositeDisposable();

    public LoginMvvm(@NonNull Application application) {
        super(application);
        loginModel = new LoginModel();
        getLoginModel().setValue(loginModel);
    }

    public MutableLiveData<LoginModel> getLoginModel(){
        if (loginModelMutableLiveData==null){
            loginModelMutableLiveData = new MutableLiveData<>();
        }

        return loginModelMutableLiveData;
    }

    public MutableLiveData<Boolean> getIsLoading(){
        if (isLoading==null){
            isLoading = new MutableLiveData<>();
        }
        return isLoading;
    }

    public MutableLiveData<Boolean> getIsLoginDone(){
        if (isLoginDone==null){
            isLoginDone = new MutableLiveData<>();
            isLoginDone.setValue(false);
        }
        return isLoginDone;
    }

    public MutableLiveData<String> getOnError(){
        if (onError==null){
            onError = new MutableLiveData<>();
        }
        return onError;
    }

    public MutableLiveData<UserModel> getOnUserLogin(){
        if (onUserLogin==null){
            onUserLogin = new MutableLiveData<>();
        }
        return onUserLogin;
    }

    public void login(){
        getIsLoading().setValue(true);
        Api.getService(Tags.base_url)
                .login(loginModel.getEmail(),loginModel.getPassword())
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
                                    getIsLoginDone().setValue(true);
                                    getOnUserLogin().setValue(response.body());
                                }else if (response.body().getStatus()==406){
                                    getIsLoginDone().setValue(false);

                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.invalid_credential));

                                }else {
                                    getIsLoginDone().setValue(false);

                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));
                                }
                            }else {
                                getIsLoginDone().setValue(false);

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                        }else {
                            getIsLoginDone().setValue(false);

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
                        getIsLoginDone().setValue(false);
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
