package com.example.arrangeit.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arrangeit.R;

import java.util.List;

public class ScreenshotAdapter extends BaseAdapter {
    private Context context;
    private List<ScreenshotItem> items;

    public ScreenshotAdapter(Context context, List<ScreenshotItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridItem;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridItem = inflater.inflate(R.layout.item_screenshot, null);
        } else {
            gridItem = convertView;
        }

        ScreenshotItem item = items.get(position);

        ImageView imageView = gridItem.findViewById(R.id.screenshot_image);
        TextView nameTextView = gridItem.findViewById(R.id.layout_name);
        TextView dateTextView = gridItem.findViewById(R.id.layout_date);
        TextView modelCountTextView = gridItem.findViewById(R.id.layout_model_count);
        TextView furnitureNamesTextView = gridItem.findViewById(R.id.furniture_names);

        nameTextView.setText(item.getName());
        dateTextView.setText(item.getDate());
        modelCountTextView.setText(String.valueOf(item.getModelCount()));

        if (item.getFurnitureNames() != null && !item.getFurnitureNames().isEmpty()) {
            String namesText = String.join(", ", item.getFurnitureNames());
            furnitureNamesTextView.setText(namesText);
            furnitureNamesTextView.setVisibility(View.VISIBLE);
        } else {
            furnitureNamesTextView.setVisibility(View.GONE);
        }

        // Load image from Firebase Storage URL
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_launcher)
                    .into(imageView);
        }
        return gridItem;
    }
}