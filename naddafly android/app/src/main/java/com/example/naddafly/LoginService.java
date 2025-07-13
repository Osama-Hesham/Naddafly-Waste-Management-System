package com.example.naddafly;

public class LoginService implements UserApi {

    private String username;
    private String email;
    private String password;
    private String isDetector;

    public LoginService( String email, String password) {
        this.email = email;
        this.password = password;
    }
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }



    @Override
    public String getIsDetector() {
        return isDetector;
    }

    @Override
    public void setIsDetector(String Type) {
        this.isDetector = Type;
    }

}
