package com.example.delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AgentPage extends AppCompatActivity {
    String id;
    Button orders,history,profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_page);
        orders=findViewById(R.id.orders);
        history=findViewById(R.id.history);
        profile=findViewById(R.id.profile);

        id=getIntent().getStringExtra("id");


        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AgentPage.this,OrderList.class);
                intent.putExtra("id",id);
                intent.putExtra("type","delivery agent");
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AgentPage.this, Profile.class);
                intent.putExtra("id", id);
                intent.putExtra("type", "delivery_agent");

                startActivity(intent);
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AgentPage.this,PaymentList.class);
                intent.putExtra("id",id);
                intent.putExtra("type","delivery_agent");
                startActivity(intent);
            }
        });
    }
}