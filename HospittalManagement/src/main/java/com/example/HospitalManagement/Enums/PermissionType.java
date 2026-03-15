package com.example.HospitalManagement.Enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Map;
import java.util.Set;


@Getter
@RequiredArgsConstructor
public enum PermissionType {
    PATIENT_READ("Patient:Read"),
    PATIENT_WRITE("Patient:Write"),
    PATIENT_DELETE("Patient:Delete"),
    APPOINTMENT_READ("Appointment:Read"),
    APPOINTMENT_WRITE("Appointment:Write"),
    APPOINTMENT_DELETE("Appointment:Delete"),
    DOCTOR_ASSIGN("Department:Assign"),
    DOCTOR_READ("Doctor:Read"),
    DOCTOR_WRITE("Doctor:Write"),
    DOCTOR_DELETE("Doctor:Delete"),
    USER_MANAGE("User:Manage"), // for admin tasks
    REPORT_VIEW("Report:View"),
    INSURANCE_READ("Insurance:Read"),
    INSURANCE_OPEARTIONS("Insurance:Operations"),
    DEPARTMENT_OPERATIONS("Department:Operations");


    private final String permissions;
}
//🧠 Meaning:
            // 1 -> Ye har permission ka central definition hai.
            // 2 -> "Patient:Read" readable string hai.
            // 3 -> Ye hi string later SimpleGrantedAuthority me convert hogi.

// Question --> Why String Store Kar Rahe Ho?
//              Kyuki Spring internally authority ko string ke form me compare karta hai.