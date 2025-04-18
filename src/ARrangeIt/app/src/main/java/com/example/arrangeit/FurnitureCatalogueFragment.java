package com.example.arrangeit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.Comparator;

public class FurnitureCatalogueFragment extends Fragment {

    private RecyclerView recyclerView;
    FurnitureAdapter furnitureAdapter;
    List<FurnitureItem> furnitureItems;
    List<FurnitureItem> filteredFurnitureItems;
    Spinner colourFilterSpinner;
    Spinner typeFilterSpinner;
    private EditText priceFilterEditText;
    private EditText heightFilterEditText;
    private EditText widthFilterEditText;
    private EditText depthFilterEditText;
    FirebaseFirestore db;
    private ImageButton filterIcon;
    EditText searchBar;
    private Spinner sortByPriceSpinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_furniture_catalogue, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ProgressBar loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        ImageButton closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                getParentFragmentManager().beginTransaction()
                        .remove(FurnitureCatalogueFragment.this)
                        .commit();
            }
            FrameLayout fragmentContainer = getActivity().findViewById(R.id.fragment_container);
            if (fragmentContainer != null) {
                fragmentContainer.setVisibility(View.GONE);
            }
        });


        colourFilterSpinner = view.findViewById(R.id.colourFilterSpinner);
        typeFilterSpinner = view.findViewById(R.id.typeFilterSpinner);
        priceFilterEditText = view.findViewById(R.id.priceFilterEditText);
        heightFilterEditText = view.findViewById(R.id.heightFilterEditText);
        widthFilterEditText = view.findViewById(R.id.widthFilterEditText);
        depthFilterEditText = view.findViewById(R.id.depthFilterEditText);
        sortByPriceSpinner = view.findViewById(R.id.sortByPriceSpinner);


        db = FirebaseFirestore.getInstance();
        furnitureItems = new ArrayList<>();
        filteredFurnitureItems = new ArrayList<>();
        loadingProgressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        loadFurnitureCatalogue();

        setupColourFilterSpinner();
        setupTypeFilterSpinner();
        setupSortByPriceSpinner();

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
                    if (!isAdded() || getView() == null) return;
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


                    ProgressBar loadingProgressBar = getView().findViewById(R.id.loadingProgressBar);
                    if (loadingProgressBar != null) {
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    if (!isAdded() || getView() == null) return;

                    ProgressBar loadingProgressBar = getView().findViewById(R.id.loadingProgressBar);
                    if (loadingProgressBar != null) {
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                    Toast.makeText(getContext(), "Failed to load furniture data", Toast.LENGTH_SHORT).show();
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
        colours.add("Brass");
        colours.add("Copper");
        colours.add("Black");
        colours.add("Teal");
        colours.add("Pink");
        colours.add("White");
        colours.add("Pearl Grey");
        colours.add("Beige");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, colours);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colourFilterSpinner.setAdapter(adapter);
    }

    void setupTypeFilterSpinner() {
        List<String> types = new ArrayList<>();
        types.add("All");
        types.add("Chair");
        types.add("Sofa");
        types.add("Bedside table");
        types.add("Lighting");
        types.add("Storage");
        types.add("Bed");
        types.add("Table");
        types.add("Ottoman");
        types.add("Chest of Drawers");
        types.add("Wardrobe");
        types.add("Armchair");
        types.add("Bar Chair");
        types.add("Bedside Table");
        types.add("Hanging Chair");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeFilterSpinner.setAdapter(adapter);
    }

    void setupSortByPriceSpinner() {
        List<String> sortOptions = new ArrayList<>();
        sortOptions.add("None");
        sortOptions.add("Price: Low to High");
        sortOptions.add("Price: High to Low");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sortOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortByPriceSpinner.setAdapter(adapter);
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
        String sortByPrice = sortByPriceSpinner.getSelectedItem().toString();
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
        if (sortByPrice.equals("Price: Low to High")) {
            filteredFurnitureItems.sort(Comparator.comparingDouble(FurnitureItem::getPrice));
        } else if (sortByPrice.equals("Price: High to Low")) {
            filteredFurnitureItems.sort((item1, item2) -> Double.compare(item2.getPrice(), item1.getPrice()));
        }

        furnitureAdapter.notifyDataSetChanged();
        FlexboxLayout filterOptionsLayout = getView().findViewById(R.id.filterOptionsLayout);
        Button applyFilterButton = getView().findViewById(R.id.applyFilterButton);
        if (filterOptionsLayout != null && applyFilterButton != null) {
            filterOptionsLayout.setVisibility(View.GONE);
            applyFilterButton.setVisibility(View.GONE);
        }
    }
}