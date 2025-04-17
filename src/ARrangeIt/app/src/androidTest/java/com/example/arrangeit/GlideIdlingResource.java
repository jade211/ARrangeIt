package com.example.arrangeit;

import androidx.test.espresso.IdlingResource;
import com.bumptech.glide.Glide;
import java.lang.reflect.Field;
import java.util.Set;
import androidx.test.core.app.ApplicationProvider;


public class GlideIdlingResource implements IdlingResource {
    private ResourceCallback callback;
    private boolean isIdle = true;

    @Override
    public String getName() {
        return "GlideIdlingResource";
    }

    @Override
    public boolean isIdleNow() {
        try {
            Class<?> glideClass = Class.forName("com.bumptech.glide.Glide");
            Field engineField = glideClass.getDeclaredField("engine");
            engineField.setAccessible(true);
            Object engine = engineField.get(Glide.get(ApplicationProvider.getApplicationContext()));

            Class<?> engineClass = Class.forName("com.bumptech.glide.load.engine.Engine");
            Field jobsField = engineClass.getDeclaredField("jobs");
            jobsField.setAccessible(true);
            Set<?> jobs = (Set<?>) jobsField.get(engine);

            isIdle = jobs.isEmpty();
        } catch (Exception e) {
            isIdle = true;
        }

        if (isIdle && callback != null) {
            callback.onTransitionToIdle();
        }
        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }
}
