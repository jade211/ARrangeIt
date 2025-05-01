package com.example.arrangeit;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.example.arrangeit.helpers.FieldValidatorHelper;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.android.material.textfield.TextInputLayout;


/**
 * MainActivity handles user authentication including:
 * - Email/password login
 * - Password reset functionality
 * - Navigation to registration page
 */
public class MainActivity extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    Button signIn;
    TextView signUp;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    TextInputLayout emailInputLayout;
    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        signIn = findViewById(R.id.sign_in);
        signUp = findViewById(R.id.sign_up);
        emailInputLayout = findViewById(R.id.email_input_layout);
        forgotPassword = findViewById(R.id.forgot_password);

        // Set up email field validation on text change
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Validate email format in real-time
                String emailError = FieldValidatorHelper.validateEmail(editTextEmail.getText().toString());
                emailInputLayout.setError(emailError);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Handle sign up text click - navigate to RegisterPage
        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterPage.class);
            startActivity(intent);
            finish();
        });

        // Handle forgot password text click - show password reset dialog
        forgotPassword.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.forgot_password_dialogue, null);
            EditText emailBox = dialogView.findViewById(R.id.emailBox);
            TextView emailErrorText = dialogView.findViewById(R.id.emailErrorText);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            dialogView.findViewById(R.id.buttonReset).setOnClickListener(view1 -> {
                String userEmail = emailBox.getText().toString();
                String emailError = FieldValidatorHelper.validateEmail(userEmail);
                if (emailError != null) {
                    emailErrorText.setText(emailError);
                    emailErrorText.setVisibility(View.VISIBLE);
                } else {
                    emailErrorText.setVisibility(View.GONE);
                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Email has been sent", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "Unable to send", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            // Handle cancel button click in dialog
            dialogView.findViewById(R.id.buttonCancel).setOnClickListener(view2 -> dialog.dismiss());
            if (dialog.getWindow() != null){
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            dialog.show();
        });

        // Handle sign in button click
        signIn.setOnClickListener(v -> {
            String email, password;
            email = editTextEmail.getText().toString();
            String emailError = FieldValidatorHelper.validateEmail(email);
            if (emailError != null) {
                emailInputLayout.setError(emailError);
                return;
            } else {
                emailInputLayout.setError(null);
            }

            password = String.valueOf(editTextPassword.getText());


            // Attempt Firebase authentication
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, ARCorePage.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(MainActivity.this, "The email or password you have entered is incorrect. Please try again.", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}
