package com.example.naddafly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.naddafly.databinding.FragmentCollectorProfileBinding;
import com.example.naddafly.databinding.FragmentProfileBinding;


public class CollectorProfile extends Fragment implements View.OnClickListener {

    FragmentCollectorProfileBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCollectorProfileBinding.inflate(inflater, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String garbageCollected = sharedPreferences.getString("garbageCollected", "");
        String username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email_address", "");


        binding.CollectorUserNameTextView.setText(username);
        binding.CollectorEmailTextView.setText(email);
        binding.CollectorCoinsTextView.setText(garbageCollected);
        binding.CollectorlogoutBtn.setOnClickListener(this);
        return binding.getRoot();
    }


    @Override
    public void onClick(View v) {

            if (v.getId() == R.id.CollectorlogoutBtn) {
                Intent intent = new Intent(getActivity(), login.class);
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token"); // Remove the token entry

                editor.apply();
                startActivity(intent);

            }

    }
}