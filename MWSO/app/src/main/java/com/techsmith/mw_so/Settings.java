package com.techsmith.mw_so;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;

public class Settings extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    TextView tvDeviceId, tvVersionName;
    EditText etUrlValue;
    TextView printer;
    String myuniqueID, URL, printerName, printer_name;
    SharedPreferences prefs;
    List<String> printList;
    Button button;
    private final Locale locale = new Locale("id", "ID");
    private final DateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", locale);
    private final NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
    private BluetoothConnection selectedDevice;
    Boolean firstTime = true;
    public static final String ACTION_APPLICATION_DETAILS_SETTINGS = "android.settings.APPLICATION_DETAILS_SETTINGS";
    public static final String[] BLUETOOTH_PERMISSIONS_S = {Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};
    String permission = Manifest.permission.BLUETOOTH_CONNECT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getSupportActionBar().hide();
        setContentView(R.layout.activity_settings);
        tvDeviceId = findViewById(R.id.tvDeviceIdValue);
        etUrlValue = findViewById(R.id.etUrlValue);
        tvVersionName = findViewById(R.id.tvAppVersionValue);
        printer = findViewById(R.id.printer);
        button = findViewById(R.id.button_bluetooth_browse);
        prefs = PreferenceManager.getDefaultSharedPreferences(Settings.this);

        if (EasyPermissions.hasPermissions(this, permission)) {


        } else {
            System.out.println("permission not granted");
            EasyPermissions.requestPermissions(this, "Our App Requires a permission to access your storage", 123
                    , permission);
        }
        try {
            String android_Id = android.provider.Settings.Secure.getString(this.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            myuniqueID = android_Id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        printerName = prefs.getString("printlist", "");
        printer_name = prefs.getString("printer_name", "");
        printList = new ArrayList<>(Arrays.asList(printerName.split(",")));
        //printList.clear();
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionNumber = pinfo.versionCode;
            String versionName = pinfo.versionName;
            tvVersionName.setText("" + versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        tvDeviceId.setText(myuniqueID);
        printer.setText(printer_name);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void SaveUrl(View view) {
        String temp = printer.getText().toString();
        if (!temp.isEmpty()) {
            URL = etUrlValue.getText().toString().trim();
            printerName = printer.getText().toString().trim();
            if (!printList.contains(printerName))
                printList.add(printerName);
            else
                System.out.println("Already in list");
            SharedPreferences.Editor editor = prefs.edit();
            //editor.putString("MultiSOStoredDevId", myuniqueID);
            //editor.putString("MultiSOStoredDevId", "saffull@gmail.com");
            firstTime = false;
            editor.putString("MultiSOStoredDevId", "salam_ka@yahoo.com");
            editor.putString("MultiSOURL", URL);
            editor.putString("printer", printList.toString().trim());
            editor.putString("printer_name", temp);
            editor.putBoolean("firstTime", firstTime);
            editor.apply();
            finish();
            startActivity(new Intent(Settings.this, MainActivity.class));
        } else {
            Toast.makeText(Settings.this, "Select a Printer", Toast.LENGTH_LONG).show();
        }
    }

    public void browseBluetoothDevice(View view) {
        try {

            if (isBluetoothEnabled()) {
                final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();

                if (bluetoothDevicesList != null) {
                    final String[] items = new String[bluetoothDevicesList.length + 1];
                    items[0] = "Default printer";
                    int i = 0;
                    for (BluetoothConnection device : bluetoothDevicesList) {
                        items[++i] = device.getDevice().getName();
                    }

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Settings.this);
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

                            // button.setText(items[i]);
                            printer.setText(items[i]);
                        }
                    });

                    AlertDialog alert = alertDialog.create();
                    alert.setCanceledOnTouchOutside(false);
                    alert.show();

                }
            } else {
                Toast.makeText(Settings.this, "Enable Bluetooth First", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Settings.this, "Bluetooth Browse Exception", Toast.LENGTH_LONG).show();
        }

    }

    public void testPrint(View view) {
        if (isBluetoothEnabled()) {
            try {
                String temp = "Test";
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 1);
                } else {
                    /*  "[L]" + df.format(new Date()) + "\n","[C]--------------------------------\n" + */
                    BluetoothConnection connection = BluetoothPrintersConnections.selectFirstPaired();
                    if (connection != null) {
                        EscPosPrinter printer = new EscPosPrinter(connection, 210, 48f, 32);
                        final String text =
                                "[L]" + temp;

                        printer.printFormattedText(text);
                        connection.disconnect();
                    } else {
                        Toast.makeText(this, "No printer was connected!", Toast.LENGTH_SHORT).show();
                       /* BluetoothConnection connectionble = BluetoothPrintersConnections.selectFirstPaired();
                        EscPosPrinter printer = new EscPosPrinter(connectionble, 210, 48f, 32);
                        final String text =
                                "[L]" + temp;
                        printer.printFormattedText(text);*/
                    }
                }
            } catch (Exception e) {
                Log.e("APP", "Can't print", e);
            }
        } else {
            Toast.makeText(Settings.this, "Enable Bluetooth & Try Again...", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isBluetoothEnabled() {
        BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return myBluetoothAdapter.isEnabled();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    public void cancelPage(View view) {
        finish();
    }
}