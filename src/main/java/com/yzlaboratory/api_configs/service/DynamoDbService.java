package com.yzlaboratory.api_configs.service;

import com.yzlaboratory.api_configs.entity.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Service
public class DynamoDbService {

    private final DynamoDbTable<Config> configTable;

    public DynamoDbService(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                           @Value("${DYNAMODB_TABLE_NAME}") String tableName
    ) {
        this.configTable = dynamoDbEnhancedClient.table(
                tableName,
                TableSchema.fromBean(Config.class)
        );
    }

    public Config getConfigById(String configId) {
        return configTable.getItem(r -> r.key(k -> k.partitionValue(configId)));
    }

    public void saveConfig(Config config) {
        configTable.putItem(config);
    }
}