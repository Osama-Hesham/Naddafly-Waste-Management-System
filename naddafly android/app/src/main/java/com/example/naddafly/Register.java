package com.example.naddafly;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Register extends AppCompatActivity implements View.OnClickListener {

    private  RegisterApi registerApi;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText usernameEditText;
    private RadioGroup radioGroup;
    private RadioButton optionDetector, optionCollector;
    String selectedValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        Button registerBtn = findViewById(R.id.RegisterBtn);
        EditText loginHyperText = findViewById(R.id.LoginHyperText);

        usernameEditText = findViewById(R.id.editTextFullName);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        radioGroup = findViewById(R.id.radioGroup);

        optionDetector = findViewById(R.id.OptionDetector);
        optionCollector = findViewById(R.id.OptionCollector);


        registerBtn.setOnClickListener(this);
        loginHyperText.setOnClickListener(this);

         registerApi = RetrofitClient.getRetrofitInstance().create(RegisterApi.class);




        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedValue = "";
                if (checkedId == R.id.OptionDetector) {
                    selectedValue = "detector";
                } else if (checkedId == R.id.OptionCollector) {
                    selectedValue = "collector";
                }
            }
        });
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.RegisterBtn) {
            RegisterClick();


        } else if (view.getId() == R.id.LoginHyperText) {
            RedirectLogin();
        }
    }

    private void RedirectLogin() {
        Intent intent = new Intent(Register.this, login.class);
        startActivity(intent);
    }


    public void RegisterClick() {
        RegisterService user= new RegisterService(usernameEditText.getText().toString(),emailEditText.getText().toString(),passwordEditText.getText().toString(),selectedValue);

        Call<Void> call = registerApi.registerUser(user);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Extract token from response body
                    String token = response.headers().get("Set-Cookie");
                    if (token != null) {
                        // Save the token using SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", token);
                        editor.apply();
                    }
                    Toast.makeText(Register.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Register.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Register.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}