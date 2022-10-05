package com.midad_pos.model.cart;

import android.content.Context;

import com.midad_pos.model.ItemModel;
import com.midad_pos.preferences.Preferences;

import java.io.Serializable;
import java.util.List;

public class ManageCartModel implements Serializable {
    private static ManageCartModel instance;

    private ManageCartModel() {
    }

    public static synchronized ManageCartModel newInstance() {
        if (instance == null) {
            instance = new ManageCartModel();
        }

        return instance;
    }

    public void addItemToCart(ItemModel item, Context context) {
        CartList cartModel = getCartModel(context);
        cartModel.addItem(item);
        Preferences.getInstance().createUpdateCart(context,cartModel);


    }


    public void deleteItemFromCart(ItemModel item, Context context) {
        CartList cartModel = getCartModel(context);
        cartModel.removeItem(item);
        Preferences.getInstance().createUpdateCart(context,cartModel);
    }



    public double getTotal(Context context) {
        CartList cartModel = getCartModel(context);
        return cartModel.getTotal();
    }

    public void clearCart(Context context) {
        CartList cartModel = getCartModel(context);
        cartModel.clear();
        Preferences.getInstance().clearCart(context);

    }

    public List<ItemModel> getCartData(Context context) {
        CartList cartModel = getCartModel(context);
        return cartModel.getItems();
    }

    public CartList getCartModel(Context context) {
        return Preferences.getInstance().getCart(context);
    }
}
