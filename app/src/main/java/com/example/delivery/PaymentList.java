package com.example.delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PaymentList extends AppCompatActivity {
    String username,type;

    private final String URL = Server.ip + "getpayments.php";
    ArrayList<Order> records;
    ListView simpleList;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_list);
        username=getIntent().getStringExtra("id");
        type=getIntent().getStringExtra("type");

        simpleList = findViewById(R.id.menu_list);
        records = new ArrayList<>();

        getInfos();
    }

    protected void onResume() {
        super.onResume();
        getInfos();
    }
    private void getInfos() {
        class Async extends AsyncTask<String, Void, String> {
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(PaymentList.this, "please waite...", "Connecting....");
            }

            @Override
            protected String doInBackground(String... params) {
                Connection con = new Connection();
                HashMap<String, String> data = new HashMap<>();
                data.put("username", username);
                data.put("type", type);

                String result = con.sendPostRequest(URL, data);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                loadingDialog.dismiss();
                records.clear();
                String res1[] = new String[0];
                adapter= new ArrayAdapter<>(getApplicationContext(), R.layout.menu_view, R.id.menu_n, res1);
                simpleList.setAdapter(adapter);
                adapter.clear();
                adapter.notifyDataSetChanged();
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

                            Order temp=new Order();
                            temp.setOrder_id(row.getString("order_id"));
                            temp.setFullname(row.getString("fullname"));
                            temp.setCustomer_id(row.getString("customer_id"));
                            temp.setTime(row.getString("time"));
                            temp.setLat(row.getString("lat"));
                            temp.setLon(row.getString("lon"));
                            temp.setStatus(row.getString("status"));
                            temp.setTotal_price(row.getString("total_price"));
                            temp.setDeliver_date(row.getString("deliver_date"));

                            records.add(temp);

                        }

                        String res[] = new String[records.size()];
                        for (int j = 0; j < records.size(); j++) {
                            res[j] =records.get(j).getFullname()+"\n"+records.get(j).getTime();
                        }
                        adapter= new ArrayAdapter<>(getApplicationContext(), R.layout.menu_view, R.id.menu_n,res);
                        simpleList.setAdapter(adapter);
                        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long idd) {

                                Intent intent = new Intent(PaymentList.this, PaymentDetails.class);
                                intent.putExtra("item", records.get(position));
                                startActivity(intent);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Async la = new Async();
        la.execute();
    }
}