package com.example.naddafly;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.naddafly.databinding.FragmentHomeBinding;


public class Home extends Fragment {

    private FragmentHomeBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment using view binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // Retrieve score from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String score = sharedPreferences.getString("score", "");
        String username = sharedPreferences.getString("username", "");
        // Set the score to the TextView using view binding
        binding.CoinsTextView.setText(score);
        binding.UserNameTextView.setText(username);

        return rootView;

    }
}