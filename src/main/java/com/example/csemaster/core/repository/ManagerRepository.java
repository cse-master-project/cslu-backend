package com.example.csemaster.core.repository;

import com.example.csemaster.core.dao.actor.ManagerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<ManagerEntity, String> {
    Optional<ManagerEntity> findById(String managerId);
}
