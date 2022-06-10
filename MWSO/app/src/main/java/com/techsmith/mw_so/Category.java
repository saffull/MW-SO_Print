package com.techsmith.mw_so;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Category extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
//        getSupportActionBar().hide();
    }

    public void gotoSalesOrder(View view) {
        finish();
        //startActivity(new Intent(Category.this, CustomerInformation.class));
        startActivity(new Intent(Category.this, SoMenu.class));
    }

    public void gotoDeliveryPage(View view) {
        finish();
        startActivity(new Intent(Category.this, Collections.class));
    }

    public void gotoCollectionPage(View view) {
        finish();
        startActivity(new Intent(Category.this, Delivery.class));
    }
}