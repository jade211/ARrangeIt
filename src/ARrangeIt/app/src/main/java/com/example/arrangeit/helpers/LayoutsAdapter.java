package com.example.arrangeit.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.arrangeit.R;

import java.util.List;
import java.util.function.Consumer;

public class LayoutsAdapter extends RecyclerView.Adapter<LayoutsAdapter.LayoutViewHolder> {
    private final List<SavedLayout> layouts;
    private final Consumer<SavedLayout> onLayoutClicked;

    public LayoutsAdapter(List<SavedLayout> layouts, Consumer<SavedLayout> onLayoutClicked) {
        this.layouts = layouts;
        this.onLayoutClicked = onLayoutClicked;
    }

    @NonNull
    @Override
    public LayoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved_layout, parent, false);
        return new LayoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LayoutViewHolder holder, int position) {
        SavedLayout layout = layouts.get(position);
        holder.layoutName.setText(layout.getLayoutName());
        
        Glide.with(holder.itemView.getContext())
                .load(layout.getScreenshotUrl())
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.layoutImage);
                
        holder.itemView.setOnClickListener(v -> onLayoutClicked.accept(layout));
    }

    @Override
    public int getItemCount() {
        return layouts.size();
    }

    static class LayoutViewHolder extends RecyclerView.ViewHolder {
        ImageView layoutImage;
        TextView layoutName;

        public LayoutViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutImage = itemView.findViewById(R.id.layoutImage);
            layoutName = itemView.findViewById(R.id.layoutName);
        }
    }
}