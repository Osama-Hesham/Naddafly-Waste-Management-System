package com.example.naddafly;

import com.google.gson.annotations.SerializedName;

public class Rewards {
    @SerializedName("reward")
    private RewardResponse reward;


    public RewardResponse getReward() {
        return reward;
    }

    public void setReward(RewardResponse reward) {
        this.reward = reward;
    }

    public static class RewardResponse {

        @SerializedName("description")
        private String description;

        @SerializedName("discount")
        private String discount;

        @SerializedName("expiration_date")
        private String expirationDate;

        @SerializedName("id")
        private String id;

        @SerializedName("platform")
        private String platform;

        @SerializedName("userId")
        private String userId;

        @SerializedName("voucher_code")
        private String voucherCode;

        // Getters and setters for the fields
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getExpirationDate() {
            return expirationDate;
        }

        public void setExpirationDate(String expirationDate) {
            this.expirationDate = expirationDate;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getVoucherCode() {
            return voucherCode;
        }

        public void setVoucherCode(String voucherCode) {
            this.voucherCode = voucherCode;
        }

    }
}
