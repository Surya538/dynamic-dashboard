package com.axlero.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class HomeController {

    /**
     * Forwards root request directly to the interactive React Data Dashboard UI
     */
    @GetMapping(value = {"/", "/dashboard"})
    public String dashboard() {
        return "forward:/index.html";
    }

    @GetMapping("/api/info")
    @ResponseBody
    public Map<String, Object> info() {
        return Map.of(
                "application", "Axlero Dynamic Data Dashboard Builder",
                "status", "RUNNING",
                "version", "1.0.0",
                "swaggerUI", "/swagger-ui.html",
                "h2Console", "/h2-console",
                "architecture", "Strict Layered (controllers, services, repositories, entities, dto, config, exceptions)"
        );
    }

    @GetMapping("/api/health")
    @ResponseBody
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "database", "H2 In-Memory (PostgreSQL ready)",
                "cache", "Caffeine"
        );
    }
}
