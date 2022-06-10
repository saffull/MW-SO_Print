package com.techsmith.mw_so;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.techsmith.mw_so.receivable_utils.AdapterRe;
import com.techsmith.mw_so.receivable_utils.Receivable;
import com.techsmith.mw_so.utils.AppConfigSettings;
import com.techsmith.mw_so.utils.AutocompleteCustomArrayAdapter;
import com.techsmith.mw_so.utils.CustomerList;
import com.techsmith.mw_so.utils.CustomerReceivables;
import com.techsmith.mw_so.utils.UserPL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class CustomerInformation extends AppCompatActivity {
    SharedPreferences prefs;
    AutoCompleteTextView acvCustomerName;
    ImageButton imgBtnCustSearchbyName, imgBtnSearchbyHUID;
    EditText etCustomerId, etCustomerAdrs, etCustomerMobile, etCustomerGSTNo, etReceivables;
    Button btnCreateSO;
    String loginResponse, Url, strCustomer, strErrorMsg, strReceivables, strReceivableDetails, uniqueID,
            selectedCustomerName, multiSOStoredDevId, user_name;
    int customerId, selectedCustomerId;
    ProgressDialog pDialog;
    CustomerList customerList;
    Double tsMsgDialogWindowHeight;
    CustomerReceivables customerReceivables;
    UserPL userPLObj;
    Receivable receivable;
    List<String> docId, billAmount, balancelist;
    RecyclerView recyclerView;
    Dialog dialog;
    AdapterRe adapterRe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_information);
//        getSupportActionBar().hide();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screen_height = displayMetrics.heightPixels;
        int screen_width = displayMetrics.widthPixels;
        tsMsgDialogWindowHeight = Double.valueOf((screen_height * 38) / 100);

        prefs = PreferenceManager.getDefaultSharedPreferences(CustomerInformation.this);
        acvCustomerName = findViewById(R.id.acvCustomerName);
        imgBtnCustSearchbyName = findViewById(R.id.imgBtnCustSearchbyName);
        imgBtnSearchbyHUID = findViewById(R.id.imgBtnSearchbyHUID);
        etCustomerId = findViewById(R.id.etCustomerId);
        etCustomerAdrs = findViewById(R.id.etCustomerAdrs);
        etCustomerMobile = findViewById(R.id.etCustomerMobile);
        etCustomerGSTNo = findViewById(R.id.etGSTIN);
        btnCreateSO = findViewById(R.id.btnCreateSO);
        etReceivables = findViewById(R.id.etReceivables);
        loginResponse = prefs.getString("loginResponse", "");
        Url = prefs.getString("MultiSOURL", "");
        uniqueID = UUID.randomUUID().toString();
        user_name = prefs.getString("user_name", "");
        System.out.println("GUID is " + user_name);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("guid", uniqueID);
        editor.apply();
        // multiSOStoredDevId = prefs.getString(""salam_ka@yahoo.com"", "");
       /* if (acvCustomerName.getText().toString().isEmpty()){
            btnCreateSO.setEnabled(false);
            btnCreateSO.setAlpha((float) 0.6);
        }
        else{
            btnCreateSO.setEnabled(true);
        }*/

        try {
            Gson gson = new Gson();
            userPLObj = gson.fromJson(loginResponse, UserPL.class);
            acvCustomerName.setText(userPLObj.summary.customerName);
            etCustomerAdrs.setText(userPLObj.summary.customerAddress);
            etCustomerGSTNo.setText(userPLObj.summary.customerGSTIN);
            etCustomerMobile.setText(userPLObj.summary.customerPhoneNo);

            if (userPLObj.summary.customerId != 0)
                etCustomerId.setText(String.valueOf(userPLObj.summary.customerId));
            else
                etCustomerId.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }


        imgBtnCustSearchbyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (acvCustomerName.getText().toString().equalsIgnoreCase("") || acvCustomerName.getText().toString().length() < 3) {

                    Toast.makeText(CustomerInformation.this, "Please input minimum 3 characters", Toast.LENGTH_SHORT).show();

                } else {
                    new GetCustomerListTask().execute();
                }
            }
        });
        imgBtnSearchbyHUID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tsMessages("Function Not yet Implemented..");
            }
        });
        acvCustomerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 0) {
                    acvCustomerName.setAdapter(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        acvCustomerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {


                String selectedCustomer = (String) parent.getItemAtPosition(pos);
                selectedCustomerId = customerList.data.get(pos).acid;
                selectedCustomerName = customerList.data.get(pos).customer;
                etCustomerGSTNo.setText(customerList.data.get(pos).gstin);
                etCustomerId.setText(String.valueOf(customerList.data.get(pos).acid));
                etCustomerAdrs.setText(customerList.data.get(pos).customerAddress);
                etCustomerMobile.setText(customerList.data.get(pos).customerPhoneNo);

                btnCreateSO.setEnabled(true);
                btnCreateSO.setAlpha(1);
                prefs = PreferenceManager.getDefaultSharedPreferences(CustomerInformation.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("CustomerId", customerList.data.get(pos).acid);
                editor.putString("customer_id", etCustomerId.getText().toString());
                editor.putString("customer_name", acvCustomerName.getText().toString());
                editor.putString("CustomerName", selectedCustomerName);
                editor.apply();
                LockButtons();


            }
        });

    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CustomerInformation.this);
        alertDialogBuilder.setMessage("Do you want to exit.!!");
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                finish();
                startActivity(new Intent(CustomerInformation.this, Category.class));
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void ClearAll(View view) {
        btnCreateSO.setEnabled(false);
        etReceivables.setText("");
        btnCreateSO.setAlpha((float) 0.6);
        acvCustomerName.setText("");
        acvCustomerName.setEnabled(true);
        imgBtnCustSearchbyName.setEnabled(true);
        acvCustomerName.setAdapter(null);
        etCustomerId.setText("");
        etCustomerMobile.setText("");
        etCustomerAdrs.setText("");
        etCustomerGSTNo.setText("");
    }

    public void CreateSO(View view) {
        if (!acvCustomerName.getText().toString().isEmpty() && !etCustomerId.getText().toString().isEmpty()
                || !etCustomerId.getText().toString().equalsIgnoreCase("0")) {
            finish();
            startActivity(new Intent(CustomerInformation.this, SOActivity.class));
        } else {
            Toast.makeText(CustomerInformation.this, "Select a Customer", Toast.LENGTH_SHORT).show();

        }
    }

    public void GetReceivables(View view) {
        System.out.println("Customer ID is " + customerId);
        customerId = userPLObj.summary.customerId;
        if (customerId != 0) {
            new GetReceivablesTask().execute();
        } else if (selectedCustomerId != 0) {
            customerId = selectedCustomerId;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("selectedCustomerName", selectedCustomerName);
            editor.putInt("selectedCustomerId", selectedCustomerId);
            editor.apply();
            new GetReceivablesTask().execute();
        } else {
            Toast.makeText(CustomerInformation.this, "No Customer Specified..!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void ReceivableDetails(View view) {
        if (!etReceivables.getText().toString().isEmpty())
            new GetReceivableDetailsTask().execute();
        else
            Toast.makeText(CustomerInformation.this, "Click Receivable Button", Toast.LENGTH_SHORT).show();
    }

    private class GetReceivableDetailsTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(CustomerInformation.this);
            pDialog.setMessage("Loading Details..");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(Url + "GetReceivableDetails?CustId=382");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setRequestProperty("authkey", AppConfigSettings.auth_id);
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                connection.setRequestProperty("docguid", prefs.getString("guid", ""));
                connection.setRequestProperty("machineid", "salam_ka@yahoo.com");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.connect();

                int responsecode = connection.getResponseCode();
                String responseMsg = connection.getResponseMessage();

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
                        strReceivableDetails = sb.toString();
                        System.out.println("Response of Customer Recievable Details  is--->" + strReceivableDetails);
                    } else {
//                        strErrorMsg = connection.getResponseMessage();
                        strErrorMsg = responseMsg;
                        strReceivableDetails = "httperror";
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strReceivableDetails;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            //strReceivableDetails
            if (strReceivableDetails == null || strReceivableDetails.isEmpty()) {
                Toast.makeText(CustomerInformation.this, "No result from web", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    docId = new ArrayList();
                    billAmount = new ArrayList();
                    balancelist = new ArrayList();
                    Gson gson = new Gson();
                    receivable = gson.fromJson(strReceivableDetails, Receivable.class);
                    for (int i = 0; i < receivable.data.size(); i++) {
                        billAmount.add(String.valueOf(receivable.data.get(i).billAmount));
                        balancelist.add(String.valueOf(receivable.data.get(i).balance));
                        //if (receivable.data.get(i).docNo.length() < 16)
                        docId.add(receivable.data.get(i).docNo);
                        /*else
                            docId.add(receivable.data.get(i).docNo.substring(0, 15));*/
                    }

                    dialog = new Dialog(CustomerInformation.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.dialog_recycler);
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.gravity = Gravity.CENTER;
                    dialog.getWindow().setAttributes(lp);


                    Button btndialog = (Button) dialog.findViewById(R.id.btndialog);
                    TextView dialog_cust = dialog.findViewById(R.id.dialog_cust);
                    TextView total = dialog.findViewById(R.id.total);
                    total.setText("Total: " + etReceivables.getText().toString().trim());
                    dialog_cust.setText(receivable.data.get(0).customer);
                    btndialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();
                        }
                    });

                    recyclerView = dialog.findViewById(R.id.recycler);
                    adapterRe = new AdapterRe(CustomerInformation.this, docId, billAmount, balancelist);
                    recyclerView.setAdapter(adapterRe);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                    recyclerView.addItemDecoration(new DividerItemDecoration(CustomerInformation.this, LinearLayoutManager.VERTICAL));


                    recyclerView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("Id is " + v.getId());
                        }
                    });

                    dialog.show();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private class GetReceivablesTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CustomerInformation.this);
            pDialog.setMessage("Loading Receivables...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            //https://tsmithy.in/somemouat/api/GetReceivables?CustId=382
            try {
                URL url = new URL(Url + "GetReceivables?CustId=" + customerId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setRequestProperty("authkey", AppConfigSettings.auth_id);
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                connection.setRequestProperty("docguid", prefs.getString("guid", ""));
                connection.setRequestProperty("machineid", "salam_ka@yahoo.com");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.connect();

                int responsecode = connection.getResponseCode();
                String responseMsg = connection.getResponseMessage();

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
                        strReceivables = sb.toString();
                        System.out.println("Response of Customer Recievables is--->" + strReceivables);
                    } else {
//                        strErrorMsg = connection.getResponseMessage();
                        strErrorMsg = responseMsg;
                        strReceivables = "httperror";
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return strReceivables;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (strReceivables == null || strReceivables.isEmpty()) {
                Toast.makeText(CustomerInformation.this, "No result from web", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Gson gson = new Gson();
                    customerReceivables = gson.fromJson(strReceivables, CustomerReceivables.class);
                    if (customerReceivables.statusFlag == 0) {
                        String var = String.valueOf(customerReceivables.data.get(0).receivables);
                        etReceivables.setText(var);
                        if (Double.parseDouble(var) < 0)
                            etReceivables.setTextColor(Color.parseColor("#00ff00"));
                        else
                            etReceivables.setTextColor(Color.parseColor("#ff0000"));
                    } else {
                        tsMessages(customerReceivables.errorMessage);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class GetCustomerListTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(CustomerInformation.this);
            pDialog.setMessage("Loading customers...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            //https://tsmithy.in/somemouat/api/GetCustomer?name=m%20g%20medic
            try {
                URL url = new URL(Url + "GetCustomer?name=" + acvCustomerName.getText().toString().trim());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setRequestProperty("authkey", AppConfigSettings.auth_id);
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                connection.setRequestProperty("docguid", prefs.getString("guid", ""));
                connection.setRequestProperty("machineid", "salam_ka@yahoo.com");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.connect();

                int responsecode = connection.getResponseCode();
                String responseMsg = connection.getResponseMessage();

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
                        strCustomer = sb.toString();
                        System.out.println("Response of Customer--->" + strCustomer);
                    } else {
//                        strErrorMsg = connection.getResponseMessage();
                        strErrorMsg = responseMsg;
                        strCustomer = "httperror";
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return strCustomer;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (strCustomer == null || strCustomer.equals("")) {

                Toast.makeText(CustomerInformation.this, "No result from web", Toast.LENGTH_SHORT).show();

            } else {

                Gson gson = new Gson();
                customerList = gson.fromJson(strCustomer, CustomerList.class);

                if (customerList.statusFlag == 0) {

                    try {
                        String[] arrCust = new String[customerList.data.size()];

                        for (int i = 0; i < customerList.data.size(); i++) {
                            arrCust[i] = customerList.data.get(i).customer;
                            System.out.println(arrCust[i]);
                        }


                        AutocompleteCustomArrayAdapter myAdapter = new AutocompleteCustomArrayAdapter(CustomerInformation.this, R.layout.autocomplete_view_row, arrCust);
                        acvCustomerName.setAdapter(myAdapter);
                        acvCustomerName.showDropDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    tsMessages(customerList.errorMessage);
                }
            }

        }
    }

    private void tsMessages(String msg) {

        try {
            final Dialog dialog = new Dialog(CustomerInformation.this);
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

    private void LockButtons() {
        imgBtnCustSearchbyName.setEnabled(false);
        acvCustomerName.setEnabled(false);
        imgBtnSearchbyHUID.setEnabled(false);
        etCustomerAdrs.setEnabled(false);
        etCustomerMobile.setEnabled(false);
        etCustomerGSTNo.setEnabled(false);
        etCustomerId.setEnabled(false);

    }

}