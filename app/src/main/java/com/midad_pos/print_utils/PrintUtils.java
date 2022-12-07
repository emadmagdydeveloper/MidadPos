package com.midad_pos.print_utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;


import com.midad_pos.R;
import com.midad_pos.databinding.ActivityAddPrinterBinding;

public class PrintUtils {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Thread thread;

    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;
    private PrintResponse response;
    private BluetoothDevice device;


    public PrintUtils(PrintResponse response) {
        this.response = response;
    }


    @SuppressLint("MissingPermission")
    public void findBluetoothDevice(AppCompatActivity context) {

        try {
            BluetoothManager bluetoothManager = null;
            BluetoothAdapter bluetoothAdapter = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                bluetoothManager = context.getSystemService(BluetoothManager.class);
                bluetoothAdapter = bluetoothManager.getAdapter();
            } else {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }


            if (bluetoothAdapter == null) {
                Toast.makeText(context, "Device not support bluetooth", Toast.LENGTH_SHORT).show();
                return;
            }

            if (bluetoothAdapter.isEnabled()) {

                Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

                if (pairedDevice.size() > 0) {
                    List<BluetoothDevice> list = new ArrayList<>(pairedDevice);
                    response.onDevices(list);
                } else {
                    Toast.makeText(context, "No devices available", Toast.LENGTH_SHORT).show();

                }
            } else {
                response.onStartIntent();
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivityForResult(enableBT, 0);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void connectBluetoothPrinter(BluetoothDevice bluetoothDevice, int paper, AppCompatActivity context, String lang, ActivityAddPrinterBinding binding) {
        try {

            //Standard uuid from string //
            UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
            bluetoothSocket.connect();
            Toast.makeText(context, device.getName() + "connected", Toast.LENGTH_SHORT).show();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            beginListenData();
            printTestDataText(context, paper, binding);

        } catch (Exception ex) {

        }
    }


    public void beginListenData() {
        try {

            final Handler handler = new Handler();
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            thread = new Thread(() -> {

                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int byteAvailable = inputStream.available();
                        if (byteAvailable > 0) {
                            byte[] packetByte = new byte[byteAvailable];
                            inputStream.read(packetByte);

                            for (int i = 0; i < byteAvailable; i++) {
                                byte b = packetByte[i];
                                if (b == delimiter) {
                                    byte[] encodedByte = new byte[readBufferPosition];
                                    System.arraycopy(
                                            readBuffer, 0,
                                            encodedByte, 0,
                                            encodedByte.length
                                    );
                                    final String data = new String(encodedByte, StandardCharsets.US_ASCII);
                                    readBufferPosition = 0;
                                    Log.e("data", data);
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (Exception ex) {
                        stopWorker = true;
                    }
                }

            });

            thread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void printTestDataText(Context context, int paper, ActivityAddPrinterBinding binding) {

        float width;
        float height;

        if (paper==576){
            width = binding.layoutPrint.scrollViewBluetooth576.getChildAt(0).getWidth();
            height = dpToPx(binding.layoutPrint.scrollViewBluetooth576.getChildAt(0).getHeight(),context);

        }else {
             width = binding.layoutPrint.scrollViewBluetooth384.getChildAt(0).getWidth();
             height = dpToPx(binding.layoutPrint.scrollViewBluetooth384.getChildAt(0).getHeight(),context);

        }


        Bitmap b = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        if (paper==576){
            binding.layoutPrint.scrollViewBluetooth576.draw(canvas);

        }else {
            binding.layoutPrint.scrollViewBluetooth384.draw(canvas);

        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        double h = b.getWidth() / width;
        double dstH = b.getHeight() / h;


        Bitmap newBitmap = Bitmap.createScaledBitmap(b, (int) width, (int) dstH, true);


        int w = newBitmap.getWidth();
        int h2 = newBitmap.getHeight();
        int newWidth = (w/8+1)*8;
        float scaleWidth = ((float) newWidth) / w;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, 1);
        Bitmap b2 = Bitmap.createBitmap(newBitmap, 0, 0, w, h2, matrix, true);

        printImage(b2);


        /*float scale = context.getResources().getDisplayMetrics().density;
        int textSize = (int) (10*scale);
        int space_top = 250;
        int spaceBetween40 = 40;
        int spaceBetween50 = 50;
        int spaceBetween100 = 100;
        int paddingLeftRight = (int) (16*scale);

        int spaceBillTopBottom = (space_top*2)+440;
        int height = 0;

        Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint1.setColor(Color.BLACK);
        paint1.setTextSize(textSize);
        Rect rect1 = new Rect();
        paint1.getTextBounds("midad", 0, "midad".length(), rect1);
        height += rect1.height();
        Rect rect2 = new Rect();
        paint1.getTextBounds("VAT#0000000000", 0, "VAT#0000000000".length(), rect2);
        height += rect2.height();

        Rect rect3 = new Rect();
        paint1.getTextBounds("Midad point of sale", 0, "Midad point of sale".length(), rect3);
        height += rect3.height();

        Rect rect4 = new Rect();
        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint2.setColor(Color.BLACK);
        paint2.setTextSize(textSize);
        paint2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint2.getTextBounds("Simplified tax invoice", 0, "Simplified tax invoice".length(), rect4);
        height += rect4.height();


        Rect rect5 = new Rect();
        paint1.getTextBounds("Receipt #:0", 0, "Receipt #:0".length(), rect5);
        height += rect5.height();

        Rect rect6 = new Rect();
        paint1.setTextSize(textSize);
        paint1.getTextBounds("Invoice date:dd-mmm-yyyy hh:mm:ss", 0, "Invoice date:dd-mmm-yyyy hh:mm:ss".length(), rect6);
        height += rect6.height();
        paint1.setTextSize(textSize);

        Rect rect7 = new Rect();
        paint1.getTextBounds("Cashier : ", 0, "Cashier : ".length(), rect7);
        height += rect7.height();

        Rect rect8 = new Rect();
        paint1.getTextBounds("POS : ", 0, "POS : ".length(), rect8);
        height += rect8.height();




        height +=spaceBillTopBottom;

        Bitmap b = Bitmap.createBitmap(576, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg.setColor(ContextCompat.getColor(context,R.color.grey3));
        canvas.drawPaint(bg);
        float dx1 = (float) ((576.0-rect1.width())/2.0);
        float dx2 = (float) ((576.0-rect2.width())/2.0);
        float dx3 = (float) ((576.0-rect3.width())/2.0);
        float dx4 = (float) ((576.0-rect4.width())/2.0);

        float dy =0;
        dy +=space_top;
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(context,R.color.transparent));
        canvas.drawText("midad",dx1,dy,paint1);
        dy += rect1.height()+spaceBetween40;
        canvas.drawText("VAT#0000000000",dx2,dy,paint1);
        dy +=rect2.height()+spaceBetween40;
        canvas.drawText("Midad point of sale",dx3,dy,paint1);
        dy +=rect3.height()+spaceBetween40;
        canvas.drawText("Simplified tax invoice",dx4,dy,paint2);
        dy += rect4.height()+spaceBetween100;
        canvas.drawText("Receipt #:0",paddingLeftRight,dy,paint1);
        dy += rect5.height()+spaceBetween40;
        canvas.drawText("Cashier : ",paddingLeftRight,dy,paint1);
        dy += rect6.height()+spaceBetween40;
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        p.setColor(Color.BLACK);
        p.setTextSize(textSize);
        canvas.drawText("Invoice date:dd-mmm-yyyy hh:mm:ss",paddingLeftRight,dy,p);

        dy += rect7.height()+spaceBetween40;
        canvas.drawText("POS : ",paddingLeftRight,dy,paint1);
        dy += rect8.height()+spaceBetween50;
        canvas.drawText(createDiscreteLine(576,textSize, (int) context.getResources().getDisplayMetrics().density),paddingLeftRight,dy,paint1);



        canvas.save();
        canvas.translate(0,0);
        canvas.restore();

        binding.layoutPrint.image.setImageBitmap(b);
        binding.layoutPrint.image.setVisibility(View.VISIBLE);
        binding.flPrintTest.setVisibility(View.VISIBLE);
*/

        /*binding.layoutPrint.image.setImageBitmap(bitmap1);
        binding.layoutPrint.image.setVisibility(View.VISIBLE);
        binding.flPrintTest.setVisibility(View.VISIBLE);*/

        /*try {
            byte[]  bitmapData = PrintPicture.POS_PrintBMP(bitmap, paper, 4);
            outputStream.write(bitmapData);

        } catch (IOException e) {
            e.printStackTrace();
        }*/


       /* try {
            UserModel model = Preferences.getInstance().getUserData(context);
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

            if (lang.equals("ar")){
                name = "مداد";
                vatField = "الرقم الضريبي#";
                invoiceField = "فاتورة ضريبية مبسطة";
                deliveryField = "التوصيل";
                receiptField = "التذكرة #:#####";
                receiptDateField = "تاريخ الفاتورة :dd-MMM-YYYY hh:mm:ss";
                cashierField = "الكاشير :" + model.getData().getSelectedUser().getName();
                posField = "نقطة بيع :" + model.getData().getSelectedPos().getTitle();
                totalBeforeTaxField = "الإجمالى قبل الضريبة";
                totalWithTaxField = "الإجمالي شامل الضريبة";
                itemField = "عنصر";
                thx = "شكرا لزيارتكم";
            }else {
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
            }

            String msg = "\n";
            Arabic864 arabic = new Arabic864();

            outputStream.write(msg.getBytes());
            outputStream.write(arabic.Convert(name,false));
            outputStream.write(arabic.Convert(vatField,false));
            outputStream.write(arabic.Convert(invoiceField,false));



           *//* msg += name + "\n";
            msg += vatField + "#0000000000000\n";
            msg += invoiceField + "\n";
            if (lang.equals("ar")) {
                outputStream.write(ESCUtil.alignRight());

            } else {
                outputStream.write(ESCUtil.alignLeft());

            }
            msg += "\n";
            msg += "\n";

            msg += receiptField + "#" + "\n";
            msg += receiptDateField + "\n";
            msg += cashierField + "\n";
            msg += posField + "\n";

            if (paper == 58) {
                msg += "--------------------------------\n";

            } else {
                msg += "------------------------------------------------\n";

            }
            msg += "\n";
            msg += deliveryField;
            msg += "\n";

            if (paper == 58) {
                msg += "--------------------------------\n";

            } else {
                msg += "------------------------------------------------\n";

            }
            for (int i = 1; i < 4; i++) {
                msg += buildRow(itemField + i, "0.00", paper, lang);
                msg += "1 X 0.00\n";
            }

            if (paper == 58) {
                msg += "--------------------------------\n";

            } else {
                msg += "------------------------------------------------\n";

            }
            outputStream.write(ESCUtil.boldOn());
            msg += buildRow(totalBeforeTaxField, "0.00", paper, lang);

            outputStream.write(ESCUtil.boldOff());

            if (paper == 58) {
                msg += "--------------------------------\n";

            } else {
                msg += "------------------------------------------------\n";

            }

            outputStream.write(ESCUtil.boldOn());

            msg += buildRow(totalWithTaxField, "0.00", paper, lang);


            outputStream.write(ESCUtil.boldOff());

            if (paper == 58) {
                msg += "--------------------------------\n";

            } else {
                msg += "------------------------------------------------\n";

            }
            outputStream.write(ESCUtil.alignCenter());
            outputStream.write(ESCUtil.boldOn());

            msg += thx + "\n";
            msg += "\n";
            msg += "\n";
            msg += "\n";
            outputStream.write(msg.getBytes(StandardCharsets.US_ASCII));*//*
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
       /* lang = "en";
        Log.e("lang","ss");

        if (lang.equals("en")){
            new Thread(() -> {
                UserModel model = Preferences.getInstance().getUserData(context);
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

                try {
                    DeviceConnection connection = new BluetoothConnection(device);
                    EscPosPrinter printer = new EscPosPrinter(connection, 203, 48f, 32);
                    String items ="";
                    for (int i = 1;i<4;i++){
                        items = "[L]"+itemField+i+"[R]"+"0.00"+"\n";
                        items +="[L]1X0.00\n";
                    }
                    printer.printFormattedText("[C]<b><font size= 'big'>" + name + "</font></b>\n" +
                            "[C]"+vatField+"#0000000000000\n"+
                            "[C]"+invoiceField+"\n"+
                            "[L]"+receiptField+"#\n"+
                            "[L]"+receiptDateField+"\n"+
                            "[L]"+cashierField+"\n"+
                            "[L]"+posField+"\n"+
                            "[C]"+"\n"+
                            "[C]--------------------------------\n"+
                            "[L]"+deliveryField+"\n"+
                            "[C]"+"--------------------------------\n"+
                            "[C]"+"\n"+
                            items+
                            "[C]--------------------------------\n"+
                            "[L]<b>"+totalBeforeTaxField+"</b>[R]<b>"+"0.00</b>\n"+
                            "[C]--------------------------------\n"+
                            "[L]<b>"+totalWithTaxField+"</b>[R]<b>"+"0.00</b>\n"+
                            "[C]\n"+
                            "[C]<b><font size= 'big'>"+thx+"</font></b>\n"+
                            "[C]" +"\n"+
                            "[C]" +"\n"+
                            "[C]" +"\n"+
                            "[C]" +"\n"+
                            "[C]" +"\n"
                    );
                } catch (EscPosConnectionException | EscPosEncodingException | EscPosBarcodeException | EscPosParserException e) {
                    e.printStackTrace();

                }
            }).start();

        }else {
            new Thread(() -> {
                UserModel model = Preferences.getInstance().getUserData(context);
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

                name = "مداد";
                vatField = "الرقم الضريبي#";
                invoiceField = "فاتورة ضريبية مبسطة";
                deliveryField = "التوصيل";
                receiptField = "التذكرة #:#####";
                receiptDateField = "تاريخ الفاتورة :dd-MMM-YYYY hh:mm:ss";
                cashierField = "الكاشير :" + model.getData().getSelectedUser().getName();
                posField = "نقطة بيع :" + model.getData().getSelectedPos().getTitle();
                totalBeforeTaxField = "الإجمالى قبل الضريبة";
                totalWithTaxField = "الإجمالي شامل الضريبة";
                itemField = "عنصر";
                thx = "شكرا لزيارتكم";

                try {
                    DeviceConnection connection = new BluetoothConnection(device);
                    EscPosPrinter printer = new EscPosPrinter(connection, 203, 48f, 32);
                    String items ="";
                    for (int i = 1;i<4;i++){
                        items = "[R]"+itemField+i+"[L]"+"0.00"+"\n";
                        items +="[R]1X0.00\n";
                    }
                    printer.printFormattedText("[C]<b><font size= 'big'>" + name + "</font></b>\n" +
                            "[C]" + vatField + "#0000000000000\n" +
                            "[C]" + invoiceField + "\n" +
                            "[R]" + receiptField + "#\n" +
                            "[R]" + receiptDateField + "\n" +
                            "[R]" + cashierField + "\n" +
                            "[R]" + posField + "\n" +
                            "[R]" +"\n"+
                            "[R]" + "--------------------------------\n"+
                            "[R]" +deliveryField+"\n"+
                            "[C]" + "--------------------------------\n"+
                            "[C]" +"\n"+
                            items+
                            "[C]" + "--------------------------------\n"+
                            "[R]<b>" + totalBeforeTaxField+"</b>[L]<b>"+"0.00</b>\n"+
                            "[C]" + "--------------------------------\n"+
                            "[R]<b>" + totalWithTaxField+"</b>[L]<b>"+"0.00</b>\n"+
                            "[C]" +"\n"+
                            "[C]<b><font size= 'big'>"+thx+"</font></b>\n"+
                            "[C]" +"\n"+
                            "[C]" +"\n"+
                            "[C]" +"\n"+
                            "[C]" +"\n"+
                            "[C]" +"\n"



                    );
                } catch (EscPosConnectionException | EscPosParserException | EscPosBarcodeException | EscPosEncodingException e) {
                    e.printStackTrace();
                }
            }).start();

        }*/


    }

    private int dpToPx(int dp,Context context){

        return (int) (context.getResources().getDisplayMetrics().density*dp);
    }

    private String space(String str1, String str2, int paperWidth) {
        int sp = paperWidth - (str1.length() + str2.length());
        sp = Math.abs(sp);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < sp; i++) {
            s.append(" ");
        }

        return s.toString();
    }

    private String createDiscreteLine(int width, int textSize,int denisty){
        Rect rect = new Rect();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.LINEAR_TEXT_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize);
        paint.getTextBounds("-", 0, "-".length(), rect);
        int count = (width/rect.width())-10;
        Log.e("count",count+"__"+width+"__"+rect.width());
        String s="";
        for (int i=0;i<count;i++){
            s +="-";
        }

        return s;
    }

    private String buildRow(String str1, String str2, int paperWidth, String lang) {
        String msg = "";
        if (lang.equals("ar")) {
            msg = str2 + space(str1, str2, (paperWidth - 8)) + str1;

        } else {
            msg = str1 + space(str1, str2, (paperWidth - 8)) + str2;

        }
        return msg + "\n";
    }

    public void printImage(Bitmap bitmap) {
        try {
            PrintPic printPic = PrintPic.getInstance();
            printPic.init(bitmap);
            byte[] bitmapData = printPic.printDraw();
            outputStream.write(bitmapData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class PrintPic {

        public Canvas canvas = null;

        public Paint paint = null;

        public Bitmap bm = null;
        public int width;
        public float length = 0.0F;

        public byte[] bitbuf = null;

        private PrintPic() {
        }

        private static PrintPic instance = new PrintPic();

        public static PrintPic getInstance() {
            return instance;
        }

        public int getLength() {
            return (int) this.length + 20;
        }

        public void init(Bitmap bitmap) {
            if (null != bitmap) {
                initCanvas(bitmap.getWidth());
            }
            if (null == paint) {
                initPaint();
            }
            if (null != bitmap) {
                drawImage(0, 0, bitmap);
            }
        }

        public void initCanvas(int w) {
            int h = 10 * w;

            this.bm = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            this.canvas = new Canvas(this.bm);

            this.canvas.drawColor(-1);
            this.width = w;
            this.bitbuf = new byte[this.width / 8];
        }

        public void initPaint() {
            this.paint = new Paint();// 新建一个画笔

            this.paint.setAntiAlias(true);//

            this.paint.setColor(-16777216);

            this.paint.setStyle(Paint.Style.STROKE);
        }

        /**
         * draw bitmap
         */
        public void drawImage(float x, float y, Bitmap btm) {
            try {
                // Bitmap btm = BitmapFactory.decodeFile(path);
                this.canvas.drawBitmap(btm, x, y, null);
                if (this.length < y + btm.getHeight())
                    this.length = (y + btm.getHeight());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != btm) {
                    btm.recycle();
                }
            }
        }

        /**
         * 使用光栅位图打印
         *
         * @return 字节
         */
        public byte[] printDraw() {
            Bitmap nbm = Bitmap
                    .createBitmap(this.bm, 0, 0, this.width, getLength());

            byte[] imgbuf = new byte[this.width / 8 * getLength() + 8];

            int s = 0;

            // 打印光栅位图的指令
            imgbuf[0] = 29;// 十六进制0x1D
            imgbuf[1] = 118;// 十六进制0x76
            imgbuf[2] = 48;// 30
            imgbuf[3] = 0;// 位图模式 0,1,2,3
            // 表示水平方向位图字节数（xL+xH × 256）
            imgbuf[4] = (byte) (this.width / 8);
            imgbuf[5] = 0;
            // 表示垂直方向位图点数（ yL+ yH × 256）
            imgbuf[6] = (byte) (getLength() % 256);//
            imgbuf[7] = (byte) (getLength() / 256);

            s = 7;
            for (int i = 0; i < getLength(); i++) {// 循环位图的高度
                for (int k = 0; k < this.width / 8; k++) {// 循环位图的宽度
                    int c0 = nbm.getPixel(k * 8 + 0, i);// 返回指定坐标的颜色
                    int p0;
                    if (c0 == -1)// 判断颜色是不是白色
                        p0 = 0;// 0,不打印该点
                    else {
                        p0 = 1;// 1,打印该点
                    }
                    int c1 = nbm.getPixel(k * 8 + 1, i);
                    int p1;
                    if (c1 == -1)
                        p1 = 0;
                    else {
                        p1 = 1;
                    }
                    int c2 = nbm.getPixel(k * 8 + 2, i);
                    int p2;
                    if (c2 == -1)
                        p2 = 0;
                    else {
                        p2 = 1;
                    }
                    int c3 = nbm.getPixel(k * 8 + 3, i);
                    int p3;
                    if (c3 == -1)
                        p3 = 0;
                    else {
                        p3 = 1;
                    }
                    int c4 = nbm.getPixel(k * 8 + 4, i);
                    int p4;
                    if (c4 == -1)
                        p4 = 0;
                    else {
                        p4 = 1;
                    }
                    int c5 = nbm.getPixel(k * 8 + 5, i);
                    int p5;
                    if (c5 == -1)
                        p5 = 0;
                    else {
                        p5 = 1;
                    }
                    int c6 = nbm.getPixel(k * 8 + 6, i);
                    int p6;
                    if (c6 == -1)
                        p6 = 0;
                    else {
                        p6 = 1;
                    }
                    int c7 = nbm.getPixel(k * 8 + 7, i);
                    int p7;
                    if (c7 == -1)
                        p7 = 0;
                    else {
                        p7 = 1;
                    }
                    int value = p0 * 128 + p1 * 64 + p2 * 32 + p3 * 16 + p4 * 8
                            + p5 * 4 + p6 * 2 + p7;
                    this.bitbuf[k] = (byte) value;
                }

                for (int t = 0; t < this.width / 8; t++) {
                    s++;
                    imgbuf[s] = this.bitbuf[t];
                }
            }
            if (null != this.bm) {
                this.bm.recycle();
                this.bm = null;
            }
            return imgbuf;
        }

    }


    public interface PrintResponse {
        void onDevices(List<BluetoothDevice> list);

        void onStartIntent();
    }

}
