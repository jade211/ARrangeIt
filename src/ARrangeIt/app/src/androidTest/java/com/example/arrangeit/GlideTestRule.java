// In file: androidTest/java/com/example/arrangeit/GlideTestRule.java
package com.example.arrangeit;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.request.RequestOptions;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class GlideTestRule implements TestRule {
    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Context context = ApplicationProvider.getApplicationContext();
                try {
                    // Initialize Glide with test configuration
                    Glide.init(context, new GlideBuilder()
                            .setDefaultRequestOptions(
                                    new RequestOptions()
                                            .dontTransform()
                                            .dontAnimate()
                            ));
                    base.evaluate();
                } finally {
                    // Clean up Glide
                    Glide.tearDown();
                }
            }
        };
    }
}