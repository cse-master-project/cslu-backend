package com.example.csemaster.core.dao.actor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "manager")
public class ManagerEntity {
    @Id
    @Column(name = "manager_id", length = 20)
    private String managerId;

    @Column(name = "manager_password", nullable = false, length = 64)
    private String managerPw;
}
