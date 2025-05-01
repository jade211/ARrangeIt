package com.example.arrangeit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.example.arrangeit.helpers.FieldValidatorHelper;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.android.material.textfield.TextInputLayout;



/**
 * RegisterPage handles new user registration with:
 * - Email and password validation
 * - Account creation via Firebase Authentication
 * - Navigation to login page
 */
public class RegisterPage extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    Button signUp;
    TextView signIn;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    TextInputLayout emailInputLayout, passwordInputLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        signIn = findViewById(R.id.sign_in);
        signUp = findViewById(R.id.sign_up);
        emailInputLayout = findViewById(R.id.email_input_layout);
        passwordInputLayout = findViewById(R.id.password_input_layout);

        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Validate email format as user types
                String emailError = FieldValidatorHelper.validateEmail(editTextEmail.getText().toString());
                emailInputLayout.setError(emailError);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Set up real-time password validation
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String passwordError = FieldValidatorHelper.validatePassword(editTextPassword.getText().toString());
                passwordInputLayout.setError(passwordError);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Handle sign in text click - navigate to login page
        signIn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterPage.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Handle sign up button click - register new user
        signUp.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            // Validate email and password before registration attempt
            String emailError = FieldValidatorHelper.validateEmail(email);
            String passwordError = FieldValidatorHelper.validatePassword(password);

            if (emailError != null) {
                emailInputLayout.setError(emailError);
                return;
            } else {
                emailInputLayout.setError(null);
            }

            if (passwordError != null) {
                passwordInputLayout.setError(passwordError);
                return;
            } else {
                passwordInputLayout.setError(null);
            }

            // Attempt to create new user with Firebase Authentication
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // On successful registration
                    Toast.makeText(RegisterPage.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterPage.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthUserCollisionException e) {
                        // Handle case where email already exists
                        emailInputLayout.setError("This email is already in use. Please use a different email.");
                    } catch (Exception e) {
                        Toast.makeText(RegisterPage.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
}
