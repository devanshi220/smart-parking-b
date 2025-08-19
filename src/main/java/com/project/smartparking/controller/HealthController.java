package com.project.smartparking.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class HealthController {

	@GetMapping("/health")
	public Map<String, Object> health() {
		Map<String, Object> map = new HashMap<>();
		map.put("status", "UP");
		return map;
	}
}



