package com.example.alldam_first.Repository;

import com.example.alldam_first.Entity.AlldamConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlldamConfigRepository extends JpaRepository<AlldamConfig, Long> {
    Optional<AlldamConfig> findByConfigKey(String configKey);
}