package com.example.delivery;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderDetails extends AppCompatActivity {
    Order Info;
    TextView tstudent,ttime, tprice, tstatus;
    Button prepare,ready,start_deliver,delivered,cancel,map;
    String username, type,action;
    String URL = Server.ip + "update_order.php";
    String URL2 = Server.ip + "getitems.php";
    TableLayout tableLayout;
    double totalPrice = 0.0;
    ArrayList<OrderItem> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        username = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");

        Info = (Order) getIntent().getSerializableExtra("item");
        records = new ArrayList<>();
        tstudent = findViewById(R.id.or_customer);
        ttime = findViewById(R.id.or_date);
        tprice = findViewById(R.id.or_total);
        tstatus = findViewById(R.id.or_status);
        prepare = findViewById(R.id.prof_start);
        ready = findViewById(R.id.prof_ready);
        start_deliver = findViewById(R.id.prof_startd);
        delivered = findViewById(R.id.prof_delivered);
        cancel = findViewById(R.id.prof_cancel);
        map = findViewById(R.id.prof_map);
        tableLayout = findViewById(R.id.items_table);

        if (type.equals("staff") ) {
            if(Info.getStatus()=="New order") {
                prepare.setVisibility(View.VISIBLE);
                ready.setVisibility(View.GONE);
                start_deliver.setVisibility(View.GONE);
                delivered.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
            }if(Info.getStatus()=="Working on it...") {
                prepare.setVisibility(View.GONE);
                ready.setVisibility(View.VISIBLE);
                start_deliver.setVisibility(View.GONE);
                delivered.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
            }else{
                prepare.setVisibility(View.GONE);
                ready.setVisibility(View.GONE);
                start_deliver.setVisibility(View.GONE);
                delivered.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
            }
        }else  if (type.equals("delivery agent") ) {
            if(Info.getStatus()=="Ready") {
                prepare.setVisibility(View.GONE);
                ready.setVisibility(View.GONE);
                start_deliver.setVisibility(View.VISIBLE);
                delivered.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
            }if(Info.getStatus()=="On the way...") {
                prepare.setVisibility(View.GONE);
                ready.setVisibility(View.GONE);
                start_deliver.setVisibility(View.GONE);
                delivered.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
            }else{
                prepare.setVisibility(View.GONE);
                ready.setVisibility(View.GONE);
                start_deliver.setVisibility(View.GONE);
                delivered.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
            }
        }else  if (type.equals("customer") ) {
            if(Info.getStatus()=="New order") {
                prepare.setVisibility(View.GONE);
                ready.setVisibility(View.GONE);
                start_deliver.setVisibility(View.GONE);
                delivered.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
            }else{
                prepare.setVisibility(View.GONE);
                ready.setVisibility(View.GONE);
                start_deliver.setVisibility(View.GONE);
                delivered.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
            }
        }
        prepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action="Working on it...";
                SendInfo();
            }
        });
        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action="Ready";
                SendInfo();
            }
        });
        start_deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action="On the way...";
                SendInfo();
            }
        });
        delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action="Delivered";
                SendInfo();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action="cancel";
                SendInfo();
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OpenMap(Info.getLat(),Info.getLon());
            }
        });
        tstudent.setText(Info.getFullname());
        ttime.setText(Info.getTime());
        tprice.setText(Info.getTotal_price());
        tstatus.setText(Info.getStatus());

getInfos();
    }

    private void SendInfo() {
        class RegAsync extends AsyncTask<String, Void, String> {
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(OrderDetails.this, getResources().getString(R.string.wait), getResources().getString(R.string.connecting));
            }

            @Override
            protected String doInBackground(String... params) {
                Connection con = new Connection();
                HashMap<String, String> data = new HashMap<>();
                data.put("username", username);
                data.put("order", Info.getOrder_id());
                data.put("status", action);

                String result = con.sendPostRequest(URL, data);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                loadingDialog.dismiss();
                if (result.isEmpty() || result.equals("Error"))
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.check), Toast.LENGTH_LONG).show();
                else if (result.equals("failure")) {
                    Toast.makeText(getApplicationContext(), "Try Again", Toast.LENGTH_LONG).show();
                } else if (result.equalsIgnoreCase("success")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.success), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
        RegAsync la = new RegAsync();
        la.execute();
    }

    private void getInfos() {
        class Async extends AsyncTask<String, Void, String> {
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(OrderDetails.this, "please waite...", "Connecting....");
            }

            @Override
            protected String doInBackground(String... params) {
                Connection con = new Connection();
                HashMap<String, String> data = new HashMap<>();
                data.put("order_id", Info.getOrder_id());

                String result = con.sendPostRequest(URL2, data);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                loadingDialog.dismiss();
                records.clear();

                if (result.isEmpty() || result.equals("Error"))
                    Toast.makeText(getApplicationContext(), "Check connection", Toast.LENGTH_LONG).show();
                else if (result.equals("failure")) {
                    Toast.makeText(getApplicationContext(), "try again", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        records = new ArrayList<>();
                        JSONArray allReq = new JSONArray(result);
                        for (int i = 0; i < allReq.length(); i++) {
                            JSONObject row = allReq.getJSONObject(i);

                            OrderItem temp=new OrderItem();
                            temp.setTitle(row.getString("title"));
                            temp.setQuantity(row.getString("quantity"));
                            temp.setPrice(row.getString("price"));


                            records.add(temp);

                        }

                        GetPrice();
                        SetItems();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Async la = new Async();
        la.execute();
    }


    public void GetPrice(){
        for (OrderItem product : records) {

            totalPrice += Double.parseDouble(product.getPrice()) * Double.parseDouble(product.getQuantity());
        }
    }
    public void SetItems() {
        tableLayout.removeAllViews();
        for (int j = 0; j < records.size(); j++) {

            TableRow tableRow = new TableRow(OrderDetails.this);

// create two text views
            TextView textView1 = new TextView(OrderDetails.this);
            textView1.setText(records.get(j).getTitle());
            textView1.setPadding(20, 20, 20, 20);
            float textSize = 18;
            textView1.setTextSize(textSize);

            TextView textView2 = new TextView(OrderDetails.this);
            textView2.setText(records.get(j).getPrice());
            textView2.setPadding(20, 20, 20, 20);
            textView2.setTextSize(textSize);

            TextView textView3 = new TextView(OrderDetails.this);
            textView3.setText(records.get(j).getQuantity());
            textView3.setPadding(20, 20, 20, 20);
            textView3.setTextSize(textSize);

// add the text views to the table row
            tableRow.addView(textView1);
            tableRow.addView(textView2);
            tableRow.addView(textView3);

// add the table row to the table layout
            tableLayout.addView(tableRow);
        }


        tprice.setText(totalPrice+"");
    }
    public void OpenMap(String lat,String lon){

    double latitude = Double.parseDouble(lat); // The latitude value
    double longitude = Double.parseDouble(lon); // The longitude value
        String uri = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
   /* // Create a Uri object with the coordinates and marker
    Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Customer Location)");

    // Create an Intent with the action and Uri
    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

    // Set the package to the Google Maps app
    mapIntent.setPackage("com.google.android.apps.maps");

    // Verify if there is an app available to handle the Intent
    if (mapIntent.resolveActivity(getPackageManager()) != null) {
        // Start the activity
        startActivity(mapIntent);
    }*/
}
}
