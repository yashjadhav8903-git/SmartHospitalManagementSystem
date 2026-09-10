package com.example.HospitalManagement.DTO.AdminDTOs;

import org.jetbrains.annotations.NotNull;

public class AdminRequestDTOs {

    @NotNull("Username is Required for onBoarding Admin")
    private String username;
    @NotNull("Password is Required for onBoarding Admin")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
