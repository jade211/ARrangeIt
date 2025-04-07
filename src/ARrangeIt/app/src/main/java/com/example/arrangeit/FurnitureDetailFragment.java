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

public class FurnitureDetailFragment extends Fragment {

    private ImageView itemImage;
    private TextView itemName;
    private TextView itemDescription;
    private TextView itemPrice;
    private TextView itemHeight;
    private TextView itemWidth;
    private TextView itemDepth;
    private TextView itemColours;
    private TextView itemTexture;

    private FurnitureItem furnitureItem;

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

        Button placeInArButton = view.findViewById(R.id.place_in_ar_button);
        placeInArButton.setOnClickListener(v -> {
            if (furnitureItem != null && furnitureItem.getModelUrl() != null) {
                ARCorePage activity = (ARCorePage) getActivity();
                if (activity != null) {
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
                itemName.setText(furnitureItem.getName());
                itemDescription.setText(furnitureItem.getDescription());
                itemPrice.setText("€" + furnitureItem.getPrice());
                itemHeight.setText("Height: " + furnitureItem.getHeight() + "cm");
                itemWidth.setText("Width: " + furnitureItem.getWidth() + "cm");
                itemDepth.setText("Depth: " + furnitureItem.getDepth() + "cm");
                itemColours.setText(furnitureItem.getColours());
                itemTexture.setText(furnitureItem.getTexture());

                StorageReference imageRef = FirebaseStorage.getInstance().getReference(furnitureItem.getImageUrl());
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(requireContext()).load(uri).into(itemImage);
                }).addOnFailureListener(exception -> {
                    Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                });

            }
        }

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
}