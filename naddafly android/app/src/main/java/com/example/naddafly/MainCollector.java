package com.example.naddafly;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.naddafly.Maps.GarbageList;
import com.example.naddafly.Maps.MapsActivity;
import com.example.naddafly.databinding.ActivityMainCollectorBinding;

public class MainCollector extends AppCompatActivity {

    ActivityMainCollectorBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainCollectorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new GarbageList());


        binding.CollectorNavigationView.setBackground(null);
        binding.CollectorNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.garbageList2) {
                replaceFragment(new GarbageList());
                return true;
            } else if (item.getItemId() == R.id.collectorProfile) {
                replaceFragment(new CollectorProfile());
                return true;
            }
            return false;
        });

        Button navigateToMapButton = findViewById(R.id.navigateToMapButton);
        navigateToMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainCollector.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerViewCollector, fragment);
        fragmentTransaction.commit();
    }

}