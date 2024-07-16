package com.example.csemaster.v2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NicknameDTO {
    @NotBlank
    String nickname;
}
