package com.example.arrangeit;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.widget.EditText;
import android.widget.Spinner;
import android.text.Editable;

import com.example.arrangeit.helpers.FurnitureAdapter;
import com.example.arrangeit.helpers.FurnitureItem;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FurnitureCatalogueUnitTest {

    @Mock
    private Context mockContext;
    private List<FurnitureItem> testFurnitureItems;
    private FurnitureAdapter adapter;
    private FurnitureCatalogueFragment fragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fragment = new FurnitureCatalogueFragment();
        fragment.furnitureItems = new ArrayList<>();
        fragment.filteredFurnitureItems = new ArrayList<>();
        fragment.db = mock(FirebaseFirestore.class);

        // mock UI
        fragment.searchBar = mock(EditText.class);
        fragment.colourFilterSpinner = mock(Spinner.class);
        fragment.typeFilterSpinner = mock(Spinner.class);

        testFurnitureItems = new ArrayList<>();
        testFurnitureItems.add(new FurnitureItem(
                "Test Chair",
                "Chair",
                "Chair Description",
                99.99,
                "Red",
                "images/chair.png",
                "models/chair.glb",
                "Fabric",
                85.0,
                50.0,
                50.0));

        testFurnitureItems.add(new FurnitureItem(
                "Test Sofa",
                "Sofa",
                "Sofa Description",
                299.99,
                "Blue",
                "images/sofa.png",
                "models/sofa.glb",
                "Leather",
                85.0,
                200.0,
                90.0));

        testFurnitureItems.add(new FurnitureItem(
                "Test Table",
                "Table",
                "Table Description",
                150.0,
                "Brown",
                "images/table1.jpg",
                "models/table1.glb",
                "Wood",
                75.0,
                120.0,
                80.0));

        adapter = new FurnitureAdapter(mockContext, testFurnitureItems);

    }

    @Test
    public void testFurnitureItemsAreLoaded() {
        fragment.furnitureItems.addAll(testFurnitureItems);
        fragment.filteredFurnitureItems.addAll(testFurnitureItems);
        assertEquals(3, fragment.furnitureItems.size());
        assertEquals(3, fragment.filteredFurnitureItems.size());
        assertNotNull(fragment.furnitureItems.get(0));
        assertNotNull(fragment.filteredFurnitureItems.get(1));
    }

    @Test
    public void testFurnitureAdapterItemCount() {
        int itemCount = adapter.getItemCount();
        assertEquals("Adapter should return the correct number of items", 3, itemCount);
    }

    @Test
    public void testFilterLogic() {
        List<FurnitureItem> filteredItems = new ArrayList<>();
        String selectedColour = "Blue";
        String selectedType = "All";
        double maxPrice = 300.0;
        double maxHeight = 90.0;
        double maxWidth = 250.0;
        double maxDepth = 100.0;

        for (FurnitureItem item : testFurnitureItems) {
            boolean matchesColour = selectedColour.equals("All") || item.getColours().equalsIgnoreCase(selectedColour);
            boolean matchesType = selectedType.equals("All") || item.getType().equalsIgnoreCase(selectedType);
            boolean matchesPrice = item.getPrice() <= maxPrice;
            boolean matchesHeight = item.getHeight() <= maxHeight;
            boolean matchesWidth = item.getWidth() <= maxWidth;
            boolean matchesDepth = item.getDepth() <= maxDepth;

            if (matchesColour && matchesType && matchesPrice && matchesHeight && matchesWidth && matchesDepth) {
                filteredItems.add(item);
            }
        }

        assertEquals("Filter should only return blue furniture items", 1, filteredItems.size());
        assertEquals("The filtered item should be the Sofa", "Test Sofa", filteredItems.get(0).getName());
        assertEquals("The filtered item should be blue", "Blue", filteredItems.get(0).getColours());
    }


    @Test
    public void testPriceSortingLogic() {
        List<FurnitureItem> itemsToSort = new ArrayList<>(testFurnitureItems);
        itemsToSort.sort(Comparator.comparingDouble(FurnitureItem::getPrice));
        assertEquals("First item should be the cheapest", "Test Chair", itemsToSort.get(0).getName());
        assertEquals("Last item should be the most expensive", "Test Sofa", itemsToSort.get(2).getName());

        itemsToSort.sort((item1, item2) -> Double.compare(item2.getPrice(), item1.getPrice()));
        assertEquals("First item should be the most expensive", "Test Sofa", itemsToSort.get(0).getName());
        assertEquals("Last item should be the cheapest", "Test Chair", itemsToSort.get(2).getName());
    }


    @Test
    public void testMultipleFilterCriteria() {
        List<FurnitureItem> filteredItems = new ArrayList<>();
        String selectedColour = "All";
        String selectedType = "Chair";
        double maxPrice = 100.0;
        double maxHeight = 100.0;
        double maxWidth = 100.0;
        double maxDepth = 100.0;

        for (FurnitureItem item : testFurnitureItems) {
            boolean matchesColour = selectedColour.equals("All") || item.getColours().equalsIgnoreCase(selectedColour);
            boolean matchesType = selectedType.equals("All") || item.getType().equalsIgnoreCase(selectedType);
            boolean matchesPrice = item.getPrice() <= maxPrice;
            boolean matchesHeight = item.getHeight() <= maxHeight;
            boolean matchesWidth = item.getWidth() <= maxWidth;
            boolean matchesDepth = item.getDepth() <= maxDepth;

            if (matchesColour && matchesType && matchesPrice && matchesHeight && matchesWidth && matchesDepth) {
                filteredItems.add(item);
            }
        }

        assertEquals("Filter should return only chair items within price range", 1, filteredItems.size());
        assertEquals("The filtered item should be the Chair", "Test Chair", filteredItems.get(0).getName());
    }

    @Test
    public void testNoResultsWhenNoItemsMatchFilter() {
        List<FurnitureItem> filteredItems = new ArrayList<>();
        String selectedColour = "Pink";
        String selectedType = "All";
        double maxPrice = 300.0;
        double maxHeight = 100.0;
        double maxWidth = 250.0;
        double maxDepth = 100.0;

        for (FurnitureItem item : testFurnitureItems) {
            boolean matchesColour = selectedColour.equals("All") || item.getColours().equalsIgnoreCase(selectedColour);
            boolean matchesType = selectedType.equals("All") || item.getType().equalsIgnoreCase(selectedType);
            boolean matchesPrice = item.getPrice() <= maxPrice;
            boolean matchesHeight = item.getHeight() <= maxHeight;
            boolean matchesWidth = item.getWidth() <= maxWidth;
            boolean matchesDepth = item.getDepth() <= maxDepth;

            if (matchesColour && matchesType && matchesPrice && matchesHeight && matchesWidth && matchesDepth) {
                filteredItems.add(item);
            }
        }

        assertEquals("Filter should return no items when no matches", 0, filteredItems.size());
    }

    @Test
    public void testDimensionFiltering() {
        List<FurnitureItem> filteredItems = new ArrayList<>();
        String selectedColour = "All";
        String selectedType = "All";
        double maxPrice = 300.0;
        double maxHeight = 80.0;
        double maxWidth = 250.0;
        double maxDepth = 100.0;

        for (FurnitureItem item : testFurnitureItems) {
            boolean matchesColour = selectedColour.equals("All") || item.getColours().equalsIgnoreCase(selectedColour);
            boolean matchesType = selectedType.equals("All") || item.getType().equalsIgnoreCase(selectedType);
            boolean matchesPrice = item.getPrice() <= maxPrice;
            boolean matchesHeight = item.getHeight() <= maxHeight;
            boolean matchesWidth = item.getWidth() <= maxWidth;
            boolean matchesDepth = item.getDepth() <= maxDepth;

            if (matchesColour && matchesType && matchesPrice && matchesHeight && matchesWidth && matchesDepth) {
                filteredItems.add(item);
            }
        }

        assertEquals("Filter should return only items with height <= 80", 1, filteredItems.size());
        assertEquals("The filtered item should be the Table", "Test Table", filteredItems.get(0).getName());
    }


    @Test
    public void testFurnitureItemProperties() {
        FurnitureItem testItem = new FurnitureItem(
                "Test Bed",
                "Bed",
                "Test Bed Description",
                123.45,
                "Green",
                "images/bed_image.png",
                "models/bed_model.glb",
                "Fabric",
                100.0,
                200.0,
                300.0
        );

        assertEquals("Name should match", "Test Bed", testItem.getName());
        assertEquals("Type should match", "Bed", testItem.getType());
        assertEquals("Description should match", "Test Bed Description", testItem.getDescription());
        assertEquals("Price should match", 123.45, testItem.getPrice(), 0.001);
        assertEquals("Color should match", "Green", testItem.getColours());
        assertEquals("Image URL should match", "images/bed_image.png", testItem.getImageUrl());
        assertEquals("Model URL should match", "models/bed_model.glb", testItem.getModelUrl());
        assertEquals("Texture should match", "Fabric", testItem.getTexture());
        assertEquals("Height should match", 100.0, testItem.getHeight(), 0.001);
        assertEquals("Width should match", 200.0, testItem.getWidth(), 0.001);
        assertEquals("Depth should match", 300.0, testItem.getDepth(), 0.001);
    }


    @Test
    public void testSearchFunctionality() {
        fragment.furnitureItems.addAll(testFurnitureItems);
        fragment.filteredFurnitureItems = new ArrayList<>();

        // create mock adapter and editable
        FurnitureAdapter mockAdapter = mock(FurnitureAdapter.class);
        fragment.furnitureAdapter = mockAdapter;
        Editable mockEditable = mock(Editable.class);
        when(mockEditable.toString()).thenReturn("chair");
        when(fragment.searchBar.getText()).thenReturn(mockEditable);
        fragment.performSearch();

        assertEquals("Search should return only chair items", 1, fragment.filteredFurnitureItems.size());
        assertEquals("The filtered item should be the Chair", "Test Chair", fragment.filteredFurnitureItems.get(0).getName());
        verify(mockAdapter).notifyDataSetChanged();
    }

    @Test
    public void testEmptySearchReturnsAllItems() {
        fragment.furnitureItems.addAll(testFurnitureItems);
        fragment.filteredFurnitureItems = new ArrayList<>();

        FurnitureAdapter mockAdapter = mock(FurnitureAdapter.class);
        fragment.furnitureAdapter = mockAdapter;
        Editable mockEditable = mock(Editable.class);
        when(mockEditable.toString()).thenReturn("");
        when(fragment.searchBar.getText()).thenReturn(mockEditable);
        fragment.performSearch();

        assertEquals(3, fragment.filteredFurnitureItems.size());
    }

    @Test
    public void testCaseInsensitiveSearch() {
        fragment.furnitureItems.addAll(testFurnitureItems);
        fragment.filteredFurnitureItems = new ArrayList<>();
        FurnitureAdapter mockAdapter = mock(FurnitureAdapter.class);
        fragment.furnitureAdapter = mockAdapter;
        Editable mockEditable = mock(Editable.class);
        when(mockEditable.toString()).thenReturn("CHAIR");
        when(fragment.searchBar.getText()).thenReturn(mockEditable);
        fragment.performSearch();

        assertEquals(1, fragment.filteredFurnitureItems.size());
        assertEquals("Test Chair", fragment.filteredFurnitureItems.get(0).getName());
    }

}