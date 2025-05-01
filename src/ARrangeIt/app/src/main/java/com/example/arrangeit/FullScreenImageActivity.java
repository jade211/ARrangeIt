package com.example.arrangeit;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;


/**
 * Displays the screenshot image in fullscreen mode
 */
public class FullScreenImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        // initialises views
        ImageView fullscreenImage = findViewById(R.id.fullscreen_image);
        ImageButton backButton = findViewById(R.id.back_button);

        // loads image in with glide
        String imageUrl = getIntent().getStringExtra("image_url");
        Glide.with(this)
                .load(imageUrl)
                .into(fullscreenImage);

        backButton.setOnClickListener(v -> finish());

        // Make the image clickable to exit fullscreen
        fullscreenImage.setOnClickListener(v -> finish());
    }
}