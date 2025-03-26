package com.example.arrangeit.helpers;

import android.content.Context;
import android.content.Intent;
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

public class FurnitureAdapter extends RecyclerView.Adapter<FurnitureAdapter.ViewHolder> {

    private final Context context;
    private final List<FurnitureItem> furnitureItems;

    public FurnitureAdapter(Context context, List<FurnitureItem> furnitureItems) {
        this.context = context;
        this.furnitureItems = furnitureItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_furniture, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FurnitureItem item = furnitureItems.get(position);

        holder.name.setText(item.getName());
        holder.price.setText("€" + item.getPrice());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(item.getImageUrl());
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(context).load(uri).into(holder.image);
        }).addOnFailureListener(exception -> {
            Log.e("FurnitureAdapter", "Error loading image", exception);
        });

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

    @Override
    public int getItemCount() {
        return furnitureItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.itemPrice);
            image = itemView.findViewById(R.id.itemImage);
        }
    }
}

