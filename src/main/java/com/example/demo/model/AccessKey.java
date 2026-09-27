package com.example.demo.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;

import java.security.SecureRandom;
import java.time.LocalDate;

@Data
@DynamoDBTable(tableName = "accesskeys")
public class AccessKey {
    public enum Status {
        ACTIVE("Active"),
        EXPIRED("Expired"),
        REVOKED("Revoked");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private String id;
    private String accessKeyValue;
    private Status status = Status.ACTIVE;
    private String dateOfProcurement;
    private String expiryDate;
    private String userId;

    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }

    @DynamoDBAttribute(attributeName = "access_key_value")
    public String getAccessKeyValue() {
        return accessKeyValue;
    }

    @DynamoDBAttribute(attributeName = "status")
    @DynamoDBTypeConvertedEnum
    public Status getStatus() {
        return status;
    }

    @DynamoDBAttribute(attributeName = "date_of_procurement")
    public String getDateOfProcurement() {
        return dateOfProcurement;
    }

    @DynamoDBAttribute(attributeName = "expiry_date")
    public String getExpiryDate() {
        return expiryDate;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "UserIdIndex", attributeName = "user_id")
    public String getUserId() {
        return userId;
    }

    public void initializeNewKey() {
        this.dateOfProcurement = LocalDate.now().toString();
        this.expiryDate = LocalDate.now().plusDays(30).toString();
        this.accessKeyValue = generateKey();
    }

    public void checkExpiry() {
        if (LocalDate.parse(expiryDate).isBefore(LocalDate.now())) {
            status = Status.EXPIRED;
        }
    }

    private String generateKey() {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int length = 32;
        StringBuilder accessKey = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++)
            accessKey.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));

        return accessKey.toString();
    }
}