package com.example.csemaster.core.dao.actor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user")
public class UserEntity {
    @Id
    @Column(name="user_id")
    private String userId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
