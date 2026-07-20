package com.example.alldam_first.Repository;

import com.example.alldam_first.Entity.Alldam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlldamRepository extends JpaRepository<Alldam, Long> {
    List<Alldam> findAllByRegion(String region);
    void deleteAllByRegionAndType(String region, String type); // ← 추가
}