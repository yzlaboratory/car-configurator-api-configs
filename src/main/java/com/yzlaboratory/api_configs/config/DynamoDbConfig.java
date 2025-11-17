package com.yzlaboratory.api_configs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbConfig {

    /**
     * Erstellt den Standard-DynamoDbClient als Spring Bean.
     * Das SDK findet die Anmeldeinformationen (Credentials) automatisch
     * (z.B. aus der ECS-Task-Rolle oder Umgebungsvariablen).
     */
    @Bean
    public DynamoDbClient dynamoDbClient(@Value("${AWS_REGION}") String region) {
        return DynamoDbClient.builder()
                // Es ist eine gute Praxis, die Region explizit festzulegen,
                // aber wenn sie in der ECS-Umgebung gesetzt ist, ist es optional.
                .region(Region.of(region))
                .build();
    }

    /**
     * Erstellt den Enhanced Client als Spring Bean,
     * der automatisch den DynamoDbClient-Bean (oben) verwendet.
     */
    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }
}