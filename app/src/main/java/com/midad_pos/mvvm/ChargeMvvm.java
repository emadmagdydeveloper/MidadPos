package com.midad_pos.mvvm;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.model.ChargeModel;
import com.midad_pos.model.cart.CartList;
import com.midad_pos.model.cart.ManageCartModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChargeMvvm extends AndroidViewModel {
    private ManageCartModel manageCartModel;
    private MutableLiveData<CartList> cartList;
    private MutableLiveData<Boolean> isSplit;
    private MutableLiveData<Integer> splitAmount;
    private MutableLiveData<Double> remaining;
    private MutableLiveData<List<ChargeModel>> splitList;
    private MutableLiveData<String> paidAmount;
    private MutableLiveData<Boolean> isPaidShown;

    public ChargeMvvm(@NonNull Application application) {
        super(application);
        manageCartModel = ManageCartModel.newInstance();
        getCartListInstance().setValue(manageCartModel.getCartModel(application.getApplicationContext()));
        getPaidAmount().setValue(manageCartModel.getCartModel(application.getApplicationContext()).getTotalPrice()+"");
    }

    public MutableLiveData<CartList> getCartListInstance() {
        if (cartList == null) {
            cartList = new MutableLiveData<>();
        }
        return cartList;
    }

    public MutableLiveData<Boolean> getIsSplit() {
        if (isSplit == null) {
            isSplit = new MutableLiveData<>();
        }
        return isSplit;
    }

    public MutableLiveData<Boolean> getIsPaidShown() {
        if (isPaidShown == null) {
            isPaidShown = new MutableLiveData<>();
        }
        return isPaidShown;
    }


    public MutableLiveData<Integer> getSplitAmount() {
        if (splitAmount == null) {
            splitAmount = new MutableLiveData<>();
            splitAmount.setValue(2);
            createSplitList(2);
        }
        return splitAmount;
    }

    public MutableLiveData<String> getPaidAmount() {
        if (paidAmount == null) {
            paidAmount = new MutableLiveData<>();
            paidAmount.setValue("0.00");

        }
        return paidAmount;
    }


    public MutableLiveData<List<ChargeModel>> getSplitList() {
        if (splitList == null) {
            splitList = new MutableLiveData<>();
        }
        return splitList;
    }

    public MutableLiveData<Double> getRemaining() {
        if (remaining == null) {
            remaining = new MutableLiveData<>();
            remaining.setValue(0.00);
        }
        return remaining;
    }

    public void increase_decrease(int type) {

        if (getSplitAmount().getValue() != null && getCartListInstance().getValue() != null) {
            int amount = getSplitAmount().getValue();
            if (type == 1) {
                if (amount < 50) {
                    amount++;
                    getSplitAmount().setValue(amount);

                }
            } else {
                if (amount > 1) {
                    amount--;
                    getSplitAmount().setValue(amount);
                }
            }

            createSplitList(amount);
        }

    }


    public void createSplitList(int size) {
        if (getCartListInstance().getValue() != null) {
            List<ChargeModel> list = new ArrayList<>();
            double total = getCartListInstance().getValue().getTotalPrice();

            for (int index = 0; index < size; index++) {
                double stepTotal = total / (size);
                ChargeModel chargeModel;
                if (stepTotal == 0.00) {
                    chargeModel = new ChargeModel("cash", "0.00", false);

                } else {
                    chargeModel = new ChargeModel("cash", String.format(Locale.US, "%.2f", stepTotal), false);

                }
                list.add(chargeModel);

            }

            getSplitList().setValue(list);
        }

    }

    public void updateList(double price, int pos) {
        if (getRemaining().getValue() != null && getSplitList().getValue() != null && getCartListInstance().getValue() != null) {

            List<ChargeModel> list = new ArrayList<>();
            ChargeModel model = getSplitList().getValue().get(pos);
            model.setPrice(String.valueOf(price));
            model.setEdited(true);

            double diff = 0.0;
            int editCount = 0;
            for (int index = 0; index < getSplitList().getValue().size(); index++) {
                if (index != pos) {
                    diff += Double.parseDouble(getSplitList().getValue().get(index).getPrice());
                }
                if (getSplitList().getValue().get(index).isEdited()) {
                    editCount++;
                }
            }

            double remain = getRemaining().getValue() - diff;

            if (Double.parseDouble(model.getPrice()) > remain) {
                model.setPrice(String.valueOf(remain));
            }

            double total = getCartListInstance().getValue().getTotalPrice();
            double stepTotal = total / (getSplitList().getValue().size() - editCount);

            for (int index = 0; index < getSplitList().getValue().size(); index++) {
                if (index == pos) {
                    list.add(model);
                } else {
                    ChargeModel chargeModel = new ChargeModel(getSplitList().getValue().get(pos).getPayment_type(), String.format(Locale.US, "%.2f", stepTotal), getSplitList().getValue().get(pos).isPaid());
                    list.add(chargeModel);
                }
            }

        }
    }

}
