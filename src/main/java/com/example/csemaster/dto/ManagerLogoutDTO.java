package com.example.csemaster.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ManagerLogoutDTO {
    private String managerId;
    private String accessToken;
    private Date expirationTime;
}
