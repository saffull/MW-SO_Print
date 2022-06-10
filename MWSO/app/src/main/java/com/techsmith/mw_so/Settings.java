package com.techsmith.mw_so;

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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Settings extends AppCompatActivity {
    TextView tvDeviceId, tvVersionName;
    EditText etUrlValue, printer;
    String myuniqueID, URL, printerName,printer_name;
    SharedPreferences prefs;
    List<String> printList;
    private final Locale locale = new Locale("id", "ID");
    private final DateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", locale);
    private final NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
    private BluetoothConnection selectedDevice;
    Boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getSupportActionBar().hide();
        setContentView(R.layout.activity_settings);
        tvDeviceId = findViewById(R.id.tvDeviceIdValue);
        etUrlValue = findViewById(R.id.etUrlValue);
        tvVersionName = findViewById(R.id.tvAppVersionValue);
        printer = findViewById(R.id.printer);
        prefs = PreferenceManager.getDefaultSharedPreferences(Settings.this);

        try {
            String android_Id = android.provider.Settings.Secure.getString(this.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            myuniqueID = android_Id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        printerName = prefs.getString("printlist", "");
        printer_name=prefs.getString("printer_name","");
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
            editor.putBoolean("firstTime", firstTime);
            editor.apply();
            finish();
            startActivity(new Intent(Settings.this, MainActivity.class));
        } else {
            Toast.makeText(Settings.this, "Select a Printer", Toast.LENGTH_LONG).show();
        }
    }

    public void browseBluetoothDevice(View view) {
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
                        Button button = (Button) findViewById(R.id.button_bluetooth_browse);
                        button.setText(items[i]);
                        printer.setText(items[i]);
                    }
                });

                AlertDialog alert = alertDialog.create();
                alert.setCanceledOnTouchOutside(false);
                alert.show();

            }
        }else{
            Toast.makeText(Settings.this,"Enable Bluetooth First",Toast.LENGTH_LONG).show();
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
                                "[L]" + temp + "\n";

                        printer.printFormattedText(text);
                    } else {
                        Toast.makeText(this, "No printer was connected!", Toast.LENGTH_SHORT).show();
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
}