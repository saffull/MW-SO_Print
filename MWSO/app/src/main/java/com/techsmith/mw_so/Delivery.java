package com.techsmith.mw_so;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.techsmith.mw_so.collection_utils.CSpinner;
import com.techsmith.mw_so.collection_utils.CollectionPL;
import com.techsmith.mw_so.collection_utils.SaveResponse;
import com.techsmith.mw_so.delivery_utils.DeliveryItems;
import com.techsmith.mw_so.delivery_utils.DeliveryResponse;
import com.techsmith.mw_so.delivery_utils.DeliveryResponseAdapter;
import com.techsmith.mw_so.delivery_utils.DeliverySO;
import com.techsmith.mw_so.delivery_utils.GpsTracker;
import com.techsmith.mw_so.delivery_utils.onClickInterface;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Delivery extends AppCompatActivity {

    SharedPreferences prefs;
    EditText edtotp, deliverydate, vehicleno;
    TextView txtDate;
    DatePickerDialog datePickerDialog;
    Button btnsendotp, btnsave;
    private CSpinner custSelect;
    public String[] customers = {"Select a Customer", "Ani", "Sam", " Joe"};
    private Double latitude, longitude;
    private GpsTracker gpsTracker;
    private ToggleButton tbUpDown;
    private LinearLayout linearLayoutBSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    List<DeliveryResponse> deliveryList = new ArrayList<>();
    RecyclerView recycler_view;
    public List<String> posList;
    public DeliveryResponseAdapter mAdapter;
    private onClickInterface onclickInterface;
    DeliveryItems deliveryItems;
    DeliverySO deliverySO;
    public ArrayList<DeliveryItems> detailList;
    Gson gson;
    Date currentTime;
    String strstocktake, strfromweb, strerrormsg;
    SaveResponse saveResponse;


    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(Delivery.this, Category.class));
    }

    private void init() {
        this.linearLayoutBSheet = findViewById(R.id.bottomSheet);
        this.bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBSheet);
        this.tbUpDown = findViewById(R.id.txtotp);
        this.btnsave = findViewById(R.id.btnsave);
        this.btnsendotp = findViewById(R.id.btnsendotp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
//        getSupportActionBar().hide();
        prefs = PreferenceManager.getDefaultSharedPreferences(Delivery.this);
        edtotp = findViewById(R.id.edtotp);
        Date c = Calendar.getInstance().getTime();
        deliverydate = findViewById(R.id.deliverydate);
        txtDate = findViewById(R.id.txtDate);
        custSelect = findViewById(R.id.custSelect);
        vehicleno = findViewById(R.id.vehicleno);
        recycler_view = findViewById(R.id.recycler_view);
        posList = new ArrayList();


        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        txtDate.setText(formattedDate);
        init();
        mAdapter = new DeliveryResponseAdapter(this, deliveryList, onclickInterface);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(mAdapter);

        onclickInterface = new onClickInterface() {
            @Override
            public void setClick(int abc) {
                System.out.println("Clicked");
            }
        };

        deliverydate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 1)
                    deliverydate.setText("1");
                else if (s.toString().equalsIgnoreCase("0"))
                    deliverydate.setText("1");

            }
        });
        btnsendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(Delivery.this, BleActivity.class));
                // startActivity(new Intent(Delivery.this,SoMenu.class));
                //startActivity(new Intent(Delivery.this, BluetoothActivity.class));

            }
        });
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTime = Calendar.getInstance().getTime();
                disableButtons();
                if (posList.size() > 0) {
                    System.out.println(posList);
                    detailList = new ArrayList<>();
                    gson = new Gson();
                    deliverySO = new DeliverySO();
                    deliverySO.date = currentTime.toString();
                    deliverySO.customer = custSelect.getSelectedItem().toString();


                    for (int i = 0; i < recycler_view.getChildCount(); i++) {

                        if (posList.contains(String.valueOf(i))) {
                            deliveryItems = new DeliveryItems();
                            System.out.println(deliveryList.get(i).getBillno());
                            deliveryItems.items = deliveryList.get(i).getItems();
                            deliveryItems.invoice = deliveryList.get(i).getInvoice();
                            deliveryItems.amount = deliveryList.get(i).getAmount();
                            deliveryItems.billno = deliveryList.get(i).getBillno();
                            detailList.add(deliveryItems);
                            deliverySO.input = detailList;
                        } else {
                            System.out.println("Nothing to add");
                        }

                    }


                    strstocktake = gson.toJson(deliverySO);
                    System.out.println("Writing data is " + strstocktake);
                    new saveDocument().execute();
                } else
                    Toast.makeText(Delivery.this, "No data selected", Toast.LENGTH_SHORT).show();
            }
        });

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

        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                customers);

        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        custSelect.setAdapter(ad);

        custSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    prepareMovieData();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        gpsTracker = new GpsTracker(Delivery.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            System.out.println("Latitude & Longitude is " + latitude + "--" + longitude);

        } else {
            gpsTracker.showSettingsAlert();
        }


    }

    private void disableButtons() {
        btnsave.setEnabled(false);
        custSelect.setEnabled(false);
    }


    public void SendOtp(View view) {
    }

    public void ResetAll(View view) {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void prepareMovieData() {
        deliveryList.clear();
        mAdapter.notifyDataSetChanged();

        DeliveryResponse movie = new DeliveryResponse("INV-123", "12", "1015", "Bill-1");
        deliveryList.add(movie);

        movie = new DeliveryResponse("INV-456", "15", "2000", "Bill-21");
        deliveryList.add(movie);
        movie = new DeliveryResponse("INV-457", "25", "5000", "Bill-7");
        deliveryList.add(movie);
        movie = new DeliveryResponse("INV-458", "25", "5000", "Bill-17");
        deliveryList.add(movie);
        movie = new DeliveryResponse("INV-407", "30", "5500", "Bill-90");
        deliveryList.add(movie);
        movie = new DeliveryResponse("INV-445", "55", "45000", "Bill-91");
        deliveryList.add(movie);
        movie = new DeliveryResponse("INV-408", "35", "6000", "Bill-92");
        deliveryList.add(movie);


        mAdapter.notifyDataSetChanged();
    }

    private class saveDocument extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog = new ProgressDialog(Delivery.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                progressDialog.setMessage("Saving Document...");
                progressDialog.show();
            } catch (WindowManager.BadTokenException e) {
                //use a log message
            }

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
                connection.setRequestProperty("docid", "2022");
                connection.setRequestProperty("module", "Ts-MWSO");
                connection.connect();

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(strstocktake);
                wr.flush();
                wr.close();
                int responsecode = connection.getResponseCode();
                System.out.println("Response code is " + responsecode);
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
                        System.out.println("Delivery Save Response is " + strfromweb);


                    } finally {
                        connection.disconnect();
                    }

                } else {
                    strerrormsg = connection.getResponseMessage();
                    strfromweb = "httperror";
                    btnsave.setEnabled(true);
                    custSelect.setEnabled(true);
                    Toast.makeText(Delivery.this, strfromweb, Toast.LENGTH_SHORT).show();
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
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            try {
                saveResponse = gson.fromJson(s, SaveResponse.class);
                if (saveResponse.errorStatus == 0) {

                    Toast.makeText(Delivery.this, "Receipt No is " + saveResponse.rcptNo, Toast.LENGTH_SHORT).show();
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        //startActivity(new Intent(tabScreen1.this, tabScreen2.class));
                    }, 300);
                } else {
                    Toast.makeText(Delivery.this, "Failed to save due to" + saveResponse.statusMessage, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}

/*362,364,365,366*/