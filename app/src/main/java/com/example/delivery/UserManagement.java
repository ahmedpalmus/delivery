package com.example.delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserManagement extends AppCompatActivity {
    String username,user_type;
    Button add,update,del;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        username = getIntent().getStringExtra("id");
        user_type = getIntent().getStringExtra("user_type");

        add=findViewById(R.id.add);
        update=findViewById(R.id.update);
        del=findViewById(R.id.delete);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserManagement.this, AddUser.class);
                intent.putExtra("user_type",user_type);
                startActivity(intent);
            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserManagement.this, UsersList.class);
                intent.putExtra("op_type","del");
                intent.putExtra("user_type",user_type);
                startActivity(intent);
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserManagement.this, UsersList.class);
                intent.putExtra("op_type","update");
                intent.putExtra("user_type",user_type);

                startActivity(intent);
            }
        });

    }
}