package com.yzlaboratory.api_configs.controller;

import com.yzlaboratory.api_configs.entity.Config;
import com.yzlaboratory.api_configs.service.DynamoDbService;
import com.yzlaboratory.api_configs.service.UUIDService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/configs")
@CrossOrigin
public class ConfigController {

    private final DynamoDbService dynamoDbService;

    public ConfigController(DynamoDbService dynamoDbService) {
        this.dynamoDbService = dynamoDbService;
    }

    @GetMapping("/status")
    public String status() {
        return "<h1>Hello World, its me the Status Controller of your friend api-configs</h1>";
    }

    @GetMapping("/{configId}")
    public Config getConfigById(@PathVariable("configId") String configId) {
        return this.dynamoDbService.getConfigById(configId);
    }

    @PostMapping()
    public ResponseEntity<Config> postConfig(@RequestBody Config config) {
        config.setConfigId(UUIDService.getUUID());
        this.dynamoDbService.saveConfig(config);
        return new ResponseEntity<>(config, HttpStatus.CREATED);
    }
}