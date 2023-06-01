package com.example.delivery;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UsersList extends AppCompatActivity {
    private final String URL = Server.ip + "getallusers.php";
    private final String URL2 = Server.ip + "deluser.php";
    public ArrayList<UserProfile> memos;
    String user_id, user_type,op_type;
    ListView simpleList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        op_type = getIntent().getStringExtra("op_type");
        user_type=getIntent().getStringExtra("user_type");
        simpleList = findViewById(R.id.memo_list);
        memos = new ArrayList<>();

        getInfos();
    }

    protected void onResume() {
        super.onResume();
        getInfos();
    }

    private void SendInfo() {
        class RegAsync extends AsyncTask<String, Void, String> {
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(UsersList.this, getResources().getString(R.string.wait), getResources().getString(R.string.connecting));
            }

            @Override
            protected String doInBackground(String... params) {
                Connection con = new Connection();
                HashMap<String, String> data = new HashMap<>();
                data.put("user_id", user_id);
                data.put("user_type", user_type);

                String result = con.sendPostRequest(URL2, data);
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
                loadingDialog = ProgressDialog.show(UsersList.this, "please waite...", "Connecting....");
            }

            @Override
            protected String doInBackground(String... params) {
                Connection con = new Connection();
                HashMap<String, String> data = new HashMap<>();
                data.put("user_type", user_type);

                String result = con.sendPostRequest(URL, data);
                return result.trim();
            }

            @Override
            protected void onPostExecute(String result) {
                loadingDialog.dismiss();
                memos.clear();
                String res1[] = new String[0];
                adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.user_view, R.id.item_n, res1);
                simpleList.setAdapter(adapter);
                adapter.clear();
                adapter.notifyDataSetChanged();
                if (result.isEmpty() || result.equals("Error"))
                    Toast.makeText(getApplicationContext(), "Check connection", Toast.LENGTH_LONG).show();
                else if (result.equals("failure")) {
                    Toast.makeText(getApplicationContext(), "No Infos", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        memos = new ArrayList<>();
                        JSONArray allReq = new JSONArray(result);
                        for (int i = 0; i < allReq.length(); i++) {
                            JSONObject row = allReq.getJSONObject(i);
                            UserProfile temp = new UserProfile();

                            temp.setUsername(row.getString("username"));
                            temp.setFullname(row.getString("fullname"));
                            memos.add(temp);

                        }

                        String res[] = new String[memos.size()];
                        for (int j = 0; j < memos.size(); j++) {
                            res[j] = memos.get(j).getFullname();
                        }
                        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.user_view, R.id.item_n, res);
                        simpleList.setAdapter(adapter);

                        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long idd) {
                                if (op_type.equals("del")) {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(UsersList.this);
                                    alert.setTitle("Deleting a user");
                                    alert.setMessage("Are You sure?");
                                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            user_id = memos.get(position).getUsername();
                                            SendInfo();
                                        }
                                    });

                                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    });
                                    alert.create().show();
                                } else {
                                    Intent intent = new Intent(UsersList.this, Profile.class);
                                    intent.putExtra("id", memos.get(position).getUsername());
                                    intent.putExtra("type", user_type);
                                    startActivity(intent);

                                }
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