package com.midad_pos.mvvm;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.R;
import com.midad_pos.model.ItemModel;
import com.midad_pos.model.OrderDataModel;
import com.midad_pos.model.OrderModel;
import com.midad_pos.model.ShiftsDataModel;
import com.midad_pos.model.UserModel;
import com.midad_pos.preferences.Preferences;
import com.midad_pos.remote.Api;
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

public class ReceiptDetailsMvvm extends AndroidViewModel {
    private final String TAG = ReceiptDetailsMvvm.class.getName();

    private MutableLiveData<Boolean> showTicketDetails;
    private MutableLiveData<String> showSendEmailDialog;
    private MutableLiveData<Boolean> isDialogShown;
    private MutableLiveData<Boolean> isLoading;

    private MutableLiveData<Boolean> isSearchOpen;
    private MutableLiveData<String> searchQuery;

    private MutableLiveData<List<OrderModel.Sale>> mainOrders;
    private MutableLiveData<List<OrderModel.Sale>> orders;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<OrderModel.Sale> selectedOrder;


    public ReceiptDetailsMvvm(@NonNull Application application) {
        super(application);
        getOrdersData();
    }

    public MutableLiveData<Boolean> getIsLoading() {
        if (isLoading == null) {
            isLoading = new MutableLiveData<>();
        }

        return isLoading;
    }

    public MutableLiveData<Boolean> getShowTicketDetails() {
        if (showTicketDetails == null) {
            showTicketDetails = new MutableLiveData<>();
        }

        return showTicketDetails;
    }

    public MutableLiveData<Boolean> getIsSearchOpen() {
        if (isSearchOpen == null) {
            isSearchOpen = new MutableLiveData<>();
        }

        return isSearchOpen;
    }

    public MutableLiveData<String> getSearchQuery() {
        if (searchQuery == null) {
            searchQuery = new MutableLiveData<>();
            searchQuery.setValue("");
        }

        return searchQuery;
    }

    public MutableLiveData<String> getShowSendEmailDialog() {
        if (showSendEmailDialog == null) {
            showSendEmailDialog = new MutableLiveData<>();
            showSendEmailDialog.setValue("");
        }

        return showSendEmailDialog;
    }

    public MutableLiveData<Boolean> getIsDialogShown() {
        if (isDialogShown == null) {
            isDialogShown = new MutableLiveData<>();
        }

        return isDialogShown;
    }

    public MutableLiveData<List<OrderModel.Sale>> getMainOrders() {
        if (mainOrders == null) {
            mainOrders = new MutableLiveData<>();
            mainOrders.setValue(new ArrayList<>());
        }

        return mainOrders;
    }

    public MutableLiveData<List<OrderModel.Sale>> getOrders() {
        if (orders == null) {
            orders = new MutableLiveData<>();
        }

        return orders;
    }

    public MutableLiveData<OrderModel.Sale> getSelectedOrder() {
        if (selectedOrder == null) {
            selectedOrder = new MutableLiveData<>();
        }

        return selectedOrder;
    }

    public void getOrdersData() {
        getIsLoading().setValue(true);
        UserModel userModel = Preferences.getInstance().getUserData(getApplication().getApplicationContext());
        Api.getService(Tags.base_url)
                .getOrders(userModel.getData().getSelectedUser().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new SingleObserver<Response<OrderDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<OrderDataModel> response) {
                        getIsLoading().setValue(false);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    updateData(response.body().getData());

                                }
                            }
                        } else {
                            Toast.makeText(getApplication().getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();

                            try {
                                if (response.errorBody() != null) {
                                    Log.e(TAG, response.code() + "__" + response.errorBody().string());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        getIsLoading().setValue(false);
                        Log.e("error", e.getMessage() + "");

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Toast.makeText(getApplication().getApplicationContext(), R.string.check_network, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplication().getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    private void updateData(List<OrderModel> data) {
        List<OrderModel.Sale> sales = new ArrayList<>();
        for (OrderModel model : data) {
            List<OrderModel.Sale> list = new ArrayList<>();
            for (OrderModel.Sale sale: model.getSales()){
                sale.setOrder_date(model.getDate());
                list.add(sale);
            }
            if (list.size() > 0) {
                list.get(0).setShow(true);
            }

            sales.addAll(list);
        }

        getMainOrders().setValue(sales);
        search();


    }

    public void search() {
        if (getSearchQuery().getValue() != null) {
            if (getSearchQuery().getValue().isEmpty()) {
                if (getMainOrders().getValue() != null && getMainOrders().getValue().size() > 0) {
                    List<OrderModel.Sale> sales = new ArrayList<>();
                    for (OrderModel.Sale sale : getMainOrders().getValue()) {
                        if (getSelectedOrder().getValue()!=null){
                            if (getSelectedOrder().getValue().getId().equals(sale.getId()))
                            {
                                sale.setSelected(true);
                                getSelectedOrder().setValue(sale);

                            }else {
                                sale.setSelected(false);

                            }
                            sales.add(sale);
                        }else {
                            sales.add(sale);
                            sales.get(0).setSelected(true);
                            getSelectedOrder().setValue(sales.get(0));
                        }




                    }


                    getMainOrders().setValue(sales);

                }
                getOrders().setValue(getMainOrders().getValue());

            } else {

                if (getMainOrders().getValue() != null) {
                    List<OrderModel.Sale> orders = new ArrayList<>();
                    for (OrderModel.Sale sale : getMainOrders().getValue()) {

                        if (sale.getId().startsWith(getSearchQuery().getValue())) {
                            if (getSelectedOrder().getValue()!=null){
                                if (getSelectedOrder().getValue().getId().equals(sale.getId())){
                                    sale.setSelected(true);

                                    getSelectedOrder().setValue(sale);


                                }else {
                                    sale.setSelected(false);

                                }
                            }else {
                               if (orders.size()>0){
                                   orders.get(0).setSelected(true);
                                   getSelectedOrder().setValue(orders.get(0));
                               }
                            }
                            orders.add(sale);
                        }

                    }

                    getOrders().setValue(orders);

                }

            }
        } else {
            if (getMainOrders().getValue() != null && getMainOrders().getValue().size() > 0) {
                List<OrderModel.Sale> sales = new ArrayList<>();
                for (OrderModel.Sale sale : getMainOrders().getValue()) {
                    if (getSelectedOrder().getValue()!=null){
                        if (getSelectedOrder().getValue().getId().equals(sale.getId())){
                            sale.setSelected(true);
                            getSelectedOrder().setValue(sale);

                        }else {
                            sale.setSelected(false);

                        }


                    }else {
                        if (sales.size()>0){
                            sales.get(0).setSelected(true);
                            getSelectedOrder().setValue(sales.get(0));
                        }
                    }

                    sales.add(sale);


                }


                getMainOrders().setValue(sales);

            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
