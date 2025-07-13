package com.example.naddafly;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.naddafly.databinding.FragmentHomeBinding;
import com.example.naddafly.databinding.FragmentRewardBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Reward extends Fragment implements View.OnClickListener {

    private RedeemApi redeemApi;
    private FragmentRewardBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRewardBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        binding.RedeemBtn.setOnClickListener(this);
        redeemApi = RetrofitClient.getRetrofitInstance().create(RedeemApi.class);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        String score = sharedPreferences.getString("score", "");
        binding.CoinsTextView.setText(score);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedToken = sharedPreferences.getString("token", null);

        String Cookie = null;
        if (savedToken != null) {
            Cookie =savedToken;
        } else {
            // Token not found, handle accordingly
        }
        if (v.getId() == R.id.RedeemBtn){
            Call<Rewards> call = redeemApi.RedeemVoucher(Cookie);

            call.enqueue(new Callback<Rewards>() {
                @Override
                public void onResponse(Call<Rewards> call, Response<Rewards> response) {
                    if (response.isSuccessful()) {
                        // Handle successful response
                        Rewards rewards = response.body();
                        if (rewards != null) {
                            Rewards.RewardResponse rewardResponse = rewards.getReward();
                            // Access the reward data from the response
                            String description = rewardResponse.getDescription();
                            String discount = rewardResponse.getDiscount();
                            String expirationDate = rewardResponse.getExpirationDate();
                            String platform = rewardResponse.getPlatform();
                            String voucherCode = rewardResponse.getVoucherCode();

                            StringBuilder rewardData = new StringBuilder();

                            rewardData.append("Description: ").append(description).append("\n");
                            rewardData.append("Discount: ").append(discount).append("\n");
                            rewardData.append("Expiration Date: ").append(expirationDate).append("\n");
                            rewardData.append("Platform: ").append(platform).append("\n");
                            rewardData.append("Voucher Code: ").append(voucherCode);
                            // Convert the score to an integer
                            String score = sharedPreferences.getString("score", "");


                            int scoreint = Integer.parseInt(score);

                            // Subtract 10 from the score
                            scoreint -= 10;

                            // Convert the updated score back to a string
                            String updatedScoreString = String.valueOf(scoreint);

                            // Update the SharedPreferences with the new score value
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("score", updatedScoreString);
                            editor.apply();
                            binding.CoinsTextView.setText(updatedScoreString);
                            // Create and show a dialog with the reward data
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            builder.setTitle("Reward Details")
                                    .setMessage(rewardData.toString())
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        // Do something if needed when the OK button is clicked
                                    })
                                    .show();
                            // Perform further operations with the redeemed voucher data
                            Toast.makeText(getContext(), platform, Toast.LENGTH_SHORT).show();
                        }

                        // Perform further operations with the redeemed voucher data
                    } else {
                        // Handle unsuccessful response
                        Toast.makeText(getContext(), "Failed to redeem voucher", Toast.LENGTH_SHORT).show();
                    }

                }
                @Override
                public void onFailure(Call<Rewards> call, Throwable t) {
                    // Handle failure
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }
    }
}