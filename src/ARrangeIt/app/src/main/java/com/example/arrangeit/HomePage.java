package com.example.arrangeit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;



public class HomePage extends AppCompatActivity {

    Button ar_core;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ar_core = findViewById(R.id.ar_core);
        ar_core.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, ARCorePage.class);
                startActivity(intent);
                finish();
            }
        });

    }
}