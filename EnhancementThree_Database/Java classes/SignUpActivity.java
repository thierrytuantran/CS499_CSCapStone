package com.zybooks.thierrytran_eventtrackingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        databaseHelper = new DatabaseHelper(this);

        EditText firstNameInput = findViewById(R.id.first_name);
        EditText lastNameInput = findViewById(R.id.last_name);
        EditText phoneNumberInput = findViewById(R.id.phone_number);
        EditText emailAddressInput = findViewById(R.id.email_address);
        EditText usernameInput = findViewById(R.id.username);
        EditText passwordInput = findViewById(R.id.password);
        Button signUpButton = findViewById(R.id.sign_up_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        signUpButton.setOnClickListener(v -> {
            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String phoneNumber = phoneNumberInput.getText().toString().trim();
            String emailAddress = emailAddressInput.getText().toString().trim();
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Call the validation method
            if (validateInputs(firstName, lastName, phoneNumber, emailAddress, username, password)) {
                long userId = databaseHelper.addUser(username, password);
                if (userId != -1) {
                    Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Input validation method
    private boolean validateInputs(String firstName, String lastName, String phoneNumber, String emailAddress, String username, String password) {
        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() || emailAddress.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (username.length() < 3) {
            Toast.makeText(this, "Username must be at least 3 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 5) {
            Toast.makeText(this, "Password must be at least 5 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Additional validation logic can be added here (e.g., checking phone/email format)
        return true;
    }
}
