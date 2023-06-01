package com.example.delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CustomerPage extends AppCompatActivity {
    String id;
    Button menu,orders,payments,profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_page);
        menu=findViewById(R.id.menu);
        orders=findViewById(R.id.orders);
        payments=findViewById(R.id.payments);
        profile=findViewById(R.id.profile);

        id=getIntent().getStringExtra("id");

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerPage.this,MenuList.class);
                intent.putExtra("id",id);
                intent.putExtra("type","customer");
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerPage.this, Profile.class);
                intent.putExtra("id", id);
                intent.putExtra("type","customer");
                startActivity(intent);
            }
        });
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerPage.this,OrderList.class);
                intent.putExtra("id",id);
                intent.putExtra("type","customer");
                startActivity(intent);
            }
        });
        payments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CustomerPage.this,PaymentList.class);
                intent.putExtra("id",id);
                intent.putExtra("type","customer");
                startActivity(intent);
            }
        });

    }
}