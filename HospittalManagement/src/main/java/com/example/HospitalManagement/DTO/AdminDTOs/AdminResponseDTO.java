package com.example.HospitalManagement.DTO.AdminDTOs;

import lombok.Data;

@Data
public class AdminResponseDTO {

    private Integer AdminId;
    private String AdminUsername;

    public Integer getAdminId() {
        return AdminId;
    }

    public void setAdminId(Integer adminId) {
        AdminId = adminId;
    }

    public String getAdminUsername() {
        return AdminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        AdminUsername = adminUsername;
    }


    public AdminResponseDTO(Integer adminId, String adminUsername) {
        AdminId = adminId;
        AdminUsername = adminUsername;
    }
}
