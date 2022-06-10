package com.techsmith.mw_so;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.techsmith.mw_so.collection_utils.AutoCollectionPL;
import com.techsmith.mw_so.collection_utils.AutoCompleteCustomerAdapter;
import com.techsmith.mw_so.collection_utils.CSpinner;
import com.techsmith.mw_so.collection_utils.Collection;
import com.techsmith.mw_so.collection_utils.CollectionPL;
import com.techsmith.mw_so.collection_utils.FillAmount;
import com.techsmith.mw_so.collection_utils.SOAutoFillAdapter;
import com.techsmith.mw_so.collection_utils.SOCollectionAdapter;
import com.techsmith.mw_so.collection_utils.SaveCollectionAutoSO;
import com.techsmith.mw_so.collection_utils.SaveCollectionSO;
import com.techsmith.mw_so.collection_utils.SaveResponse;
import com.techsmith.mw_so.get_utils.GetCollection;
import com.techsmith.mw_so.utils.AppConfigSettings;
import com.techsmith.mw_so.utils.CustomerList;
import com.techsmith.mw_so.utils.UserPL;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Collections extends AppCompatActivity {
    public EditText userInput, tvAmountValue;
    RadioButton cash, cheque;
    Gson gson;
    Double TempAutoFillAmount, total = 0.0;
    Collection collection;
    SharedPreferences.Editor editor;
    private CSpinner storeSelect;
    CustomerList customerList;
    AutoCompleteTextView acvItemSearchSOActivity;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout linearLayoutBSheet, ll;
    private ToggleButton tbUpDown;
    private Button save, reset, remarks, autoFill, last;
    public TextView tvDueValue;
    SharedPreferences prefs;
    public ListView lvCollectionlist;
    ProgressDialog pDialog;
    ImageButton imgBtnCustSearchbyName;
    SOCollectionAdapter arrayAdapter;
    AutoCompleteTextView acvCustomerName;
    String StoreId, etCustomerId, Url, userRemarks = "", multiSOStoredDevId, loginResponse,
            strErrorMsg, strCollections, strCustomer, strfromweb, strstocktake, strerrormsg, pMode = "";
    UserPL userPLObj;
    public String[] stores, amt, vTmp;
    int i = 0, edPos, autoCount = 0, dueTotal = 0;
    HashMap<String, String> storeMap, custMap;
    List<CollectionPL> listCollectionPL;
    Boolean autoFlag = false;
    public ArrayList<CollectionPL> detailList;
    public ArrayList<AutoCollectionPL> autodetailList;

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(Collections.this, Category.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);
//        getSupportActionBar().hide();
        prefs = PreferenceManager.getDefaultSharedPreferences(Collections.this);
        acvCustomerName = findViewById(R.id.acvCustomerName);
        lvCollectionlist = findViewById(R.id.lvCollectionlist);
        cash = findViewById(R.id.radioCOLCash);
        cheque = findViewById(R.id.radioCOLCheque);
        storeSelect = findViewById(R.id.storeSelect);
        ll = findViewById(R.id.paymentDetails);
        autoFill = findViewById(R.id.btnCOL_AutoFill);
        imgBtnCustSearchbyName = findViewById(R.id.imgBtnCustSearchbyName);
        listCollectionPL = new ArrayList<>();
        detailList = new ArrayList<>();
        //recycler_view=findViewById(R.id.recycler_view);

        loginResponse = prefs.getString("loginResponse", "");
        gson = new Gson();
        userPLObj = gson.fromJson(loginResponse, UserPL.class);
        Url = prefs.getString("MultiSOURL", "");
        multiSOStoredDevId = prefs.getString("MultiSOStoredDevId", "");
        storeMap = new HashMap<>();

        init();

        tbUpDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        imgBtnCustSearchbyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (acvCustomerName.getText().toString().equalsIgnoreCase("") || acvCustomerName.getText().toString().length() < 3) {

                    Toast.makeText(Collections.this, "Please input minimum 3 characters", Toast.LENGTH_SHORT).show();

                } else {
                    new GetCustomerListTask().execute();
                }
            }
        });
        acvCustomerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                storeSelect.setEnabled(true);
            }
        });
        acvCustomerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    acvCustomerName.setAdapter(null);
                    storeSelect.setEnabled(false);
                }
            }
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    tbUpDown.setChecked(true);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    tbUpDown.setChecked(false);
                }
            }

            @Override
            public void onSlide(View view, float v) {

            }
        });


        try {
            gson = new Gson();
            if (userPLObj.detail.size() > 0)
                stores = new String[userPLObj.detail.size() + 1];
            else
                stores = new String[5000];
            userPLObj = gson.fromJson(loginResponse, UserPL.class);

            acvCustomerName.setText(userPLObj.summary.customerName);
            stores[0] = "Select Store";
            for (int i = 0; i < userPLObj.detail.size(); i++) {
                stores[i + 1] = userPLObj.detail.get(i).storeName;
                storeMap.put(userPLObj.detail.get(i).storeName, String.valueOf(userPLObj.detail.get(i).storeId));
            }
            storeSelect.setEnabled(!acvCustomerName.getText().toString().isEmpty());

            ArrayAdapter ad
                    = new ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    stores);

            ad.setDropDownViewResource(
                    android.R.layout
                            .simple_spinner_dropdown_item);

            storeSelect.setAdapter(ad);
            storeSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    edPos = position;
                    StoreId = storeMap.get(stores[position]);

                    if (StoreId != null && position > 0) {
                        autoFill.setEnabled(true);
                        tvAmountValue.setText("");
                        new TakeCollectionTask().execute();
                    } else if (position == 0) {
                        autoFill.setEnabled(false);
                        tvAmountValue.setText("");
                        lvCollectionlist.setAdapter(null);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {


                }
            });

            if (userPLObj.summary.customerId != 0)
                etCustomerId = String.valueOf(userPLObj.summary.customerId);
            else
                etCustomerId = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mTotalReceiver,
                new IntentFilter("custom-total"));


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!pMode.isEmpty()) {
                    SaveCollectionSO saveCollectionSO = new SaveCollectionSO();
                    SaveCollectionAutoSO sso = new SaveCollectionAutoSO();
                    String temp = acvCustomerName.getText().toString();
                    if (!autoFlag) {
                        if (!pMode.equalsIgnoreCase("cheque")) {
                            saveCollectionSO.acid = custMap.get(temp);
                            saveCollectionSO.partyName = temp;
                            saveCollectionSO.paymentMode = pMode;
                            saveCollectionSO.storeId = StoreId;
                            saveCollectionSO.data = detailList;
                            saveCollectionSO.total = tvAmountValue.getText().toString();
                        } else {
                            saveCollectionSO.acid = custMap.get(temp);
                            saveCollectionSO.partyName = temp;
                            saveCollectionSO.storeId = StoreId;
                            saveCollectionSO.data = detailList;
                            saveCollectionSO.total = tvAmountValue.getText().toString();
                            saveCollectionSO.paymentMode = pMode;
                            saveCollectionSO.docBank = "Yes Bank";
                            saveCollectionSO.docDate = "12-09-2021";
                            saveCollectionSO.docType = "";
                            saveCollectionSO.depositBank = "";
                        }

                        strstocktake = gson.toJson(saveCollectionSO);
                        System.out.println("Writing data is " + strstocktake);
                    } else {
                        if (!pMode.equalsIgnoreCase("cheque")) {
                            sso.acid = custMap.get(temp);
                            sso.partyName = temp;
                            sso.paymentMode = pMode;
                            sso.storeId = StoreId;
                            sso.data = autodetailList;
                            sso.total = tvAmountValue.getText().toString();
                        } else {
                            sso.acid = custMap.get(temp);
                            sso.partyName = temp;
                            sso.storeId = StoreId;
                            sso.data = autodetailList;
                            sso.total = tvAmountValue.getText().toString();
                            sso.paymentMode = pMode;
                            sso.docBank = "Yes Bank";
                            sso.docDate = "12-09-2021";
                            sso.docType = "";
                            sso.depositBank = "";
                        }

                        strstocktake = gson.toJson(sso);
                        System.out.println("Writing data in auto Fill adapter is  " + strstocktake);
                    }


                    //new SaveCollection().execute();
                } else {
                    Toast.makeText(Collections.this, "Select a payment Mode", Toast.LENGTH_LONG).show();
                }

            }
        });
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TakeLastCollectionTask().execute();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(Collections.this);
                alertDialogBuilder.setMessage("This will reset everything.. do you want to continue..?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });

                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();

            }
        });
        remarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(Collections.this);
                View promptsView = li.inflate(R.layout.remarks_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Collections.this);
                alertDialogBuilder.setView(promptsView);
                userInput = promptsView.findViewById(R.id.etUserRemarks);

                userRemarks = prefs.getString("remarks", "");
                userInput.setText(userRemarks);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                userRemarks = userInput.getText().toString().trim();
                                prefs = PreferenceManager.getDefaultSharedPreferences(Collections.this);
                                editor = prefs.edit();
                                editor.putString("remarks", userRemarks);
                                editor.commit();
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();


            }
        });

    }

    public BroadcastReceiver mTotalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String command = intent.getStringExtra("command");
                if (command.equalsIgnoreCase("ok")) {
                    Double tp = 0.0;
                    for (String s : vTmp) {

                        tp = tp + Double.parseDouble(s);
                    }
                    tvAmountValue.setText(String.valueOf(tp));
                    //calculateTotal();
                } else {
                    // calculateElse();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    };


    private void init() {
        this.linearLayoutBSheet = findViewById(R.id.bottomSheet);
        this.bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBSheet);
        this.tbUpDown = findViewById(R.id.toggleButton);
        this.save = findViewById(R.id.btnCOL_Save);
        this.reset = findViewById(R.id.btnCOL_Reset);
        this.remarks = findViewById(R.id.btnCOL_Remarks);
        this.tvAmountValue = findViewById(R.id.tvAmountValue);
        this.tvDueValue = findViewById(R.id.tvDueValue);
        this.last = findViewById(R.id.btnCOL_Last);
    }


    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        String str = "";
        switch (view.getId()) {
            case R.id.radioCOLCash:
                if (checked) {
                    pMode = "cash";
                }
                break;

            case R.id.radioCOLCheque:
                if (checked)
                    pMode = "cheque";
                callDialog();


                break;
            default:
                break;
        }
    }

    private void callDialog() {
        LayoutInflater li = LayoutInflater.from(Collections.this);
        View promptsView = li.inflate(R.layout.cheque_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Collections.this);
        alertDialogBuilder.setView(promptsView);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void AutoFillAmount(View view) {
        LayoutInflater li = LayoutInflater.from(Collections.this);
        View promptsView = li.inflate(R.layout.autofill_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Collections.this);
        alertDialogBuilder.setView(promptsView);
        userInput = promptsView.findViewById(R.id.etUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TempAutoFillAmount = 0.0;
                        TempAutoFillAmount = Double.parseDouble(userInput.getText().toString());
                        callDialouge();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void callDialouge() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(Collections.this);
        alertDialogBuilder.setMessage("Do you want to continue..?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                new AutoFillTask().execute();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void ClearSearch(View view) {
        acvCustomerName.setText("");
        acvCustomerName.setAdapter(null);
        storeSelect.setSelection(0);
        total = 0.0;
        storeSelect.setEnabled(false);
        tvAmountValue.setText("");
        autoCount = 0;
    }


    private class AutoFillTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Collections.this);
            pDialog.setMessage("AutoFilling..Please wait.!!");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(Url + "CollectionAutoFill?CustId=382&StoreId=" + StoreId + "&AmountToFill=" + TempAutoFillAmount);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setRequestProperty("authkey", AppConfigSettings.auth_id);
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                connection.setRequestProperty("machineid", "saffull@gmail.com");
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
                        strCollections = sb.toString();
                        System.out.println("Response of  collection Auto Fill  is--->" + strCollections);
                    } else {
//                        strErrorMsg = connection.getResponseMessage();
                        strErrorMsg = responseMsg;
                        strCollections = "httperror";
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return strCollections;
        }

        @Override
        protected void onPostExecute(String s) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (strCollections == null || strCollections.isEmpty()) {
                Toast.makeText(Collections.this, "No result from web", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    gson = new Gson();
                    FillAmount fillAmount = gson.fromJson(strCollections, FillAmount.class);
                    if (fillAmount.statusFlag == 0) {
                        autoCount++;
                        vTmp = new String[0];
                        autodetailList = new ArrayList<>();
                        autodetailList = (ArrayList<AutoCollectionPL>) fillAmount.data;
                        lvCollectionlist.setAdapter(null);
                        autoFlag = true;

                        SOAutoFillAdapter arrayFillAdapter = new SOAutoFillAdapter(Collections.this, R.layout.list_row, autodetailList, autoFlag);
                        lvCollectionlist.setAdapter(arrayFillAdapter);
                        arrayFillAdapter.notifyDataSetChanged();


                        autoFill.setEnabled(false);

                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tvAmountValue.setText(String.valueOf(TempAutoFillAmount));
                            }
                        }, 1000);

                    } else {
                        lvCollectionlist.setAdapter(null);
                        tsMessages(collection.errorMessage);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private class TakeCollectionTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Collections.this);
            pDialog.setMessage("Collecting..Please wait.!!");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(Url + "CustomerReceivablesForCollection?CustId=382&StoreId=" + StoreId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setRequestProperty("authkey", AppConfigSettings.auth_id);
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                connection.setRequestProperty("machineid", "saffull@gmail.com");
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
                        strCollections = sb.toString();
                        System.out.println("Response of Store collection is--->" + strCollections);
                    } else {
//                        strErrorMsg = connection.getResponseMessage();
                        strErrorMsg = responseMsg;
                        strCollections = "httperror";
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return strCollections;
        }

        @Override
        protected void onPostExecute(String s) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (strCollections == null || strCollections.isEmpty()) {
                Toast.makeText(Collections.this, "No result from web", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    gson = new Gson();
                    collection = gson.fromJson(strCollections, Collection.class);
                    /*ObjectMapper om = new ObjectMapper();
                    collection = om.readValue(strCollections, Collection.class);*/
                    if (collection.statusFlag == 0) {
                        listCollectionPL = collection.data;
                        detailList = new ArrayList<>();
                        detailList = (ArrayList<CollectionPL>) collection.data;
                        amt = new String[detailList.size()];
                        dueTotal = 0;
                        autoFlag = false;
                        vTmp = new String[detailList.size()];
                        for (int i = 0; i < detailList.size(); i++) {
                            vTmp[i] = "0";

                        }
                        System.out.println(Arrays.toString(vTmp));
                        for (i = 0; i < collection.data.size(); i++) {
                            dueTotal += collection.data.get(i).Balance;
                            tvDueValue.setText(String.valueOf(dueTotal));

                        }
                        arrayAdapter = new SOCollectionAdapter(Collections.this, R.layout.list_row, detailList);
                        lvCollectionlist.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();

                    } else {
                        lvCollectionlist.setAdapter(null);
                        tsMessages(collection.errorMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        }
    }

    private void tsMessages(String msg) {

        try {
            final Dialog dialog = new Dialog(Collections.this);
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

    private class GetCustomerListTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Collections.this);
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
                connection.setRequestProperty("machineid", "saffull@gmail.com");
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

                Toast.makeText(Collections.this, "No result from web", Toast.LENGTH_SHORT).show();

            } else {

                Gson gson = new Gson();
                customerList = gson.fromJson(strCustomer, CustomerList.class);

                if (customerList.statusFlag == 0) {

                    try {
                        custMap = new HashMap<>();
                        String[] arrCust = new String[customerList.data.size()];

                        for (int i = 0; i < customerList.data.size(); i++) {
                            arrCust[i] = customerList.data.get(i).customer;
                            custMap.put(customerList.data.get(i).customer, String.valueOf(customerList.data.get(i).acid));

                        }


                        AutoCompleteCustomerAdapter myAdapter = new AutoCompleteCustomerAdapter(Collections.this, R.layout.autocomplete_view_row, arrCust);
                        acvCustomerName.setAdapter(myAdapter);
                        acvCustomerName.showDropDown();
                        storeSelect.setEnabled(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    tsMessages(customerList.errorMessage);
                }
            }

        }
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                int pos = intent.getIntExtra("pos", -1);


                //arrayAdapter = new SOCollectionAdapter(context, R.layout.list_row, detailList);
                // lvCollectionlist.setAdapter(arrayAdapter);
                //arrayAdapter.notifyDataSetChanged();

                lvCollectionlist.setSelection(pos);

                Double tp = 0.0;
                for (String s : vTmp) {

                    tp = tp + Double.parseDouble(s);
                }
                tvAmountValue.setText(String.valueOf(tp));


            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    };

    private class SaveCollection extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Collections.this);
            pDialog.setMessage("Saving SO...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                URL url = new URL("https://tsmithy.in/dummy/api/PostDummy");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(30000);
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("authkey", "D39C1EB0-DC32-4CF2-817B-EE748A8E4A30");
                connection.setRequestProperty("docid", "12");
                connection.setRequestProperty("module", "Ts-MWSO");
                connection.connect();

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(strstocktake);
                wr.flush();
                wr.close();
                int responsecode = connection.getResponseCode();
                if (responsecode == 200) {
                    try {
                        InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(streamReader);
                        StringBuilder sb = new StringBuilder();
                        String inputLine = "";
                        while ((inputLine = reader.readLine()) != null) {
                            sb.append(inputLine);
                            break;
                        }

                        reader.close();
                        strfromweb = sb.toString();
                        System.out.println("Save Document Response is " + strfromweb);


                    } finally {
                        connection.disconnect();
                    }

                } else {
                    strerrormsg = connection.getResponseMessage();
                    strfromweb = "httperror";
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }


            return strfromweb;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            SaveResponse saveResponse = gson.fromJson(s, SaveResponse.class);
            if (saveResponse.errorStatus == 0) {
                editor = prefs.edit();
                editor.putString("rcptno", String.valueOf(saveResponse.rcptNo));
                editor.commit();
                tsMessages("SO Saved..\n Recipet No is:\t" + saveResponse.rcptNo);
                last.setEnabled(true);

            } else {
                Toast.makeText(Collections.this, "Failed to save due to" + saveResponse.statusMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class TakeLastCollectionTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Collections.this);
            pDialog.setMessage("Loading Last Saved SO...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String rcpt = prefs.getString("rcptno", "");

                URL url = new URL("https://tsmithy.in/dummy/api/GetDummy?Module=Ts-MWSO&DocId=&RcptNo=" + rcpt);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(30000);
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("authkey", "D39C1EB0-DC32-4CF2-817B-EE748A8E4A30");
                connection.setRequestProperty("docid", "13");
                connection.setRequestProperty("module", "Ts-MWSO");
                connection.connect();

                int responsecode = connection.getResponseCode();
                if (responsecode == 200) {
                    try {
                        InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(streamReader);
                        StringBuilder sb = new StringBuilder();
                        String inputLine = "";
                        while ((inputLine = reader.readLine()) != null) {
                            sb.append(inputLine);
                            break;
                        }

                        reader.close();
                        strfromweb = sb.toString();
                        System.out.println("Get Document Response is " + strfromweb);


                    } finally {
                        connection.disconnect();
                    }

                } else {
                    strerrormsg = connection.getResponseMessage();
                    strfromweb = "httperror";
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }


            return strfromweb;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            GetCollection saveResponse = gson.fromJson(s, GetCollection.class);
            if (saveResponse.errorStatus == 0) {

                tsMessages(saveResponse.data.partyName);
            }

        }
    }


}