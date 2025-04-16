// package com.example.arrangeit;

// import android.content.Intent;
// import android.os.Bundle;
// import android.widget.Toast;

// import androidx.appcompat.app.AppCompatActivity;
// import androidx.recyclerview.widget.LinearLayoutManager;
// import androidx.recyclerview.widget.RecyclerView;

// import com.example.arrangeit.helpers.LayoutsAdapter;
// import com.example.arrangeit.helpers.SavedLayout;
// import com.google.firebase.auth.FirebaseAuth;
// import com.google.firebase.auth.FirebaseUser;
// import com.google.firebase.firestore.FirebaseFirestore;
// import com.google.firebase.firestore.Query;
// import com.google.firebase.firestore.QueryDocumentSnapshot;

// import java.util.ArrayList;
// import java.util.List;

// public class SavedLayoutsActivity extends AppCompatActivity {
//     private RecyclerView layoutsRecyclerView;
//     private LayoutsAdapter layoutsAdapter;
//     private List<SavedLayout> savedLayouts = new ArrayList<>();

//     @Override
//     protected void onCreate(Bundle savedInstanceState) {
//         super.onCreate(savedInstanceState);
//         setContentView(R.layout.activity_saved_layouts);

//         layoutsRecyclerView = findViewById(R.id.layoutsRecyclerView);
//         layoutsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//         layoutsAdapter = new LayoutsAdapter(savedLayouts, this::loadLayout);
//         layoutsRecyclerView.setAdapter(layoutsAdapter);

//         findViewById(R.id.backButton).setOnClickListener(v -> finish());

//         loadSavedLayouts();
//     }

//     private void loadSavedLayouts() {
//         FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//         if (user == null) {
//             finish();
//             return;
//         }

//         FirebaseFirestore.getInstance()
//                 .collection("screenshots")
//                 .whereEqualTo("userId", user.getUid())
//                 .orderBy("timestamp", Query.Direction.DESCENDING)
//                 .get()
//                 .addOnSuccessListener(queryDocumentSnapshots -> {
//                     savedLayouts.clear();
//                     for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                         SavedLayout layout = doc.toObject(SavedLayout.class);
//                         layout.setId(doc.getId());
//                         savedLayouts.add(layout);
//                     }
//                     layoutsAdapter.notifyDataSetChanged();
//                 })
//                 .addOnFailureListener(e -> {
//                     Toast.makeText(this, "Failed to load layouts", Toast.LENGTH_SHORT).show();
//                 });
//     }

//     private void loadLayout(SavedLayout layout) {
//         Intent intent = new Intent(this, ARCorePage.class);
//         intent.putExtra("layoutId", layout.getId());
//         startActivity(intent);
//     }
// }


package com.example.arrangeit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arrangeit.helpers.LayoutsAdapter;
import com.example.arrangeit.helpers.SavedLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SavedLayoutsActivity extends AppCompatActivity {
    private RecyclerView layoutsRecyclerView;
    private LayoutsAdapter layoutsAdapter;
    private List<SavedLayout> savedLayouts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_layouts);

        layoutsRecyclerView = findViewById(R.id.layoutsRecyclerView);
        layoutsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        layoutsAdapter = new LayoutsAdapter(savedLayouts, this::loadLayout);
        layoutsRecyclerView.setAdapter(layoutsAdapter);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        loadSavedLayouts();
    }

    private void loadSavedLayouts() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        // Change from "screenshots" to "savedLayouts" collection
        FirebaseFirestore.getInstance()
                .collection("savedLayouts")
                .whereEqualTo("userId", user.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    savedLayouts.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        SavedLayout layout = doc.toObject(SavedLayout.class);
                        layout.setId(doc.getId());
                        savedLayouts.add(layout);
                    }
                    layoutsAdapter.notifyDataSetChanged();
                    
                    // Show message if no layouts found
                    if (savedLayouts.isEmpty()) {
                        Toast.makeText(this, "No saved layouts found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load layouts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void loadLayout(SavedLayout layout) {
        Intent intent = new Intent(this, ARCorePage.class);
        intent.putExtra("layoutId", layout.getId());
        startActivity(intent);
    }
}