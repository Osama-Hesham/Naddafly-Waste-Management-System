package com.example.naddafly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.naddafly.databinding.FragmentProfileBinding;


public class Profile extends Fragment implements View.OnClickListener {

    private FragmentProfileBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String score = sharedPreferences.getString("score", "");
        String username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email_address", "");


        binding.UserNameTextView.setText(username);
        binding.EmailTextView.setText(email);
        binding.CoinsTextView.setText(score);
        binding.logoutBtn.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.logoutBtn) {
            Intent intent = new Intent(getActivity(), login.class);
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("token"); // Remove the token entry

            editor.apply();
            startActivity(intent);

        }
    }
}