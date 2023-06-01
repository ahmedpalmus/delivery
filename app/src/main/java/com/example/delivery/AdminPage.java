package com.example.delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminPage extends AppCompatActivity {
    String id;
    Button staff,agents,profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        staff=findViewById(R.id.staff);
        agents=findViewById(R.id.agents);
        profile=findViewById(R.id.profile);

        id=getIntent().getStringExtra("id");

        staff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminPage.this,UserManagement.class);
                intent.putExtra("id",id);
                intent.putExtra("user_type","staff");
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminPage.this, Profile.class);
                intent.putExtra("id", id);
                intent.putExtra("type", "admin");

                startActivity(intent);
            }
        });
        agents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminPage.this, UserManagement.class);
                intent.putExtra("id", id);
                intent.putExtra("user_type", "delivery_agent");

                startActivity(intent);
            }
        });

    }
}