package com.example.arrangeit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    private List<FurnitureItem> filteredFurnitureItems;
    private Spinner colorFilterSpinner;
    private Button applyFilterButton;
    private EditText priceFilterEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_catalogue);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        homepage_button = findViewById(R.id.homepage_button);
        colorFilterSpinner = findViewById(R.id.colorFilterSpinner);
        applyFilterButton = findViewById(R.id.applyFilterButton);
        priceFilterEditText = findViewById(R.id.priceFilterEditText);

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
        filteredFurnitureItems = new ArrayList<>();
        loadFurnitureCatalogue();

        setupColorFilterSpinner();
        applyFilterButton.setOnClickListener(view -> applyFilters());
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
                        double height = doc.getDouble("height");
                        double width = doc.getDouble("width");
                        double depth = doc.getDouble("depth");
                        double price = doc.getDouble("price");
                        String imageUrl = doc.getString("imageUrl");
                        String modelUrl = doc.getString("modelUrl");

                        furnitureItems.add(new FurnitureItem(name, description, price, colours, imageUrl, modelUrl, texture, height, width, depth));
                    }
                    filteredFurnitureItems.addAll(furnitureItems);
                    furnitureAdapter = new FurnitureAdapter(FurnitureCataloguePage.this, filteredFurnitureItems);
                    recyclerView.setAdapter(furnitureAdapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FurnitureCataloguePage.this, "Failed to load furniture catalogue", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Error fetching data", e);
                });
    }


    private void setupColorFilterSpinner() {
        List<String> colors = new ArrayList<>();
        colors.add("All");
        colors.add("Red");
        colors.add("Blue");
        colors.add("Green");
        colors.add("Grey");
        colors.add("Pink");
        colors.add("Brown");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorFilterSpinner.setAdapter(adapter);
    }
    private void applyFilters() {
        String selectedColor = colorFilterSpinner.getSelectedItem().toString();
        String priceText = priceFilterEditText.getText().toString();
        double maxPrice = priceText.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(priceText);
        filteredFurnitureItems.clear();

        for (FurnitureItem item : furnitureItems) {
            boolean matchesColor = selectedColor.equals("All") || item.getColours().equalsIgnoreCase(selectedColor);
            boolean matchesPrice = item.getPrice() <= maxPrice;
            if (matchesColor && matchesPrice) {
                filteredFurnitureItems.add(item);
            }
        }

        furnitureAdapter.notifyDataSetChanged();
    }
}