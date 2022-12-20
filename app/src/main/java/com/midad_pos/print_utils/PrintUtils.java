package com.midad_pos.print_utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.midad_pos.R;

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
    private int paperWidth = 550;
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

    public void connectPrinter(final PrinterConnectListener listener,BluetoothDevice printer,int paperWidth,Context context,String lang) {
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
                    listener.onConnected();
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
        void onConnected();

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
        return text.replace('á', 'a')
                .replace('č', 'c')
                .replace('ď', 'd')
                .replace('é', 'e')
                .replace('ě', 'e')
                .replace('í', 'i')
                .replace('ň', 'n')
                .replace('ó', 'o')
                .replace('ř', 'r')
                .replace('š', 's')
                .replace('ť', 't')
                .replace('ú', 'u')
                .replace('ů', 'u')
                .replace('ý', 'y')
                .replace('ž', 'z')
                .replace('Á', 'A')
                .replace('Č', 'C')
                .replace('Ď', 'D')
                .replace('É', 'E')
                .replace('Ě', 'E')
                .replace('Í', 'I')
                .replace('Ň', 'N')
                .replace('Ó', 'O')
                .replace('Ř', 'R')
                .replace('Š', 'S')
                .replace('Ť', 'T')
                .replace('Ú', 'U')
                .replace('Ů', 'U')
                .replace('Ý', 'Y')
                .replace('Ž', 'Z');
    }

    public boolean printImage(Bitmap bitmap) {
        byte[] command = decodeBitmap(bitmap);
        return printUnicode(command);
    }




    public boolean printMultiLangText(String stringData, Paint.Align align, float textSize)  {
        return printMultiLangText(stringData, align, textSize, null);
    }

    public boolean printMultiLangText(String stringData, Paint.Align align, float textSize, Typeface typeface)  {
        return printImage(getMultiLangTextAsImage(stringData, align, textSize, typeface));
    }


    public boolean printBarCode(String message, BarcodeFormat format, int width, int height) throws WriterException {
        return printImage(createBarCode( message,  format,  width,  height));
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


    public Bitmap getMultiLangTextAsImage(String text, Paint.Align align, float textSize, Typeface typeface)  {


        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize);
        if (typeface != null) paint.setTypeface(typeface);

        // A real printlabel width (pixel)
        float xWidth = 385;

        // A height per text line (pixel)
        float xHeight = textSize + 5;

        // it can be changed if the align's value is CENTER or RIGHT
        float xPos = 0f;

        // If the original string data's length is over the width of print label,
        // or '\n' character included,
        // it will be increased per line gerneating.
        float yPos = 27f;

        // If the original string data's length is over the width of print label,
        // or '\n' character included,
        // each lines splitted from the original string are added in this list
        // 'PrintData' class has 3 members, x, y, and splitted string data.
        List<PrintData> printDataList = new ArrayList<PrintData>();

        // if '\n' character included in the original string
        String[] tmpSplitList = text.split("\\n");
        for (int i = 0; i <= tmpSplitList.length - 1; i++) {
            String tmpString = tmpSplitList[i];

            // calculate a width in each split string item.
            float widthOfString = paint.measureText(tmpString);

            // If the each split string item's length is over the width of print label,
            if (widthOfString > xWidth) {
                String lastString = tmpString;
                while (!lastString.isEmpty()) {

                    String tmpSubString = "";

                    // retrieve repeatedly until each split string item's length is
                    // under the width of print label
                    while (widthOfString > xWidth) {
                        if (tmpSubString.isEmpty())
                            tmpSubString = lastString.substring(0, lastString.length() - 1);
                        else
                            tmpSubString = tmpSubString.substring(0, tmpSubString.length() - 1);

                        widthOfString = paint.measureText(tmpSubString);
                    }

                    // this each split string item is finally done.
                    if (tmpSubString.isEmpty()) {
                        // this last string to print is need to adjust align
                        if (align == Paint.Align.CENTER) {
                            if (widthOfString < xWidth) {
                                xPos = ((xWidth - widthOfString) / 2);
                            }
                        } else if (align == Paint.Align.RIGHT) {
                            if (widthOfString < xWidth) {
                                xPos = xWidth - widthOfString;
                            }
                        }
                        //printDataList.add(new PrintData(xPos, yPos, lastString));
                        lastString = "";
                    } else {
                        // When this logic is reached out here, it means,
                        // it's not necessary to calculate the x position
                        // 'cause this string line's width is almost the same
                        // with the width of print label
                       // printDataList.add(new PrintData(0f, yPos, tmpSubString));

                        // It means line is needed to increase
                        yPos += 27;
                        xHeight += 30;

                        lastString = lastString.replaceFirst(tmpSubString, "");
                        widthOfString = paint.measureText(lastString);
                    }
                }
            } else {
                // This split string item's length is
                // under the width of print label already at first.
                if (align == Paint.Align.CENTER) {
                    if (widthOfString < xWidth) {
                        xPos = ((xWidth - widthOfString) / 2);
                    }
                } else if (align == Paint.Align.RIGHT) {
                    if (widthOfString < xWidth) {
                        xPos = xWidth - widthOfString;
                    }
                }
                //printDataList.add(new PrintData(xPos, yPos, tmpString));
            }

            if (i != tmpSplitList.length - 1) {
                // It means the line is needed to increase
                yPos += 27;
                xHeight += 30;
            }
        }

        // If you want to print the text bold
        //paint.setTypeface(Typeface.create(null as String?, Typeface.BOLD))

        // create bitmap by calculated width and height as upper.
        Bitmap bm = Bitmap.createBitmap((int) xWidth, (int) xHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        canvas.drawColor(Color.WHITE);

        for (PrintData tmpItem : printDataList)
            canvas.drawText(tmpItem.text, tmpItem.xPos, tmpItem.yPos, paint);


        return bm;
    }

    static class PrintData {
        private float xPos;
        private float yPos;
        private String text;
        private Paint paint;


        public PrintData(float xPos, float yPos, String text,Paint paint) {
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


    public void addTestItem(float textSize,boolean isBold,String text,int align){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize);
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);

        float dx =0;
        if (align==ALIGN_CENTER){
            dx = (float) ((paperWidth-rect.width())/2.0);

        }else if (align==ALIGN_LEFT){
            dx = 0;
        }else {
            dx = (paperWidth-rect.width());
            Log.e("wwww",paperWidth+"__"+ rect.width()+"___"+dx);
        }
        if (isBold){
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        }




        paper_height +=rect.height()+10;
        paperYPos +=rect.height()+10;
        printDataList.add(new PrintData(dx, paperYPos,text,paint));


    }

    public void addLineWithHeight(int height){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        Rect rect = new Rect();
        paint.getTextBounds(" ", 0, " ".length(), rect);
        float dx = 0;




        paper_height +=rect.height()+height;
        paperYPos +=rect.height()+height;
        printDataList.add(new PrintData(dx, paperYPos," ",paint));

    }

    public void addLineSeparator(){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        Rect rect = new Rect();
        if (paperWidth==576){
            paint.getTextBounds("   ----------------------------------------------   ", 0,"   ----------------------------------------------   ".length(), rect);

            paper_height +=rect.height()+10;
            paperYPos +=rect.height()+10;
            printDataList.add(new PrintData(0, paperYPos,"   ----------------------------------------------   ",paint));

        }else {
            paint.getTextBounds("    ------------------------------    ", 0,"    ------------------------------    ".length(), rect);

            paper_height +=rect.height()+10;
            paperYPos +=rect.height()+10;
            printDataList.add(new PrintData(0, paperYPos,"    ------------------------------    ",paint));

        }

    }

    public Bitmap printTestData(){
        Bitmap b = Bitmap.createBitmap(paperWidth, paper_height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg.setColor(ContextCompat.getColor(context,R.color.grey3));
        canvas.drawPaint(bg);

        for (PrintData printData:printDataList){
            canvas.drawText(printData.getText(), printData.getxPos(), printData.getyPos(), printData.getPaint());

        }

         //printImage(b);

        return b;
    }


    public void clear(){
        paper_height =0;
        paperYPos = 0;
        printDataList.clear();

    }

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



           */



    public interface PrintResponse {
        void onDevices(List<BluetoothDevice> list);

        void onStartIntent();
    }

}
