package com.example.arrangeit;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.arrangeit.helpers.FurnitureAdapter;
import com.example.arrangeit.helpers.FurnitureItem;
import com.example.arrangeit.helpers.ModelLoader;

import java.util.List;

public class FurnitureCataloguePage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FurnitureAdapter furnitureAdapter;
    private List<FurnitureItem> furnitureItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furniture_catalogue);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        furnitureItems = ModelLoader.loadCatalogue(this);

        if (furnitureItems != null) {
            furnitureAdapter = new FurnitureAdapter(this, furnitureItems);
            recyclerView.setAdapter(furnitureAdapter);
        } else {
            Toast.makeText(this, "Failed to load furniture catalogue", Toast.LENGTH_SHORT).show();
        }
    }
}
