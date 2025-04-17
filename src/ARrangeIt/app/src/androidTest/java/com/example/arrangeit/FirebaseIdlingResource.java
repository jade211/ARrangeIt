package com.example.arrangeit;

import androidx.test.espresso.IdlingResource;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseIdlingResource implements IdlingResource {
    private ResourceCallback callback;
    private FirebaseAuth.AuthStateListener listener;

    @Override
    public String getName() {
        return "FirebaseAuthIdlingResource";
    }

    @Override
    public boolean isIdleNow() {
        boolean isIdle = FirebaseAuth.getInstance().getCurrentUser() != null;
        if (isIdle && callback != null) {
            callback.onTransitionToIdle();
        }
        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
        this.listener = auth -> isIdleNow();
        FirebaseAuth.getInstance().addAuthStateListener(listener);
    }
}