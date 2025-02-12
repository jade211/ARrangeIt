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
    private Spinner colourFilterSpinner;
    private Spinner typeFilterSpinner;
    private Button applyFilterButton;
    private EditText priceFilterEditText;
    private EditText heightFilterEditText;
    private EditText widthFilterEditText;
    private EditText depthFilterEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_catalogue);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        homepage_button = findViewById(R.id.homepage_button);
        colourFilterSpinner = findViewById(R.id.colourFilterSpinner);
        typeFilterSpinner = findViewById(R.id.typeFilterSpinner);
        applyFilterButton = findViewById(R.id.applyFilterButton);
        priceFilterEditText = findViewById(R.id.priceFilterEditText);
        heightFilterEditText = findViewById(R.id.heightFilterEditText);
        widthFilterEditText = findViewById(R.id.widthFilterEditText);
        depthFilterEditText = findViewById(R.id.depthFilterEditText);

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

        setupColourFilterSpinner();
        setupTypeFilterSpinner();
        applyFilterButton.setOnClickListener(view -> applyFilters());
    }

    private void loadFurnitureCatalogue() {
        db.collection("furniture")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    furnitureItems.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        String type = doc.getString("type");
                        String description = doc.getString("description");
                        String colours = doc.getString("colours");
                        String texture = doc.getString("texture");
                        double height = doc.getDouble("height");
                        double width = doc.getDouble("width");
                        double depth = doc.getDouble("depth");
                        double price = doc.getDouble("price");
                        String imageUrl = doc.getString("imageUrl");
                        String modelUrl = doc.getString("modelUrl");

                        furnitureItems.add(new FurnitureItem(name, type, description, price, colours, imageUrl, modelUrl, texture, height, width, depth));
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


    private void setupColourFilterSpinner() {
        List<String> colours = new ArrayList<>();
        colours.add("All");
        colours.add("Red");
        colours.add("Blue");
        colours.add("Green");
        colours.add("Grey");
        colours.add("Pink");
        colours.add("Brown");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colours);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colourFilterSpinner.setAdapter(adapter);
    }
    private void setupTypeFilterSpinner() {
        List<String> types = new ArrayList<>();
        types.add("All");
        types.add("Chair");
        types.add("Table");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeFilterSpinner.setAdapter(adapter);
    }


    private void applyFilters() {
        String selectedColour = colourFilterSpinner.getSelectedItem().toString();
        String selectedType = typeFilterSpinner.getSelectedItem().toString();
        String priceText = priceFilterEditText.getText().toString();
        double maxPrice = priceText.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(priceText);
        String heightText = heightFilterEditText.getText().toString();
        double maxHeight = heightText.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(heightText);
        String widthText = widthFilterEditText.getText().toString();
        double maxWidth = widthText.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(widthText);
        String depthText = depthFilterEditText.getText().toString();
        double maxDepth = depthText.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(depthText);
        filteredFurnitureItems.clear();

        for (FurnitureItem item : furnitureItems) {
            boolean matchesColour = selectedColour.equals("All") || item.getColours().equalsIgnoreCase(selectedColour);
            boolean matchesType = selectedType.equals("All") || item.getType().equalsIgnoreCase(selectedType);
            boolean matchesPrice = item.getPrice() <= maxPrice;
            boolean matchesHeight = item.getHeight() <= maxHeight;
            boolean matchesWidth = item.getWidth() <= maxWidth;
            boolean matchesDepth = item.getDepth() <= maxDepth;
            if (matchesColour && matchesType && matchesPrice && matchesHeight && matchesWidth && matchesDepth) {
                filteredFurnitureItems.add(item);
            }
        }

        furnitureAdapter.notifyDataSetChanged();
    }
}