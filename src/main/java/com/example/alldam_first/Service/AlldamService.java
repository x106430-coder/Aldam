package com.example.alldam_first.Service;

public interface AlldamService {
    boolean supports(String type);
    void fetchAndSave(String region);
}