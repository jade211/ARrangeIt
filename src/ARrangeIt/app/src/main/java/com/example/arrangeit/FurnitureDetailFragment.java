package com.example.arrangeit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.arrangeit.helpers.FurnitureItem;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Fragment that displays detailed information about a furniture item
 * and provides option to place it in AR view
 */
public class FurnitureDetailFragment extends Fragment {

    ImageView itemImage;
    TextView itemName;
    TextView itemDescription;
    TextView itemPrice;
    TextView itemHeight;
    TextView itemWidth;
    TextView itemDepth;
    TextView itemColours;
    TextView itemTexture;

    FurnitureItem furnitureItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_furniture_detail, container, false);

        itemImage = view.findViewById(R.id.itemImage);
        itemName = view.findViewById(R.id.itemName);
        itemDescription = view.findViewById(R.id.itemDescription);
        itemPrice = view.findViewById(R.id.itemPrice);
        itemHeight = view.findViewById(R.id.itemHeight);
        itemWidth = view.findViewById(R.id.itemWidth);
        itemDepth = view.findViewById(R.id.itemDepth);
        itemColours = view.findViewById(R.id.itemColours);
        itemTexture = view.findViewById(R.id.itemTexture);

        // Set up the "Place in AR" button click listener
        Button placeInArButton = view.findViewById(R.id.place_in_ar_button);
        placeInArButton.setOnClickListener(v -> {
            if (furnitureItem != null && furnitureItem.getModelUrl() != null) {
                ARCorePage activity = (ARCorePage) getActivity();
                if (activity != null) {
                    activity.setCurrentModelName(furnitureItem.getName());
                    activity.loadModelFromFirebase(furnitureItem.getModelUrl());
        
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.remove(FurnitureDetailFragment.this);
                    transaction.commit();
                }
            } else {
                Toast.makeText(getContext(), "3D model not available for this item", Toast.LENGTH_SHORT).show();
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            furnitureItem = (FurnitureItem) args.getSerializable("furniture_item");
            
            if (furnitureItem != null) {
                // Populate UI with furniture item data
                itemName.setText(furnitureItem.getName());
                itemDescription.setText(furnitureItem.getDescription());
                itemPrice.setText("€" + furnitureItem.getPrice());
                itemHeight.setText("Height: " + furnitureItem.getHeight() + "cm");
                itemWidth.setText("Width: " + furnitureItem.getWidth() + "cm");
                itemDepth.setText("Depth: " + furnitureItem.getDepth() + "cm");
                itemColours.setText(furnitureItem.getColours());
                itemTexture.setText(furnitureItem.getTexture());

                // Load image from Firebase Storage using Glide
                StorageReference imageRef = getFirebaseStorage().getReference(furnitureItem.getImageUrl());
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(requireContext()).load(uri).into(itemImage);
                }).addOnFailureListener(exception -> {
                    Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                });

            }
        }

        // Set up back button to return to catalogue
        View catalogue_button = view.findViewById(R.id.back_button);
        catalogue_button.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new FurnitureCatalogueFragment());
            transaction.commit();
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new FurnitureCatalogueFragment());
                transaction.commit();
            }
        });
        return view;
    }

    /**
     * Helper method to get FirebaseStorage instance
     * @return (FirebaseStorage instance)
     */
    protected FirebaseStorage getFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }
}