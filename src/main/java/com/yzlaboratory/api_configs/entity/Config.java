package com.yzlaboratory.api_configs.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.math.BigDecimal;
import java.util.List;

@DynamoDbBean
public class Config {
    private String configId;
    private List<String> config;
    private BigDecimal price;
    private String modelId;

    public Config() {}

    @DynamoDbPartitionKey
    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public List<String> getConfig() {
        return config;
    }

    public void setConfig(List<String> config) {
        this.config = config;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
}
