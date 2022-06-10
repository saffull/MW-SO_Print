package com.techsmith.mw_so;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.techsmith.mw_so.utils.AppConfigSettings;
import com.techsmith.mw_so.utils.UserPL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    TextInputEditText etUsername, etPassword;
    SharedPreferences prefs;
    ProgressDialog pDialog;
    Double tsMsgDialogWindowHeight;
    Boolean firstTime;
    String username, password, Url, multiSOStoredDevId, strCheckLogin, strErrorMsg;
    String[] permissions = {Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // getSupportActionBar().hide();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screen_height = displayMetrics.heightPixels;
        int screen_width = displayMetrics.widthPixels;
        tsMsgDialogWindowHeight = Double.valueOf((screen_height * 38) / 100);

        Dexter.withContext(this)
                .withPermissions(
                        permissions
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                System.out.println("Permission Checked" + report.toString());
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }

        }).check();
        prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        firstTime = prefs.getBoolean("firstTime", true);
        if (firstTime)
            startActivity(new Intent(MainActivity.this, Settings.class));

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        String string = "5, 15", string1 = "5002/22/WS-16, APPL/22/WS-27";
        String[] parts = string.split(",");
        String[] parts1 = string1.split(",");
        //  System.out.println(parts.length+"--<>"+parts[0]+"\n"+parts[1].trim());
        for (int i = 0; i < parts.length; i++) {
            System.out.println(parts[i].trim() + "\n" + parts1[i].trim());

        }

        Url = prefs.getString("MultiSOURL", "");
        multiSOStoredDevId = prefs.getString("MultiSOStoredDevId", "");
        System.out.println("Machine id is " + multiSOStoredDevId);
        etUsername.setText(prefs.getString("username", ""));
        etPassword.setText(prefs.getString("password", ""));
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }

    public void Login(View view) {

        try {

            username = etUsername.getText().toString();
            password = etPassword.getText().toString();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.commit();
            editor.apply();

            new CheckLoginTask().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void SaveUrl(View view) {
        startActivity(new Intent(MainActivity.this, Settings.class));
    }

    private class CheckLoginTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Logging in..Please wait.!!");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            strCheckLogin = "";

            System.out.println("Url used is " + Url);//https://tsmithy.in/somemouat/api/
            try {
                //URL url = new URL("https://tsmithy.in/somemouat/api/LoginVer2?Name=salam_ka@yahoo.com&secret=1047109119116122626466");
                URL url = new URL("https://tsmithy.in/somemouat/api/LoginVer2?Name=" + username + "&secret=1047109119116122626466");
                System.out.println(Url + "LoginVer2?Name=" + username + "&pwd=" + password);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setRequestProperty("authkey", AppConfigSettings.auth_id);
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                //connection.setRequestProperty("machineid", "saffull@gmail.com");
                //connection.setRequestProperty("machineid","salam_ka@yahoo.com");
                connection.setRequestProperty("machineid", username.trim());
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
                        System.out.println("Response of Login--->" + strCheckLogin);
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

            if (strCheckLogin.equals("") || strCheckLogin == null) {
                Toast.makeText(MainActivity.this, "No result", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Gson gson = new Gson();
                    UserPL userPLObj = gson.fromJson(strCheckLogin, UserPL.class);
                    if (userPLObj.statusFlag == 0) {
                        prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("loginResponse", strCheckLogin);
                        editor.putString("BillRemarksMWSO", "");
                        editor.putString("user_name",username.trim());
                        editor.apply();
                        startActivity(new Intent(MainActivity.this, Category.class));

                    } else {
                        tsMessages("" + userPLObj.errorMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void tsMessages(String msg) {

        try {
            final Dialog dialog = new Dialog(MainActivity.this);
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

}