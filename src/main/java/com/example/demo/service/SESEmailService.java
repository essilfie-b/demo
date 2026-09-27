package com.example.demo.service;

import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;
import software.amazon.awssdk.regions.Region;

public class SESEmailService {
    private final SesClient sesClient;

    public SESEmailService() {
        this.sesClient = SesClient.builder()
                .region(Region.US_EAST_1) // or your preferred region
                .build();
    }

    public void sendEmail(String to, String subject, String htmlBody) {
        try {
            SendEmailRequest request = SendEmailRequest.builder()
                .destination(Destination.builder()
                    .toAddresses(to)
                    .build())
                .message(Message.builder()
                    .subject(Content.builder()
                        .data(subject)
                        .charset("UTF-8")
                        .build())
                    .body(Body.builder()
                        .html(Content.builder()
                            .data(htmlBody)
                            .charset("UTF-8")
                            .build())
                        .build())
                    .build())
                .source(System.getenv("SENDER_EMAIL")) // Configure this in the Lambda environment
                .build();

            sesClient.sendEmail(request);
            System.out.println("Email sent successfully to: " + to);
        } catch (SesException e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw e;
        }
    }
}