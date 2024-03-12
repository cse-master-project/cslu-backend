package com.example.csemaster.login.manager;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "manager")
public class ManagerModel {
    @Id
    @Column(name = "manager_id", length = 20)
    private String managerId;

    @Column(name = "manager_password", nullable = false, length = 64)
    private String managerPw;
}
