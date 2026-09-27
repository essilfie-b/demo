package com.example.demo.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.demo.model.AccessKey;
import com.example.demo.repository.DynamoDBAccessKeyRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AccessKeyHandler extends LambdaHandler {
    private final DynamoDBAccessKeyRepository accessKeyRepository;

    public AccessKeyHandler() {
        this.accessKeyRepository = new DynamoDBAccessKeyRepository();
    }

    @Override
    protected APIGatewayProxyResponseEvent processRequest(APIGatewayProxyRequestEvent input, Context context) throws Exception {
        String path = input.getPath();
        String method = input.getHttpMethod();
        Map<String, String> pathParameters = input.getPathParameters();

        if ("/access-keys".equals(path)) {
            switch (method) {
                case "POST":
                    return handleCreateKey(input);
                case "GET":
                    return handleGetKeys(input);
                default:
                    return createResponse(405, "{\"message\":\"Method not allowed\"}");
            }
        } else if (pathParameters != null && pathParameters.containsKey("id")) {
            String keyId = pathParameters.get("id");
            if ("DELETE".equals(method)) {
                return handleDeleteKey(keyId);
            }
        }

        return createResponse(404, "{\"message\":\"Not found\"}");
    }

    private APIGatewayProxyResponseEvent handleCreateKey(APIGatewayProxyRequestEvent input) throws Exception {
        Map<String, String> claims = (Map<String, String>) input.getRequestContext().getAuthorizer().get("claims");
        String userId = claims.get("sub");

        AccessKey accessKey = new AccessKey();
        accessKey.setUserId(userId);
        accessKey.setKey(UUID.randomUUID().toString());
        accessKey.setActive(true);

        AccessKey saved = accessKeyRepository.save(accessKey);
        return createResponse(201, saved);
    }

    private APIGatewayProxyResponseEvent handleGetKeys(APIGatewayProxyRequestEvent input) {
        Map<String, String> claims = (Map<String, String>) input.getRequestContext().getAuthorizer().get("claims");
        String userId = claims.get("sub");

        List<AccessKey> keys = accessKeyRepository.findByUserId(userId);
        return createResponse(200, keys);
    }

    private APIGatewayProxyResponseEvent handleDeleteKey(String keyId) {
        return accessKeyRepository.findById(keyId)
                .map(key -> {
                    accessKeyRepository.delete(key);
                    return createResponse(204, "");
                })
                .orElse(createResponse(404, "{\"message\":\"Access key not found\"}"));
    }
}