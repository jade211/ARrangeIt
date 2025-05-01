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


/**
 * Helper adapter for displaying saved layout in grid view
 */
public class ScreenshotAdapter extends BaseAdapter {
    private Context context;
    private List<ScreenshotItem> items;


    /**
     * Constructs a new ScreenshotAdapter
     * @param context (activity context needed for layout inflation and Glide)
     * @param items (list of ScreenshotItem objects)
     */
    public ScreenshotAdapter(Context context, List<ScreenshotItem> items) {
        this.context = context;
        this.items = items;
    }

    /**
     * Returns the number of screenshots
     * @return (total number of screenshots)
     */
    @Override
    public int getCount() {
        return items.size();
    }

    /**
     * Gets the screenshot at the specified position.
     * @param position (position of the screenshot)
     * @return (screenshot at that position)
     */
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }


    /**
     * Gets the row id of the item at the specified position.
     * @param position (position of the item)
     * @return (position as the ID)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * Gets view that displays the data at the specified position.
     * @param position (position of the item)
     * @param convertView (Recycled view if available)
     * @param parent (parent ViewGroup)
     * @return (view displaying the screenshot and info)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridItem;

        // view recycling implementation
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridItem = inflater.inflate(R.layout.item_screenshot, null);
        } else {
            // reuse existing view
            gridItem = convertView;
        }

        ScreenshotItem item = items.get(position);

        // initialising information values
        ImageView imageView = gridItem.findViewById(R.id.screenshot_image);
        TextView nameTextView = gridItem.findViewById(R.id.layout_name);
        TextView dateTextView = gridItem.findViewById(R.id.layout_date);
        TextView modelCountTextView = gridItem.findViewById(R.id.layout_model_count);
        TextView furnitureNamesTextView = gridItem.findViewById(R.id.furniture_names);

        // sets values
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

        // load image from Firebase Storage
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(imageView);
        }
        return gridItem;
    }
}