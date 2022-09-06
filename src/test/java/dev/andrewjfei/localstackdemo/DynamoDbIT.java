package dev.andrewjfei.localstackdemo;

import dev.andrewjfei.localstackdemo.util.DynamoDbUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DynamoDbIT extends BaseIT {

    private final String DYNAMO_DB_TABLE_NAME = "localstack-demo";
    private final String DYNAMO_DB_TABLE_KEY = "localstack-demo-key";
    private final int NUM_OF_DYNAMO_DB_TABLES = 0;

    private final DynamoDbClient dynamoDbClient = DynamoDbUtil.getDynamoDbClient(localstackContainer);

    @BeforeEach
    public void setUp() {
        DynamoDbUtil.createTable(dynamoDbClient, DYNAMO_DB_TABLE_NAME, DYNAMO_DB_TABLE_KEY);
    }

    @Test
    public void fetchAllTables_success() {
        List<String> dynamoDbTableList = DynamoDbUtil.getAllTables(dynamoDbClient);

        assertEquals(NUM_OF_DYNAMO_DB_TABLES + 1, dynamoDbTableList.size());
        assertTrue(!dynamoDbTableList.stream().filter((dynamoDbTable) -> dynamoDbTable.equals(DYNAMO_DB_TABLE_NAME)).collect(Collectors.toList()).isEmpty());
    }
}
