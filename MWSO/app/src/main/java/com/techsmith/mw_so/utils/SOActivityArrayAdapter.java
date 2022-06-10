package com.techsmith.mw_so.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.techsmith.mw_so.R;
import com.techsmith.mw_so.SOActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SOActivityArrayAdapter extends ArrayAdapter {
    Context context;
    SOPL soplObj;
    Button btnAdd;
    public Double total;
    List<AllocateQtyPL> listSODetailPL;
    int size, itemQty, selectedQty, CustomerId, itemId, pos;
    Button calculate;
    Dialog qtydialog;
    ProgressDialog pDialog;
    ArrayList<AllocateQtyPL> itemArraylist;
    TextView tvProductName, tvSOH, tvmrp, allocStore, cashDisc, volDisc, tvSelectedItemName, tvMrp, whSoh, offers, whText, Freeqty;
    AllocateQtyPL itemDetail;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    ImageButton btnDelete;
    String Url, strGetTotal, strErrorMsg, current_qty, strGetItemDetail, loginResponse,multiSOStoredDevId,CustomerName;
    AllocateQty allocateQty, Allocateqty;
    List<String> houseList, sohList, offerList;
    UserPL userPLObj;


    public SOActivityArrayAdapter(Context context, int resource, List<AllocateQtyPL> listSODetailPL, int size, ArrayList<AllocateQtyPL> detailList, Double total) {
        super(context, resource);
        this.context = context;
        this.itemArraylist = detailList;
        this.listSODetailPL = listSODetailPL;
        this.size = size;
        this.total = total;

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
        loginResponse = prefs.getString("loginResponse", "");
        try {
            Gson gson = new Gson();
            userPLObj = gson.fromJson(loginResponse, UserPL.class);

            Url = prefs.getString("MultiSOURL", "");
            multiSOStoredDevId=prefs.getString("MultiSOStoredDevId","");

            if (userPLObj.summary.customerName == null) {
                CustomerName = prefs.getString("selectedCustomerName", "");
                CustomerId = prefs.getInt("selectedCustomerId", 0);
            } else {
                CustomerName = userPLObj.summary.customerName;
                CustomerId = userPLObj.summary.customerId;
            }
        }catch (Exception e){e.printStackTrace();}

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        try {
            itemDetail = itemArraylist.get(position);
        } catch (Exception e) {
            e.printStackTrace();
            itemArraylist.remove(itemArraylist.size()-1);
            itemDetail = itemArraylist.get(position);
        }

        try {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lv_saleorder, parent, false);
            tvProductName = convertView.findViewById(R.id.tvProductName);
            tvmrp = convertView.findViewById(R.id.tvmrp);
            tvMrp = convertView.findViewById(R.id.tvMRP);
            TextView tvRate = convertView.findViewById(R.id.tvRate);
            TextView tvqty = convertView.findViewById(R.id.tvqty);
            tvSOH = convertView.findViewById(R.id.tvSOHInQtySelection);
            TextView tvQty = convertView.findViewById(R.id.tvQty);
            TextView tvFreeQty = convertView.findViewById(R.id.tvFreeQty);
            TextView tvTotal = convertView.findViewById(R.id.tvTotal);
            final ImageButton imgBtnRemarksItem = convertView.findViewById(R.id.imgBtnRemarksItem);
            btnDelete = convertView.findViewById(R.id.btnDeleteItem);


            tvProductName.setText(itemDetail.productName);
            tvmrp.setText(String.valueOf(itemDetail.MRP));
            tvMrp.setText(String.valueOf(itemDetail.allocStoreCode));
            tvQty.setText(String.valueOf(itemDetail.volDiscPer));
            tvqty.setText(String.valueOf(itemDetail.qty));
            tvRate.setText(String.valueOf(itemDetail.rate));
            tvTotal.setText(String.valueOf(itemDetail.cashDiscPer));


            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage("Do you want to delete the entry ...?");
                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            System.out.println("Clicked Position is " + position);
                            total = total - (itemArraylist.get(position).qty * itemArraylist.get(position).MRP);
                            itemArraylist.remove(position);
                            ((SOActivity) context).detailList=itemArraylist;
                            SOActivityArrayAdapter arrayAdapter = new SOActivityArrayAdapter(context,
                                    R.layout.list_row, listSODetailPL, itemArraylist.size(), itemArraylist, total);
                            ((SOActivity) context).lvProductlist.setAdapter(arrayAdapter);
                            ((SOActivity) context).tvAmountValue.setText(String.format("%.2f", total));
                            arrayAdapter.notifyDataSetChanged();
                            if (itemArraylist.isEmpty())
                                itemArraylist = new ArrayList<AllocateQtyPL>();

                        }
                    });
                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
            });
            tvqty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GetItemDetailsTask().execute();
                    System.out.println("Clicked position is " + position);
                    pos = position;

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;

    }


    @Override
    public int getCount() {
        return size;
    }


    private class AllocateQtyTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Updating details...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONObject appUserPlJsnStr = new JSONObject();
                appUserPlJsnStr.put("qty", selectedQty);
                appUserPlJsnStr.put("itemid", itemDetail.itemId);
                appUserPlJsnStr.put("customer", CustomerId);

                //https://tsmithy.in/somemouat/api/AllocateQty
                URL url = new URL(Url + "AllocateQty");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setRequestProperty("authkey", AppConfigSettings.auth_id);
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                connection.setRequestProperty("machineid",multiSOStoredDevId);
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
                Gson gson = new Gson();
                allocateQty = gson.fromJson(strGetTotal, AllocateQty.class);
                if (allocateQty.statusFlag == 0) {
                    cashDisc.setText("Cash Disc: " + allocateQty.data.cashDiscPer + "%");
                    volDisc.setText("Vol Disc:  " + allocateQty.data.volDiscPer + "%");
                    tvSOH.setText("Rate: " + String.format("%.2f", allocateQty.data.rate));
                    allocStore.setText("Allocated Warehouse: " + allocateQty.data.allocStoreCode);
                    Freeqty.setText("Free Quantity: " + allocateQty.data.freeQty);

                    listSODetailPL.add(allocateQty.data);
                    // detailList.add(allocateQty.data);
                } else {
                    tsMessages(allocateQty.errorMessage);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private void tsMessages(String msg) {

        try {
            final Dialog dialog = new Dialog(context);
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
            Toast.makeText(context, "" + ex, Toast.LENGTH_SHORT).show();
        }

    }

    private class GetItemDetailsTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Loading item details...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                //https://tsmithy.in/somemouat/api/GetSOHAndSchemes?ItemId=14290&CustId=382
                URL url = new URL(Url + "GetSOHAndSchemes?ItemId=" + itemDetail.itemId + "&CustId=" + CustomerId);
                System.out.println(Url + "GetSOHAndSchemes?ItemId=" + itemDetail.itemId + "&CustId=" + CustomerId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setRequestProperty("authkey", AppConfigSettings.auth_id);
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                connection.setRequestProperty("machineid",multiSOStoredDevId);
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
                        System.out.println("Adapter Response is "+ sb);
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


            ItemDetails itemDetails = new ItemDetails();
            if (strGetItemDetail == null || strGetItemDetail.isEmpty()) {
                Toast.makeText(context, "No result from web for item details", Toast.LENGTH_LONG).show();
            } else {
                if (itemDetails.statusFlag == 0) {
                    try {

                        Gson gson = new Gson();
                        soplObj = new SOPL();
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

                    try {
                        qtydialog = new Dialog(context);
                        qtydialog.setContentView(R.layout.quantity_selection_dialogwindow);
                        qtydialog.setCanceledOnTouchOutside(false);
                        qtydialog.setTitle("Quantity Selection");
                        qtydialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                        TextView tvSelectedItemName = qtydialog.findViewById(R.id.tvSelectedItemName);
                        offers = qtydialog.findViewById(R.id.offers);
                        tvMrp = qtydialog.findViewById(R.id.tvMrpInQtySelection);
                        tvSOH = qtydialog.findViewById(R.id.tvSOHInQtySelection);
                        allocStore = qtydialog.findViewById(R.id.allocStore);
                        whText = qtydialog.findViewById(R.id.whText);
                        whSoh = qtydialog.findViewById(R.id.whSoh);
                        Freeqty = qtydialog.findViewById(R.id.Freeqty);

                        current_qty = String.valueOf((int) itemArraylist.get(pos).qty);

                        cashDisc = qtydialog.findViewById(R.id.cashDisc);
                        volDisc = qtydialog.findViewById(R.id.volDisc);

                        ImageButton imgBtnCloseQtySelection = qtydialog.findViewById(R.id.imgBtnCloseQtySelection);
                        ImageButton btnPlus = qtydialog.findViewById(R.id.imgBtnPlusPack);
                        ImageButton btnMinus = qtydialog.findViewById(R.id.imgBtnMinusPack);
                        btnAdd = qtydialog.findViewById(R.id.btnAddItem_qtySelection);
                        calculate = qtydialog.findViewById(R.id.caculate);

                        EditText etQty = qtydialog.findViewById(R.id.etQty);
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(qtydialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.MATCH_PARENT; //Added by Pavithra on 09-11-2020

                        lp.gravity = Gravity.CENTER;
                        qtydialog.getWindow().setAttributes(lp);
                        etQty.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                System.out.println(s.toString());
                                btnAdd.setEnabled(false);
                            }
                        });
                        etQty.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                btnAdd.setEnabled(false);
                                etQty.setSelection(0, etQty.getText().toString().length());

                            }
                        });

                        try {
                            etQty.setText(current_qty);
                            tvSelectedItemName.setText(itemArraylist.get(pos).productName);
                            tvMrp.setText(String.valueOf(itemArraylist.get(pos).MRP));
                            tvSOH.setText("Rate: " + (int) itemArraylist.get(pos).rate);
                            allocStore.setText(offerList.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        selectedQty = Integer.parseInt(etQty.getText().toString());

                        if (selectedQty > 0)
                            new AllocateQtyTask().execute();


                        calculate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                btnAdd.setEnabled(true);
                                selectedQty = Integer.parseInt(etQty.getText().toString());
                                if (selectedQty < 1)
                                    Toast.makeText(context, "Add atleast 1", Toast.LENGTH_LONG).show();
                                else
                                    new ChangeQtyTask().execute();

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
                                    Toast.makeText(context, "" + ex, Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(context, "Qty field is empty", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception ex) {
                                    Toast.makeText(context, "" + ex, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        btnAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    if (etQty.getText().toString().equals("")) {
                                        Toast.makeText(context, "Qty cannot be empty", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    if (Integer.parseInt(etQty.getText().toString()) < 1) {
                                        Toast.makeText(context, "Qty cannot be less than 1", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        ((SOActivity) context).detailList=itemArraylist;
                                        SOActivityArrayAdapter arrayAdapter = new SOActivityArrayAdapter(context,
                                                R.layout.list_row, listSODetailPL, itemArraylist.size(), itemArraylist, total);
                                        ((SOActivity) context).lvProductlist.setAdapter(arrayAdapter);
                                        ((SOActivity) context).tvAmountValue.setText(String.format("%.2f", total));
                                        arrayAdapter.notifyDataSetChanged();
                                        qtydialog.dismiss();

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        qtydialog.show();

                        offers.setText(offerList.toString().replace("[", "").replace("]", ""));

                        StringBuilder builder = new StringBuilder();
                        for (Object details : houseList) {
                            builder.append(details.toString().subSequence(0, 9) + "\n\n");
                        }
                        StringBuilder sbuilder = new StringBuilder();
                        for (Object soh : sohList) {
                            sbuilder.append(soh.toString() + "\n\n");
                        }
                        whText.setText(builder.toString());
                        whSoh.setText(sbuilder
                                .toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(context, itemDetails.errorMessage, Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    private class ChangeQtyTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Updating details...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONObject appUserPlJsnStr = new JSONObject();
                appUserPlJsnStr.put("qty", selectedQty);
                appUserPlJsnStr.put("itemid", itemDetail.itemId);
                appUserPlJsnStr.put("customer", CustomerId);

                //https://tsmithy.in/somemouat/api/AllocateQty
                URL url = new URL(Url + "AllocateQty");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(300000);
                connection.setConnectTimeout(300000);
                connection.setRequestProperty("authkey", AppConfigSettings.auth_id);
                connection.setRequestProperty("name", "");
                connection.setRequestProperty("password", "");
                connection.setRequestProperty("debugkey", "");
                connection.setRequestProperty("remarks", "");
                connection.setRequestProperty("machineid",multiSOStoredDevId);
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
                Gson gson = new Gson();
                Allocateqty = gson.fromJson(strGetTotal, AllocateQty.class);
                System.out.println("Current position is " + pos);

                itemArraylist.get(pos).qty = Allocateqty.data.qty;
                itemArraylist.get(pos).volDiscPer = Allocateqty.data.volDiscPer;
                itemArraylist.get(pos).cashDiscPer = Allocateqty.data.cashDiscPer;
                itemArraylist.get(pos).freeQty = Allocateqty.data.freeQty;
                itemArraylist.get(pos).soh = Allocateqty.data.soh;
                total = 0.0;
                for (int i = 0; i < itemArraylist.size(); i++) {

                    total = total + (itemArraylist.get(i).qty * itemArraylist.get(i).MRP);
                    System.out.println("New total is \t" + i + "-------" + total);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
