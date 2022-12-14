package com.midad_app_pos.preferences;

import android.content.Context;
import android.content.SharedPreferences;


import com.midad_app_pos.model.cart.CartList;
import com.midad_app_pos.model.AppSettingModel;
import com.midad_app_pos.model.UserModel;
import com.google.gson.Gson;

public class Preferences {

    private static Preferences instance = null;

    private Preferences() {
    }

    public static Preferences getInstance() {
        if (instance == null) {
            instance = new Preferences();
        }
        return instance;
    }

    public UserModel getUserData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String user_data = preferences.getString("user_data", "");
        UserModel userModel = gson.fromJson(user_data, UserModel.class);
        return userModel;
    }


    public void createUpdateUserData(Context context, UserModel userModel) {
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String user_data = gson.toJson(userModel);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_data", user_data);
        editor.apply();

    }

    public AppSettingModel getAppSetting(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String setting_data = preferences.getString("setting_data", "");
        AppSettingModel settingModel = gson.fromJson(setting_data, AppSettingModel.class);
        if (settingModel == null) {
            settingModel = new AppSettingModel();
        }
        return settingModel;
    }

    public void createUpdateAppSetting(Context context, AppSettingModel model) {
        SharedPreferences preferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String user_data = gson.toJson(model);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("setting_data", user_data);
        editor.apply();

    }


    public CartList getCart(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("cart", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String setting_data = preferences.getString("cart_data", "");
        CartList model = gson.fromJson(setting_data, CartList.class);
        if (model == null) {
            model = new CartList();
        }
        return model;
    }

    public void createUpdateCart(Context context, CartList model) {
        SharedPreferences preferences = context.getSharedPreferences("cart", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String cart_data = gson.toJson(model);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cart_data", cart_data);
        editor.apply();

    }

    public void clearUserData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        SharedPreferences preferences2 = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = preferences2.edit();
        editor2.clear();
        editor2.apply();

    }

    public void clearCart(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("cart", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

    }



}
