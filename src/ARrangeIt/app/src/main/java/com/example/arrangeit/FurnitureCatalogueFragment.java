package com.example.arrangeit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.arrangeit.helpers.FurnitureAdapter;
import com.example.arrangeit.helpers.FurnitureItem;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class FurnitureCatalogueFragment extends Fragment {

    private RecyclerView recyclerView;
    private FurnitureAdapter furnitureAdapter;
    private List<FurnitureItem> furnitureItems;
    private List<FurnitureItem> filteredFurnitureItems;
    private Spinner colourFilterSpinner;
    private Spinner typeFilterSpinner;
    private EditText priceFilterEditText;
    private EditText heightFilterEditText;
    private EditText widthFilterEditText;
    private EditText depthFilterEditText;
    private FirebaseFirestore db;
    private ImageButton filterIcon; // New: Filter icon
    private EditText searchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_furniture_catalogue, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        colourFilterSpinner = view.findViewById(R.id.colourFilterSpinner);
        typeFilterSpinner = view.findViewById(R.id.typeFilterSpinner);
        priceFilterEditText = view.findViewById(R.id.priceFilterEditText);
        heightFilterEditText = view.findViewById(R.id.heightFilterEditText);
        widthFilterEditText = view.findViewById(R.id.widthFilterEditText);
        depthFilterEditText = view.findViewById(R.id.depthFilterEditText);

        db = FirebaseFirestore.getInstance();
        furnitureItems = new ArrayList<>();
        filteredFurnitureItems = new ArrayList<>();
        loadFurnitureCatalogue();

        setupColourFilterSpinner();
        setupTypeFilterSpinner();

        searchBar = view.findViewById(R.id.searchBar);
        filterIcon = view.findViewById(R.id.filterIcon);

        FlexboxLayout filterOptionsLayout = view.findViewById(R.id.filterOptionsLayout);
        Button applyFilterButton = view.findViewById(R.id.applyFilterButton);

        filterIcon.setOnClickListener(v -> {
            if (filterOptionsLayout.getVisibility() == View.GONE) {
                filterOptionsLayout.setVisibility(View.VISIBLE);
                applyFilterButton.setVisibility(View.VISIBLE);
            } else {
                filterOptionsLayout.setVisibility(View.GONE);
                applyFilterButton.setVisibility(View.GONE);
            }
        });
        applyFilterButton.setOnClickListener(v -> applyFilters());

        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        return view;
    }
    void performSearch() {
        String query = searchBar.getText().toString().toLowerCase().trim();
        filteredFurnitureItems.clear();
        for (FurnitureItem item : furnitureItems) {
            if (item.getName().toLowerCase().contains(query)) {
                filteredFurnitureItems.add(item);
            }
        }
        furnitureAdapter.notifyDataSetChanged();
    }

    void loadFurnitureCatalogue() {
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
                    furnitureAdapter = new FurnitureAdapter(getContext(), filteredFurnitureItems);
                    recyclerView.setAdapter(furnitureAdapter);
                })
                .addOnFailureListener(e -> {
                });
    }

    void setupColourFilterSpinner() {
        List<String> colours = new ArrayList<>();
        colours.add("All");
        colours.add("Red");
        colours.add("Blue");
        colours.add("Green");
        colours.add("Grey");
        colours.add("Pink");
        colours.add("Brown");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, colours);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colourFilterSpinner.setAdapter(adapter);
    }

    void setupTypeFilterSpinner() {
        List<String> types = new ArrayList<>();
        types.add("All");
        types.add("Chair");
        types.add("Table");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeFilterSpinner.setAdapter(adapter);
    }

    void applyFilters() {
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