package com.midad_pos.uis.activity_base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.midad_pos.model.AppSettingModel;
import com.midad_pos.model.UserModel;
import com.midad_pos.preferences.Preferences;

import io.paperdb.Paper;

public class BaseFragment extends Fragment {
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    protected String getLang() {
        Paper.init(context);
        String lang = Paper.book().read("lang", "ar");
        return lang;
    }

    protected UserModel getUserModel() {
        Preferences preferences = Preferences.getInstance();
        return preferences.getUserData(context.getApplicationContext());

    }

    protected void clearUserModel(Context context) {
        Preferences preferences = Preferences.getInstance();
        preferences.clearUserData(context);

    }

    protected void setUserModel(UserModel userModel) {
        Preferences preferences = Preferences.getInstance();
        preferences.createUpdateUserData(context.getApplicationContext(), userModel);
    }

    protected AppSettingModel getAppSetting() {
        Preferences preferences = Preferences.getInstance();
        return preferences.getAppSetting(context);
    }

    protected void setAppSettingModel(AppSettingModel model) {
        Preferences preferences = Preferences.getInstance();
        preferences.createUpdateAppSetting(context, model);
    }

}
