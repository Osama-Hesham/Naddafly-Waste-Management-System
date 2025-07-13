package com.example.naddafly;

public class RegisterService implements UserApi {

    private String username;
    private String email;
    private String password;

    private String user_type;

    public RegisterService(String username, String email, String password,String isDetector) {

        this.username = username;
        this.email = email;
        this.password = password;

        this.user_type=isDetector;
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
        return user_type;
    }

    @Override
    public void setIsDetector(String Type) {
        this.user_type = Type;
    }


}
