package com.example.delivery;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ProductDetails extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    Product Info;
    TextView title, price, detail;
    ImageView imageView;
    Spinner quan;
    Button edit, order;
    String username, type, quantity;
    String URL = Server.ip + "sendorder.php";
    LinearLayout lin1;
    String lat = "", lon = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        username = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");

        Info = (Product) getIntent().getSerializableExtra("item");

        title = findViewById(R.id.item_t);
        price = findViewById(R.id.item_p);
        detail = findViewById(R.id.item_d);
        imageView = findViewById(R.id.item_i);
        edit = findViewById(R.id.edit_item);
        order = findViewById(R.id.order_item);
        quan = findViewById(R.id.item_q);
        lin1 = findViewById(R.id.lin1);

        if (type.equals("staff")) {
            edit.setVisibility(View.VISIBLE);
            order.setVisibility(View.GONE);
            lin1.setVisibility(View.GONE);
        } else {
            edit.setVisibility(View.GONE);
            order.setVisibility(View.VISIBLE);
            lin1.setVisibility(View.VISIBLE);

        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetails.this, AddProduct.class);
                intent.putExtra("op_type", "edit");
                intent.putExtra("info", Info);
                intent.putExtra("id", username);
                startActivity(intent);
                finish();
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = quan.getSelectedItem().toString();
                if (isInteger(quantity)) {
                    checkPermission();
                } else {
                    Toast.makeText(getApplicationContext(), "Enter valid Quantity", Toast.LENGTH_LONG).show();
                }
            }
        });
        title.setText(Info.getItem_title());
        price.setText(Info.getPrice());

        detail.setText(Info.getDetail());

        getImage(Info.getImage(), imageView);
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            getLocation();
            if (lat.length() < 3)
                Toast.makeText(ProductDetails.this, "" +
                        "Try agian to get the location", Toast.LENGTH_LONG).show();
            else {
                SendInfo();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                Toast.makeText(ProductDetails.this, "Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getLocation() {
        lat="18.107809438262883";
        lon="43.1144998134949";
        // Define a LocationListener
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                lat = "" + location.getLatitude();
                lon = "" + location.getLongitude();

                return;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        // Get a reference to the LocationManager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        // or locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    private void SendInfo() {
        class RegAsync extends AsyncTask<String, Void, String> {
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(ProductDetails.this, getResources().getString(R.string.wait), getResources().getString(R.string.connecting));
            }

            @Override
            protected String doInBackground(String... params) {
                Connection con = new Connection();
                HashMap<String, String> data = new HashMap<>();
                data.put("username", username);
                data.put("product", Info.getItem_id());
                data.put("quantity", quantity);
                data.put("lat", lat);
                data.put("lon", lon);

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


    public void getImage(final String img, final ImageView viewHolder) {

        class packTask extends AsyncTask<Void, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(Void... voids) {
                Bitmap image1 = null;
                java.net.URL url = null;
                try {
                    url = new URL(Server.ip + img);
                    image1 = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return image1;
            }

            protected void onPostExecute(Bitmap image) {
                viewHolder.setImageBitmap(image);
            }
        }
        packTask t = new packTask();
        t.execute();
    }

    public boolean isInteger(String str) {
        try {
            int number = Integer.parseInt(str); // try to parse the string as an integer
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
