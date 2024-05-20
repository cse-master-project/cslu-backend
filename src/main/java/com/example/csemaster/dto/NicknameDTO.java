package com.example.csemaster.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NicknameDTO {
    @NotBlank
    String nickname;
}
