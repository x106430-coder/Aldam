package com.example.alldam_first.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlldamRequestDto {
    private String region; // 사용자가 입력하는 "서산시"
    private String serviceKey;
    private String userName;
}