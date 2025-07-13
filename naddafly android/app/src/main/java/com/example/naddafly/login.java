package com.example.naddafly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class login extends AppCompatActivity implements View.OnClickListener {

    private EditText emailEditText;
    private LoginApi loginApi;
    private EditText passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Button loginBtn = findViewById(R.id.LoginBtn);
        EditText registerHyperText = findViewById(R.id.RegisterHyperLink);

        passwordEditText = findViewById(R.id.editTextTextPasswordLogin);
        emailEditText = findViewById(R.id.editTextEmailLogin);
        loginApi = RetrofitClient.getRetrofitInstance().create(LoginApi.class);
        loginBtn.setOnClickListener(this);
        registerHyperText.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.LoginBtn) {
            LoginClick();
        } else if (view.getId() == R.id.RegisterHyperLink) {
            RedirectRegister();
        }
    }

    private void RedirectRegister() {
        Intent intent = new Intent(login.this, Register.class);
        startActivity(intent);
    }

    private void LoginClick() {
        LoginService user = new LoginService(emailEditText.getText().toString(), passwordEditText.getText().toString());
        Call<RetrievedUser> call = loginApi.LoginUser(user);
        call.enqueue(new Callback<RetrievedUser>() {
            @Override
            public void onResponse(Call<RetrievedUser> call, Response<RetrievedUser> response) {
                if (response.isSuccessful()) {
                    RetrievedUser retrievedUser = response.body();
                    if (retrievedUser != null) {
                        // Get the User object from RetrievedUser
                        RetrievedUser.User user = retrievedUser.getUser();
                        if (user != null) {
                            String emailAddress = user.getEmailAddress();
                            String id = user.getId();
                            String score = user.getScore();
                            String username = user.getUsername();
                            String isDetector = user.getDiscriminator();

                            // Save the token using SharedPreferences
                            String token = response.headers().get("Set-Cookie");
                            if (token != null) {
                                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("token", token);
                                editor.putString("email_address", emailAddress);
                                editor.putString("id", id);
                                editor.putString("score", score);
                                editor.putString("username", username);
                                editor.putString("discriminator",isDetector);
                                if(isDetector.equals("collector")){
                                    String garbageCollected = user.getGarbageCollected();
                                    editor.putString("garbageCollected",garbageCollected);
                                }

                                editor.apply();
                            }

//                            Toast.makeText(login.this, user.getGarbageCollected(), Toast.LENGTH_SHORT).show();

                            if (isDetector.equals("detector")) {
                                Intent intent = new Intent(login.this, MainActivity.class);
                                startActivity(intent);
                            } else if (isDetector.equals("collector")) {
                                Intent intent = new Intent(login.this, MainCollector.class);
                                startActivity(intent);
                            }

                            Toast.makeText(login.this, "User Logged in successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(login.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(login.this, "Response body is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(login.this, "Failed to login user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RetrievedUser> call, Throwable t) {
                // Handle failure
                Toast.makeText(login.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    }