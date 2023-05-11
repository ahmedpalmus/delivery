package com.example.delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ProductDetails extends AppCompatActivity {
    Product Info;
    TextView title,price,detail;
    ImageView imageView;

    Button edit,order;
String username,type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        username = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");

        Info=(Product) getIntent().getSerializableExtra("item");

        title=findViewById(R.id.item_t);
        price=findViewById(R.id.item_p);
        detail=findViewById(R.id.item_d);
        imageView=findViewById(R.id.item_i);
        edit=findViewById(R.id.edit_item);
        order=findViewById(R.id.order_item);

        if(type.equals("staff")){
            edit.setVisibility(View.VISIBLE);
            order.setVisibility(View.GONE);
        }else{
            edit.setVisibility(View.GONE);
            order.setVisibility(View.VISIBLE);
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetails.this, AddProduct.class);
                intent.putExtra("op_type","edit");
                intent.putExtra("info",Info);
                startActivity(intent);
                finish();
            }
        });

        title.setText(Info.getItem_title());
        price.setText(Info.getPrice());

        detail.setText(Info.getDetail());

        getImage(Info.getImage(), imageView);
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
}
