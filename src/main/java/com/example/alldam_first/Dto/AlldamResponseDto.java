package com.example.alldam_first.Dto;

import com.example.alldam_first.Entity.Alldam;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlldamResponseDto {
    private String type;
    private String name;
    private String address;
    private String tel;

    public static AlldamResponseDto from(Alldam entity) {
        return AlldamResponseDto.builder()
                .type(entity.getType())
                .name(entity.getName())
                .address(entity.getAddress())
                .tel(entity.getTel())
                .build();
    }
}
