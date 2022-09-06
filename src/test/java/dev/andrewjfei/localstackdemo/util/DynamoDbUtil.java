package dev.andrewjfei.localstackdemo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DynamoDbUtil {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDbUtil.class);

    public static DynamoDbClient getDynamoDbClient(LocalStackContainer localstackContainer) {
        return DynamoDbClient
                .builder()
                .endpointOverride(localstackContainer.getEndpointOverride(LocalStackContainer.Service.DYNAMODB))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(localstackContainer.getAccessKey(), localstackContainer.getSecretKey())
                        )
                )
                .region(Region.of(localstackContainer.getRegion()))
                .build();
    }

    public static String createTable(DynamoDbClient dynamoDbClient, String tableName, String key) {
        CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName(key)
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName(key)
                        .keyType(KeyType.HASH)
                        .build())
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(5L)
                        .writeCapacityUnits(5L)
                        .build())
                .tableName(tableName)
                .build();

        try {
            CreateTableResponse createTableResponse = dynamoDbClient.createTable(createTableRequest);

            return createTableResponse.tableDescription().tableName();
        } catch (DynamoDbException e) {
            logger.error("Error creating table. {}", e.awsErrorDetails().errorMessage());
            return null;
        }
    }

    public static List<String> getAllTables(DynamoDbClient dynamoDbClient){
        boolean moreTables = true;
        String lastName = null;
        List<String> tableList = new ArrayList<>();

        while (moreTables) {
            try {
                ListTablesRequest request;

                if (lastName == null) {
                    request = ListTablesRequest.builder().build();
                } else {
                    request = ListTablesRequest.builder()
                            .exclusiveStartTableName(lastName).build();
                }

                ListTablesResponse listTablesResponse = dynamoDbClient.listTables(request);

                tableList.addAll(listTablesResponse.tableNames());

                lastName = listTablesResponse.lastEvaluatedTableName();

                if (lastName == null) {
                    moreTables = false;
                }
            } catch (DynamoDbException e) {
                logger.error("Error getting all tables. {}", e.awsErrorDetails().errorMessage());
                return Arrays.asList();
            }
        }

        return tableList;
    }
}
