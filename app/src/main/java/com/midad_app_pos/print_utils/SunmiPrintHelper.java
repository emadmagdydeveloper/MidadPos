package com.midad_app_pos.print_utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.RemoteException;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.midad_app_pos.R;
import com.midad_app_pos.model.UserModel;
import com.midad_app_pos.model.cart.CartList;
import com.midad_app_pos.model.cart.CartModel;
import com.midad_app_pos.tags.Tags;
import com.sunmi.peripheral.printer.InnerLcdCallback;
import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.InnerResultCallback;
import com.sunmi.peripheral.printer.SunmiPrinterService;
import com.sunmi.peripheral.printer.WoyouConsts;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SunmiPrintHelper {
    public static int NoSunmiPrinter = 0x00000000;
    public static int CheckSunmiPrinter = 0x00000001;
    public static int FoundSunmiPrinter = 0x00000002;
    public static int LostSunmiPrinter = 0x00000003;

    /**
     * sunmiPrinter means checking the printer connection status
     */
    public int sunmiPrinter = CheckSunmiPrinter;
    /**
     * SunmiPrinterService for API
     */
    private SunmiPrinterService sunmiPrinterService;

    private static SunmiPrintHelper helper = new SunmiPrintHelper();

    private SunmiPrintHelper() {
    }

    public static SunmiPrintHelper getInstance() {
        return helper;
    }

    private InnerPrinterCallback innerPrinterCallback = new InnerPrinterCallback() {
        @Override
        protected void onConnected(SunmiPrinterService service) {
            sunmiPrinterService = service;
            checkSunmiPrinterService(service);
        }

        @Override
        protected void onDisconnected() {
            sunmiPrinterService = null;
            sunmiPrinter = LostSunmiPrinter;
        }
    };

    /**
     * init sunmi print service
     */
    public void initSunmiPrinterService(Context context) {
        try {
            boolean ret = InnerPrinterManager.getInstance().bindService(context,
                    innerPrinterCallback);
            if (!ret) {
                sunmiPrinter = NoSunmiPrinter;
            }
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }

    /**
     * deInit sunmi print service
     */
    public void deInitSunmiPrinterService(Context context) {
        try {
            if (sunmiPrinterService != null) {
                InnerPrinterManager.getInstance().unBindService(context, innerPrinterCallback);
                sunmiPrinterService = null;
                sunmiPrinter = LostSunmiPrinter;
            }
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check the printer connection,
     * like some devices do not have a printer but need to be connected to the cash drawer through a print service
     */
    private void checkSunmiPrinterService(SunmiPrinterService service) {
        boolean ret = false;
        try {
            ret = InnerPrinterManager.getInstance().hasPrinter(service);
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
        sunmiPrinter = ret ? FoundSunmiPrinter : NoSunmiPrinter;
    }


    private void handleRemoteException(RemoteException e) {
        //TODO process when get one exception
    }

    /**
     * send esc cmd
     */
    public void sendRawData(byte[] data) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }
        try {
            sunmiPrinterService.sendRAWData(data, null);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     * Printer cuts paper and throws exception on machines without a cutter
     */
    public void cutpaper() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }
        try {
            sunmiPrinterService.cutPaper(null);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     * Initialize the printer
     * All style settings will be restored to default
     */
    public void initPrinter() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }
        try {
            sunmiPrinterService.printerInit(null);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     * paper feed three lines
     * Not disabled when line spacing is set to 0
     */
    public void print3Line() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.lineWrap(3, null);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     * Get printer serial number
     */
    public String getPrinterSerialNo() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return "";
        }
        try {
            return sunmiPrinterService.getPrinterSerialNo();
        } catch (RemoteException e) {
            handleRemoteException(e);
            return "";
        }
    }

    /**
     * Get device model
     */
    public String getDeviceModel() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return "";
        }
        try {
            return sunmiPrinterService.getPrinterModal();
        } catch (RemoteException e) {
            handleRemoteException(e);
            return "";
        }
    }

    /**
     * Get firmware version
     */
    public String getPrinterVersion() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return "";
        }
        try {
            return sunmiPrinterService.getPrinterVersion();
        } catch (RemoteException e) {
            handleRemoteException(e);
            return "";
        }
    }

    /**
     * Get paper specifications
     */
    public String getPrinterPaper() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return "";
        }
        try {
            return sunmiPrinterService.getPrinterPaper() == 1 ? "58mm" : "80mm";
        } catch (RemoteException e) {
            handleRemoteException(e);
            return "";
        }
    }

    /**
     * Get paper specifications
     */
    public void getPrinterHead(InnerResultCallback callbcak) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }
        try {
            sunmiPrinterService.getPrinterFactory(callbcak);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     * Get printing distance since boot
     * Get printing distance through interface callback since 1.0.8(printerlibrary)
     */
    public void getPrinterDistance(InnerResultCallback callback) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }
        try {
            sunmiPrinterService.getPrintedLength(callback);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     * Set printer alignment
     */
    public void setAlign(int align) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }
        try {
            sunmiPrinterService.setAlignment(align, null);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     * Due to the distance between the paper hatch and the print head,
     * the paper needs to be fed out automatically
     * But if the Api does not support it, it will be replaced by printing three lines
     */
    public void feedPaper() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.autoOutPaper(null);
        } catch (RemoteException e) {
            print3Line();
        }
    }

    /**
     * print text
     * setPrinterStyle Api require V4.2.22 or later, So use esc cmd instead when not supported
     * More settings reference documentation {@link WoyouConsts}
     */
    public void printText(String content, float size, boolean isBold, boolean isUnderLine) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, isBold ?
                        WoyouConsts.ENABLE : WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                if (isBold) {
                    sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);
                } else {
                    sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
                }
            }
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_UNDERLINE, isUnderLine ?
                        WoyouConsts.ENABLE : WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                if (isUnderLine) {
                    sunmiPrinterService.sendRAWData(ESCUtil.underlineWithOneDotWidthOn(), null);
                } else {
                    sunmiPrinterService.sendRAWData(ESCUtil.underlineOff(), null);
                }
            }
            sunmiPrinterService.printTextWithFont(content, null, size, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * print Bar Code
     */
    public void printBarCode(String data, int symbology, int height, int width, int textposition) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.printBarCode(data, symbology, height, width, textposition, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * print Qr Code
     */
    public void printQr(String data, int modulesize, int errorlevel) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.printQRCode(data, modulesize, errorlevel, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print a row of a table
     */
    public void printTable(String[] txts, int[] width, int[] align) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.printColumnsString(txts, width, align, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print pictures and text in the specified orde
     * After the picture is printed,
     * the line feed output needs to be called,
     * otherwise it will be saved in the cache
     * In this example, the image will be printed because the print text content is added
     */
    public void printBitmap(Bitmap bitmap, int orientation) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {
            if (orientation == 0) {

                sunmiPrinterService.printBitmap(bitmap, null);
                // sunmiPrinterService.printText("????????????\n", null);
                //sunmiPrinterService.printBitmap(bitmap, null);
                //sunmiPrinterService.printText("????????????\n", null);
            } else {
                sunmiPrinterService.printBitmap(bitmap, null);
                //sunmiPrinterService.printText("\n????????????\n", null);
                //sunmiPrinterService.printBitmap(bitmap, null);
                //sunmiPrinterService.printText("\n????????????\n", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets whether the current printer is in black mark mode
     */
    public boolean isBlackLabelMode() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return false;
        }
        try {
            return sunmiPrinterService.getPrinterMode() == 1;
        } catch (RemoteException e) {
            return false;
        }
    }

    /**
     * Gets whether the current printer is in label-printing mode
     */
    public boolean isLabelMode() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return false;
        }
        try {
            return sunmiPrinterService.getPrinterMode() == 2;
        } catch (RemoteException e) {
            return false;
        }
    }

    /**
     * Transaction printing:
     * enter->print->exit(get result) or
     * enter->first print->commit(get result)->twice print->commit(get result)->exit(don't care
     * result)
     */
    public void printTrans(Context context, InnerResultCallback callbcak, Bitmap bitmap) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.enterPrinterBuffer(true);
            printExample(context, bitmap);
            sunmiPrinterService.exitPrinterBufferWithCallback(true, callbcak);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open cash box
     * This method can be used on Sunmi devices with a cash drawer interface
     * If there is no cash box (such as V1???P1) or the call fails, an exception will be thrown
     * <p>
     * Reference to https://docs.sunmi.com/general-function-modules/external-device-debug/cash-box-driver/}
     */
    public void openCashBox() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.openDrawer(null);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     * LCD screen control
     *
     * @param flag 1 ?????? Initialization
     *             2 ?????? Light up screen
     *             3 ?????? Extinguish screen
     *             4 ?????? Clear screen contents
     */
    public void controlLcd(int flag) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.sendLCDCommand(flag);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     * Display text SUNMI,font size is 16 and format is fill
     * sendLCDFillString(txt, size, fill, callback)
     * Since the screen pixel height is 40, the font should not exceed 40
     */
    public void sendTextToLcd() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.sendLCDFillString("SUNMI", 16, true, new InnerLcdCallback() {
                @Override
                public void onRunResult(boolean show) throws RemoteException {
                    //TODO handle result
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * Display two lines and one empty line in the middle
     */
    public void sendTextsToLcd() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {
            String[] texts = {"SUNMI", null, "SUNMI"};
            int[] align = {2, 1, 2};
            sunmiPrinterService.sendLCDMultiString(texts, align, new InnerLcdCallback() {
                @Override
                public void onRunResult(boolean show) throws RemoteException {
                    //TODO handle result
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * Display one 128x40 pixels and opaque picture
     */
    public void sendPicToLcd(Bitmap pic) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.sendLCDBitmap(pic, new InnerLcdCallback() {
                @Override
                public void onRunResult(boolean show) throws RemoteException {
                    //TODO handle result
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sample print receipt
     */
    public void printExample(Context context, Bitmap bitmap) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {
            int paper = sunmiPrinterService.getPrinterPaper();
            sunmiPrinterService.printerInit(null);
            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printText("????????????\n", null);
            sunmiPrinterService.printBitmap(bitmap, null);
            sunmiPrinterService.lineWrap(1, null);
            sunmiPrinterService.setAlignment(0, null);
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.SET_LINE_SPACING, 0);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(new byte[]{0x1B, 0x33, 0x00}, null);
            }
            sunmiPrinterService.printTextWithFont("???????????????????????????????????????????????????,?????????????????????????????????????????????\n",
                    null, 12, null);
            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);
            }
            String txts[] = new String[]{"??????", "??????"};
            int width[] = new int[]{1, 1};
            int align[] = new int[]{0, 2};
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
            }
            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }
            txts[0] = "??????";
            txts[1] = "17??";
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            txts[0] = "??????";
            txts[1] = "10??";
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            txts[0] = "??????";
            txts[1] = "11??";
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            txts[0] = "??????";
            txts[1] = "11??";
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            txts[0] = "??????";
            txts[1] = "10??";
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }
            sunmiPrinterService.printTextWithFont("??????:          59??\b", null, 40, null);
            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printQRCode("????????????", 10, 0, null);
            sunmiPrinterService.setFontSize(36, null);
            sunmiPrinterService.printText("????????????", null);
            sunmiPrinterService.autoOutPaper(null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to report the real-time query status of the printer, which can be used before each
     * printing
     */
    public void showPrinterStatus(Context context) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }
        String result = "Interface is too low to implement interface";
        try {
            int res = sunmiPrinterService.updatePrinterState();
            switch (res) {
                case 1:
                    result = "printer is running";
                    break;
                case 2:
                    result = "printer found but still initializing";
                    break;
                case 3:
                    result = "printer hardware interface is abnormal and needs to be reprinted";
                    break;
                case 4:
                    result = "printer is out of paper";
                    break;
                case 5:
                    result = "printer is overheating";
                    break;
                case 6:
                    result = "printer's cover is not closed";
                    break;
                case 7:
                    result = "printer's cutter is abnormal";
                    break;
                case 8:
                    result = "printer's cutter is normal";
                    break;
                case 9:
                    result = "not found black mark paper";
                    break;
                case 505:
                    result = "printer does not exist";
                    break;
                default:
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }

    /**
     * Demo printing a label
     * After printing one label, in order to facilitate the user to tear the paper, call
     * labelOutput to push the label paper out of the paper hatch
     * ????????????????????????
     * ??????????????????????????????????????????????????????labelOutput,???????????????????????????
     */
    public void printOneLabel() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }
        try {
            sunmiPrinterService.labelLocate();
            printLabelContent();
            sunmiPrinterService.labelOutput();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Demo printing multi label
     * <p>
     * After printing multiple labels, choose whether to push the label paper to the paper hatch according to the needs
     * ????????????????????????
     * ????????????????????????????????????????????????????????????????????????
     */
    public void printMultiLabel(int num) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }
        try {
            for (int i = 0; i < num; i++) {
                sunmiPrinterService.labelLocate();
                printLabelContent();
            }
            sunmiPrinterService.labelOutput();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Custom label ticket content
     * In the example, not all labels can be applied. In actual use, please pay attention to adapting the size of the label. You can adjust the font size and content position.
     * ??????????????????????????????
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private void printLabelContent() throws RemoteException {
        sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE);
        sunmiPrinterService.lineWrap(1, null);
        sunmiPrinterService.setAlignment(0, null);
        sunmiPrinterService.printText("??????         ??????\n", null);
        sunmiPrinterService.printText("????????????         12-13  14???\n", null);
        sunmiPrinterService.printBarCode("{C1234567890123456", 8, 90, 2, 2, null);
        sunmiPrinterService.lineWrap(1, null);
    }


    public void printTestExample(Context context, UserModel model, int paper, String lang) {


        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {

            int paperWidth = 32;
            if (paper == 2) {
                paperWidth = 48;
            }

            sunmiPrinterService.printerInit(null);
            sunmiPrinterService.setAlignment(1, null);
            printText(model.getData().getSelectedUser().getCompany_name(), "ar");
            String vatField = "";
            String invoiceField = "";
            String deliveryField = "";
            String name = "";
            String receiptField = "";
            String receiptDateField = "";
            String cashierField = "";
            String posField = "";
            String totalBeforeTaxField = "";
            String totalWithTaxField = "";
            String itemField = "";
            String thx = "";
            if (lang.equals("en")) {
                name = "Midad";
                vatField = "VAT#";
                invoiceField = "Simplified tax invoice";
                deliveryField = "Delivery";
                receiptField = "Receipt #:#####";
                receiptDateField = "Invoice date :dd-MMM-YYYY hh:mm:ss";
                cashierField = "Cashier :" + model.getData().getSelectedUser().getName();
                posField = "POS :" + model.getData().getSelectedPos().getTitle();
                totalBeforeTaxField = "Total before tax";
                totalWithTaxField = "Total with tax";
                itemField = "item";
                thx = "Thank you";
            } else {
                name = "????????";
                vatField = "?????????? ??????????????#";
                invoiceField = "???????????? ???????????? ??????????";
                deliveryField = "??????????????";
                receiptField = "?????????????? #:#####";
                receiptDateField = "?????????? ???????????????? :dd-MMM-YYYY hh:mm:ss";
                cashierField = "?????????????? :" + model.getData().getSelectedUser().getName();
                posField = "???????? ?????? :" + model.getData().getSelectedPos().getTitle();
                totalBeforeTaxField = "???????????????? ?????? ??????????????";
                totalWithTaxField = "???????????????? ???????? ??????????????";
                itemField = "????????";
                thx = "???????? ????????????????";

            }
            printText(name, "");
            printText(vatField + "0000000000000", "");
            printText(invoiceField, "");

            ///sunmiPrinterService.printBitmap(bitmap, null);
            sunmiPrinterService.lineWrap(1, null);
            sunmiPrinterService.setAlignment(0, null);


            printText(receiptField, lang);
            printText(receiptDateField, lang);
            printText(cashierField, lang);
            printText(posField, lang);
            sunmiPrinterService.lineWrap(1, null);
            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }
            printText(deliveryField, lang);

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            for (int index = 1; index < 4; index++) {
                String text1 = itemField + index;
                String text2 = "0.00";
                String amount = "1X0.00";
                printRowData(text1, text2, paperWidth - 7, lang);
                printText(amount, lang);
            }


            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            printRowWithFontAndStyle(totalBeforeTaxField, "0.00", paperWidth - 7, 25, true, lang);


            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            printRowWithFontAndStyle(totalWithTaxField, "0.00", paperWidth - 7, 25, true, lang);


            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }
            sunmiPrinterService.setAlignment(1, null);
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);

            }

            sunmiPrinterService.printTextWithFont(thx + "\n", null, 30, null);
            sunmiPrinterService.lineWrap(8, null);
            sunmiPrinterService.autoOutPaper(null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void printInvoice(Context context, UserModel model, int paper, String lang, CartModel cartModel, List<Integer> ids, Bitmap logo) {


        if (sunmiPrinterService == null) {
            return;
        }

        try {

            int paperWidth = 32;
            if (paper == 2) {
                paperWidth = 48;
            }

            sunmiPrinterService.printerInit(null);
            sunmiPrinterService.setAlignment(1, null);
            if (logo!=null){
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                logo.compress(Bitmap.CompressFormat.PNG,0,outputStream);

                byte [] rv = new byte[]{};
                sunmiPrinterService.sendRAWData(BytesUtil.byteMerger(rv,ESCUtil.printBitmap(logo,0)),null);
                //sunmiPrinterService.printBitmap(logo,null);

            }

            int index = 0;
            for (CartModel.Cart cart : cartModel.getData()) {

                int invoice_id = ids.get(index);
                printText(model.getData().getSelectedUser().getCompany_name(), "ar");
                String vatField = "";
                String invoiceField = "";
                String deliveryField = "";
                String name = "";
                String receiptField = "";
                String receiptDateField = "";
                String cashierField = "";
                String posField = "";
                String totalBeforeTaxField = "";
                String totalWithTaxField = "";
                String discounts = "";

                String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",Locale.ENGLISH).format(Long.parseLong(cart.getDate()));

                name = model.getData().getSelectedUser()!=null?model.getData().getSelectedUser().getCompany_name():(lang.equals("ar")?"????????":"Midad");
                if (lang.equals("en")) {
                    vatField = "VAT#" + (model.getData().getSelectedWereHouse() != null ? model.getData().getSelectedWereHouse().getTax_number() : "");
                    invoiceField = "Simplified tax invoice";
                    deliveryField = cart.getDelivery_name();
                    receiptField = "Receipt #:" + invoice_id;
                    receiptDateField = "Invoice date :" + date;
                    cashierField = "Cashier :" + model.getData().getSelectedUser().getName();
                    posField = "POS :" + model.getData().getSelectedPos().getTitle();
                    totalBeforeTaxField = "Total before tax";
                    totalWithTaxField = "Total with tax";
                    discounts = "Discounts";

                } else {
                    vatField = "?????????? ??????????????#" + (model.getData().getSelectedWereHouse() != null ? model.getData().getSelectedWereHouse().getTax_number() : "");
                    invoiceField = "???????????? ???????????? ??????????";
                    deliveryField = cart.getDelivery_name();
                    receiptField = "?????????????? #:" + invoice_id;
                    receiptDateField = "?????????? ???????????????? :" + date;
                    cashierField = "?????????????? :" + model.getData().getSelectedUser().getName();
                    posField = "???????? ?????? :" + model.getData().getSelectedPos().getTitle();
                    totalBeforeTaxField = "?????????????? ?????? ??????????????";
                    totalWithTaxField = "?????????????? ???????? ??????????????";
                    discounts = "????????????????";

                }
                sunmiPrinterService.setAlignment(1, null);
                printText(name, "");
                printText(vatField, "");
                printText(invoiceField, "");

                sunmiPrinterService.lineWrap(1, null);
                sunmiPrinterService.setAlignment(0, null);


                printText(receiptField, lang);
                printText(receiptDateField, lang);
                printText(cashierField, lang);
                printText(posField, lang);
                sunmiPrinterService.lineWrap(1, null);

                if (deliveryField!=null&&!deliveryField.isEmpty()){
                    if (paper == 1) {
                        sunmiPrinterService.printText("--------------------------------\n", null);
                    } else {
                        sunmiPrinterService.printText("------------------------------------------------\n",
                                null);
                    }
                    printText(deliveryField, lang);
                }


                if (paper == 1) {
                    sunmiPrinterService.printText("--------------------------------\n", null);
                } else {
                    sunmiPrinterService.printText("------------------------------------------------\n",
                            null);
                }

                for (CartModel.Detail detail : cart.getDetails()) {
                    String text1 = detail.getProduct_name();
                    String text2 = detail.getTotalPrice();
                    String amount = detail.getQty() + "X" + detail.getNet_unit_price();
                    printRowData(text1, text2, paperWidth - 7, lang);
                    printText(amount, lang);
                }

                if (paper == 1) {
                    sunmiPrinterService.printText("--------------------------------\n", null);
                } else {
                    sunmiPrinterService.printText("------------------------------------------------\n",
                            null);
                }

                printRowWithFontAndStyle(totalBeforeTaxField, String.format(Locale.US, "%.2f", cart.getTotal_price()), paperWidth - 7, 22, true, lang);

                for (CartModel.Detail detail : cart.getDetails()) {
                    if (Double.parseDouble(detail.getTax_rate())!=0){
                        String text1 = "VAT "+detail.getTax_rate()+"%";
                        String text2 = String.format(Locale.US,"%.2f",(detail.getQty()*detail.getNet_unit_price())*Double.parseDouble(detail.getTax_rate())/100);
                        printRowData(text1, text2, paperWidth - 7, lang);
                    }

                }

                if (cart.getOrder_discount()!=0){
                    String text2 = String.format(Locale.US,"%.2f",cart.getOrder_discount());
                    printRowData(discounts, text2, paperWidth - 7, lang);
                }

                if (paper == 1) {
                    sunmiPrinterService.printText("--------------------------------\n", null);
                } else {
                    sunmiPrinterService.printText("------------------------------------------------\n",
                            null);
                }

                printRowWithFontAndStyle(totalWithTaxField, String.format(Locale.US, "%.2f", cart.getGrand_total()) , paperWidth - 7, 28, true, lang);
                sunmiPrinterService.lineWrap(1,null);
                for (CartModel.Payment payment :cart.getPayments()){
                    printRowData(payment.getName(), String.format(Locale.US, "%.2f", payment.getPaid()) , paperWidth - 7, lang);

                }


                if (paper == 1) {
                    sunmiPrinterService.printText("--------------------------------\n", null);
                } else {
                    sunmiPrinterService.printText("------------------------------------------------\n",
                            null);
                }
                sunmiPrinterService.setAlignment(1, null);
                try {
                    sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE);
                } catch (RemoteException e) {
                    sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);

                }

                sunmiPrinterService.printTextWithFont((model.getData().getInvoiceSettings() == null ? "\n" : model.getData().getInvoiceSettings().getFooter()) + "\n", null, 30, null);
                sunmiPrinterService.lineWrap(1, null);

                ZatcaQRCodeGeneration.Builder builder = new ZatcaQRCodeGeneration.Builder();
                builder.sellerName(model.getData().getSelectedUser().getName()) // Shawrma House
                        .taxNumber(model.getData().getSelectedWereHouse().getTax_number()) // 1234567890
                        .invoiceDate(date) //..> 22/11/2021 03:00 am
                        .totalAmount(String.format(Locale.US, "%.2f", cart.getGrand_total())) // 100
                        .taxAmount(String.format(Locale.US, "%.2f", cart.getOrder_tax()));

                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap Qrcode = barcodeEncoder.encodeBitmap(builder.getBase64(), BarcodeFormat.QR_CODE, 300, 300);
                sunmiPrinterService.printBitmap(Qrcode, null);
                feedPaper();
                sunmiPrinterService.cutPaper(null);
                index++;
            }


        } catch (RemoteException | WriterException e) {
            e.printStackTrace();
        }

    }


    private String spaceBetween(String str1, String str2, int paperWidth) {
        int space = paperWidth - (str1.length() - str2.length());

        space = Math.abs(space);

        StringBuilder sp = new StringBuilder();

        for (int index = 0; index < space; index++) {
            sp.append(" ");
        }

        return sp.toString();
    }

    private void printRowData(String str1, String str2, int paperWidth, String lang) {
        String data = "";

        if (lang.equals("en")) {
            data = str2 + spaceBetween(str1, str2, (paperWidth - 3)) + str1;
        } else {
            data = str1 + spaceBetween(str1, str2, (paperWidth - 3)) + str2;


        }

        try {


            sunmiPrinterService.printText(data + "\n", null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void printText(String str, String lang) {
        int align = 1;
        if (lang.equals("ar")) {
            align = 2;
        } else if (lang.equals("en")) {
            align = 0;
        }
        try {
            sunmiPrinterService.setAlignment(align, null);
            sunmiPrinterService.printText(str + "\n", null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    private void printRowWithFontAndStyle(String str1, String str2, int paperWidth, int fontSize, boolean isBold, String lang) {
        String data = "";

        if (lang.equals("en")) {
            data = str2 + spaceBetween(str1, str2, (paperWidth - 3)) + str1;
        } else {
            data = str1 + spaceBetween(str1, str2, (paperWidth - 3)) + str2;

        }
        if (isBold) {
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE);
            } catch (RemoteException e) {
                try {
                    sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        }


        try {
            sunmiPrinterService.printTextWithFont(data + "\n", null, fontSize, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
