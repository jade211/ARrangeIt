package com.example.arrangeit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_furniture_detail, container, false);

        itemImage = view.findViewById(R.id.itemImage);
        itemName = view.findViewById(R.id.itemName);
        itemDescription = view.findViewById(R.id.itemDescription);
        itemPrice = view.findViewById(R.id.itemPrice);
        itemHeight = view.findViewById(R.id.itemHeight);
        itemWidth = view.findViewById(R.id.itemWidth);
        itemDepth = view.findViewById(R.id.itemDepth);
        itemColours = view.findViewById(R.id.itemColours);
        itemTexture = view.findViewById(R.id.itemTexture);

        Bundle args = getArguments();
        if (args != null) {
            FurnitureItem item = (FurnitureItem) args.getSerializable("furniture_item");

            if (item != null) {
                itemName.setText(item.getName());
                itemDescription.setText(item.getDescription());
                itemPrice.setText("€" + item.getPrice());
                itemHeight.setText("Height: " + item.getHeight() + "cm");
                itemWidth.setText("Width: " + item.getWidth() + "cm");
                itemDepth.setText("Depth: " + item.getDepth() + "cm");
                itemColours.setText(item.getColours());
                itemTexture.setText(item.getTexture());


                StorageReference imageRef = FirebaseStorage.getInstance().getReference(item.getImageUrl());
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(requireContext()).load(uri).into(itemImage);
                }).addOnFailureListener(exception -> {
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
