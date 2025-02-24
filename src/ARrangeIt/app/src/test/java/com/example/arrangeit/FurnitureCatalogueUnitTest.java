//package com.example.arrangeit;
//
//import static org.mockito.Mockito.*;
//import static org.junit.Assert.*;
//
//import android.content.Intent;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Spinner;
//
//import com.example.arrangeit.helpers.FurnitureAdapter;
//import com.example.arrangeit.helpers.FurnitureItem;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.robolectric.Robolectric;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.android.controller.ActivityController;
//import org.robolectric.annotation.Config;
//import org.robolectric.shadows.ShadowApplication;
//import org.robolectric.shadows.ShadowLooper;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@RunWith(RobolectricTestRunner.class)
//@Config(application = TestApplication.class)
//public class FurnitureCatalogueUnitTest {
//
//    private FurnitureCataloguePage activity;
//    private ActivityController<FurnitureCataloguePage> controller;
//
//    @Mock
//    private FirebaseFirestore mockFirestore;
//
//    @Mock
//    private CollectionReference mockCollection;
//
//    @Mock
//    private Task<QuerySnapshot> mockTask;
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        // Mock Firestore collection and query
//        when(mockFirestore.collection("furniture")).thenReturn(mockCollection);
//        when(mockCollection.get()).thenReturn(mockTask);
//
//        // Build the activity
//        controller = Robolectric.buildActivity(FurnitureCataloguePage.class);
//        activity = controller.get();
//
//        // Inject the mock FirebaseFirestore instance
//        activity.db = mockFirestore;
//
//        // Initialize the adapter
//        activity.furnitureItems = new ArrayList<>();
//        activity.filteredFurnitureItems = new ArrayList<>();
//        activity.furnitureAdapter = new FurnitureAdapter(activity, activity.filteredFurnitureItems);
//
//        // Start the activity lifecycle
//        controller.create().start().resume();
//    }
//
//    @Test
//    public void testLoadFurnitureCatalogue() {
//        // Call the method under test
//        activity.loadFurnitureCatalogue();
//
//        // Wait for the main looper to idle (if asynchronous)
//        ShadowLooper.idleMainLooper();
//
//        // Verify Firestore query is executed
//        verify(mockFirestore).collection("furniture");
//        verify(mockCollection).get();
//    }
//
//    @Test
//    public void testApplyFilters() {
//        // Set up test data
//        List<FurnitureItem> furnitureItems = new ArrayList<>();
//        furnitureItems.add(new FurnitureItem("Chair", "Chair", "Comfortable chair", 50.0, "Red", "imageUrl", "modelUrl", "Smooth", 50.0, 50.0, 50.0));
//        furnitureItems.add(new FurnitureItem("Table", "Table", "Wooden table", 100.0, "Blue", "imageUrl", "modelUrl", "Rough", 100.0, 100.0, 100.0));
//        activity.furnitureItems = furnitureItems;
//
//        // Set up filter inputs
//        activity.colourFilterSpinner.setSelection(1); // Select "Red"
//        activity.typeFilterSpinner.setSelection(1); // Select "Chair"
//        activity.priceFilterEditText.setText("60");
//        activity.heightFilterEditText.setText("60");
//        activity.widthFilterEditText.setText("60");
//        activity.depthFilterEditText.setText("60");
//
//        // Apply filters
//        activity.applyFilters();
//
//        // Verify filtered list
//        assertEquals(1, activity.filteredFurnitureItems.size());
//        assertEquals("Chair", activity.filteredFurnitureItems.get(0).getName());
//    }
//
//    @Test
//    public void testHomepageButton() {
//        Button homepageButton = activity.findViewById(R.id.homepage_button);
//        homepageButton.performClick();
//
//        // Verify intent is launched
//        Intent expectedIntent = new Intent(activity, HomePage.class);
//        assertNotNull(ShadowApplication.getInstance().getNextStartedActivity());
//    }
//}





//package com.example.arrangeit;
//
//import static org.mockito.Mockito.*;
//import static org.junit.Assert.*;
//
//import android.content.Intent;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Spinner;
//
//import com.example.arrangeit.helpers.FurnitureItem;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.robolectric.Robolectric;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.android.controller.ActivityController;
//import org.robolectric.shadows.ShadowApplication;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@RunWith(RobolectricTestRunner.class)
//public class FurnitureCatalogueUnitTest {
//
//    private FurnitureCataloguePage activity;
//    private ActivityController<FurnitureCataloguePage> controller;
//
//    @Mock
//    private FirebaseFirestore mockFirestore;
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        controller = Robolectric.buildActivity(FurnitureCataloguePage.class);
//        activity = controller.get();
//
//        // Inject mock FirebaseFirestore
//        activity.db = mockFirestore;
//        controller.create().start().resume();
//    }
//
//    @Test
//    public void testLoadFurnitureCatalogue() {
//        // Mock Firestore response
//        when(mockFirestore.collection("furniture").get()).thenReturn(mock(Task.class));
//
//        activity.loadFurnitureCatalogue();
//
//        // Verify Firestore query is executed
//        verify(mockFirestore).collection("furniture").get();
//    }
//
//    @Test
//    public void testApplyFilters() {
//        // Set up test data
//        List<FurnitureItem> furnitureItems = new ArrayList<>();
//        furnitureItems.add(new FurnitureItem("Chair", "Chair", "Comfortable chair", 50.0, "Red", "imageUrl", "modelUrl", "Smooth", 50.0, 50.0, 50.0));
//        furnitureItems.add(new FurnitureItem("Table", "Table", "Wooden table", 100.0, "Blue", "imageUrl", "modelUrl", "Rough", 100.0, 100.0, 100.0));
//        activity.furnitureItems = furnitureItems;
//
//        // Set up filter inputs
//        activity.colourFilterSpinner.setSelection(1); // Select "Red"
//        activity.typeFilterSpinner.setSelection(1); // Select "Chair"
//        activity.priceFilterEditText.setText("60");
//        activity.heightFilterEditText.setText("60");
//        activity.widthFilterEditText.setText("60");
//        activity.depthFilterEditText.setText("60");
//
//        // Apply filters
//        activity.applyFilters();
//
//        // Verify filtered list
//        assertEquals(1, activity.filteredFurnitureItems.size());
//        assertEquals("Chair", activity.filteredFurnitureItems.get(0).getName());
//    }
//
//    @Test
//    public void testSetupColourFilterSpinner() {
//        activity.setupColourFilterSpinner();
//
//        // Verify spinner options
//        ArrayAdapter<String> adapter = (ArrayAdapter<String>) activity.colourFilterSpinner.getAdapter();
//        assertEquals("All", adapter.getItem(0));
//        assertEquals("Red", adapter.getItem(1));
//        assertEquals("Blue", adapter.getItem(2));
//    }
//
//    @Test
//    public void testSetupTypeFilterSpinner() {
//        activity.setupTypeFilterSpinner();
//
//        // Verify spinner options
//        ArrayAdapter<String> adapter = (ArrayAdapter<String>) activity.typeFilterSpinner.getAdapter();
//        assertEquals("All", adapter.getItem(0));
//        assertEquals("Chair", adapter.getItem(1));
//        assertEquals("Table", adapter.getItem(2));
//    }
//
//    @Test
//    public void testHomepageButton() {
//        Button homepageButton = activity.findViewById(R.id.homepage_button);
//        homepageButton.performClick();
//
//        // Verify intent is launched
//        Intent expectedIntent = new Intent(activity, HomePage.class);
//        assertNotNull(ShadowApplication.getInstance().getNextStartedActivity());
//    }
//}





package com.example.arrangeit;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.arrangeit.helpers.FurnitureAdapter;
import com.example.arrangeit.helpers.FurnitureItem;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLooper;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestApplication.class)
public class FurnitureCatalogueUnitTest {

    private FurnitureCataloguePage activity;
    private ActivityController<FurnitureCataloguePage> controller;

    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock
    private CollectionReference mockCollection;

    @Mock
    private Task<QuerySnapshot> mockTask;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock Firestore collection and query
        when(mockFirestore.collection("furniture")).thenReturn(mockCollection);
        when(mockCollection.get()).thenReturn(mockTask);

        // Build the activity
        controller = Robolectric.buildActivity(FurnitureCataloguePage.class, new Intent().putExtra("test", true));
        activity = controller.get();

        // Inject the mock FirebaseFirestore instance
        activity.db = mockFirestore;

        // Initialize the adapter
        activity.furnitureItems = new ArrayList<>();
        activity.filteredFurnitureItems = new ArrayList<>();
        activity.furnitureAdapter = new FurnitureAdapter(activity, activity.filteredFurnitureItems);

        // Start the activity lifecycle
        controller.create().start().resume();
    }

    @Test
    public void testLoadFurnitureCatalogue() {
        // Call the method under test
        activity.loadFurnitureCatalogue();

        // Wait for the main looper to idle (if asynchronous)
        ShadowLooper.idleMainLooper();

        // Verify Firestore query is executed
        verify(mockFirestore).collection("furniture");
        verify(mockCollection).get();
    }

    @Test
    public void testApplyFilters() {
        // Set up test data
        List<FurnitureItem> furnitureItems = new ArrayList<>();
        furnitureItems.add(new FurnitureItem("Chair", "Chair", "Comfortable chair", 50.0, "Red", "imageUrl", "modelUrl", "Smooth", 50.0, 50.0, 50.0));
        furnitureItems.add(new FurnitureItem("Table", "Table", "Wooden table", 100.0, "Blue", "imageUrl", "modelUrl", "Rough", 100.0, 100.0, 100.0));
        activity.furnitureItems = furnitureItems;

        // Set up filter inputs
        activity.colourFilterSpinner.setSelection(1); // Select "Red"
        activity.typeFilterSpinner.setSelection(1); // Select "Chair"
        activity.priceFilterEditText.setText("60");
        activity.heightFilterEditText.setText("60");
        activity.widthFilterEditText.setText("60");
        activity.depthFilterEditText.setText("60");

        // Apply filters
        activity.applyFilters();

        // Verify filtered list
        assertEquals(1, activity.filteredFurnitureItems.size());
        assertEquals("Chair", activity.filteredFurnitureItems.get(0).getName());
    }

    @Test
    public void testHomepageButton() {
        Button homepageButton = activity.findViewById(R.id.homepage_button);
        homepageButton.performClick();

        // Verify intent is launched
        Intent expectedIntent = new Intent(activity, HomePage.class);
        assertNotNull(ShadowApplication.getInstance().getNextStartedActivity());
    }
}