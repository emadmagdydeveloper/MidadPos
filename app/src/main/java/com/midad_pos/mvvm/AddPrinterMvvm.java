package com.midad_pos.mvvm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.pdf417.encoder.PDF417;
import com.midad_pos.R;
import com.midad_pos.database.AppDatabase;
import com.midad_pos.database.DAO;
import com.midad_pos.model.DiscountModel;
import com.midad_pos.model.PrinterModel;
import com.midad_pos.model.StatusResponse;
import com.midad_pos.model.UserModel;
import com.midad_pos.preferences.Preferences;
import com.midad_pos.print_utils.PrintUtils;
import com.midad_pos.print_utils.SunmiPrintHelper;
import com.midad_pos.remote.Api;
import com.midad_pos.share.Common;
import com.midad_pos.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class AddPrinterMvvm extends AndroidViewModel implements PrintUtils.PrintResponse{
    private final String TAG = AddPrinterMvvm.class.getName();
    private MutableLiveData<Integer> selectedPrinterPos;
    private MutableLiveData<Integer> selectedPaperPos;
    private MutableLiveData<Integer> selectedInterfacePos;
    private MutableLiveData<Boolean> canPrint;
    private MutableLiveData<String> name;
    private MutableLiveData<BluetoothDevice> bluetoothDevice;
    private MutableLiveData<PrinterModel> onAddPrinterSuccess;
    private MutableLiveData<String> onAddedError;
    public boolean showPin =false;
    public boolean forNavigation = false;
    private AppDatabase database;
    private DAO dao;
    private PrintUtils printUtils;



    private CompositeDisposable disposable = new CompositeDisposable();


    public AddPrinterMvvm(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        dao = database.getDAO();
        printUtils = new PrintUtils(this);
    }


    public MutableLiveData<Integer> getSelectedPrinterPos() {
        if (selectedPrinterPos == null) {
            selectedPrinterPos = new MutableLiveData<>();
            selectedPrinterPos.setValue(0);
        }

        return selectedPrinterPos;
    }

    public MutableLiveData<String> getOnAddedError() {
        if (onAddedError == null) {
            onAddedError = new MutableLiveData<>();
        }

        return onAddedError;
    }

    public MutableLiveData<Integer> getSelectedInterfacePos() {
        if (selectedInterfacePos == null) {
            selectedInterfacePos = new MutableLiveData<>();
            selectedInterfacePos.setValue(-1);
        }

        return selectedInterfacePos;
    }

    public MutableLiveData<Integer> getSelectedPaperPos() {
        if (selectedPaperPos == null) {
            selectedPaperPos = new MutableLiveData<>();
            selectedPaperPos.setValue(0);
        }

        return selectedPaperPos;
    }

    public MutableLiveData<Boolean> getCanPrint() {
        if (canPrint == null) {
            canPrint = new MutableLiveData<>();
            canPrint.setValue(false);
        }

        return canPrint;
    }

    public MutableLiveData<String> getName() {
        if (name == null) {
            name = new MutableLiveData<>();
        }

        return name;
    }

    public MutableLiveData<BluetoothDevice> getBluetoothDevice() {
        if (bluetoothDevice == null) {
            bluetoothDevice = new MutableLiveData<>();
        }

        return bluetoothDevice;
    }

    public MutableLiveData<PrinterModel> getOnAddPrinterSuccess() {
        if (onAddPrinterSuccess == null) {
            onAddPrinterSuccess = new MutableLiveData<>();
        }

        return onAddPrinterSuccess;
    }

    @SuppressLint("MissingPermission")
    public void addPrinter(boolean test, Bitmap bitmap){
        String printer_type = "";
        String paper_width = "80";
        String bluetooth_name = "";
        String bluetooth_address ="";
        String ip_address ="";
        boolean canPrintReceipt = false;
        boolean canPrintOrder = false;
        boolean isOtherPrinter = false;
        if (getSelectedPrinterPos().getValue()!=null){
            int pos = getSelectedPrinterPos().getValue();
            if (pos==0){
                printer_type = "";

            }else if (pos ==1){
                printer_type = "sunmi";

            }else {
                printer_type = "other";
                isOtherPrinter = true;
            }
        }

        if (getSelectedPrinterPos().getValue()!=null){
            int pos = getSelectedPrinterPos().getValue();
            if (pos==0){
                paper_width = "80";
            }else {
                paper_width = "58";
            }
        }

        if (getBluetoothDevice().getValue()!=null){
            bluetooth_name = getBluetoothDevice().getValue().getName();
            bluetooth_address = getBluetoothDevice().getValue().getAddress();
        }

        if (getCanPrint().getValue()!=null){
            canPrintReceipt = getCanPrint().getValue();
        }

        PrinterModel printerModel = new PrinterModel(getName().getValue(),printer_type,isOtherPrinter,bluetooth_name,bluetooth_address,ip_address,paper_width,canPrintReceipt,canPrintOrder);


        if (printer_type.equalsIgnoreCase("sunmi")){

            if (!test){
                dao.getPrinterSunmi("sunmi")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<PrinterModel>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                disposable.add(d);
                            }

                            @Override
                            public void onSuccess(PrinterModel model) {
                                if (model!=null){
                                    getOnAddedError().setValue(getApplication().getApplicationContext().getString(R.string.already_added_printer));

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e.getMessage()!=null&&e.getMessage().contains("Query returned empty result")){

                                    savePrinter(printerModel);
                                }
                            }
                        });

            }else {
                printTest(printer_type,paper_width,bluetooth_name,bitmap);
            }

        }
        else if (printer_type.equalsIgnoreCase("other")){
            if (getSelectedInterfacePos().getValue()!=null){
                int pos = getSelectedInterfacePos().getValue();
                if (pos==0){
                    if (!bluetooth_name.isEmpty()){
                        if (!test){
                            dao.getPrinterBluetoothName(bluetooth_name)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<PrinterModel>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {
                                            disposable.add(d);
                                        }

                                        @Override
                                        public void onSuccess(PrinterModel model) {
                                            if (model!=null){
                                                getOnAddedError().setValue(getApplication().getApplicationContext().getString(R.string.already_added_printer));
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            if (e.getMessage()!=null&&e.getMessage().contains("Query returned empty result")){
                                                savePrinter(printerModel);
                                            }
                                        }
                                    });
                        }else {
                            printTest(printer_type,paper_width,bluetooth_name,bitmap);

                        }


                    }else {
                        Toast.makeText(getApplication(), R.string.choose_bluetooth_printer, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }else {
            Toast.makeText(getApplication().getApplicationContext(), R.string.choose_printer, Toast.LENGTH_SHORT).show();
        }


    }

    private void printTest(String printer_type, String paper_width,String bluetooth_name,Bitmap bitmap) {
        if (printer_type.equalsIgnoreCase("sunmi")){
            printSunmiTest(paper_width);
        }else {
            if (!bluetooth_name.isEmpty()){
                printBluetoothTest(paper_width,bluetooth_name,bitmap);
            }else {
                Toast.makeText(getApplication(), "Error bluetooth settings", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePrinter(PrinterModel printerModel) {
        dao.addPrinter(printerModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Long aLong) {
                        printerModel.setId(aLong);
                        getOnAddPrinterSuccess().setValue(printerModel);

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e.getMessage()!=null){

                            Toast.makeText(getApplication().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void printSunmiTest(String paper_width){
        int paper = 2;
        if (paper_width.equalsIgnoreCase("80")){
        }else {
            paper = 1;
        }
        UserModel userModel = Preferences.getInstance().getUserData(getApplication().getApplicationContext());
        SunmiPrintHelper.getInstance().printTestExample(getApplication().getApplicationContext(),userModel,paper);

    }

    private void printBluetoothTest(String paper_width, String bluetooth_name,Bitmap bitmap){

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

    @Override
    public void onDevices(List<BluetoothDevice> list) {

    }

    @Override
    public void onStartIntent() {

    }
}
