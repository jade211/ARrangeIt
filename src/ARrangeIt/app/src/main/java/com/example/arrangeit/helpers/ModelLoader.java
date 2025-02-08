package com.example.arrangeit.helpers;
import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class ModelLoader {
    public static List<FurnitureItem> loadCatalogue(Context context) {
        try {
            InputStream is = context.getAssets().open("models/catalogue.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            return new Gson().fromJson(json, new TypeToken<List<FurnitureItem>>() {}.getType());
        } catch (IOException e) {
            Log.e("ModelLoader", "Error loading catalogue", e);
            return null;
        }
    }
}
