package com.example.demo.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.demo.service.SESEmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class EmailHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final SESEmailService emailService;
    private final ObjectMapper objectMapper;

    public EmailHandler() {
        this.emailService = new SESEmailService();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setHeaders(new HashMap<>());
        response.getHeaders().put("Content-Type", "application/json");

        try {
            Map<String, String> body = objectMapper.readValue(input.getBody(), Map.class);
            String to = body.get("to");
            String subject = body.get("subject");
            String htmlBody = body.get("htmlBody");

            if (to == null || subject == null || htmlBody == null) {
                return response
                    .withStatusCode(400)
                    .withBody("{\"error\": \"Missing required fields: to, subject, htmlBody\"}");
            }

            emailService.sendEmail(to, subject, htmlBody);

            return response
                .withStatusCode(200)
                .withBody("{\"message\": \"Email sent successfully\"}");

        } catch (Exception e) {
            context.getLogger().log("Error processing request: " + e.getMessage());
            return response
                .withStatusCode(500)
                .withBody("{\"error\": \"Error sending email: " + e.getMessage() + "\"}");
        }
    }
}