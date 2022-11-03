package com.midad_pos.model.cart;

import android.content.Context;
import android.util.Log;

import com.midad_pos.model.CustomerModel;
import com.midad_pos.model.DiscountModel;
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

    public void addItemToCart(ItemModel item,int addOrUpdate, Context context) {
        CartList cartModel = getCartModel(context);
        cartModel.addItem(item,addOrUpdate);
        Preferences.getInstance().createUpdateCart(context,cartModel);


    }
    public void addDeliveryToCart(String delivery_id,String delivery_name,Context context) {
        CartList cartModel = getCartModel(context);
        cartModel.setDelivery_name(delivery_name);
        cartModel.setDelivery_id(delivery_id);
        Preferences.getInstance().createUpdateCart(context,cartModel);


    }

    public void addDiscountForAllTicket(DiscountModel model, Context context) {
        CartList cartModel = getCartModel(context);
        cartModel.addDiscountForAll(model);
        Preferences.getInstance().createUpdateCart(context,cartModel);


    }


    public void deleteItemFromCart(ItemModel item, Context context) {
        CartList cartModel = getCartModel(context);
        cartModel.removeItem(item);
        Preferences.getInstance().createUpdateCart(context,cartModel);
    }

    public void assignCustomerToCart(CustomerModel customerModel, Context context) {
        CartList cartModel = getCartModel(context);
        cartModel.setCustomerModel(customerModel);
        Preferences.getInstance().createUpdateCart(context,cartModel);
    }

    public void removeCustomerFromCart(Context context) {
        CartList cartModel = getCartModel(context);
        cartModel.setCustomerModel(null);
        Preferences.getInstance().createUpdateCart(context,cartModel);
    }

    public void deleteGeneralCartDiscount(List<DiscountModel> discounts, Context context) {
        CartList cartModel = getCartModel(context);
        cartModel.removeGeneralDiscount(discounts);
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
