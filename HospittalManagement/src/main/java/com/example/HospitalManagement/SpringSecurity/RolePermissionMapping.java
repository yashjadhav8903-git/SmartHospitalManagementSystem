package com.example.HospitalManagement.SpringSecurity;

import com.example.HospitalManagement.Enums.PermissionType;
import com.example.HospitalManagement.Enums.RolesType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.HospitalManagement.Enums.PermissionType.*;
import static com.example.HospitalManagement.Enums.RolesType.*;

public class RolePermissionMapping {

    // Role ko permission se map kr rahe hai 😌
    private static final Map<RolesType,Set<PermissionType>> map = Map.of(     //---> Role or Uske permissions ka set
            PATIENT, Set.of(PATIENT_READ,APPOINTMENT_READ,APPOINTMENT_WRITE), // Patient ke pass konn konshi permission hai
            DOCTOR, Set.of(APPOINTMENT_READ,APPOINTMENT_WRITE,REPORT_VIEW),   // Doctor ke pass konn konshi permission hai
            ADMIN, Set.of(PATIENT_WRITE,PATIENT_DELETE,PATIENT_READ,APPOINTMENT_WRITE,APPOINTMENT_READ,APPOINTMENT_DELETE,
                    USER_MANAGE,REPORT_VIEW,INSURANCE_READ, INSURANCE_OPEARTIONS,DOCTOR_ASSIGN,DOCTOR_WRITE,DOCTOR_READ,DOCTOR_DELETE,DEPARTMENT_OPERATIONS)  // Admin ke pass konn konshi permission hai
    );


    // Covert Roles and Authority/permission into the String( Convert Role's and Authority into SimpleGrantedAuthority )🫡
    public static Set<SimpleGrantedAuthority> getAuthoritieforRole(RolesType role) {  //---> Ek role lo Uske permissions nikalo Unko GrantedAuthority me convert karo
        return map.get(role).stream()  //--> Role ke permissions ka set le rahe ho.
              .map(permissionType -> new SimpleGrantedAuthority(permissionType.getPermissions()))  //--> Yaha conversion ho raha hai,PermissionType enum → actual string authority
                                                                                                                    // ex.APPOINTMENT_WRITE → "Appointment:Write"
              .collect(Collectors.toSet());   //--> Sabko set me collect kar diya.
    }
}