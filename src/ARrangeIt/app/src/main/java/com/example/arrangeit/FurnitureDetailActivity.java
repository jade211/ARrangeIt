package com.example.arrangeit;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.arrangeit.helpers.FurnitureItem;

public class FurnitureDetailActivity extends AppCompatActivity {

    private FurnitureItem furnitureItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_detail);

        ImageView itemImage = findViewById(R.id.itemImage);
        TextView itemName = findViewById(R.id.itemName);
        TextView itemDescription = findViewById(R.id.itemDescription);
        TextView itemPrice = findViewById(R.id.itemPrice);

        FurnitureItem item = (FurnitureItem) getIntent().getSerializableExtra("furniture_item");

        if (item != null) {
            itemName.setText(item.getName());
            itemDescription.setText(item.getDescription());
            itemPrice.setText("$" + item.getPrice());
            Glide.with(this).load("file:///android_asset/" + item.getImage()).into(itemImage);
        }
    }
}