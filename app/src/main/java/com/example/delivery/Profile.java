package com.example.delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Profile extends AppCompatActivity {

    EditText pass, pass2, perName, eemail, ephone;
    TextView l2, l3, l4, l7, l8;

    String id,type, password , fullname,  email, phone;
    String URL = Server.ip + "edit.php";
    String URL2 = Server.ip + "getuser.php";
    Button reg, cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
        pass = findViewById(R.id.prof_pass);
        pass2 = findViewById(R.id.prof_pass2);
        perName = findViewById(R.id.prof_name);

        eemail = findViewById(R.id.prof_email);
        ephone = findViewById(R.id.prof_phone);

        l2 = findViewById(R.id.lp2);
        l3 = findViewById(R.id.lp3);
        l4 = findViewById(R.id.lp4);

        l7 = findViewById(R.id.lp7);
        l8 = findViewById(R.id.lp8);

        reg = findViewById(R.id.prof_update);
        cancel = findViewById(R.id.prof_cancel);
        reg.setOnClickListener(view -> RegOnclick(view));

        cancel.setOnClickListener(view -> RegOnclick(view));
        getInfos();
    }

    public void RegOnclick(View view) {
        if (view.getId() == R.id.prof_update) {
            l2.setText(getResources().getString(R.string.password));
            l2.setTextColor(Color.BLACK);
            l3.setText(getResources().getString(R.string.conform_pasword));
            l3.setTextColor(Color.BLACK);
            l4.setText(getResources().getString(R.string.pername));
            l4.setTextColor(Color.BLACK);

            l7.setText(getResources().getString(R.string.email));
            l7.setTextColor(Color.BLACK);
            l8.setText(getResources().getString(R.string.email));
            l8.setTextColor(Color.BLACK);

            password = pass.getText().toString().trim();
            String password2 = pass2.getText().toString().trim();
            fullname = perName.getText().toString().trim();
            email = eemail.getText().toString().trim();
            phone = ephone.getText().toString().trim();
            boolean error = false;
            if (password.length() > 0 || password2.length() > 0) {
                if (password.length() < 8) {
                    error = true;
                    l2.setText(getResources().getString(R.string.enterPass));
                    l2.setTextColor(Color.RED);
                }
                if (!isValidPassword(password)) {
                    error = true;
                    l2.setText("The password must contain letters and numbers");
                    l2.setTextColor(Color.RED);
                }
                if (!password.equals(password2)) {
                    error = true;
                    l2.setTextColor(Color.RED);
                    l3.setText(getResources().getString(R.string.enterPass2));
                    l3.setTextColor(Color.RED);
                }
            }else{
                password="no";
            }
            if (fullname.length() < 2) {
                error = true;
                l4.setText(getResources().getString(R.string.enterPersonal));
                l4.setTextColor(Color.RED);
            }

            if (!isValidEmail(email)) {
                error = true;
                l7.setTextColor(Color.RED);
            }
            if (phone.length() != 10 || !phone.startsWith("05")) {
                error = true;
                l8.setTextColor(Color.RED);
            }
            if (!error) {
                Registration();
            }

        } else if (view.getId() == R.id.prof_cancel) {
            finish();
        }

    }

    private void Registration() {
        class RegAsync extends AsyncTask<String, Void, String> {
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(Profile.this, getResources().getString(R.string.wait), getResources().getString(R.string.connecting));
            }

            @Override
            protected String doInBackground(String... params) {
                Connection con = new Connection();
                HashMap<String, String> data = new HashMap<>();
                data.put("id", id);
                data.put("password", password);
                data.put("name", fullname);
                data.put("email", email);
                data.put("phone", phone);
                data.put("type", type);
                String result = con.sendPostRequest(URL, data);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                loadingDialog.dismiss();
                if (result.isEmpty() || result.equals("Error"))
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.check), Toast.LENGTH_LONG).show();
                else if (result.equals("failure")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.noResult), Toast.LENGTH_LONG).show();
                }  else if (result.equals("success")){
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
                loadingDialog = ProgressDialog.show(Profile.this, "please waite...", "Connecting....");
            }

            @Override
            protected String doInBackground(String... params) {
                Connection con = new Connection();
                HashMap<String, String> data = new HashMap<>();
                data.put("id", id);
                data.put("type", type);

                String result = con.sendPostRequest(URL2, data);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                loadingDialog.dismiss();

                if (result.isEmpty() || result.equals("Error"))
                    Toast.makeText(getApplicationContext(), "Check connection", Toast.LENGTH_LONG).show();
                else if (result.equals("failure")) {
                    Toast.makeText(getApplicationContext(), "No results", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        JSONArray allReq = new JSONArray(result);
                        for (int i = 0; i < allReq.length(); i++) {
                            JSONObject row = allReq.getJSONObject(i);
                            perName.setText(row.getString("name"));
                            eemail.setText(row.getString("email"));
                            ephone.setText(row.getString("phone"));
                            break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Async la = new Async();
        la.execute();
    }

    public boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public boolean isValidPassword(String s) {
        String n = ".*[0-9].*";
        String a = ".*[A-Z].*";
        String sa = ".*[a-z].*";
        return (s.matches(n) && s.matches(a)) || (s.matches(n) && s.matches(sa));
    }

}
