package com.wowraid.jobspoon.account.dto;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse {

    public static ResponseEntity<?> ok(Map<String, Object> body) {
        body.put("success", true);
        return ResponseEntity.ok(body);
    }

    public static ResponseEntity<?> ok(String key, Object value) {
        Map<String, Object> body = new HashMap<>();
        body.put(key, value);
        return ok(body);
    }

    public static ResponseEntity<?> fail(String message) {
        return fail(message, 400);
    }

    public static ResponseEntity<?> fail(String message, int status) {
        return ResponseEntity.status(status).body(Map.of(
                "success", false,
                "error", message
        ));
    }
}
