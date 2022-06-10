package com.techsmith.mw_so.collection_utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.techsmith.mw_so.Collections;
import com.techsmith.mw_so.R;
import com.techsmith.mw_so.SOActivity;
import com.techsmith.mw_so.utils.AllocateQtyPL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SOCollectionAdapter extends ArrayAdapter {

    public Double total = 0.0;
    Boolean autoFlag = false;
    ArrayList<CollectionPL> itemArraylist;
    Context context;
    TextView due, billno, count;
    EditText pay;
    CheckBox select;
    CollectionPL collectionPL;
    SOCollectionAdapter arrayAdapter;
    Button btnOk;
    String[] vTmp;
    ArrayList<EditText> edt;
    ArrayList<CheckBox> chk;

    public SOCollectionAdapter(@NonNull Context context, int resource, ArrayList<CollectionPL> detailList) {
        super(context, resource);
        this.context = context;
        this.itemArraylist = detailList;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //View convertView = null;
        try {

            //  if (convertView==null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.collection_row_layout, parent, false);
            collectionPL = itemArraylist.get(position);
            final int edPos = position;

            convertView.setTag(String.valueOf(position));
            due = convertView.findViewById(R.id.due);
            billno = convertView.findViewById(R.id.billno);
            pay = convertView.findViewById(R.id.pay);
            select = convertView.findViewById(R.id.select);
            btnOk = convertView.findViewById(R.id.btnOK);

            btnOk.setTag(R.id.key_col_edt_ramt, pay);
            select.setTag(R.id.key_col_edt_ramt, pay);

            due.setText(String.valueOf(collectionPL.Balance));
            billno.setText(collectionPL.DocNo);
            pay.setText(collectionPL.ReceivedAmt);

//1635951471998
            try {

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button btnClicked = (Button) v;
                        EditText edtCurrentRAmt = (EditText) (btnClicked.getTag(R.id.key_col_edt_ramt));
                        if (edtCurrentRAmt.getText().toString().trim().length() > 0) {
                            System.out.println("data there");
                        }
                        edtCurrentRAmt.setText(String.valueOf(itemArraylist.get(edPos).Balance));

                        //itemArraylist.get(edPos).ReceivedAmt = String.valueOf(itemArraylist.get(edPos).Balance);
                        //itemArraylist.get(edPos).select = true;

                        //Intent intent = new Intent("custom-message");
                        //System.out.println("sended value is " + edPos);
                        // intent.putExtra("pos", edPos);
                        //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });
                pay.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {


                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        System.out.println("String Length is " + s.toString());
                        itemArraylist.get(edPos).ReceivedAmt = "";
                        itemArraylist.get(edPos).ReceivedAmt = s.toString();

                        //((Collections) context).detailList = itemArraylist;

                        if (s.toString().length() == 0) {
                            ((Collections) context).vTmp[edPos] = "0";
                            //vTmp[edPos] = "";
                            //((Collections) context).vTmp = vTmp;
                            //((Collections) context).detailList = itemArraylist;

                        } else {
                            ((Collections) context).vTmp[edPos] = s.toString();
                            // vTmp[edPos] = "";
                            //vTmp[edPos] = s.toString();
                            //((Collections) context).vTmp = vTmp;
                            // ((Collections) context).detailList = itemArraylist;
                        }

                        Intent intent = new Intent("custom-total");
                        intent.putExtra("command", "ok");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });

                select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (buttonView.isChecked()) {
                            try {
                                CheckBox btnClicked = (CheckBox) buttonView;
                                EditText edtCurrentRAmt = (EditText) (btnClicked.getTag(R.id.key_col_edt_ramt));
                                if (edtCurrentRAmt.getText().toString().trim().length() > 0) {
                                    System.out.println("data there");
                                }
                                edtCurrentRAmt.setText(String.valueOf(itemArraylist.get(edPos).Balance));
                                edtCurrentRAmt.setEnabled(false);
                                itemArraylist.get(edPos).ReceivedAmt = String.valueOf(itemArraylist.get(edPos).Balance);


                                ((Collections) context).vTmp[edPos] = String.valueOf(itemArraylist.get(edPos).Balance);
                                // edt.get(edPos).setText(String.valueOf(itemArraylist.get(edPos).Balance));
                                //System.out.println("Pushed amount is " + Arrays.toString(vTmp));
                                //edt.get(edPos).setEnabled(false);
//                              ((Collections) context).vTmp = vTmp;
                                //((Collections) context).detailList = itemArraylist;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else {
                            try {
                                CheckBox btnClicked = (CheckBox) buttonView;
                                EditText edtCurrentRAmt = (EditText) (btnClicked.getTag(R.id.key_col_edt_ramt));
                                if (edtCurrentRAmt.getText().toString().trim().length() > 0) {
                                    edtCurrentRAmt.setText("");
                                    edtCurrentRAmt.setEnabled(true);
                                }
                                itemArraylist.get(edPos).ReceivedAmt = "";
                                ((Collections) context).vTmp[edPos] = "0";
                                // ((Collections) context).vTmp = vTmp;
                                // edt.get(edPos).setText("");
                                // edt.get(edPos).setEnabled(true);
                                // ((Collections) context).detailList = itemArraylist;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                      /*  try {
                            ((Collections) context).detailList = itemArraylist;
                            arrayAdapter = new SOCollectionAdapter(context, R.layout.list_row, itemArraylist);
                            ((Collections) context).lvCollectionlist.setAdapter(arrayAdapter);
                             arrayAdapter.notifyDataSetChanged();


                            Intent intent = new Intent("custom-message");
                            System.out.println("sended value is " + edPos);
                            intent.putExtra("pos", edPos);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/


                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }


    @Override
    public int getCount() {
        return itemArraylist.size();
    }

    public BroadcastReceiver mEditReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                int command = intent.getIntExtra("command", -1);
                for (int i = 0; i < chk.size(); i++) {
                    if (chk.get(i).isChecked() && !edt.get(i).getText().toString().isEmpty())
                        edt.get(command).setText(String.valueOf(itemArraylist.get(command).Balance));
                    else
                        System.out.println("Do nothing");

                }


            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    };


}
/* ((Collections) context).detailList = itemArraylist;
                            Intent intent = new Intent("custom-total");
                            intent.putExtra("command", "ok");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);*/

   /*((Collections) context).detailList = itemArraylist;
                        arrayAdapter = new SOCollectionAdapter(context, R.layout.list_row, itemArraylist,autoFlag);
                        ((Collections) context).lvCollectionlist.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();

                          final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                }
                            }, 500);


                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (edt.get(edPos).getText().toString().isEmpty())
                                        edt.get(edPos).setText(vTmp[edPos]);
                                    System.out.println("Inside this..!!");
                                }
                            }, 500);



                        */