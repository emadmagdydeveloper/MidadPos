package com.midad_app_pos.print_utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.midad_app_pos.R;
import com.midad_app_pos.model.UserModel;

public class PrintUtils {


    public static final int ALIGN_CENTER = 100;
    public static final int ALIGN_RIGHT = 101;
    public static final int ALIGN_LEFT = 102;

    private static final byte[] NEW_LINE = {10};
    private static final byte[] ESC_ALIGN_CENTER = new byte[]{0x1b, 'a', 0x01};
    private static final byte[] ESC_ALIGN_RIGHT = new byte[]{0x1b, 'a', 0x02};
    private static final byte[] ESC_ALIGN_LEFT = new byte[]{0x1b, 'a', 0x00};
    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = {"0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"};

    private BluetoothDevice printer;
    private BluetoothSocket btSocket = null;
    private OutputStream btOutputStream = null;
    private PrintResponse response;
    private int paperWidth = 576;
    private Context context;
    private String lang;
    private int paper_height = 0;
    private int paperYPos = 0;
    private List<PrintData> printDataList = new ArrayList<>();


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

    @SuppressLint("MissingPermission")
    public BluetoothDevice getBluetoothDeviceByMacAddress(String mac, AppCompatActivity context) {
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
                return null;
            }

            if (bluetoothAdapter.isEnabled()) {

                Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

                if (pairedDevice.size() > 0) {
                    List<BluetoothDevice> list = new ArrayList<>(pairedDevice);
                    for (BluetoothDevice device:list){
                        if (device.getAddress().equals(mac)){
                            return device;
                        }
                    }
                } else {
                    new Handler(Looper.getMainLooper())
                            .post(() -> Toast.makeText(context, "No devices available", Toast.LENGTH_SHORT).show());

                }


            } else {
                new Handler(Looper.getMainLooper())
                        .post(() -> Toast.makeText(context, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show());

            }


        } catch (Exception ex) {
            new Handler(Looper.getMainLooper())
                    .post(() ->
                            Toast.makeText(context, "2"+ex.getMessage(), Toast.LENGTH_SHORT).show()

                    );

            ex.printStackTrace();
        }

        return null;
    }


    public void connectPrinter(final PrinterConnectListener listener, boolean printKitchen, BluetoothDevice printer, int paperWidth, AppCompatActivity context, String lang) {
        this.paperWidth = paperWidth;
        this.context = context;
        this.lang = lang;
        this.printer = printer;
        new ConnectTask(new ConnectTask.BtConnectListener() {
            @Override
            public void onConnected(BluetoothSocket socket) {
                btSocket = socket;
                try {
                    btOutputStream = socket.getOutputStream();
                    listener.onConnected(printKitchen);
                } catch (IOException e) {
                    listener.onFailed();
                }
            }

            @Override
            public void onFailed() {
                listener.onFailed();
            }
        }).execute(printer);
    }

    private static class ConnectTask extends AsyncTask<BluetoothDevice, Void, BluetoothSocket> {
        private BtConnectListener listener;

        private ConnectTask(BtConnectListener listener) {
            this.listener = listener;
        }

        @SuppressLint("MissingPermission")
        @Override
        protected BluetoothSocket doInBackground(BluetoothDevice... bluetoothDevices) {
            BluetoothDevice device = bluetoothDevices[0];
            UUID uuid = device.getUuids()[0].getUuid();
            BluetoothSocket socket = null;
            boolean connected = true;

            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
            }
            try {
                socket.connect();
            } catch (IOException e) {
                try {
                    socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class})
                            .invoke(device, 1);
                    socket.connect();
                } catch (Exception e2) {
                    connected = false;
                }
            }

            return connected ? socket : null;
        }

        @Override
        protected void onPostExecute(BluetoothSocket bluetoothSocket) {
            if (listener != null) {
                if (bluetoothSocket != null) listener.onConnected(bluetoothSocket);
                else listener.onFailed();
            }
        }

        private interface BtConnectListener {
            void onConnected(BluetoothSocket socket);

            void onFailed();
        }
    }

    public interface PrinterConnectListener {
        void onConnected(boolean printKitchen);

        void onFailed();
    }

    public boolean isConnected() {
        return btSocket != null && btSocket.isConnected();
    }


    public boolean printText(String text) {
        try {
            btOutputStream.write(encodeNonAscii(text).getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean printUnicode(byte[] data) {
        try {
            btOutputStream.write(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean printLine() {
        return printText("________________________________");
    }

    public boolean addNewLine() {
        return printUnicode(NEW_LINE);
    }

    public int addNewLines(int count) {
        int success = 0;
        for (int i = 0; i < count; i++) {
            if (addNewLine()) success++;
        }

        return success;
    }


    public void setAlign(int alignType) {
        byte[] d;
        switch (alignType) {
            case ALIGN_CENTER:
                d = ESC_ALIGN_CENTER;
                break;
            case ALIGN_LEFT:
                d = ESC_ALIGN_LEFT;
                break;
            case ALIGN_RIGHT:
                d = ESC_ALIGN_RIGHT;
                break;
            default:
                d = ESC_ALIGN_LEFT;
                break;
        }

        try {
            btOutputStream.write(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLineSpacing(int lineSpacing) {
        byte[] cmd = new byte[]{0x1B, 0x33, (byte) lineSpacing};
        printUnicode(cmd);
    }

    public void setBold(boolean bold) {
        byte[] cmd = new byte[]{0x1B, 0x45, bold ? (byte) 1 : 0};
        printUnicode(cmd);
    }

    public void feedPaper() {
        addNewLine();
        addNewLine();
        addNewLine();
        addNewLine();
    }


    private static String encodeNonAscii(String text) {
        return text.replace('??', 'a')
                .replace('??', 'c')
                .replace('??', 'd')
                .replace('??', 'e')
                .replace('??', 'e')
                .replace('??', 'i')
                .replace('??', 'n')
                .replace('??', 'o')
                .replace('??', 'r')
                .replace('??', 's')
                .replace('??', 't')
                .replace('??', 'u')
                .replace('??', 'u')
                .replace('??', 'y')
                .replace('??', 'z')
                .replace('??', 'A')
                .replace('??', 'C')
                .replace('??', 'D')
                .replace('??', 'E')
                .replace('??', 'E')
                .replace('??', 'I')
                .replace('??', 'N')
                .replace('??', 'O')
                .replace('??', 'R')
                .replace('??', 'S')
                .replace('??', 'T')
                .replace('??', 'U')
                .replace('??', 'U')
                .replace('??', 'Y')
                .replace('??', 'Z');
    }

    public boolean printImage(Bitmap bitmap) {
        byte[] command = decodeBitmap(bitmap);
        return printUnicode(command);
    }

    public void printBitmap(Bitmap bitmap) {
        try {
            byte[] bitmapData = PrintPicture.Print_1D2A(bitmap);
            btOutputStream.write(bitmapData);

            //btOutputStream.write(BytesUtil.getBytesFromBitMap(bitmap));
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage() + "__", Toast.LENGTH_SHORT).show();
        }
    }


    public boolean printMultiLangText(String stringData, Paint.Align align, float textSize) {
        return printMultiLangText(stringData, align, textSize, null);
    }

    public boolean printMultiLangText(String stringData, Paint.Align align, float textSize, Typeface typeface) {
        return printImage(getMultiLangTextAsImage(stringData, align, textSize, typeface));
    }


    public boolean printBarCode(String message, BarcodeFormat format, int width, int height) throws WriterException {
        return printImage(createBarCode(message, format, width, height));
    }

    public static byte[] decodeBitmap(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<>();
        StringBuffer sb;
        int zeroCount = bmpWidth % 8;
        String zeroStr = "";
        if (zeroCount > 0) {
            for (int i = 0; i < (8 - zeroCount); i++) zeroStr = zeroStr + "0";
        }

        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;
                if (r > 160 && g > 160 && b > 160) sb.append("0");
                else sb.append("1");
            }
            if (zeroCount > 0) sb.append(zeroStr);
            list.add(sb.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8 : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    private static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<>();
        for (String binaryStr : list) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);
                String hexString = strToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;
    }


    private static String strToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    private static byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<>();
        for (String hexStr : list) commandList.add(hexStringToBytes(hexStr));
        return sysCopy(commandList);
    }

    private static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) return null;
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }

        return destArray;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    public Bitmap getMultiLangTextAsImage(String text, Paint.Align align, float textSize, Typeface typeface) {


        return null;
    }

    static class PrintData {
        private float xPos;
        private float yPos;
        private String text;
        private Paint paint;


        public PrintData(float xPos, float yPos, String text, Paint paint) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.text = text;
            this.paint = paint;
        }

        public float getxPos() {
            return xPos;
        }

        public void setxPos(float xPos) {
            this.xPos = xPos;
        }

        public float getyPos() {
            return yPos;
        }

        public void setyPos(float yPos) {
            this.yPos = yPos;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Paint getPaint() {
            return paint;
        }
    }

    public Bitmap createBarCode(String message, BarcodeFormat format, int width, int height) throws WriterException {


        BitMatrix bitMatrix = new MultiFormatWriter().encode(message, format, width, height);

        int matrixWidth = bitMatrix.getWidth();
        int matrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[matrixWidth * matrixHeight];
        for (int i = 0; i < matrixHeight; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (bitMatrix.get(j, i)) {
                    pixels[i * matrixWidth + j] = 0xff000000;
                } else {
                    pixels[i * matrixWidth + j] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight);
        return bitmap;


    }

    public BluetoothSocket getSocket() {
        return btSocket;
    }

    public BluetoothDevice getDevice() {
        return printer;
    }

    public void finish() {
        if (btSocket != null) {
            try {
                btOutputStream.close();
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btSocket = null;
        }
    }

    private void cut() {
        if (btOutputStream != null) {

            try {
                btOutputStream.write(0x1D);
                btOutputStream.write(86);
                btOutputStream.write(48);
                btOutputStream.write(0);
                btOutputStream.flush();
                btOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap printTestData(UserModel userModel, boolean isOrder, Bitmap logo) {

        try {
            Bitmap b = Bitmap.createBitmap(paperWidth, paper_height + 500, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(b);
            Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
            bg.setAntiAlias(true);
            bg.setColor(ContextCompat.getColor(context, R.color.white));
            canvas.drawPaint(bg);
            canvas.drawColor(Color.WHITE);


            if (logo != null) {
                if (!isOrder) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    logo.compress(Bitmap.CompressFormat.PNG,0,outputStream);
                    int center = (paperWidth - 150) / 2;
                    canvas.drawBitmap(logo, center, paperYPos, null);

                }

            }


            for (PrintData printData : printDataList) {
                canvas.drawText(printData.getText(), printData.getxPos(), printData.getyPos(), printData.getPaint());

            }

            if (!isOrder) {
                ZatcaQRCodeGeneration.Builder builder = new ZatcaQRCodeGeneration.Builder();
                builder.sellerName(userModel.getData().getSelectedUser().getName()) // Shawrma House
                        .taxNumber(userModel.getData().getSelectedWereHouse().getTax_number()) // 1234567890
                        .invoiceDate(getNow()) //..> 22/11/2021 03:00 am
                        .totalAmount(String.format(Locale.US, "%.2f", 0.00)) // 100
                        .taxAmount(String.format(Locale.US, "%.2f", 0.00));

                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                try {
                    int center = (paperWidth - 300) / 2;
                    Bitmap Qrcode = barcodeEncoder.encodeBitmap(builder.getBase64(), BarcodeFormat.QR_CODE, 300, 300);
                    canvas.drawBitmap(Qrcode, center, paperYPos, null);




                } catch (WriterException e) {
                    e.printStackTrace();

                }
            }


            printImageBitmap(b);
            return b;
        }catch (Exception e){
            new Handler(Looper.getMainLooper())
                    .post(()->{
                        Toast.makeText(context, e.getMessage()+"_", Toast.LENGTH_SHORT).show();
                    });
        }


        return null;
    }

    private String getNow() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH).format(new Date());
    }

    public void addItem(float textSize, boolean isBold, String text, int align) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize);
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);

        float dx = 0;
        if (align == ALIGN_CENTER) {
            dx = (float) ((paperWidth - rect.width()) / 2.0);

        } else if (align == ALIGN_LEFT) {
            dx = 0;
        } else {
            dx = (paperWidth - rect.width()) - 40;
        }
        if (isBold) {
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        }


        paper_height += rect.height() + 10;
        paperYPos += rect.height() + 10;
        printDataList.add(new PrintData(dx, paperYPos, text, paint));


    }

    public void addRowItem(float textSize, boolean isBold, String text1, String text2, int align) {
        Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint1.setAntiAlias(true);
        paint1.setColor(Color.BLACK);
        paint1.setTextSize(textSize);
        Rect rect1 = new Rect();
        paint1.getTextBounds(text1, 0, text1.length(), rect1);

        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint2.setAntiAlias(true);
        paint2.setColor(Color.BLACK);
        paint2.setTextSize(textSize);
        Rect rect2 = new Rect();
        paint2.getTextBounds(text1, 0, text2.length(), rect2);

        float dx1 = 0;
        float dx2 = 0;
        if (isBold) {
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            paint2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        }


        if (align == ALIGN_LEFT) {
            dx1 = 40;
            dx2 = (paperWidth - rect2.width()) - 40;
        } else {
            dx2 = 40;
            dx1 = (paperWidth - rect1.width()) - 40;
        }


        paper_height += rect1.height() + 10;
        paperYPos += rect1.height() + 10;
        printDataList.add(new PrintData(dx1, paperYPos, text1, paint1));
        printDataList.add(new PrintData(dx2, paperYPos, text2, paint2));


    }


    public void addLineWithHeight(int height) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(40);
        Rect rect = new Rect();
        paint.getTextBounds(" ", 0, " ".length(), rect);
        float dx = 0;


        paper_height += rect.height() + height;
        paperYPos += rect.height() + height;
        printDataList.add(new PrintData(dx, paperYPos, " ", paint));

    }

    public void addLineSeparator() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(40);
        Rect rect = new Rect();
        if (paperWidth == 576) {
            paint.getTextBounds("   - - - - - - - - - - - - - - - - - - - - - - - -  ", 0, "   - - - - - - - - - - - - - - - - - - - - - - - -  ".length(), rect);

            paper_height += rect.height() + 10;
            paperYPos += rect.height() + 10;
            printDataList.add(new PrintData(0, paperYPos, "   - - - - - - - - - - - - - - - - - - - - - - - -  ", paint));

        } else {
            paint.getTextBounds("    - - - - - - - - - - - - - - - -   ", 0, "    - - - - - - - - - - - - - - - -   ".length(), rect);

            paper_height += rect.height() + 10;
            paperYPos += rect.height() + 10;
            printDataList.add(new PrintData(0, paperYPos, "    - - - - - - - - - - - - - - - -   ", paint));

        }

    }



    public void clear() {
        paper_height = 0;
        paperYPos = 0;
        printDataList = new ArrayList<>();
        finish();

    }


    public void printImageBitmap(Bitmap bitmap) {
        new Handler(Looper.getMainLooper())
                .post(() -> Toast.makeText(context, "Prepare to print", Toast.LENGTH_SHORT).show());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        double h = bitmap.getWidth() / 576.0;
        double dstH = bitmap.getHeight() / h;


        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 576, (int) dstH, true);


        int width = newBitmap.getWidth();
        int height = newBitmap.getHeight();
        int newWidth = (width / 8 + 1) * 8;
        float scaleWidth = ((float) newWidth) / width;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, 1);
        Bitmap b = Bitmap.createBitmap(newBitmap, 0, 0, width, height, matrix, true);


        PrintPic printPic = PrintPic.getInstance();
        printPic.init(b);
        byte[] bitmapData = printPic.printDraw();
        try {
            btOutputStream.write(bitmapData);
            clear();
            cut();
        } catch (IOException e) {
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
            this.paint = new Paint();// ??????????????????

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
         * ????????????????????????
         *
         * @return ??????
         */
        public byte[] printDraw() {
            Bitmap nbm = Bitmap
                    .createBitmap(this.bm, 0, 0, this.width, getLength());

            byte[] imgbuf = new byte[this.width / 8 * getLength() + 8];

            int s = 0;

            // ???????????????????????????
            imgbuf[0] = 29;// ????????????0x1D
            imgbuf[1] = 118;// ????????????0x76
            imgbuf[2] = 48;// 30
            imgbuf[3] = 0;// ???????????? 0,1,2,3
            // ????????????????????????????????????xL+xH ?? 256???
            imgbuf[4] = (byte) (this.width / 8);
            imgbuf[5] = 0;
            // ????????????????????????????????? yL+ yH ?? 256???
            imgbuf[6] = (byte) (getLength() % 256);//
            imgbuf[7] = (byte) (getLength() / 256);

            s = 7;
            for (int i = 0; i < getLength(); i++) {// ?????????????????????
                for (int k = 0; k < this.width / 8; k++) {// ?????????????????????
                    int c0 = nbm.getPixel(k * 8 + 0, i);// ???????????????????????????
                    int p0;
                    if (c0 == -1)// ???????????????????????????
                        p0 = 0;// 0,???????????????
                    else {
                        p0 = 1;// 1,????????????
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
