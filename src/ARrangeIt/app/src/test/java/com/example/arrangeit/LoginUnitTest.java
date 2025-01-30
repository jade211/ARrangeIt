//package com.example.arrangeit;
//
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//public class LoginUnitTest {
//
//    @Mock
//    private FirebaseAuth mockFirebaseAuth;
//
//    @Mock
//    private Task<AuthResult> mockAuthResultTask;
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    private boolean isValidEmail(String email) {
//        return email != null && !email.trim().isEmpty();
//    }
//
//    private boolean isValidPassword(String password) {
//        return password != null && !password.trim().isEmpty();
//    }
//
