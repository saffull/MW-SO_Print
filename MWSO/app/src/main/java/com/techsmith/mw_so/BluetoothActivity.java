package com.techsmith.mw_so;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anggastudio.printama.Printama;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.techsmith.mw_so.utils.PrintResponse;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BluetoothActivity extends AppCompatActivity {
    ProgressDialog pDialog;
    String strCheckLogin, strErrorMsg, printData = "", printStoreId = "",
            printSbNumber = "", s1 = "", s2 = "", printSBNumber, printStoreID,
            qrString = "No data", qrData = "", irnNo = "", doc_no = "";
    PrintResponse printResponse;
    TextView MessageDisplay;
    SharedPreferences prefs;
    BluetoothConnection connection;
    List<String> printList, sbList, idList;
    String[] sbNumber, storeId;
    ArrayList<String> list1, list2;
    ImageView imageView, qrDisplay;
    int sizeCount = 0, reCount = 0;
    Bitmap bitmap1, bitmaap = null;
    private final Locale locale = new Locale("id", "ID");
    private final DateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", locale);
    private final NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
    private BluetoothConnection selectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        prefs = PreferenceManager.getDefaultSharedPreferences(BluetoothActivity.this);
        MessageDisplay = findViewById(R.id.MessageDisplay);
        qrDisplay = findViewById(R.id.qrDisplay);
        printList = new ArrayList<>();
        sbList = new ArrayList<>();
        idList = new ArrayList<>();

        sizeCount = prefs.getInt("sizeCount", 0);
        printStoreID = prefs.getString("printStoreID", "");
        printSBNumber = prefs.getString("printSBNumber", "");


        printStoreId = prefs.getString("printStoreId", "").replace("[", "").replace("]", "");
        printSbNumber = prefs.getString("printSbNumber", "").replace("[", "").replace("]", "");

        System.out.println("Incoming data is " + printStoreId + "<----------------------------->" + printSbNumber);

        try {
            sbNumber = printSbNumber.split(",");
            storeId = printStoreId.split(",");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sizeCount == 1) {
            try {
                s1 = printStoreID;
                s2 = printSBNumber;
                new TakeBillSingle().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (sizeCount > 1) {
            if (!printSbNumber.isEmpty() && !printStoreId.isEmpty()) {
                try {
                    new TakeBill().execute();
                    findViewById(R.id.printtsmith).setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(BluetoothActivity.this, "SbNumber or StoreID is Empty", Toast.LENGTH_LONG).show();
            }

        }

        new TakeBillSingle().execute();
    }

    private void CheckCount() {
        reCount++;
        if (reCount != sizeCount) {
            try {
                new TakeBill().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            findViewById(R.id.printtsmith).setEnabled(true);
        }
    }


    public boolean isBluetoothEnabled() {
        BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return myBluetoothAdapter.isEnabled();
    }

    private class TakeBill extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BluetoothActivity.this);
            pDialog.setMessage("Loading..Please wait....!!");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            strCheckLogin = "";

            //  System.out.println("Url used is " + Url);//https://tsmithy.in/somemouat/api/
            try {
                //URL url = new URL("https://tsmithy.in/somemouat/api/LoginVer2?Name=salam_ka@yahoo.com&secret=1047109119116122626466");
                //URL url = new URL("https://tsmithy.in/somemouat/api/PrintBill?StoreId=15&SBillNo=APPL/22/WS-13");
                URL url = new URL("https://tsmithy.in/somemouat/api/PrintBill?StoreId=" + storeId[reCount].trim() + "&SBillNo=" + sbNumber[reCount].trim());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setRequestProperty("authkey", "MGG427A3-F9F6-N7DA-T698-SOF60CE0MEMO");
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                //connection.setRequestProperty("machineid", "saffull@gmail.com");
                connection.setRequestProperty("machineid", "salam_ka@yahoo.com");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.connect();

                int responsecode = connection.getResponseCode();
                String responseMsg = connection.getResponseMessage();
                System.out.println("Response message is " + responsecode);


                try {
                    if (responsecode == 200) {
                        InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(streamReader);
                        StringBuilder sb = new StringBuilder();
                        String inputLine = "";

                        while ((inputLine = reader.readLine()) != null) {
                            sb.append(inputLine);
                            break;
                        }

                        reader.close();
                        String str = "";
                        strCheckLogin = sb.toString();
                        System.out.println("Response of Print Bill--->" + strCheckLogin);
                    } else {
//                        strErrorMsg = connection.getResponseMessage();
                        strErrorMsg = responseMsg;
                        strCheckLogin = "httperror";
                    }

//                    {"Id":9,"Username":"500_9","Name":"500 User 9","Password":"","Email":"pavithrapurushan06@gmail.com","Phone":"","Active":0,"StoreId":50,"StoreCode":"500","StoreName":"SEPL-PHARMA WAREHOUSE","DeviceId":"","PasswordOtp":"","ErrorStatus":0,"Message":"Success"}

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return strCheckLogin;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            printData = s;
            Gson gson = new Gson();
            printResponse = gson.fromJson(printData, PrintResponse.class);

            if (strCheckLogin.equals("") || strCheckLogin == null) {
                Toast.makeText(BluetoothActivity.this, "No result in Multiple Bills", Toast.LENGTH_SHORT).show();
            } else if (printResponse.statusFlag == 1) {
                tsMessages(printResponse.errorMessage);

            } else {
                try {

                    String test = printResponse.data.replace("\r", "\n");
                    System.out.println("Print data is " + test);
                    printList.add(test);
                    System.out.println("Print List is " + printList);
                    MessageDisplay.setText(printList.toString().replace(",", "\n").
                            replace("[", "").replace("]", ""));
                    MessageDisplay.setMovementMethod(new ScrollingMovementMethod());
                    // tsMessages(test);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CheckCount();
                        }
                    }, 2000);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void tsMessages(String msg) {

        try {
            final Dialog dialog = new Dialog(BluetoothActivity.this);
            dialog.setContentView(R.layout.ts_message_dialouge);
//            dialog.setCanceledOnTouchOutside(false);
            dialog.setCanceledOnTouchOutside(true);
//            dialog.setTitle("Save");
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            ImageButton imgBtnCloseSaveWindow = dialog.findViewById(R.id.imgBtnClosetsMsgWindow);
            TextView tvMsgTodisplay = dialog.findViewById(R.id.tvTsMessageDisplay);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);

            imgBtnCloseSaveWindow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            tvMsgTodisplay.setText(msg);
            dialog.show();
        } catch (Exception ex) {
            Toast.makeText(this, "" + ex, Toast.LENGTH_SHORT).show();
        }

    }


    private void qrdo() {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
            bitMatrix = writer.encode(qrString, BarcodeFormat.QR_CODE, 150, 150);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            bitmaap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int color = Color.WHITE;
                    if (bitMatrix.get(x, y)) color = Color.BLACK;
                    bitmaap.setPixel(x, y, color);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        qrDisplay.setImageBitmap(bitmaap);
    }


    private void printOurReceipt() {
        String address = "http://www.tsmith.co.in";
        bitmap1 = Printama.getBitmapFromVector(this, R.drawable.techsmith);
        Printama.with(this).connect(printama -> {
            printama.printTextln("Techsmith Software Pvt Limited", Printama.CENTER);
            printama.setNormalText();
            printama.printDashedLine();
            printama.addNewLine();

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix;
            try {
                bitMatrix = writer.encode(qrString, BarcodeFormat.QR_CODE, 100, 100);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bitmaap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int color = Color.WHITE;
                        if (bitMatrix.get(x, y)) color = Color.BLACK;
                        bitmaap.setPixel(x, y, color);
                    }
                }
                if (bitmaap != null) {
                    printama.printImage(bitmap1, 600, Printama.CENTER);
                    printama.printImage(bitmaap);
                }
            } catch (WriterException e) {
                e.printStackTrace();
            }
            printama.addNewLine();
            printama.feedPaper();
            printama.close();


        }, this::showToast);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String printerName = Printama.getPrinterResult(resultCode, requestCode, data);
        // showResult(printerName);
    }

    private void showToast(String message) {
        System.out.println("Message is " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private class TakeBillSingle extends AsyncTask<String, String, String> {
        ProgressDialog Pdialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Pdialog = new ProgressDialog(BluetoothActivity.this);
            Pdialog.setMessage("Loading Single Bill..Please wait.!!");
            Pdialog.setCancelable(true);
            Pdialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            strCheckLogin = "";
            System.out.println("S1 and s2 is " + s1 + s2);
            //  System.out.println("Url used is " + Url);//https://tsmithy.in/somemouat/api/
            try {
                //URL url = new URL("https:`//tsmithy.in/somemouat/api/LoginVer2?Name=salam_ka@yahoo.com&secret=1047109119116122626466");
                //URL url = new URL("https://tsmithy.in/somemouat/api/PrintBill?StoreId=15&SBillNo=APPL/22/WS-45");
                URL url = new URL("https://tsmithy.in/somemouat/api/PrintBill?StoreId=" + s1 + "&SBillNo=" + s2);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setRequestProperty("authkey", "MGG427A3-F9F6-N7DA-T698-SOF60CE0MEMO");
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                //connection.setRequestProperty("machineid", "saffull@gmail.com");
                connection.setRequestProperty("machineid", "salam_ka@yahoo.com");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.connect();

                int responsecode = connection.getResponseCode();
                String responseMsg = connection.getResponseMessage();
                System.out.println("Response message is " + responsecode);


                try {

                    if (responsecode == 200) {
                        InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(streamReader);
                        StringBuilder sb = new StringBuilder();
                        String inputLine = "";

                        while ((inputLine = reader.readLine()) != null) {
                            sb.append(inputLine);
                            break;
                        }

                        reader.close();
                        String str = "";
                        strCheckLogin = sb.toString();
                        System.out.println("Response of Print Bill--->" + strCheckLogin);
                    } else {
//                        strErrorMsg = connection.getResponseMessage();
                        strErrorMsg = responseMsg;
                        strCheckLogin = "httperror";
                    }

//                    {"Id":9,"Username":"500_9","Name":"500 User 9","Password":"","Email":"pavithrapurushan06@gmail.com","Phone":"","Active":0,"StoreId":50,"StoreCode":"500","StoreName":"SEPL-PHARMA WAREHOUSE","DeviceId":"","PasswordOtp":"","ErrorStatus":0,"Message":"Success"}

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return strCheckLogin;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (Pdialog.isShowing()) {
                Pdialog.dismiss();
            }
            Gson gson = new Gson();
            printResponse = gson.fromJson(s, PrintResponse.class);
            try {
                if (s.isEmpty()) {
                    Toast.makeText(BluetoothActivity.this, "No result For Single bIll", Toast.LENGTH_SHORT).show();
                } else if (printResponse.statusFlag == 1) {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                    tsMessages(printResponse.errorMessage);
                } else {
                    try {
                        if (Pdialog.isShowing()) {
                            Pdialog.dismiss();
                        }

                        printList.clear();
                        String test = printResponse.data.replace("\r", "\n");
                        printList.add(test);
                        MessageDisplay.setText(printList.toString().replace(",", "\n").
                                replace("[", "").replace("]", ""));
                        MessageDisplay.setMovementMethod(new ScrollingMovementMethod());
                        // tsMessages(test);

                        // if (!doc_no.isEmpty()){
                        doc_no = printResponse.einvString.DocNo;
                        JSONObject obj = new JSONObject();

                        obj.put("BuyerGstin", printResponse.einvString.BuyerGstin);
                        obj.put("SellerGstin", printResponse.einvString.SellerGstin);
                        obj.put("DocDt", printResponse.einvString.DocDt);
                        obj.put("DocNo", doc_no);
                        obj.put("DocTyp", printResponse.einvString.DocTyp);
                        obj.put("Irn", printResponse.einvString.Irn);
                        obj.put("ItemCnt", printResponse.einvString.ItemCnt);
                        obj.put("TotInvVal", String.valueOf(printResponse.einvString.TotInvVal));
                        obj.put("MainHsnCode", String.valueOf(printResponse.einvString.MainHsnCode));

                        irnNo = String.valueOf(printResponse.einvString.Irn);


                        qrString = obj.toString();
                        System.out.println(qrString);
                        qrdo();
              /*  }else{
                    if (Pdialog.isShowing()) {
                        Pdialog.dismiss();
                    }
                    System.out.println("IRN String empty");
                }*/


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }


    public void doPrint(View view) {
        if (isBluetoothEnabled()) {
            try {
                String temp = MessageDisplay.getText().toString();
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 1);
                } else {
                    /*  "[L]" + df.format(new Date()) + "\n","[C]--------------------------------\n" + */
                    connection = BluetoothPrintersConnections.selectFirstPaired();
                    if (connection != null) {
                        EscPosPrinter printer = new EscPosPrinter(connection, 210, 48f, 32);
                        if (irnNo.isEmpty() || irnNo == null) {

                            final String text =
                                    "[L]" + temp + "\n" +
                                            "[C]--------------------------------\n";
                            printer.printFormattedText(text);
                        } else {
                            final String text =
                                    "[L]" + temp + "\n" +
                                            "[C]--------------------------------\n" +
                                            "[C]<qrcode size='20'>" + qrString + "</qrcode>\n" +
                                            "[L]<b>\tIRN Number</b>" +
                                            "[L]<b>\n" + irnNo + "</b>";
                            printer.printFormattedText(text);
                        }
                        connection.disconnect();

                       /* final String text =
                                "[L]" + temp + "\n" +
                                        "[L]<qrcode size='20'>\t" + qrString + "</qrcode>\n\n" +
                                        "[L]<b>\tIRN Number</b>" +
                                        "[L]<b>\n" + irnNo + "</b>";

                        printer.printFormattedText(text);*/
                    } else {
                        Toast.makeText(this, "No printer was connected!", Toast.LENGTH_SHORT).show();
                    }
                }

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("printer_name", connection.getDevice().getName());
                editor.apply();
            } catch (Exception e) {
                Log.e("APP", "Can't print", e);
            }
        } else {
            Toast.makeText(BluetoothActivity.this, "Enable Bluetooth & Try Again...", Toast.LENGTH_LONG).show();
        }

    }

    public void browseBluetoothDevice(View view) {
        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();

        if (bluetoothDevicesList != null) {
            final String[] items = new String[bluetoothDevicesList.length + 1];
            items[0] = "Default printer";
            int i = 0;
            for (BluetoothConnection device : bluetoothDevicesList) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                items[++i] = device.getDevice().getName();
            }

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(BluetoothActivity.this);
            alertDialog.setTitle("Bluetooth printer selection");
            alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int index = i - 1;
                    if (index == -1) {
                        selectedDevice = null;
                    } else {
                        selectedDevice = bluetoothDevicesList[index];
                    }
                  //  Button button = findViewById(R.id.button_bluetooth_browse);
                   // button.setText(items[i]);
                }
            });

            AlertDialog alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();

        }
    }
}



/*
 private void showPrinterListActivity() {
        // only use this when your project is not androidX
        Printama.showPrinterList(this);
    }
    private void PrintPic(Bitmap resource) {
        if (resource != null) {
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Printama.with(this).connect(printama -> {
                printama.addNewLine(1);
                //printama.printImage(resource, 500, Printama.LEFT);
                printama.printImage(resource, Printama.ORIGINAL_WIDTH);
                //printama.printImage(resource,Printama.FULL_WIDTH);
                printama.addNewLine(3);
                printama.printImage(bitmap, Printama.ORIGINAL_WIDTH);
                printama.close();
            }, this::showToast);
        } else {
            System.out.println("Resource EMPTY");
        }
    }
     private class TakePic extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BluetoothActivity.this);
            pDialog.setMessage("Fetching Image...Please wait.!!");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            strCheckLogin = "";

            //  System.out.println("Url used is " + Url);//https://tsmithy.in/somemouat/api/
            try {
                URL url = new URL("https://tsmithy.in/dummy/api/GetImage?name=5");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setRequestProperty("authkey", "D39C1EB0-DC32-4CF2-817B-EE748A8E4A30");
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                //connection.setRequestProperty("machineid", "saffull@gmail.com");
                connection.setRequestProperty("machineid", "salam_ka@yahoo.com");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.connect();

                int responsecode = connection.getResponseCode();
                String responseMsg = connection.getResponseMessage();
                System.out.println("Response message is " + responsecode);

                try {

                    if (responsecode == 200) {
                        InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(streamReader);
                        StringBuilder sb = new StringBuilder();
                        String inputLine = "";

                        while ((inputLine = reader.readLine()) != null) {
                            sb.append(inputLine);
                            break;
                        }

                        reader.close();
                        String str = "";
                        strCheckLogin = sb.toString();
                        System.out.println("Response of Image URL is --->" + strCheckLogin);
                    } else {
//                        strErrorMsg = connection.getResponseMessage();
                        strErrorMsg = responseMsg;
                        strCheckLogin = "httperror";
                    }

//                    {"Id":9,"Username":"500_9","Name":"500 User 9","Password":"","Email":"pavithrapurushan06@gmail.com","Phone":"","Active":0,"StoreId":50,"StoreCode":"500","StoreName":"SEPL-PHARMA WAREHOUSE","DeviceId":"","PasswordOtp":"","ErrorStatus":0,"Message":"Success"}

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strCheckLogin;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            printData = s;


            if (strCheckLogin.equals("") || strCheckLogin == null) {
                Toast.makeText(BluetoothActivity.this, "No result", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Gson gson = new Gson();
                    printResponse = gson.fromJson(printData, PrintResponse.class);
                    String test = printResponse.info;
                    System.out.println("Url data is " + test);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


   public void SampReceipt(View view) {
        String temp = MessageDisplay.getText().toString().trim();
        if (irnNo.isEmpty()) {
            Toast toast = Toast.makeText(BluetoothActivity.this,
                    "IRN Number is Empty...", Toast.LENGTH_SHORT);
            toast.show();

        } else if (temp.isEmpty()) {
            Toast toast = Toast.makeText(BluetoothActivity.this,
                    "Bill data is Empty", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            String nota = "Some Text";
            Printama.with(this).connect(printama -> {
                // printama.addNewLine();
                //printama.printTextln(temp, Printama.LEFT);
                // printama.setNormalText();
                // printama.printTextln("Some Text", Printama.CENTER);
                // printama.printDashedLine();
                // printama.addNewLine();
                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix bitMatrix;
                try {
                    bitMatrix = writer.encode(qrString, BarcodeFormat.QR_CODE, 150, 150);
                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            int color = Color.WHITE;
                            if (bitMatrix.get(x, y)) color = Color.BLACK;
                            bitmap.setPixel(x, y, color);
                        }
                    }
                    if (bitmap != null) {
                        printama.printImage(bitmap);
                    }
                    //printama.printDashedLine();
                    printama.setSmallText();
                    //printama.printTextln(temp, Printama.LEFT);
                    printama.printTextln("IRN Number", Printama.CENTER);
                    printama.setNormalText();
                    printama.printTextln(irnNo, Printama.CENTER);
                    printama.printDashedLine();
                    printama.addNewLine();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                printama.addNewLine();
                printama.feedPaper();
                printama.close();
            }, this::showToast);

        }

    } */
