package com.example.naddafly.Maps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.naddafly.R;
import com.example.naddafly.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GarbageAdapter extends RecyclerView.Adapter<GarbageAdapter.DistanceViewHolder> {

    //dah beta3 el sora el mafrood bey2a zayo zay el base el 3ady
    private String  baseUrl = "https://as1.ftcdn.net/";
    private List<GarbageItem> garbageItemList;

    public GarbageAdapter(List<GarbageItem> garbageItemList) {
        this.garbageItemList = garbageItemList;
    }

    @NonNull
    @Override
    public DistanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.garbagecard, parent, false);
        return new DistanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DistanceViewHolder holder, int position) {
        GarbageItem garbageItem = garbageItemList.get(position);
        holder.distanceTimeTextView.setText(garbageItem.getDistance() + " | " + garbageItem.getDuration());
        holder.size.setText(garbageItem.getSize());
        holder.id.setText("Garbage ID: " + garbageItem.getId());
        Picasso.get().load(baseUrl + garbageItem.getImageUrl()).into(holder.mapImageView);
        holder.navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationUri = "google.navigation:q=" + garbageItem.getLatitude() + "," + garbageItem.getLongitude();
                Uri gmmIntentUri = Uri.parse(locationUri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                Context context = v.getContext();
                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                }
            }
        });




        holder.collectedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = v.getContext();
               SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
              String  savedToken = sharedPreferences.getString("token", null);
                RemoveGarbage removeGarbageService = RetrofitClient.getRetrofitInstance().create(RemoveGarbage.class);
                Call<Void> call = removeGarbageService.removeGarbage(garbageItem.getId(),savedToken);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            garbageItemList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, garbageItemList.size());
                            Toast.makeText(context, "Garbage collected", Toast.LENGTH_SHORT).show();
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return garbageItemList.size();
    }

    public static class DistanceViewHolder extends RecyclerView.ViewHolder {
        ImageView mapImageView;
        TextView distanceTimeTextView;

        TextView size;
        TextView id;
        Button navigateBtn;
        Button collectedBtn;

        public DistanceViewHolder(View itemView) {
            super(itemView);
            mapImageView = itemView.findViewById(R.id.ImageImageViewUserProduct);
            distanceTimeTextView = itemView.findViewById(R.id.TitleTextViewUserProduct);
            size = itemView.findViewById(R.id.PriceTextViewUserProduct);
            id = itemView.findViewById(R.id.GarbageID);
            navigateBtn = itemView.findViewById(R.id.navigate_button);
            collectedBtn = itemView.findViewById(R.id.collected_button);
        }
    }
}

