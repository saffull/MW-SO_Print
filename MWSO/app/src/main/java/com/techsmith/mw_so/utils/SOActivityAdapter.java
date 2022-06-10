package com.techsmith.mw_so.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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

import com.google.gson.Gson;
import com.techsmith.mw_so.R;
import com.techsmith.mw_so.SOActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class SOActivityAdapter extends ArrayAdapter<String> {

    Context cntext;
    int CustomerId, itemId;
    SharedPreferences prefs;
    String totalData, itemName, Url = "", strGetTotal, strErrorMsg, itemMrp;
    Button calculate;
    Gson gson;
    TextView tvSOH, allocStore;
    ProgressDialog pDialog;
    List<String> offerList;
    int selectedQty, itemQty;
    AllocateQty allocateQty;
    TextView cashDisc, volDisc, Freeqty;
    Dialog qtydialog;
    JSONArray jsonArray;
    List<String> houseList, sohList;

    public SOActivityAdapter(Context context, String strGetTotal, String itemName, List<String> offerList, String itemMrp,
                             List<String> houseList, int itemId, List<String> sohList, JSONArray jsonArray) {
        super(context, R.layout.activity_so);
        this.cntext = context;
        this.totalData = strGetTotal;
        this.itemName = itemName;
        this.offerList = offerList;
        this.itemMrp = itemMrp;
        this.houseList = houseList;
        this.sohList = sohList;
        this.itemId = itemId;
        this.jsonArray = jsonArray;

        prefs = PreferenceManager.getDefaultSharedPreferences(cntext);
        Url = prefs.getString("MultiSOURL", "");
        CustomerId = prefs.getInt("CustomerId", 0);
        System.out.println("Json array coming here is " + jsonArray);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) cntext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gson = new Gson();

        View rowView = inflater.inflate(R.layout.item_lv_saleorder, parent, false);


        TextView tvSlNo = rowView.findViewById(R.id.tvSlNo);
        TextView tvmrp = rowView.findViewById(R.id.tvmrp);
        TextView tvProductName = rowView.findViewById(R.id.tvProductName);
        TextView tvItem = rowView.findViewById(R.id.tvProductName);
        TextView tvMrp = rowView.findViewById(R.id.tvMRP);
        TextView tvRate = rowView.findViewById(R.id.tvRate);
        TextView tvqty = rowView.findViewById(R.id.tvqty);
        tvSOH = rowView.findViewById(R.id.tvSOHInQtySelection);
        TextView tvQty = rowView.findViewById(R.id.tvQty);
        TextView tvFreeQty = rowView.findViewById(R.id.tvFreeQty);
        TextView tvTotal = rowView.findViewById(R.id.tvTotal);
        final ImageButton imgBtnRemarksItem = rowView.findViewById(R.id.imgBtnRemarksItem);
        ImageButton btnDelete = rowView.findViewById(R.id.btnDeleteItem);

        allocateQty = gson.fromJson(totalData, AllocateQty.class);
        tvMrp.setText(allocateQty.data.allocStoreCode);
        try {
            tvProductName.setText(String.valueOf(allocateQty.data.itemId));
            tvRate.setText(String.valueOf(allocateQty.data.qty));
            tvQty.setText(String.valueOf(allocateQty.data.freeQty));
            tvFreeQty.setText(String.valueOf(allocateQty.data.volDiscPer));
            tvmrp.setText(String.valueOf(itemMrp));
            tvTotal.setText(String.valueOf(allocateQty.data.cashDiscPer));
            tvqty.setText(String.valueOf((int) allocateQty.data.qty));
        } catch (Exception e) {
            e.printStackTrace();
        }

     /*   btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Delete CLicked");
                //showDeletePopUP(position);
            }
        });*/
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(position);
                qtydialog = new Dialog(cntext);
                qtydialog.setContentView(R.layout.quantity_selection_dialogwindow);
                qtydialog.setCanceledOnTouchOutside(false);
                qtydialog.setTitle("Quantity Selection");
                qtydialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                TextView tvSelectedItemName = qtydialog.findViewById(R.id.tvSelectedItemName);
                TextView offers = qtydialog.findViewById(R.id.offers);
                TextView tvMrp = qtydialog.findViewById(R.id.tvMrpInQtySelection);
                tvSOH = qtydialog.findViewById(R.id.tvSOHInQtySelection);
                allocStore = qtydialog.findViewById(R.id.allocStore);
                TextView whText = qtydialog.findViewById(R.id.whText);
                TextView whSoh = qtydialog.findViewById(R.id.whSoh);
                TextView Freeqty = qtydialog.findViewById(R.id.Freeqty);

                cashDisc = qtydialog.findViewById(R.id.cashDisc);
                volDisc = qtydialog.findViewById(R.id.volDisc);

                ImageButton imgBtnCloseQtySelection = qtydialog.findViewById(R.id.imgBtnCloseQtySelection);
                ImageButton btnPlus = qtydialog.findViewById(R.id.imgBtnPlusPack);
                ImageButton btnMinus = qtydialog.findViewById(R.id.imgBtnMinusPack);
                Button btnAdd = qtydialog.findViewById(R.id.btnAddItem_qtySelection);
                calculate = qtydialog.findViewById(R.id.caculate);

                EditText etQty = qtydialog.findViewById(R.id.etQty);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(qtydialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT; //Added by Pavithra on 09-11-2020

                lp.gravity = Gravity.CENTER;
                qtydialog.getWindow().setAttributes(lp);

                try {

                    tvSelectedItemName.setText("" + itemName);
                    tvMrp.setText("MRP : " + itemMrp);

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
                    allocStore.setText(allocateQty.data.allocStoreCode);
                    cashDisc.setText("Cash Disc - " + allocateQty.data.cashDiscPer + "%");
                    volDisc.setText("Vol Disc - " + allocateQty.data.volDiscPer + "%");
                    Freeqty.setText("Free Quantity: " + allocateQty.data.freeQty);
                    etQty.setText(String.valueOf((int) allocateQty.data.qty));
                    itemId = allocateQty.data.itemId;
                    offers.setText(offerList.toString().replace("[", "").replace("]", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                calculate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnAdd.setEnabled(true);
                        selectedQty = Integer.parseInt(etQty.getText().toString());
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
                            Toast.makeText(cntext, "" + ex, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(cntext, "Qty field is empty", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(cntext, "" + ex, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (etQty.getText().toString().equals("")) {
                                Toast.makeText(cntext, "Qty cannot be empty", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (Integer.parseInt(etQty.getText().toString()) < 1) {
                                Toast.makeText(cntext, "Qty cannot be less than 1", Toast.LENGTH_SHORT).show();
                                return;
                            } else {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                qtydialog.show();
            }
        });

        return rowView;
    }


    @Override
    public int getCount() {
        return jsonArray.length();
    }

    private class AllocateQtyTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(cntext);
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
                Gson gson = new Gson();
                allocateQty = gson.fromJson(strGetTotal, AllocateQty.class);
                cashDisc.setText("Cash Disc - " + allocateQty.data.cashDiscPer + "%");
                volDisc.setText("Vol Disc - " + allocateQty.data.volDiscPer + "%");
                tvSOH.setText("Rate : " + String.format("%.2f", allocateQty.data.rate));
                allocStore.setText("Allocated Warehouse : " + allocateQty.data.allocStoreCode);
                itemId = allocateQty.data.itemId;


                SOActivityAdapter productListActivityAdapter = new SOActivityAdapter(cntext,
                        strGetTotal, itemName, offerList, itemMrp, houseList, itemId, sohList, jsonArray);
                ((SOActivity) cntext).lvProductlist.setAdapter(productListActivityAdapter);

                qtydialog.dismiss();
                // acvItemSearchSOActivity.setText("");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
