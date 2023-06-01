package com.example.delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PaymentDetails extends AppCompatActivity {
    Order Info;
    TextView tstudent,ttime, dtime,tprice;
    TableLayout tableLayout;
    double totalPrice = 0.0;
    ArrayList<OrderItem> records;
    String URL2 = Server.ip + "getitems.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        Info = (Order) getIntent().getSerializableExtra("item");
        records = new ArrayList<>();
        tstudent = findViewById(R.id.or_customer);
        ttime = findViewById(R.id.or_date);
        tprice = findViewById(R.id.or_total);
        dtime = findViewById(R.id.del_date);

        tableLayout = findViewById(R.id.items_table);

        tstudent.setText(Info.getFullname());
        ttime.setText(Info.getTime());
        tprice.setText(Info.getTotal_price());
        dtime.setText(Info.getDeliver_date());

        getInfos();
    }


    private void getInfos() {
        class Async extends AsyncTask<String, Void, String> {
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(PaymentDetails.this, "please waite...", "Connecting....");
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
        TableRow tableRow1 = new TableRow(PaymentDetails.this);
        tableRow1.setBackgroundColor(Color.CYAN);

// create two text views
        TextView textView11 = new TextView(PaymentDetails.this);
        textView11.setText("Title");
        textView11.setPadding(20, 20, 20, 20);
        float textSize = 18;
        textView11.setTextSize(textSize);

        TextView textView12 = new TextView(PaymentDetails.this);
        textView12.setText("Price");
        textView12.setPadding(20, 20, 20, 20);
        textView12.setTextSize(textSize);

        TextView textView13 = new TextView(PaymentDetails.this);
        textView13.setText("Quantity");
        textView13.setPadding(20, 20, 20, 20);
        textView13.setTextSize(textSize);

// add the text views to the table row
        tableRow1.addView(textView11);
        tableRow1.addView(textView12);
        tableRow1.addView(textView13);

// add the table row to the table layout
        tableLayout.addView(tableRow1);
        for (int j = 0; j < records.size(); j++) {

            TableRow tableRow = new TableRow(PaymentDetails.this);

// create two text views
            TextView textView1 = new TextView(PaymentDetails.this);
            textView1.setText(records.get(j).getTitle());
            textView1.setPadding(20, 20, 20, 20);
            textView1.setTextSize(textSize);

            TextView textView2 = new TextView(PaymentDetails.this);
            textView2.setText(records.get(j).getPrice());
            textView2.setPadding(20, 20, 20, 20);
            textView2.setTextSize(textSize);

            TextView textView3 = new TextView(PaymentDetails.this);
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

}
