package com.example.csemaster.login.manager;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<ManagerEntity, String> {
    Optional<ManagerEntity> findById(String managerId);
}
