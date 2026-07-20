/*
package com.example.alldam_first.Service;

import com.example.alldam_first.Entity.Alldam;
import com.example.alldam_first.Repository.AlldamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlldamPoliceService implements AlldamService {

    private final AlldamRepository alldamRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String serviceKey = "efb60vbpRbpuYOlegyYYaivNeAoeBhV8";

    @Override
    public boolean supports(String type) {
        return "POLICE".equals(type);
    }

    @Override
    @Transactional
    public void fetchAndSave(String region) {
        // 경찰서 API 엔드포인트가 다르다고 가정
        String baseUrl = "https://alldam.chungnam.go.kr/api/getPoliceStation/locallist.do";
        URI uri = UriComponentsBuilder.fromUriString(baseUrl.trim())
                .queryParam("serviceKey", serviceKey)
                .queryParam("inst_nm", region)
                .queryParam("type", "json")
                .build().encode().toUri();

        try {
            Map<String, Object> response = restTemplate.getForObject(uri, Map.class);
            if (response != null && response.get("list") != null) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("list");
                for (Map<String, Object> item : items) {
                    alldamRepository.save(Alldam.builder()
                            .type("POLICE")
                            .region(region)
                            .name(String.valueOf(item.getOrDefault("inst_nm", "경찰서명 없음")))
                            .address(String.valueOf(item.getOrDefault("addr", "주소 없음")))
                            .tel(String.valueOf(item.getOrDefault("tel", "전화번호 없음")))
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("경찰서 API 호출 오류: {}", e.getMessage());
        }
    }
}
*/