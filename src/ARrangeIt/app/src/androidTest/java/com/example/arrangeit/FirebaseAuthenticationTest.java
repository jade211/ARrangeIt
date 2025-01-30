package com.example.arrangeit;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthenticationTest {

    private FirebaseAuth firebaseAuth;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.useEmulator("10.0.2.2", 9099);
    }

    @Test
    public void testRegisterAndLogin() {
        firebaseAuth.createUserWithEmailAndPassword("testuser1@example.com", "password111")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        assertNotNull(firebaseAuth.getCurrentUser());
                    } else {
                        fail("Registration Failed!");
                    }
                });

        firebaseAuth.signInWithEmailAndPassword("testuser1@example.com", "password111")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        assertNotNull(firebaseAuth.getCurrentUser());
                    } else {
                        fail("Login failed!");
                    }
                });
    }
}