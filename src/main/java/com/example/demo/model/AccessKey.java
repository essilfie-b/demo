package com.example.demo.model;


import jakarta.persistence.*;
import lombok.Data;

import java.security.SecureRandom;
import java.time.LocalDate;


@Data
@Entity
@Table(name = "accesskeys")
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

    @Id
    @Column(name = "key_id", length = 45)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int accessKeyId;

    @Column(name = "access_key_value", length = 32)
    private String accessKeyValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private Status status = Status.ACTIVE;

    @Column(name = "date_of_procurement")
    private LocalDate dateOfProcurement;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @PrePersist
    protected void onCreate() {
        dateOfProcurement = LocalDate.now();
        expiryDate = dateOfProcurement.plusDays(30);
        accessKeyValue = generateKey();
    }

    @PostLoad
    protected void checkExpiry() {
        if (expiryDate.isBefore(LocalDate.now())) {
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
