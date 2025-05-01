package com.example.arrangeit.helpers;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.arrangeit.ARCorePage;
import com.example.arrangeit.FurnitureDetailFragment;
import com.example.arrangeit.R;
import java.util.List;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * Helper Adapter class for displaying furniture item details
 * in RecyclerView
 */
public class FurnitureAdapter extends RecyclerView.Adapter<FurnitureAdapter.ViewHolder> {

    private final Context context;
    private final List<FurnitureItem> furnitureItems;


    /**
     * Constructs a new FurnitureAdapter.
     * @param context (activity context)
     * @param furnitureItems (list of furniture items)
     */
    public FurnitureAdapter(Context context, List<FurnitureItem> furnitureItems) {
        this.context = context;
        this.furnitureItems = furnitureItems;
    }


    /**
     * Creates a new ViewHolder instance.
     * @param parent (ViewGroup into which the new View will be added)
     * @param viewType (view type of the new View)
     * @return A new ViewHolder that holds a View of the given view type
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_furniture, parent, false);
        return new ViewHolder(view);
    }


    /**
     * Adds data to the ViewHolder at the specified position
     * @param holder (ViewHolder)
     * @param position (position)
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FurnitureItem item = furnitureItems.get(position);

        holder.name.setText(item.getName());
        holder.price.setText("€" + item.getPrice());

        // load image from firebase storage using glide
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(item.getImageUrl());
        storageReference.getDownloadUrl().addOnSuccessListener(
                uri -> Glide.with(context).load(uri).into(holder.image)).addOnFailureListener(
                        exception -> Log.e("FurnitureAdapter", "Error loading image", exception));

        // set click listener to show details fragment
        holder.itemView.setOnClickListener(v -> {
            FurnitureDetailFragment fragment = new FurnitureDetailFragment();
            Bundle args = new Bundle();
            args.putSerializable("furniture_item", item);
            fragment.setArguments(args);
            if (context instanceof ARCorePage) {
                ((ARCorePage) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
        });
    }


    /**
     * Gets the total number of items in the catalogue
     * @return size
     */
    @Override
    public int getItemCount() {
        return furnitureItems.size();
    }


    /**
     * ViewHolder class that holds references to the
     * views for each item
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView image;


        /**
         * Initialises ViewHolder and finds views
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.itemPrice);
            image = itemView.findViewById(R.id.itemImage);
        }
    }
}
