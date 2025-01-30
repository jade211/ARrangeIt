package com.example.arrangeit;

//import static org.junit.Assert.*;
//import org.junit.Before;
//import org.junit.Test;
//
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//
//
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//import org.robolectric.Robolectric;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.annotation.Config;
//import org.robolectric.shadows.ShadowToast;
//
////@RunWith(RobolectricTestRunner.class)
////@Config(sdk = {30}, manifest=Config.NONE)
//public class LoginUnitTest {
//
//    private MainActivity activity;
//    @Mock
//    private FirebaseAuth mockFirebaseAuth;
//    @Mock
//    private Task<AuthResult> mockAuthResultTask;
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
////        FirebaseApp.initializeApp(Robolectric.buildActivity(MainActivity.class).get().getApplicationContext());
////        activity = Robolectric.buildActivity(MainActivity.class).create().get();
//        mockFirebaseAuth = Mockito.mock(FirebaseAuth.class);
//        activity.firebaseAuth = mockFirebaseAuth;  // Assuming firebaseAuth is a field in MainActivity
//    }
//
//
//    @Test
//    public void testLoginSuccess() {
//        String email = "test@example.com";
//        String password = "password123";
//        when(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockAuthResultTask);
//        when(mockAuthResultTask.isSuccessful()).thenReturn(true);  // Simulate successful login
//        boolean result = mockFirebaseAuth.signInWithEmailAndPassword(email, password).isSuccessful();
//        assertTrue(result);
//    }
//
//
//    @Test
//    public void testLoginSuccess() {
//
//        String email = "testemail@example.com";
//        String password = "password100";
//        activity.editTextEmail.setText(email);
//        activity.editTextPassword.setText(password);
//
//        when(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockAuthResultTask);
//        when(mockAuthResultTask.isSuccessful()).thenReturn(true);
//        activity.signIn.performClick();
//
//        assertEquals("Login Successful!", ShadowToast.getTextOfLatestToast());
//        assertNotNull(ShadowToast.getLatestToast());
//    }
