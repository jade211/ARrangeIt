package com.example.arrangeit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.arrangeit.helpers.FurnitureItem;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FurnitureDetailActivity extends AppCompatActivity {

    private ImageView itemImage;
    private TextView itemName;
    private TextView itemDescription;
    private TextView itemPrice;
    private TextView itemHeight;
    private TextView itemWidth;
    private TextView itemDepth;
    private TextView itemColours;
    private TextView itemTexture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_detail);
        View catalogue_button = findViewById(R.id.catalogue_button);
        catalogue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FurnitureDetailActivity.this, FurnitureCataloguePage.class);
                startActivity(intent);
                finish();
            }
        });

        itemImage = findViewById(R.id.itemImage);
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        itemPrice = findViewById(R.id.itemPrice);
        itemHeight = findViewById(R.id.itemHeight);
        itemWidth = findViewById(R.id.itemWidth);
        itemDepth = findViewById(R.id.itemDepth);
        itemColours = findViewById(R.id.itemColours);
        itemTexture = findViewById(R.id.itemTexture);

        FurnitureItem item = (FurnitureItem) getIntent().getSerializableExtra("furniture_item");

        if (item != null) {
            itemName.setText(item.getName());
            itemDescription.setText(item.getDescription());
            itemPrice.setText("$" + item.getPrice());
            itemHeight.setText("Height: " + item.getHeight() + "cm");
            itemWidth.setText("Width: " + item.getWidth() + "cm");
            itemDepth.setText("Depth: " + item.getDepth() + "cm");
            itemColours.setText("Colours: " + item.getColours());
            itemTexture.setText("Texture: " + item.getTexture());

            StorageReference imageRef = FirebaseStorage.getInstance().getReference(item.getImageUrl());
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(this).load(uri).into(itemImage);
            }).addOnFailureListener(exception -> {
            });
        }
    }
}
