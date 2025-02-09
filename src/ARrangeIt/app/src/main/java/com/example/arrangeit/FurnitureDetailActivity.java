package com.example.arrangeit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.arrangeit.helpers.FurnitureItem;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FurnitureDetailActivity extends AppCompatActivity {

    private FurnitureItem furnitureItem;
    Button catalogue_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_detail);
        catalogue_button = findViewById(R.id.catalogue_button);
        catalogue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FurnitureDetailActivity.this, FurnitureCataloguePage.class);
                startActivity(intent);
                finish();
            }
        });
        ImageView itemImage = findViewById(R.id.itemImage);
        TextView itemName = findViewById(R.id.itemName);
        TextView itemDescription = findViewById(R.id.itemDescription);
        TextView itemPrice = findViewById(R.id.itemPrice);

        FurnitureItem item = (FurnitureItem) getIntent().getSerializableExtra("furniture_item");

        if (furnitureItem != null) {
            itemName.setText(furnitureItem.getName());
            itemDescription.setText(furnitureItem.getDescription());
            itemPrice.setText("$" + furnitureItem.getPrice());

            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(furnitureItem.getImageUrl());
            Glide.with(this)
                    .load(imageRef)
                    .into(itemImage);
        }
    }
}