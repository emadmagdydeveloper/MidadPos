package com.midad_app_pos.mvvm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_app_pos.R;
import com.midad_app_pos.database.AppDatabase;
import com.midad_app_pos.database.DAO;
import com.midad_app_pos.databinding.ActivityAddPrinterBinding;
import com.midad_app_pos.model.PrinterModel;
import com.midad_app_pos.model.UserModel;
import com.midad_app_pos.preferences.Preferences;
import com.midad_app_pos.print_utils.PrintUtils;
import com.midad_app_pos.print_utils.SunmiPrintHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddPrinterMvvm extends AndroidViewModel implements PrintUtils.PrintResponse, PrintUtils.PrinterConnectListener {
    private final String TAG = AddPrinterMvvm.class.getName();
    private MutableLiveData<Integer> selectedPrinterPos;
    private MutableLiveData<Integer> selectedPaperPos;
    private MutableLiveData<Integer> selectedInterfacePos;
    private MutableLiveData<Boolean> canPrint;
    private MutableLiveData<Boolean> automaticPrint;
    private MutableLiveData<String> name;
    private MutableLiveData<BluetoothDevice> bluetoothDevice;
    private MutableLiveData<PrinterModel> onAddPrinterSuccess;
    private MutableLiveData<String> onAddedError;
    public boolean showPin = false;
    public boolean forNavigation = false;
    private AppDatabase database;
    private DAO dao;
    private PrintUtils printUtils;
    private UserModel model;
    private ActivityAddPrinterBinding binding;
    private String lang = "ar";


    private CompositeDisposable disposable = new CompositeDisposable();


    public AddPrinterMvvm(@NonNull Application application) {
        super(application);
        model = Preferences.getInstance().getUserData(application.getApplicationContext());
        database = AppDatabase.getInstance(application);
        dao = database.getDAO();
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

    public MutableLiveData<Boolean> getAutomaticPrint() {
        if (automaticPrint == null) {
            automaticPrint = new MutableLiveData<>();
            automaticPrint.setValue(false);
        }

        return automaticPrint;
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
    public void addPrinter(boolean test, AppCompatActivity activity, String lang, ActivityAddPrinterBinding binding) {
        this.binding = binding;
        this.lang = lang;
        String printer_type = "";
        String paper_width = "80";
        String ip_address = "";
        String bluetooth_name = "";
        boolean canPrintReceipt = false;
        boolean canPrintOrder = false;
        boolean canPrintAutomatic = false;

        if (getSelectedPrinterPos().getValue() != null) {
            int pos = getSelectedPrinterPos().getValue();
            if (pos == 0) {
                printer_type = "";

            } else if (pos == 1) {
                printer_type = "sunmi";

            } else if (pos == 2) {
                printer_type = "kitchen";

            } else {
                printer_type = "other";
            }
        }

        if (getSelectedPaperPos().getValue() != null) {
            int pos = getSelectedPaperPos().getValue();
            if (pos == 0) {
                paper_width = "80";
            } else {
                paper_width = "58";
            }
        }


        if (getCanPrint().getValue() != null) {
            canPrintReceipt = getCanPrint().getValue();
        }

        if (getAutomaticPrint().getValue() != null) {
            canPrintAutomatic = getAutomaticPrint().getValue();
        }
        if (getBluetoothDevice().getValue() != null) {
            bluetooth_name = getBluetoothDevice().getValue().getName();
        }

        PrinterModel printerModel = new PrinterModel(getName().getValue(), printer_type, canPrintReceipt, canPrintOrder, canPrintAutomatic, ip_address, bluetooth_name, getBluetoothDevice().getValue(), paper_width);


        if (printer_type.equalsIgnoreCase("sunmi")) {

            if (!test) {
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
                                Log.e("tt", "qq");
                                if (model != null) {
                                    getOnAddedError().setValue(getApplication().getApplicationContext().getString(R.string.already_added_printer));

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("q", e.getMessage());
                                if (e.getMessage() != null && e.getMessage().contains("Query returned empty result")) {

                                    savePrinter(printerModel);
                                }
                            }
                        });

            } else {
                printSunmiTest(paper_width, lang);
            }

        } else if (printer_type.equalsIgnoreCase("other")) {
            if (getSelectedInterfacePos().getValue() != null) {
                int pos = getSelectedInterfacePos().getValue();
                if (pos == 0) {
                    if (getBluetoothDevice().getValue() != null) {
                        if (!test) {
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
                                            if (model != null) {
                                                getOnAddedError().setValue(getApplication().getApplicationContext().getString(R.string.already_added_printer));
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            if (e.getMessage() != null && e.getMessage().contains("Query returned empty result")) {
                                                savePrinter(printerModel);
                                            }
                                        }
                                    });
                        } else {
                            int paper = 576;
                            if (paper_width.equals("58")) {
                                paper = 384;
                            }

                            printBluetoothTest(paper, activity, lang, binding);

                        }


                    } else {
                        Toast.makeText(getApplication().getApplicationContext(), R.string.choose_bluetooth_printer, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            Toast.makeText(getApplication().getApplicationContext(), R.string.choose_printer, Toast.LENGTH_SHORT).show();
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
                        if (e.getMessage() != null) {

                            Toast.makeText(getApplication().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void printSunmiTest(String paper_width, String lang) {
        int paper = 2;
        if (paper_width.equalsIgnoreCase("80")) {
        } else {
            paper = 1;
        }


        UserModel model = Preferences.getInstance().getUserData(getApplication().getApplicationContext());
        SunmiPrintHelper.getInstance().initPrinter();
        SunmiPrintHelper.getInstance().printTestExample(getApplication().getApplicationContext(), model, paper, lang);

    }

    @SuppressLint("MissingPermission")
    private void printBluetoothTest(int paper_width, AppCompatActivity activity, String lang, ActivityAddPrinterBinding binding) {
        Log.e("1", "1");
        printUtils = new PrintUtils(this);

        if (getBluetoothDevice().getValue() != null) {


            if (getBluetoothDevice().getValue().getBondState() != BluetoothDevice.BOND_BONDED) {
                Toast.makeText(activity, "Device not paired", Toast.LENGTH_SHORT).show();
                return;
            }


            try {

                Log.e("2", "2");
                printUtils.connectPrinter(this, getBluetoothDevice().getValue(), paper_width, activity, lang);

                //printUtils.printTestDataText(activity,80,binding);


            } catch (Exception e) {
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("err", e.getMessage());
                e.printStackTrace();
            }
        }
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


    @Override
    public void onConnected() {
        Toast.makeText(getApplication().getApplicationContext(), "connected", Toast.LENGTH_SHORT).show();
        printTestBluetoothData();
    }


    @Override
    public void onFailed() {
        //printTestBluetoothData();

        Toast.makeText(getApplication().getApplicationContext(), "fail to connect printer", Toast.LENGTH_SHORT).show();

    }

    private void printTestBluetoothData() {
        if (lang.equals("ar")){
            printArBluetoothTest();
        }else {
            printEnBluetoothTest();
        }


    }

    private void printArBluetoothTest(){

        try {
            //Log.e("ddd",model.getData().getSelectedWereHouse().getName()+"___"+model.getData().getSelectedWereHouse().getTax_number()+"__"+model.getData().getSelectedUser().getName()+"__"+model.getData().getSelectedPos().getTitle());
            printUtils.addLineWithHeight(50);
            printUtils.addItem(28.0f, true, "مداد", PrintUtils.ALIGN_CENTER);
            printUtils.addItem(28.0f, false, "الرقم الضريبي" + ":" + (model.getData().getSelectedWereHouse()!=null?(model.getData().getSelectedWereHouse().getTax_number()!=null?model.getData().getSelectedWereHouse().getTax_number():""):""), PrintUtils.ALIGN_CENTER);
            printUtils.addItem(28.0f, false, (model.getData().getSelectedWereHouse()!=null?(model.getData().getSelectedWereHouse().getName()!=null?model.getData().getSelectedWereHouse().getName():""):""), PrintUtils.ALIGN_CENTER);
            printUtils.addItem(28.0f, false, "فاتورة ضريبية مبسطة", PrintUtils.ALIGN_CENTER);
            printUtils.addLineWithHeight(50);
            printUtils.addItem(28.0f, false, "رقم الإيصال:#", PrintUtils.ALIGN_RIGHT);
            printUtils.addItem(28.0f, false, "التاريخ:" + getNow(), PrintUtils.ALIGN_RIGHT);
            printUtils.addItem(28.0f, false, "امين الصندوق:" + (model.getData().getSelectedUser()!=null?(model.getData().getSelectedUser().getName()!=null?model.getData().getSelectedUser().getName():""):""), PrintUtils.ALIGN_RIGHT);
            printUtils.addItem(28.0f, false, "نقطة بيع:" + (model.getData().getSelectedPos()!=null?(model.getData().getSelectedPos().getTitle()!=null?model.getData().getSelectedPos().getTitle():""):""), PrintUtils.ALIGN_RIGHT);
            printUtils.addLineWithHeight(100);
            printUtils.addLineSeparator();
            printUtils.addLineWithHeight(15);
            printUtils.addItem(28.0f, false, "التوصيل", PrintUtils.ALIGN_RIGHT);
            printUtils.addLineWithHeight(50);
            printUtils.addLineSeparator();
            printUtils.addLineWithHeight(15);

            printUtils.addRowItem(25.0f, false, "عنصر1" ,"0.00", PrintUtils.ALIGN_RIGHT);
            printUtils.addItem(25.0f, true, "0.00X1", PrintUtils.ALIGN_RIGHT);
            printUtils.addLineWithHeight(8);

            printUtils.addRowItem(25.0f, false, "عنصر2" ,"0.00", PrintUtils.ALIGN_RIGHT);
            printUtils.addItem(25.0f, true, "0.00X1", PrintUtils.ALIGN_RIGHT);

            printUtils.addLineWithHeight(50);
            printUtils.addLineSeparator();
            printUtils.addLineWithHeight(15);
            printUtils.addRowItem(30.0f, true, "الإجمالى قبل الضريبة" ,"0.00", PrintUtils.ALIGN_RIGHT);

            printUtils.addLineWithHeight(50);
            printUtils.addLineSeparator();
            printUtils.addLineWithHeight(15);

            printUtils.addRowItem(30.0f, true, "الإجمالى شامل الضريبة" ,"0.00", PrintUtils.ALIGN_RIGHT);

            printUtils.addLineWithHeight(50);
            printUtils.addLineSeparator();
            printUtils.addLineWithHeight(15);

            printUtils.addItem(40.0f, true, "شكرا لزيارتكم", PrintUtils.ALIGN_CENTER);
            printUtils.addLineWithHeight(50);

            Bitmap bitmap = printUtils.printTestData(model);
       /* binding.flPrintTest.setVisibility(View.VISIBLE);
        binding.layoutPrint.image.setImageBitmap(bitmap);*/
            printUtils.clear();
        }catch (Exception e){

            Toast.makeText(getApplication().getApplicationContext(), e.getMessage()+"_", Toast.LENGTH_SHORT).show();
        }





    }

    private void printEnBluetoothTest(){

        printUtils.addLineWithHeight(50);
        printUtils.addItem(28.0f, true, "Midad", PrintUtils.ALIGN_CENTER);
        printUtils.addItem(28.0f, false, "VAT" + ":" + (model.getData().getSelectedWereHouse()!=null?(model.getData().getSelectedWereHouse().getTax_number()!=null?model.getData().getSelectedWereHouse().getTax_number():""):""), PrintUtils.ALIGN_CENTER);
        printUtils.addItem(28.0f, false, (model.getData().getSelectedWereHouse()!=null?(model.getData().getSelectedWereHouse().getName()!=null?model.getData().getSelectedWereHouse().getName():""):""), PrintUtils.ALIGN_CENTER);
        printUtils.addItem(28.0f, false, "Simplified tax invoice", PrintUtils.ALIGN_CENTER);
        printUtils.addLineWithHeight(50);
        printUtils.addItem(28.0f, false, "Receipt:#", PrintUtils.ALIGN_RIGHT);
        printUtils.addItem(28.0f, false, "Date:" + getNow(), PrintUtils.ALIGN_RIGHT);
        printUtils.addItem(28.0f, false, "Cashier:" + (model.getData().getSelectedUser()!=null?(model.getData().getSelectedUser().getName()!=null?model.getData().getSelectedUser().getName():""):""), PrintUtils.ALIGN_RIGHT);
        printUtils.addItem(28.0f, false, "POS:" + (model.getData().getSelectedPos()!=null?(model.getData().getSelectedPos().getTitle()!=null?model.getData().getSelectedPos().getTitle():""):""), PrintUtils.ALIGN_RIGHT);
        printUtils.addLineWithHeight(100);
        printUtils.addLineSeparator();
        printUtils.addLineWithHeight(15);
        printUtils.addItem(28.0f, false, "Delivery", PrintUtils.ALIGN_RIGHT);
        printUtils.addLineWithHeight(50);
        printUtils.addLineSeparator();
        printUtils.addLineWithHeight(15);

        printUtils.addRowItem(25.0f, false, "Item1" ,"0.00", PrintUtils.ALIGN_RIGHT);
        printUtils.addItem(25.0f, true, "0.00X1", PrintUtils.ALIGN_RIGHT);
        printUtils.addLineWithHeight(8);

        printUtils.addRowItem(25.0f, false, "Item2" ,"0.00", PrintUtils.ALIGN_RIGHT);
        printUtils.addItem(25.0f, true, "0.00X1", PrintUtils.ALIGN_RIGHT);

        printUtils.addLineWithHeight(50);
        printUtils.addLineSeparator();
        printUtils.addLineWithHeight(15);
        printUtils.addRowItem(30.0f, true, "Total before tax" ,"0.00", PrintUtils.ALIGN_RIGHT);

        printUtils.addLineWithHeight(50);
        printUtils.addLineSeparator();
        printUtils.addLineWithHeight(15);

        printUtils.addRowItem(30.0f, true, "Total with tax" ,"0.00", PrintUtils.ALIGN_RIGHT);

        printUtils.addLineWithHeight(50);
        printUtils.addLineSeparator();
        printUtils.addLineWithHeight(15);

        printUtils.addItem(40.0f, true, "Thank you", PrintUtils.ALIGN_CENTER);
        printUtils.addLineWithHeight(50);

        Bitmap bitmap = printUtils.printTestData(model);
    }

    private String getNow() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH).format(new Date());
    }
}
