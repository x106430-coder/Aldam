package com.example.alldam_first.Service;

import com.example.alldam_first.Entity.Alldam;
import com.example.alldam_first.Entity.AlldamConfig;
import com.example.alldam_first.Repository.AlldamConfigRepository;
import com.example.alldam_first.Repository.AlldamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlldamHospitalService implements AlldamService {

    private final AlldamRepository alldamRepository;
    private final AlldamConfigRepository configRepository;
    private final RestTemplate restTemplate;

    @Override
    public boolean supports(String type) {
        return "HOSPITAL".equals(type);
    }

    @Override
    @Transactional
    public void fetchAndSave(String region) {
        AlldamConfig config = configRepository.findAll().stream().findFirst()
                .orElseThrow(()-> new RuntimeException("등록된 서비스 키가 없습니다."));

        String serviceKey = config.getConfigValue();

        String url = "https://alldam.chungnam.go.kr/api/getLocalMedicInstitution01/locallist.do"
                + "?serviceKey={servicekey}&pageNo=1&numOfRows=100&inst_nm={inst_nm}&type={type}";

        Map<String, String> params = Map.of(
                "servicekey", serviceKey,
                "inst_nm", region,
                "type", "json"
        );

        try {
            // ✅ String 대신 Map으로 바로 받음 → ObjectMapper 불필요
            @SuppressWarnings("unchecked")
            Map<String, Object> rootResponse = restTemplate.getForObject(url, Map.class, params);

            if (rootResponse == null) { log.warn("[병원] 응답 null"); return; }

            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = (Map<String, Object>) rootResponse.get("response");
            if (responseMap == null) return;

            @SuppressWarnings("unchecked")
            Map<String, Object> bodyMap = (Map<String, Object>) responseMap.get("body");
            if (bodyMap == null || bodyMap.get("items") == null || "".equals(bodyMap.get("items"))) return;

            @SuppressWarnings("unchecked")
            Map<String, Object> itemsMap = (Map<String, Object>) bodyMap.get("items");
            Object itemObj = itemsMap.get("item");

            alldamRepository.deleteAllByRegionAndType(region, "HOSPITAL");

            if (itemObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> itemList = (List<Map<String, Object>>) itemObj;
                itemList.forEach(item -> saveHospital(item, region));
                log.info("[병원] 저장 완료: {}건", itemList.size());
            } else if (itemObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> singleItem = (Map<String, Object>) itemObj;
                saveHospital(singleItem, region);
            }

        } catch (Exception e) {
            log.error("[병원] 처리 오류: {}", e.getMessage());
        }
    }

    private void saveHospital(Map<String, Object> item, String region) {
        alldamRepository.save(Alldam.builder()
                .type("HOSPITAL")
                .region(region)
                .name(String.valueOf(item.getOrDefault("BPLCNM", "이름 없음")))
                .address(String.valueOf(item.getOrDefault("RDNWHLADDR", "주소 없음")))
                .tel(String.valueOf(item.getOrDefault("SITETEL", "전화번호 없음")))
                .build());
    }
}