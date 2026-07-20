package com.example.alldam_first.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AlldamConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   //serviceKey

    @Column(unique = true, nullable = false)
    private String configValue; //API 키 값

    private String userName; //사용자의 이름 입력

    private String configKey;
}
