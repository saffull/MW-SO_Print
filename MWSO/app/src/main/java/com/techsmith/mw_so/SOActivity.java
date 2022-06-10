package com.techsmith.mw_so;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anggastudio.printama.Printama;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.techsmith.mw_so.Expandable.RecyclerTouchListener;
import com.techsmith.mw_so.Model.CardModel;
import com.techsmith.mw_so.collection_utils.CollectionPL;
import com.techsmith.mw_so.e_invoice.Einvoice;
import com.techsmith.mw_so.e_invoice.EinvoicePL;
import com.techsmith.mw_so.e_invoice.IRNAdapter;
import com.techsmith.mw_so.e_invoice.InvResponse;
import com.techsmith.mw_so.e_invoice.InvoiceAdapter;
import com.techsmith.mw_so.utils.AllocateQty;
import com.techsmith.mw_so.utils.AllocateQtyPL;
import com.techsmith.mw_so.utils.AppConfigSettings;
import com.techsmith.mw_so.utils.AutoCompleteProductListCustomAdapter;
import com.techsmith.mw_so.utils.CustomerReceivables;
import com.techsmith.mw_so.utils.ItemDetails;
import com.techsmith.mw_so.utils.ItemList;
import com.techsmith.mw_so.utils.PrintResponse;
import com.techsmith.mw_so.utils.SOActivityArrayAdapter;
import com.techsmith.mw_so.utils.SOMemo;
import com.techsmith.mw_so.utils.SOPL;
import com.techsmith.mw_so.utils.SOSave;
import com.techsmith.mw_so.utils.SaveSODetail;
import com.techsmith.mw_so.utils.SaveSOResponse;
import com.techsmith.mw_so.utils.SaveSummarySO;
import com.techsmith.mw_so.utils.UserPL;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class SOActivity extends AppCompatActivity {

    SharedPreferences prefs;
    SOActivityArrayAdapter arrayAdapter;
    List<String> storeidList;
    String loginResponse, filter = "", Url = "", strGetItems, strErrorMsg, strSaveMiniSO, billRemarks = "",
            strReceivables, CustomerName, strGetItemDetail, itemName, strGetTotal, itemCode, soString = "", strCheckLogin, printData, s1 = "", s2 = "",
            cceId = "", multiSOStoredDevId = "", uniqueId, strfromweb, strerrormsg, strstocktake;
    public Double itemSoh, total = 0.0, totalSOH;// item made public so that to access in its adapter class
    public String itemMrp, irnNo = "", qrString = "NO Data", doc_no = "";// item made public so that to access in its adapter class
    Boolean isRepeat = false;
    EditText etQty;
    Bitmap bitmap;
    int CustomerId, itemId, itemQty, selectedQty, soResponseCount = 0;
    ImageButton ic_search;
    Gson gson;
    ItemList itemList;
    ItemDetails itemDetails;
    TextView cashDisc, volDisc, allocStore, tvRate, whText, tvSelectedItemName, tvMrp, whSoh, offers;
    public TextView tvAmountValue; // item made public so that to access in its adapter class
    SOPL soplObj;
    EditText etAddRemarks;
    Button caculate, btnAdd, btnAllClear, btnSave;
    List<String> offerList, houseList, sohList, printStoreId, printSbNumber;
    TextView tvCustomerName, tvDate, Freeqty, etReceivables;
    AutoCompleteTextView acvItemSearchSOActivity;
    ProgressDialog pDialog;
    ImageButton imgBtnRemarksPrescrptn;
    Dialog qtydialog, dialog;
    UserPL userPLObj;
    Double tsMsgDialogWindowHeight;
    Double saveDialogWindowHeight;
    public ListView lvProductlist;// item made public so that to access in its adapter class
    AllocateQty allocateQty;
    List<AllocateQtyPL> listSODetailPL;
    public ArrayList<AllocateQtyPL> detailList;/*list made common to both main & adapter class, so that UI changes done in adapter class
    eg: changing the qty or deleting get reflected globally*/
    HashMap<String, String> offerMap = new HashMap<>();

    FloatingActionButton mAddAlarmFab, mAddPersonFab, email_fab, invoice_fab, preview_fab;
    ExtendedFloatingActionButton mAddFab;
    TextView addAlarmActionText, addPersonActionText, email_text, MessageDisplay, invoice_text, preview_text;
    // to check whether sub FABs are visible or not
    Boolean isAllFabsVisible, isPrint;

    List<String> printList, sbList, idList, billnoList, billidList;
    String[] sbNumber, storeId;
    PrintResponse printResponse;
    int sizeCount = 0, reCount = 0;
    Einvoice einvoice;
    EinvoicePL einvoicePL;
    InvResponse invResponse;
    private String[] myImageNameList = new String[]{"15"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so);
//        getSupportActionBar().hide();

        //Determine screen size
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Toast.makeText(this, "Large screen", Toast.LENGTH_LONG).show();
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            Toast.makeText(this, "Normal sized screen", Toast.LENGTH_LONG).show();
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            Toast.makeText(this, "Small sized screen", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Screen size is neither large, normal or small", Toast.LENGTH_LONG).show();
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(SOActivity.this);
        mAddFab = findViewById(R.id.add_fab);
        mAddAlarmFab = findViewById(R.id.add_alarm_fab);
        mAddPersonFab = findViewById(R.id.add_person_fab);
        preview_fab = findViewById(R.id.preview_fab);
        email_fab = findViewById(R.id.email_fab);
        invoice_fab = findViewById(R.id.invoice_fab);
        MessageDisplay = findViewById(R.id.MessageDisplay);
        addAlarmActionText =
                findViewById(R.id.add_alarm_action_text);
        addPersonActionText =
                findViewById(R.id.add_person_action_text);
        email_text = findViewById(R.id.email_text);
        invoice_text = findViewById(R.id.invoice_text);
        preview_text = findViewById(R.id.preview_text);
        initializeFab();
        tvCustomerName = findViewById(R.id.tvCustomerName);
        ic_search = findViewById(R.id.imgBtnSearchItem);
        lvProductlist = findViewById(R.id.lvProductlist);
        tvAmountValue = findViewById(R.id.tvAmountValue);
        btnAllClear = findViewById(R.id.btnAllClear);
        etReceivables = findViewById(R.id.etReceivables);
        imgBtnRemarksPrescrptn = findViewById(R.id.imgBtnRemarksPrescrptn);
        btnSave = findViewById(R.id.btnSave);
        tvDate = findViewById(R.id.tvDate);
        loginResponse = prefs.getString("loginResponse", "");
        gson = new Gson();
        userPLObj = gson.fromJson(loginResponse, UserPL.class);
        Url = prefs.getString("MultiSOURL", "");
        multiSOStoredDevId = prefs.getString("MultiSOStoredDevId", "");
        uniqueId = prefs.getString("guid", "");
        System.out.println("Unique Id is " + uniqueId);
        try {
            if (userPLObj.summary.customerName == null) {
                CustomerName = prefs.getString("selectedCustomerName", "");
                CustomerId = prefs.getInt("selectedCustomerId", 0);
            } else {
                CustomerName = userPLObj.summary.customerName;
                CustomerId = userPLObj.summary.customerId;
            }
            System.out.println("CustomerId is " + CustomerId);
            if (CustomerId != 0) {
                new GetReceivablesTask().execute();
            } else {
                String cust = prefs.getString("customer_id", "");
                try {
                    CustomerId = Integer.parseInt(cust);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (CustomerName.isEmpty()) {
                CustomerName = prefs.getString("customer_name", "");
            } else {
                System.out.println("Customer name already there...");
            }
            cceId = String.valueOf(userPLObj.summary.cceId);
            listSODetailPL = new ArrayList<>();
            detailList = new ArrayList<>();


            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screen_height = displayMetrics.heightPixels;
            int screen_width = displayMetrics.widthPixels;
            tsMsgDialogWindowHeight = Double.valueOf((screen_height * 38) / 100);
            saveDialogWindowHeight = (double) (screen_height * 42) / 100;

            acvItemSearchSOActivity = findViewById(R.id.acvItemSearchSOActivity);


            tvCustomerName.setText(CustomerName);
            tvDate.setText(String.valueOf(CustomerId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAddFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {
                            mAddAlarmFab.show();
                            mAddPersonFab.show();
                            email_fab.show();
                            invoice_fab.show();
                            preview_fab.show();
                            addAlarmActionText
                                    .setVisibility(View.VISIBLE);
                            addPersonActionText
                                    .setVisibility(View.VISIBLE);
                            email_text.setVisibility(View.VISIBLE);
                            invoice_text.setVisibility(View.VISIBLE);
                            preview_text.setVisibility(View.VISIBLE);
                            mAddFab.extend();
                            isAllFabsVisible = true;
                        } else {
                            mAddAlarmFab.hide();
                            mAddPersonFab.hide();
                            email_fab.hide();
                            preview_fab.hide();
                            invoice_fab.hide();
                            addAlarmActionText.setVisibility(View.GONE);
                            addPersonActionText.setVisibility(View.GONE);
                            email_text.setVisibility(View.GONE);
                            invoice_text.setVisibility(View.GONE);
                            preview_text.setVisibility(View.GONE);
                            mAddFab.shrink();
                            isAllFabsVisible = false;
                        }
                    }
                });
        email_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SOActivity.this, "Function not yet implemented..", Toast.LENGTH_SHORT).show();
            }
        });
        mAddPersonFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SOActivity.this, "Function not yet implemented..", Toast.LENGTH_SHORT).show();
            }
        });
        mAddAlarmFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPrint = true;
                PrintDocument(isPrint);

            }
        });
        preview_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SOActivity.this, BluetoothActivity.class));
            }
        });
        acvItemSearchSOActivity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println("text length is "+charSequence.toString().length());
                if (charSequence.toString().length()==0)
                    acvItemSearchSOActivity.setAdapter(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        acvItemSearchSOActivity.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    filter = acvItemSearchSOActivity.getText().toString();
                    if (filter.length() >= 3) {
                        new GetItemsTask().execute();
                    } else {
                        Toast.makeText(SOActivity.this, "Add atleast 3 characters", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });
        acvItemSearchSOActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int pos, long arg3) {
                try {

                    String SelectedText = acvItemSearchSOActivity.getText().toString();

                    if (SelectedText.length() >= 3) {
                        itemId = itemList.data.get(pos).pmid;
                        itemName = itemList.data.get(pos).product;
                        itemCode = itemList.data.get(pos).code;

                        itemMrp = String.valueOf(itemList.data.get(pos).mrp);
                        itemSoh = itemList.data.get(pos).sohInPacks;
                        for (int i = 0; i < detailList.size(); i++) {
                            if (detailList.get(i).itemId == itemId) {
                                Toast.makeText(SOActivity.this, "item already added..", Toast.LENGTH_LONG).show();
                                acvItemSearchSOActivity.setAdapter(null);
                                isRepeat = true;
                                return;
                            } else {
                                isRepeat = false;
                            }
                        }
                        if (!isRepeat)
                            new GetItemDetailsTask().execute();
                        else
                            Toast.makeText(SOActivity.this, "item already added..", Toast.LENGTH_LONG).show();

                    } else {
                        acvItemSearchSOActivity.setAdapter(null);
                    }
                } catch (Exception ex) {
                    Toast.makeText(SOActivity.this, "" + ex, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initializeFab() {
        // Now set all the FABs and all the action name
        // texts as GONE
        mAddAlarmFab.setVisibility(View.GONE);
        mAddPersonFab.setVisibility(View.GONE);
        email_fab.setVisibility(View.GONE);
        preview_fab.setVisibility(View.GONE);
        addAlarmActionText.setVisibility(View.GONE);
        addPersonActionText.setVisibility(View.GONE);
        email_text.setVisibility(View.GONE);
        invoice_fab.setVisibility(View.GONE);
        invoice_text.setVisibility(View.GONE);
        preview_text.setVisibility(View.GONE);
        // make the boolean variable as false, as all the
        // action name texts and all the sub FABs are
        // invisible
        isAllFabsVisible = false;
        // Set the Extended floating action button to
        // shrinked state initially
        mAddFab.shrink();
    }

    public void SearchItem(View view) {
        filter = acvItemSearchSOActivity.getText().toString();
        if (filter.length() >= 3) {
            new GetItemsTask().execute();
        } else {
            Toast.makeText(SOActivity.this, "Add atleast 3 characters", Toast.LENGTH_LONG).show();
        }
    }

    public void ShowRemarks(View view) {
        try {
            prefs = PreferenceManager.getDefaultSharedPreferences(SOActivity.this);
            billRemarks = prefs.getString("BillRemarksMWSO", "");
            dialog = new Dialog(SOActivity.this);
            dialog.setContentView(R.layout.add_remarks);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setTitle("Remarks");
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


            ImageButton imgBtnCloseRemarksWindow = dialog.findViewById(R.id.imgBtnCloseRemarksWindow);
            Button btnOkRemarks = dialog.findViewById(R.id.btnOkRemarks_Itemwise);
            Button btnClearRemarks_Itemwise = dialog.findViewById(R.id.btnClearRemarks_Itemwise);
            etAddRemarks = dialog.findViewById(R.id.etAddRemarks_Itemwise);
            etAddRemarks.setText(billRemarks);


            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            //lp.height = Integer.parseInt(String.valueOf(saveDialogWindowHeight));
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);


            imgBtnCloseRemarksWindow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            btnOkRemarks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    billRemarks = etAddRemarks.getText().toString();

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("BillRemarksMWSO", billRemarks);
                    editor.commit();

                    if (billRemarks.equalsIgnoreCase("")) {
                        imgBtnRemarksPrescrptn.setImageResource(R.drawable.ic_comments);
                    } else {
                        imgBtnRemarksPrescrptn.setImageResource(R.drawable.ic_comment_changes);
                    }

                    dialog.dismiss();

                }
            });

            btnClearRemarks_Itemwise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    etAddRemarks.setText("");
                }
            });

            dialog.show();

        } catch (Exception ex) {
            Toast.makeText(SOActivity.this, "--->" + ex, Toast.LENGTH_SHORT).show();
        }
    }

    public void GoBack(View view) {
        finish();
        startActivity(new Intent(SOActivity.this, CustomerInformation.class));
    }

    public void takeInvoice(View view) {
        try {
            if (soResponseCount == 1) {
                billidList = new ArrayList<>();
                billnoList = new ArrayList<>();
                storeidList = new ArrayList();
                prefs = PreferenceManager.getDefaultSharedPreferences(SOActivity.this);
                storeidList.add(prefs.getString("printStoreID", ""));
                billidList.add(prefs.getString("printSBID", ""));
                billnoList.add(prefs.getString("printSBNumber", ""));
                gson = new Gson();

                einvoice = new Einvoice();
                einvoicePL = new EinvoicePL();
                List<EinvoicePL> invDetailPL = new ArrayList<>();

                for (int i = 0; i < billnoList.size(); i++) {
                    System.out.println(i);
                    einvoicePL.billId = billidList.get(i);
                    einvoicePL.storeId = storeidList.get(i);
                    einvoicePL.billNo = billnoList.get(i);
                    invDetailPL.add(einvoicePL);
                }

                einvoice.list = invDetailPL;
                strstocktake = gson.toJson(einvoice);
                System.out.println("Writing data is " + strstocktake);
                popDialog(SOActivity.this);

            } else {
                billidList = new ArrayList<>();
                billnoList = new ArrayList<>();
                storeidList = new ArrayList();

              /*  billidList.add("24994");
                billidList.add("25013");
                billnoList.add("APPL/22/WS-149");
                billnoList.add("APPL/22/WS-162");
                storeidList.add("15");
                storeidList.add("15");*/

                gson = new Gson();
                Toast.makeText(SOActivity.this, "E-invoice", Toast.LENGTH_SHORT).show();
                einvoice = new Einvoice();
                einvoicePL = new EinvoicePL();
                List<EinvoicePL> invDetailPL = new ArrayList<>();

                for (int i = 0; i < billnoList.size(); i++) {
                    System.out.println(i);
                    einvoicePL.billId = billidList.get(i);
                    einvoicePL.storeId = storeidList.get(i);
                    einvoicePL.billNo = billnoList.get(i);
                    invDetailPL.add(einvoicePL);
                }

                einvoice.list = invDetailPL;
                strstocktake = gson.toJson(einvoice);
                System.out.println("Writing data is " + strstocktake);

                popDialog(SOActivity.this);


                //new GetEInvoice().execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void popDialog(SOActivity activity) {
        dialog = new Dialog(activity);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_invoice_recycler);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        Button btndialog = dialog.findViewById(R.id.btndialog);
        btndialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        RecyclerView recyclerView = dialog.findViewById(R.id.recycler);
        InvoiceAdapter adapterRe = new InvoiceAdapter(SOActivity.this, myImageNameList, billidList, billnoList, storeidList);
        recyclerView.setAdapter(adapterRe);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                System.out.println("Main activity click" + position);
                startDialog(billidList.get(position));

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        dialog.show();

    }

    private class GetEInvoice extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SOActivity.this);
            pDialog.setMessage("Loading E-invoice....");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                URL url = new URL(Url + "UploadEInvoice");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(30000);
                connection.setRequestProperty("authkey", AppConfigSettings.auth_id);
                connection.setRequestProperty("docguid", uniqueId);
                connection.setRequestProperty("machineid", "salam_ka@yahoo.com");
                connection.setRequestProperty("Content-Type", "application/json");
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
                        System.out.println("Einvoice Response is " + strfromweb);


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
            if (pDialog.isShowing())
                pDialog.dismiss();
            gson = new Gson();
            invResponse = gson.fromJson(strfromweb, InvResponse.class);

            if (strfromweb.isEmpty() || strfromweb.equalsIgnoreCase("httperror")) {
                tsMessages("Response Empty");
            } else if (invResponse.list.get(0).statusFlag == 1) {
                tsMessages(invResponse.list.get(0).errorMessage);
            } else {
                try {

                    System.out.println(invResponse.list.get(0).irnNo);
                    if (!invResponse.list.get(0).irnNo.isEmpty()) {
                        popUp(SOActivity.this);
                    } else {
                        Toast.makeText(SOActivity.this, "IRN Number Empty", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void popUp(SOActivity activity) {
        final Dialog dialog = new Dialog(activity);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_invoice_listview);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        Button btndialog = dialog.findViewById(R.id.btndialog);
        btndialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        RecyclerView recyclerView = dialog.findViewById(R.id.recycler);
        IRNAdapter adapterRe = new IRNAdapter(SOActivity.this, myImageNameList, strfromweb);
        recyclerView.setAdapter(adapterRe);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


        dialog.show();

    }


    private class GetItemDetailsTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SOActivity.this);
            pDialog.setMessage("Loading item details...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                //https://tsmithy.in/somemouat/api/GetSOHAndSchemes?ItemId=14290&CustId=382
                URL url = new URL(Url + "GetSOHAndSchemes?ItemId=" + itemId + "&CustId=" + CustomerId);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setRequestProperty("authkey", AppConfigSettings.auth_id);
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                connection.setRequestProperty("docguid", uniqueId);
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
                        strGetItemDetail = sb.toString();
                        System.out.println("Response of Item Detail  is --->" + strGetItemDetail);
                    } else {
//                        strErrorMsg = connection.getResponseMessage();
                        strErrorMsg = responseMsg;
                        strGetItemDetail = "httperror";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return strGetItemDetail;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing())
                pDialog.dismiss();


            itemDetails = new ItemDetails();
            if (strGetItemDetail == null || strGetItemDetail.isEmpty()) {
                Toast.makeText(SOActivity.this, "No result from web for item details", Toast.LENGTH_LONG).show();
            } else {
                if (itemDetails.statusFlag == 0) {
                    try {
                        soplObj = new SOPL();
                        gson = new Gson();
                        soplObj = gson.fromJson(strGetItemDetail, SOPL.class);
                        offerList = new ArrayList<>();
                        houseList = new ArrayList<>();
                        sohList = new ArrayList<>();


                        for (int i = 0; i < soplObj.data.size(); i++) {
                            houseList.add(soplObj.data.get(i).storeName);
                            sohList.add(String.valueOf(soplObj.data.get(i).SOH));
                        }

                        for (int j = 0; j < soplObj.data.size(); j++) {
                            if (soplObj.data.get(j).offerDesc == null)
                                System.out.println("Empty data came");
                            else
                                offerList.add(soplObj.data.get(j).offerDesc.replace("null", ""));
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    qtydialog = new Dialog(SOActivity.this);
                    qtydialog.setContentView(R.layout.quantity_selection_dialogwindow);
                    qtydialog.setCanceledOnTouchOutside(false);
                    qtydialog.setTitle("Quantity Selection");
                    qtydialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    acvItemSearchSOActivity.setAdapter(null);

                    tvSelectedItemName = qtydialog.findViewById(R.id.tvSelectedItemName);
                    offers = qtydialog.findViewById(R.id.offers);
                    tvMrp = qtydialog.findViewById(R.id.tvMrpInQtySelection);
                    tvRate = qtydialog.findViewById(R.id.tvSOHInQtySelection);
                    Freeqty = qtydialog.findViewById(R.id.Freeqty);
                    allocStore = qtydialog.findViewById(R.id.allocStore);
                    whText = qtydialog.findViewById(R.id.whText);
                    whSoh = qtydialog.findViewById(R.id.whSoh);

                    cashDisc = qtydialog.findViewById(R.id.cashDisc);
                    volDisc = qtydialog.findViewById(R.id.volDisc);

                    ImageButton imgBtnCloseQtySelection = qtydialog.findViewById(R.id.imgBtnCloseQtySelection);
                    ImageButton btnPlus = qtydialog.findViewById(R.id.imgBtnPlusPack);
                    ImageButton btnMinus = qtydialog.findViewById(R.id.imgBtnMinusPack);
                    caculate = qtydialog.findViewById(R.id.caculate);
                    btnAdd = qtydialog.findViewById(R.id.btnAddItem_qtySelection);

                    etQty = qtydialog.findViewById(R.id.etQty);
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(qtydialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;

                    lp.gravity = Gravity.CENTER;
                    qtydialog.getWindow().setAttributes(lp);

                    etQty.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            System.out.println(s.toString());
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            System.out.println(s.toString());
                            btnAdd.setEnabled(false);
                            if (Double.parseDouble(s.toString()) > totalSOH) {
                                Toast.makeText(SOActivity.this, "Max units exceeded...", Toast.LENGTH_LONG).show();
                                caculate.setEnabled(false);
                            } else {
                                caculate.setEnabled(true);
                            }
                        }
                    });

                    etQty.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            etQty.setSelection(0, etQty.getText().toString().length());

                        }
                    });

                    tvSelectedItemName.setText("" + itemName);
                    selectedQty = Integer.parseInt(etQty.getText().toString());
                    if (selectedQty > 0)
                        new AllocateQtyTask().execute();

                    caculate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btnAdd.setEnabled(true);
                            selectedQty = Integer.parseInt(etQty.getText().toString());
                            if (selectedQty < 1)
                                Toast.makeText(SOActivity.this, "Add atleast 1", Toast.LENGTH_LONG).show();
                            else
                                new AllocateQtyTask().execute();
                        }
                    });
                    imgBtnCloseQtySelection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            qtydialog.dismiss();
                        }
                    });
                    etQty.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            etQty.setSelection(0, etQty.getText().toString().length());

                        }
                    });
                    btnPlus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                if (etQty.getText().toString().equals("") || etQty.getText().toString() == null) {
                                    etQty.setText("0");
                                }

                                itemQty = Integer.parseInt(etQty.getText().toString());
                                itemQty += 1;
                                etQty.setText("" + itemQty);
                                if (itemQty > 0) {
                                }

                            } catch (Exception ex) {
                                Toast.makeText(SOActivity.this, "" + ex, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    btnMinus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (!etQty.getText().toString().equals("") && etQty.getText().toString() != null) {

                                    itemQty = Integer.parseInt(etQty.getText().toString());
                                    if (itemQty >= 1)
                                        itemQty -= 1;

                                    etQty.setText("" + itemQty);

                                } else {
                                    Toast.makeText(SOActivity.this, "Qty field is empty", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception ex) {
                                Toast.makeText(SOActivity.this, "" + ex, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    btnAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (pDialog.isShowing())
                                    pDialog.dismiss();
                                if (etQty.getText().toString().equals("")) {
                                    Toast.makeText(SOActivity.this, "Qty cannot be empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (Integer.parseInt(etQty.getText().toString()) < 1) {
                                    Toast.makeText(SOActivity.this, "Qty cannot be less than 1", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    allocateQty.data.productName = itemName;
                                    allocateQty.data.MRP = Double.parseDouble(itemMrp);
                                    allocateQty.data.code = Integer.parseInt(itemCode);
                                    allocateQty.data.storeId = String.valueOf(allocateQty.data.allocStore);
                                    detailList.add(allocateQty.data);
                                    listSODetailPL.add(allocateQty.data);
                                    total = total + (allocateQty.data.qty * Double.parseDouble(itemMrp));

                                    String[] from = {"SlNo,ItemName", "BatchCode", "ExpiryDate", "MRP", "BillingRate", "TaxPer", "TaxAmount", "TotalDisc", "LineTotalAmount"};
                                    int[] to = {R.id.itemcode, R.id.name, R.id.batchcode, R.id.batchbarcode, R.id.location, R.id.uperpack, R.id.expiry, R.id.sysstock, R.id.currentsoh};
                                    arrayAdapter = new SOActivityArrayAdapter(SOActivity.this, R.layout.list_row, listSODetailPL,
                                            listSODetailPL.size(), detailList, total);
                                    lvProductlist.setAdapter(arrayAdapter);
                                    arrayAdapter.notifyDataSetChanged();
                                    tvAmountValue.setText(String.format("%.2f", total));

                                    qtydialog.dismiss();
                                    acvItemSearchSOActivity.setText("");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    qtydialog.show();


                } else {
                    Toast.makeText(SOActivity.this, itemDetails.errorMessage, Toast.LENGTH_LONG).show();
                }
            }

        }
    }


    private class AllocateQtyTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SOActivity.this);
            pDialog.setMessage("Updating details...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONObject appUserPlJsnStr = new JSONObject();
                appUserPlJsnStr.put("qty", selectedQty);
                appUserPlJsnStr.put("itemid", itemId);
                appUserPlJsnStr.put("customer", CustomerId);

                //https://tsmithy.in1/somemouat/api/AllocateQty
                URL url = new URL(Url + "AllocateQty");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setRequestProperty("authkey", AppConfigSettings.auth_id);
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                connection.setRequestProperty("docguid", uniqueId);
                connection.setRequestProperty("machineid", "salam_ka@yahoo.com");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(String.valueOf(appUserPlJsnStr));
                wr.flush();
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
                        strGetTotal = sb.toString();
                        System.out.println("Response of AllocateQty is --->" + sb);
                    } else {
//                        strErrorMsg = connection.getResponseMessage();
                        strErrorMsg = responseMsg;
                        strGetTotal = "httperror";
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return strGetTotal;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing())
                pDialog.dismiss();

            try {

                gson = new Gson();
                allocateQty = gson.fromJson(strGetTotal, AllocateQty.class);
                if (allocateQty.statusFlag == 0) {

                    try {
                        caculate.setEnabled(true);
                        cashDisc.setText("Cash Disc: " + allocateQty.data.cashDiscPer + "%");
                        volDisc.setText("Vol Disc: " + allocateQty.data.volDiscPer + "%");
                        tvRate.setText("Rate: " + String.format("%.2f", allocateQty.data.rate));
                        allocStore.setText("Allocated Warehouse: " + allocateQty.data.allocStoreCode);
                        Freeqty.setText("Free Quantity: " + allocateQty.data.freeQty);
                        tvSelectedItemName.setText("" + itemName);
                        tvMrp.setText("MRP: " + itemMrp);
                        totalSOH = allocateQty.data.soh;

                        StringBuilder builder = new StringBuilder();
                        for (String details : houseList) {
                            builder.append(details.subSequence(0, 9) + "\n\n");
                        }
                        StringBuilder sbuilder = new StringBuilder();
                        for (String soh : sohList) {
                            sbuilder.append(soh + "\n\n");
                        }
                        whText.setText(builder.toString());
                        whSoh.setText(sbuilder
                                .toString());


                        offers.setText(offerList.toString().replace("[", "").replace("]", ""));
                        offerMap.put(String.valueOf(itemId), offerList.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    qtydialog.dismiss();
                    acvItemSearchSOActivity.setText("");
                    acvItemSearchSOActivity.setAdapter(null);
                    tsMessages(allocateQty.errorMessage);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GetItemsTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SOActivity.this);
            pDialog.setMessage("Loading items...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(Url + "GetProduct?name=" + filter);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setRequestProperty("authkey", AppConfigSettings.auth_id);
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                connection.setRequestProperty("docguid", uniqueId);
                connection.setRequestProperty("machineid", "salam_ka@yahoo.com");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.connect();

                int responsecode = connection.getResponseCode();
                String responseMsg = connection.getResponseMessage();
                System.out.println("Response code is " + responsecode);

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
                        strGetItems = sb.toString();
                        System.out.println("Response of Product List is --->" + strGetItems);
                    } else {
//                        strErrorMsg = connection.getResponseMessage();
                        strErrorMsg = responseMsg;
                        strGetItems = "httperror";
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return strGetItems;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing())
                pDialog.dismiss();
            try {
                if (strGetItems.equals("httperror")) {
                    //tsMessages("Http error occured\n\n" + strErrorMsg);
                    Toast.makeText(SOActivity.this, "" + strErrorMsg, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (strGetItems == null || strGetItems.equals("")) {
                    tsMessages("No result from web");
                } else {
                    gson = new Gson();
                    itemList = gson.fromJson(strGetItems, ItemList.class);
                    if (itemList.statusFlag == 0) {
                        String[] arrProducts = new String[itemList.data.size()];
                        String[] arrMrp = new String[itemList.data.size()];
                        String[] arrSoh = new String[itemList.data.size()];
                        for (int i = 0; i < itemList.data.size(); i++) {
                            arrProducts[i] = itemList.data.get(i).product;
                            arrMrp[i] = String.valueOf(itemList.data.get(i).mrp);
                            arrSoh[i] = String.valueOf(itemList.data.get(i).sohInPacks);
                        }
                        AutoCompleteProductListCustomAdapter myAdapter = new AutoCompleteProductListCustomAdapter(SOActivity.this, R.layout.autocomplete_view_row, arrProducts, arrMrp, arrSoh);
                        acvItemSearchSOActivity.setAdapter(myAdapter);
                        acvItemSearchSOActivity.showDropDown();
                    } else {
                        tsMessages(itemList.errorMessage);
                        acvItemSearchSOActivity.setText("");
                        acvItemSearchSOActivity.setAdapter(null);

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }


    public void ClearSearch(View view) {
        acvItemSearchSOActivity.setText("");
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SOActivity.this);
        alertDialogBuilder.setMessage("Do you want to logout..!!");
        alertDialogBuilder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                finish();
                startActivity(new Intent(SOActivity.this, Category.class));
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

    public void SaveSO(View view) {
        try {
            if (detailList.size() > 0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SOActivity.this);
                alertDialogBuilder.setMessage("Do you want to continue saving?");
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        listSODetailPL = detailList;
                        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        SaveSODetail saveSODetail = new SaveSODetail();
                        saveSODetail.item = listSODetailPL;

                        SaveSummarySO saveSummarySO = new SaveSummarySO();
                        saveSummarySO.custId = CustomerId;
                        saveSummarySO.customer = CustomerName;
                        saveSummarySO.docSeries = "SOM";
                        saveSummarySO.docDate = date;
                        saveSummarySO.docComplete = 1;
                        saveSummarySO.docSeries = "";
                        saveSummarySO.remarks = billRemarks;
                        saveSummarySO.cceId = cceId;
                        saveSummarySO.docGuid = uniqueId;
                        saveSummarySO.machineId = "";
                        saveSummarySO.userId = "";


                        SOMemo soMemo = new SOMemo();
                        soMemo.detail = saveSODetail;
                        soMemo.summary = saveSummarySO;


                        SOSave soSave = new SOSave();
                        soSave.soMemo = soMemo;


                        gson = new Gson();
                        soString = gson.toJson(soSave);
                        System.out.println(soString);
                        new SaveSOTask().execute();
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SOActivity.this);
                alertDialogBuilder.setMessage("No SO to save.....!!");
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //tsMessages("Function not yet implemented...");
    }

    private class SaveSOTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SOActivity.this);
            pDialog.setMessage("Saving SO...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            //https://tsmithy.in/somemouat/api/SaveSO
            try {
                System.out.println("GUID coming here is" + uniqueId);
                URL url = new URL(Url + "SaveSO");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("authkey", AppConfigSettings.auth_id);
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                connection.setRequestProperty("noofitems", String.valueOf(detailList.size()));
                connection.setRequestProperty("guid", uniqueId);
                connection.setRequestProperty("custid", String.valueOf(CustomerId));
                connection.setRequestProperty("machineid", "salam_ka@yahoo.com");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(soString);
                wr.flush();

                connection.connect();

                int responsecode = connection.getResponseCode();
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
                        strSaveMiniSO = sb.toString();
                        System.out.println("SO Saving Response is " + sb);

                    } else {
                        strErrorMsg = connection.getResponseMessage();
                        strSaveMiniSO = "httperror";
                    }

                } finally {
                    connection.disconnect();
                }

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
            return strSaveMiniSO;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing())
                pDialog.dismiss();
            try {
                printStoreId = new ArrayList<>();
                printSbNumber = new ArrayList<>();
                gson = new Gson();

                SaveSOResponse saveSOResponse;
                saveSOResponse = gson.fromJson(strSaveMiniSO, SaveSOResponse.class);
                if (saveSOResponse.statusFlag == 0) {
                    dialog = new Dialog(SOActivity.this);
                    dialog.setContentView(R.layout.save_dialogwindow);
//                        dialog.setCanceledOnTouchOutside(false);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.setTitle("Save");
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//                        ImageButton imgBtnCloseSaveWindow = (ImageButton) dialog.findViewById(R.id.imgBtnCloseSaveWindow);
                    Button btnOkSavePopup = dialog.findViewById(R.id.btnOkSavePopup);
                    TextView tvSaveStatus = dialog.findViewById(R.id.tvSaveStatus);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.gravity = Gravity.CENTER;
                    dialog.getWindow().setAttributes(lp);

                    btnOkSavePopup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            disableButtons();
                            dialog.dismiss();
                            mAddFab.setEnabled(true);
                            // startActivity(new Intent(SOActivity.this, BluetoothActivity.class));
                        }
                    });

                    // tvSaveStatus.setText("Saved \n\nToken No: " + tokenNo);
                    tvSaveStatus.setText("Saved\n\n SOMemoNo:\t" + saveSOResponse.data.get(saveSOResponse.data.size() - 1).SOMemoNo);
                    //printSbNumber = saveSOResponse.data.get(saveSOResponse.data.size() - 1).SB_Number;
                    //printStoreId = saveSOResponse.data.get(saveSOResponse.data.size() - 1).storeId;
                    if (saveSOResponse.data.size() > 1) {
                        for (int i = 0; i < saveSOResponse.data.size(); i++) {
                            printSbNumber.add(saveSOResponse.data.get(i).SB_Number);
                            printStoreId.add(saveSOResponse.data.get(i).storeId);
                        }
                        System.out.println("First List is " + printSbNumber + "\n" + printStoreId);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("printStoreId", printStoreId.toString());
                        editor.putString("printSbNumber", printSbNumber.toString());
                        editor.putInt("sizeCount", saveSOResponse.data.size());
                        editor.apply();

                    } else if (saveSOResponse.data.size() == 1) {
                        soResponseCount = saveSOResponse.data.size();
                        String printSBNumber = saveSOResponse.data.get(saveSOResponse.data.size() - 1).SB_Number;
                        String printStoreID = saveSOResponse.data.get(saveSOResponse.data.size() - 1).storeId;
                        String printSBID = saveSOResponse.data.get(saveSOResponse.data.size() - 1).SB_Id;
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("printStoreID", printStoreID);
                        editor.putString("printSBNumber", printSBNumber);
                        editor.putString("printSBID", printSBID);
                        editor.putInt("sizeCount", saveSOResponse.data.size());
                        editor.apply();
                    }


                    System.out.println("size count is " + saveSOResponse.data.size());
                    tvSaveStatus.setMovementMethod(new ScrollingMovementMethod());
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                } else {
                    tsMessages(saveSOResponse.errorMessage);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void disableButtons() {
        btnSave.setEnabled(false);
        btnAllClear.setEnabled(false);
        acvItemSearchSOActivity.setEnabled(false);
        ic_search.setEnabled(false);
    }


    public void ClearList(View view) {
        if (detailList.size() > 0) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SOActivity.this);
            alertDialogBuilder.setMessage("Do you want to delete the full SO..?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    detailList.clear();
                    total = total * 0.0;
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("BillRemarksMWSO", "");
                    editor.apply();
                    arrayAdapter = new SOActivityArrayAdapter(SOActivity.this, R.layout.list_row, listSODetailPL,
                            listSODetailPL.size(), detailList, total);
                    lvProductlist.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                    tvAmountValue.setText("");
                }
            });
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SOActivity.this);
            alertDialogBuilder.setMessage("Nothing to clear..?");
            alertDialogBuilder.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }

    public void Restart(View view) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SOActivity.this);
        alertDialogBuilder.setMessage("Do you want to start a new SO..?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                try {
                    String uniqueID = UUID.randomUUID().toString();
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("BillRemarksMWSO", "");
                    editor.putString("guid", "");
                    editor.putString("guid", uniqueID);
                    editor.apply();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    private void tsMessages(String msg) {

        try {
            final Dialog dialog = new Dialog(SOActivity.this);
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


    private void PrintDocument(Boolean isPrint) {
        String printStoreID = "", printSBNumber = "", printStoreId, printSbNumber;

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
                System.out.println("came 1");
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
                Toast.makeText(SOActivity.this, "SbNumber or StoreID is Empty", Toast.LENGTH_LONG).show();
            }

        }
    }

    private class TakeBill extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SOActivity.this);
            pDialog.setMessage("Loading..Please wait..!!");
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


            if (strCheckLogin.equals("") || strCheckLogin == null) {
                Toast.makeText(SOActivity.this, "No result", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    gson = new Gson();
                    printResponse = gson.fromJson(printData, PrintResponse.class);
                    String test = printResponse.data.replace("\r", "\n");
                    System.out.println("Print data is " + test);
                    printList.add(test);
                    System.out.println("Print List is " + printList);
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private class TakeBillSingle extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SOActivity.this);
            pDialog.setMessage("Loading Single Bill..Please wait.!!");
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            strCheckLogin = "";
            System.out.println("S1 and s2..!!" + s1 + "\t\t" + s2);
            //  System.out.println("Url used is " + Url);//https://tsmithy.in/somemouat/api/
            try {
                //URL url = new URL("https://tsmithy.in/somemouat/api/LoginVer2?Name=salam_ka@yahoo.com&secret=1047109119116122626466");
                //URL url = new URL("https://tsmithy.in/somemouat/api/PrintBill?StoreId=15&SBillNo=APPL/22/WS-13");
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
                        System.out.println("Response of Print Bill Single is--->" + strCheckLogin);
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
            gson = new Gson();
            printResponse = gson.fromJson(printData, PrintResponse.class);

            if (strCheckLogin.equals("") || strCheckLogin == null) {
                Toast.makeText(SOActivity.this, "No result", Toast.LENGTH_SHORT).show();
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
                    if (printResponse.einvString != null) {

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
                        System.out.println("came 2");
                        System.out.println(qrString);
                    } else {
                        //tsMessages("E-Invoice Data Empty..");
                        Toast.makeText(SOActivity.this,"E-Invoice Data Empty..",Toast.LENGTH_LONG).show();
                    }

                    qrdo();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isPrint) {
                                RcptPrint();
                            } else {
                                System.out.println("came 4");
                            }

                        }
                    }, 1000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void qrdo() {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
            bitMatrix = writer.encode(qrString, BarcodeFormat.QR_CODE, 150, 150);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int color = Color.WHITE;
                    if (bitMatrix.get(x, y)) color = Color.BLACK;
                    bitmap.setPixel(x, y, color);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isPrint)
                        RcptPrint();
                    else
                        System.out.println("Do nothing....");
                }
            }, 1000);

        }
    }

    private void RcptPrint() {
        if (isBluetoothEnabled()) {
            try {
                System.out.println("Came here..!!");
                String temp = MessageDisplay.getText().toString();
                //qrString="hello world";
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 1);
                } else {
                    /*  "[L]" + df.format(new Date()) + "\n" */
                    BluetoothConnection connection = BluetoothPrintersConnections.selectFirstPaired();
                    if (connection != null) {
                        EscPosPrinter printer = new EscPosPrinter(connection, 203, 48f, 32);
                        if (irnNo.isEmpty()|| irnNo==null){

                            final String text =
                                    "[L]" + temp + "\n" +
                                            "[C]--------------------------------\n" ;
                            printer.printFormattedText(text);
                        }else{
                            final String text =
                                    "[L]" + temp + "\n" +
                                            "[C]--------------------------------\n" +
                                            "[C]<qrcode size='20'>" + qrString + "</qrcode>\n" +
                                            "[L]<b>\tIRN Number</b>" +
                                            "[L]<b>\n" + irnNo + "</b>";
                            printer.printFormattedText(text);
                        }

                      /*  final String text =
                                "[L]" + temp + "\n" +
                                        "[C]--------------------------------\n" +
                                        "[C]<qrcode size='20'>" + qrString + "</qrcode>\n" +
                                        "[L]<b>\tIRN Number</b>" +
                                        "[L]<b>\n" + irnNo + "</b>";

                        printer.printFormattedText(text);*/
                    } else {
                        Toast.makeText(this, "No printer was connected!", Toast.LENGTH_SHORT).show();
                    }
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("printer_name",connection.getDevice().getName());
                    editor.apply();
                }
            } catch (Exception e) {
                Log.e("APP", "Can't print", e);
            }
        } else {
            Toast.makeText(SOActivity.this, "Turn On Bluetooth First", Toast.LENGTH_LONG).show();
        }
    }


    public boolean isBluetoothEnabled() {
        BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return myBluetoothAdapter.isEnabled();
    }

    private void startDialog(String billid) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("E-Invoice")
                .setMessage("Do you want to load e-invoice of bill id " + billid + "..?")

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new GetEInvoice().execute();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }


    private class GetReceivablesTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SOActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            //https://tsmithy.in/somemouat/api/GetReceivables?CustId=382
            try {
                URL url = new URL(Url + "GetReceivables?CustId=" + CustomerId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setRequestProperty("authkey", AppConfigSettings.auth_id);
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                connection.setRequestProperty("docguid", uniqueId);
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
                Toast.makeText(SOActivity.this, "No result from web", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    gson = new Gson();
                    CustomerReceivables customerReceivables = gson.fromJson(strReceivables, CustomerReceivables.class);
                    if (customerReceivables.statusFlag == 0) {
                        String var = String.valueOf(customerReceivables.data.get(0).receivables);
                        etReceivables.setText("Receivables: " + var);
                    } else {
                        tsMessages(customerReceivables.errorMessage);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}


/*private void RcptPrint() {
        if (isBluetoothEnabled()) {
            String temp = MessageDisplay.getText().toString();
            try {
                Bitmap bitmap = Printama.getBitmapFromVector(this, R.drawable.techsmith);
                Printama.with(this).connect(printama -> {
                    printama.addNewLine(1);
                    // printama.setNormalText();
                    printama.setSmallText();
                    printama.printText(temp, Printama.LEFT);

                    printama.addNewLine(3);
                    printama.close();
                }, this::showToast);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast toast = Toast.makeText(SOActivity.this,
                    "Please Enable Bluetooth & Try Again.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

      private void RcptPrint() {
        String temp = MessageDisplay.getText().toString().trim();
        if (isBluetoothEnabled()) {
            if (irnNo.isEmpty()) {
                Toast toast = Toast.makeText(SOActivity.this,
                        "IRN Number is Empty...", Toast.LENGTH_SHORT);
                toast.show();

            } else if (temp.isEmpty()) {
                Toast toast = Toast.makeText(SOActivity.this,
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
        } else {
            Toast.makeText(SOActivity.this,
                    "Please Enable Bluetooth & Try Again.", Toast.LENGTH_SHORT).show();
        }
    }*/