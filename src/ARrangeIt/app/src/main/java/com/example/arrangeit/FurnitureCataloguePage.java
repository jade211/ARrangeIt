package com.example.arrangeit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.arrangeit.helpers.FurnitureAdapter;
import com.example.arrangeit.helpers.FurnitureItem;
import android.util.Log;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;


public class FurnitureCataloguePage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FurnitureAdapter furnitureAdapter;
    private List<FurnitureItem> furnitureItems;
    Button homepage_button;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_catalogue);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        homepage_button = findViewById(R.id.homepage_button);
        homepage_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FurnitureCataloguePage.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });
        db = FirebaseFirestore.getInstance();
        furnitureItems = new ArrayList<>();
        loadFurnitureCatalogue();
    }

    private void loadFurnitureCatalogue() {
        db.collection("furniture")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    furnitureItems.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        String description = doc.getString("description");
                        String colours = doc.getString("colours");
                        String texture = doc.getString("texture");
                        String dimensions = doc.getString("dimensions");
                        double price = doc.getDouble("price");
                        String imageUrl = doc.getString("imageUrl");
                        String modelUrl = doc.getString("modelUrl");

                        furnitureItems.add(new FurnitureItem(name, description, price, colours, imageUrl, modelUrl, texture, dimensions));

                    }

                    furnitureAdapter = new FurnitureAdapter(FurnitureCataloguePage.this, furnitureItems);
                    recyclerView.setAdapter(furnitureAdapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FurnitureCataloguePage.this, "Failed to load furniture catalogue", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Error fetching data", e);
                });
    }
}