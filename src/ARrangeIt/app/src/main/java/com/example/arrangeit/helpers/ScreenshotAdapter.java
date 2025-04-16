package com.example.arrangeit.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.arrangeit.R;

import java.util.List;

public class ScreenshotAdapter extends BaseAdapter {
    private Context context;
    private List<String> imageUrls;

    public ScreenshotAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_screenshot, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.screenshot_image);
        Glide.with(context)
                .load(imageUrls.get(position))
                .placeholder(R.drawable.ic_placeholder)
                .into(imageView);

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return true; // Make items clickable and long-clickable
    }
}