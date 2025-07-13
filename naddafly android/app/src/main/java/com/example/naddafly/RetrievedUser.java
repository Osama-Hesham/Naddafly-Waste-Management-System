package com.example.naddafly;

import com.google.gson.annotations.SerializedName;

public class RetrievedUser {

    @SerializedName("user")
    private User user;

    // Getters and setters for the user object
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Inner User class definition
    public static class User {
        @SerializedName("discriminator")
        private String discriminator;

        @SerializedName("email_address")
        private String emailAddress;

        @SerializedName("id")
        private String id;

        @SerializedName("is_verified")
        private boolean isVerified;

        @SerializedName("score")
        private String score;

        @SerializedName("username")
        private String username;

        @SerializedName("garbageCollected")
        private String garbageCollected;

        public String getGarbageCollected() {
            return garbageCollected;
        }

        public void setGarbageCollected(String garbageCollected) {
            this.garbageCollected = garbageCollected;
        }

        // Getters and setters for the fields
        public String getDiscriminator() {
            return discriminator;
        }

        public void setDiscriminator(String discriminator) {
            this.discriminator = discriminator;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isVerified() {
            return isVerified;
        }

        public void setVerified(boolean verified) {
            isVerified = verified;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
