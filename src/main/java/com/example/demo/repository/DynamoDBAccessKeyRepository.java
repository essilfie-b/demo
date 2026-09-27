package com.example.demo.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.example.demo.model.AccessKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DynamoDBAccessKeyRepository {
    private final DynamoDB dynamoDB;
    private final Table table;

    public DynamoDBAccessKeyRepository() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        this.dynamoDB = new DynamoDB(client);
        this.table = dynamoDB.getTable(System.getenv("ACCESS_KEYS_TABLE"));
    }

    public AccessKey save(AccessKey accessKey) {
        if (accessKey.getId() == null) {
            accessKey.setId(UUID.randomUUID().toString());
        }

        Item item = new Item()
                .withPrimaryKey("id", accessKey.getId())
                .withString("userId", accessKey.getUserId())
                .withString("key", accessKey.getKey())
                .withBoolean("active", accessKey.isActive());

        table.putItem(item);
        return accessKey;
    }

    public Optional<AccessKey> findById(String id) {
        Item item = table.getItem("id", id);
        if (item == null) {
            return Optional.empty();
        }

        AccessKey accessKey = new AccessKey();
        accessKey.setId(item.getString("id"));
        accessKey.setUserId(item.getString("userId"));
        accessKey.setKey(item.getString("key"));
        accessKey.setActive(item.getBoolean("active"));
        return Optional.of(accessKey);
    }

    public List<AccessKey> findByUserId(String userId) {
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("userId = :userId")
                .withValueMap(new com.amazonaws.services.dynamodbv2.document.spec.ValueMap()
                        .withString(":userId", userId));

        List<AccessKey> result = new ArrayList<>();
        table.getIndex("UserIdIndex").query(spec).forEach(item -> {
            AccessKey accessKey = new AccessKey();
            accessKey.setId(item.getString("id"));
            accessKey.setUserId(item.getString("userId"));
            accessKey.setKey(item.getString("key"));
            accessKey.setActive(item.getBoolean("active"));
            result.add(accessKey);
        });

        return result;
    }

    public void delete(AccessKey accessKey) {
        table.deleteItem("id", accessKey.getId());
    }
}