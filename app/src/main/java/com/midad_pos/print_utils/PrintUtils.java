package com.midad_pos.print_utils;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class PrintUtils {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Thread thread;

    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;


    public void findBluetoothDevice(AppCompatActivity context) {

        try {

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                return;
            }

            if (bluetoothAdapter.isEnabled()) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
            } else {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivityForResult(enableBT, 0);


            }

            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            if (pairedDevice.size() > 0) {
                for (BluetoothDevice pairedDev : pairedDevice) {
                    Log.e("dev", pairedDev.getName());

                }
            } else {
                Log.e("dev", "no devices");

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void connectBluetoothPrinter(BluetoothDevice bluetoothDevice, AppCompatActivity context) throws IOException {
        try {

            //Standard uuid from string //
            UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
            bluetoothSocket.connect();
            Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();

            beginListenData();

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

    public void printData() throws IOException {
        try {
            String msg = "";
            msg += "\n";
            outputStream.write(msg.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void printImage(Bitmap bitmap) {
        try {
            PrintPic printPic = PrintPic.getInstance();
            printPic.init(bitmap);
            byte[] bitmapData = printPic.printDraw();
            outputStream.write(bitmapData);
        }
        catch(Exception e){
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

}
