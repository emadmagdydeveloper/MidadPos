package com.midad_pos.mvvm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.R;
import com.midad_pos.database.AppDatabase;
import com.midad_pos.database.DAO;
import com.midad_pos.databinding.ActivityAddPrinterBinding;
import com.midad_pos.databinding.PrintTestLayoutBinding;
import com.midad_pos.model.PrinterModel;
import com.midad_pos.model.UserModel;
import com.midad_pos.preferences.Preferences;
import com.midad_pos.print_utils.PrintUtils;
import com.midad_pos.print_utils.SunmiPrintHelper;

import java.io.ByteArrayOutputStream;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddPrinterMvvm extends AndroidViewModel implements PrintUtils.PrintResponse {
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

            }else if (pos == 2) {
                printer_type = "kitchen";

            } else {
                printer_type = "other";
            }
        }

        if (getSelectedPrinterPos().getValue() != null) {
            int pos = getSelectedPrinterPos().getValue();
            if (pos == 0) {
                paper_width = "80";
            } else {
                paper_width = "58";
            }
        }


        if (getCanPrint().getValue() != null) {
            canPrintReceipt = getCanPrint().getValue();
        }

        if (getAutomaticPrint().getValue()!=null){
            canPrintAutomatic = getAutomaticPrint().getValue();
        }
        if (getBluetoothDevice().getValue()!=null){
            bluetooth_name = getBluetoothDevice().getValue().getName();
        }

        PrinterModel printerModel = new PrinterModel(getName().getValue(), printer_type, canPrintReceipt, canPrintOrder,canPrintAutomatic,ip_address,bluetooth_name, getBluetoothDevice().getValue());


        if (printer_type.equalsIgnoreCase("sunmi"))
        {

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
                                Log.e("tt","qq");
                                if (model != null) {
                                    getOnAddedError().setValue(getApplication().getApplicationContext().getString(R.string.already_added_printer));

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("q",e.getMessage());
                                if (e.getMessage() != null && e.getMessage().contains("Query returned empty result")) {

                                    savePrinter(printerModel);
                                }
                            }
                        });

            } else {
                printSunmiTest(paper_width,lang);
            }

        }
        else if (printer_type.equalsIgnoreCase("other")) {
            if (getSelectedInterfacePos().getValue() != null) {
                int pos = getSelectedInterfacePos().getValue();
                if (pos == 0) {
                    if (getBluetoothDevice().getValue()!=null) {
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
                            printBluetoothTest(paper_width,activity,lang,binding);

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

    private void printSunmiTest(String paper_width,String lang) {
        int paper = 2;
        if (paper_width.equalsIgnoreCase("80")) {
        } else {
            paper = 1;
        }


        UserModel model = Preferences.getInstance().getUserData(getApplication().getApplicationContext());
        SunmiPrintHelper.getInstance().initPrinter();
        SunmiPrintHelper.getInstance().printTestExample(getApplication().getApplicationContext(),model,paper,lang);
        /*SunmiPrintHelper.getInstance().showPrinterStatus(getApplication().getApplicationContext());
        SunmiPrintHelper.getInstance().printBitmap(bitmap,1);
        SunmiPrintHelper.getInstance().printBitmap(bitmap,1);
        SunmiPrintHelper.getInstance().cutpaper();*/
    }

    @SuppressLint("MissingPermission")
    private void printBluetoothTest(String paper_width, AppCompatActivity activity,String lang,ActivityAddPrinterBinding binding) {
        Log.e("1","1");
        if (getBluetoothDevice().getValue()!=null){


           /* if (getBluetoothDevice().getValue().getBondState()!=BluetoothDevice.BOND_BONDED){
                Toast.makeText(activity, "Device not paired", Toast.LENGTH_SHORT).show();
                return;
            }*/



            try {

                Log.e("2","2");
                Bitmap bitmap = Bitmap.createBitmap(binding.layoutPrint.scrollViewBluetooth576.getChildAt(0).getWidth(),binding.layoutPrint.scrollViewBluetooth576.getChildAt(0).getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                binding.layoutPrint.scrollViewBluetooth576.draw(canvas);

                double w = 576.0;
               /* if (paper_width.equals("58")){
                    w = 384;
                    bitmap = Bitmap.createBitmap(binding.layoutPrint.scrollViewBluetooth384.getChildAt(0).getWidth(),binding.layoutPrint.scrollViewBluetooth384.getChildAt(0).getHeight(), Bitmap.Config.ARGB_8888);

                    canvas = new Canvas(bitmap);
                    binding.layoutPrint.scrollViewBluetooth384.draw(canvas);

                }*/


                //new Thread(() -> printUtils.connectBluetoothPrinter(getBluetoothDevice().getValue(), (int) w,activity,lang,b)).start();
                printUtils.printTestDataText(bitmap, (int) w,binding);





            } catch (Exception e) {
                Log.e("err",e.getMessage());
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




}
