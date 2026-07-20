package com.example.alldam_first.Controller;

import com.example.alldam_first.CommonResponse;
import com.example.alldam_first.Dto.AlldamRequestDto;
import com.example.alldam_first.Dto.AlldamResponseDto;
import com.example.alldam_first.Entity.AlldamConfig;
import com.example.alldam_first.Repository.AlldamConfigRepository;
import com.example.alldam_first.Repository.AlldamRepository;
import com.example.alldam_first.Service.AlldamService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/Alldam")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 프론트엔드 연동용
public class AlldamController {

    private final List<AlldamService> alldamServices;
    private final AlldamConfigRepository configRepository;
    private final AlldamRepository alldamRepository;

    // 1. 서비스 키 저장
    @PostMapping("/Sim/key/Sign_up")
    @Transactional
    public CommonResponse<String> saveUserKey(@RequestBody AlldamRequestDto alldamRequestDto) {
        String serviceKey = alldamRequestDto.getServiceKey();
        String userName = alldamRequestDto.getUserName();

        // 1. 이름 누락 검사
        if (userName == null || userName.isEmpty()) {
            return new CommonResponse<>(false, "이름이 누락되었습니다.", null);
        }

        if (serviceKey == null || serviceKey.isEmpty()) {
            return new CommonResponse<>(false, "이미 존재하는 서비스 키 입니다.", null);
        }

        if(configRepository.findByConfigKey(userName).isPresent()){
            return new CommonResponse<>(false,"ddd",null);
        }

        // 3. 데이터 설정
        AlldamConfig config = new AlldamConfig();
        config.setConfigKey(userName);     // 엔티티 필드가 configKey이므로 setConfigKey 사용
        config.setConfigValue(serviceKey); // 서비스 키 저장

        // 4. 저장 및 고유 ID 반환
        AlldamConfig savedConfig = configRepository.save(config);
        return new CommonResponse<>(true, userName + "님, 가입이 완료되었습니다.", String.valueOf(savedConfig.getId()));
    }

    // 조회
    @GetMapping("/Sim/key/find_key/{userName}")
    public CommonResponse<String> getUserKey(@PathVariable String userName) {
        return configRepository.findByConfigKey(userName) // 인자 1개로 수정
                .map(config -> new CommonResponse<>(true, userName + "님의 키 조회 성공!", config.getConfigValue()))
                .orElse(new CommonResponse<>(false, userName + "님의 키가 없습니다.", null));
    }

    // 로그인
    @PostMapping("/Sim/login")
    public CommonResponse<String> login(@RequestBody AlldamRequestDto requestDto) {
        String userName = requestDto.getUserName();
        String serviceKey = requestDto.getServiceKey();

        return configRepository.findByConfigKey(userName) // 인자 1개로 수정
                .map(config -> {
                    if (config.getConfigValue().equals(serviceKey)) {
                        return new CommonResponse<>(true, userName + "님, 환영합니다!", "SUCCESS");
                    }
                    return new CommonResponse<>(false, "서비스 키가 일치하지 않습니다.", "INVALID_KEY");
                })
                .orElse(new CommonResponse<>(false, "등록되지 않은 사용자입니다.", "USER_NOT_FOUND"));
    }

    // 2. 데이터 수집 실행
    @PostMapping("/Sim/import/{region}")
    public ResponseEntity<String> importData(@PathVariable String region) {
        alldamServices.forEach(service -> service.fetchAndSave(region));
        return ResponseEntity.ok(region + " 데이터 수집 완료.");
    }

    // 3. 저장된 데이터 조회 (프론트엔드용)
    @GetMapping("/Sim/data/{region}")
    public CommonResponse<List<AlldamResponseDto>> getData(@PathVariable String region) {
        List<AlldamResponseDto> result = alldamRepository.findAllByRegion(region)
                .stream()
                .map(AlldamResponseDto::from)
                .collect(Collectors.toList());
        return new CommonResponse<>(true, region + " 데이터 조회 성공", result);
    }
}