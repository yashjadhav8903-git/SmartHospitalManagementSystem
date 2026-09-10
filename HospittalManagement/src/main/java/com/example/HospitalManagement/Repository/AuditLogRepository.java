package com.example.HospitalManagement.Repository;

import com.example.HospitalManagement.Entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository  extends JpaRepository<AuditLog,Long> {

}
