package com.example.demo.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

public class UserManagementHandler extends LambdaHandler {
    private final UserService userService;

    @Autowired
    public UserManagementHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected APIGatewayProxyResponseEvent processRequest(APIGatewayProxyRequestEvent input, Context context) throws Exception {
        String path = input.getPath();
        String method = input.getHttpMethod();
        Map<String, String> pathParameters = input.getPathParameters();

        if (pathParameters != null && pathParameters.containsKey("id")) {
            String userId = pathParameters.get("id");

            switch (method) {
                case "GET":
                    return handleGetUser(userId);
                case "PUT":
                    return handleUpdateUser(userId, input.getBody());
                case "DELETE":
                    return handleDeleteUser(userId);
                default:
                    return createResponse(405, "{\"message\":\"Method not allowed\"}");
            }
        }

        return createResponse(400, "{\"message\":\"Invalid request\"}");
    }

    private APIGatewayProxyResponseEvent handleGetUser(String userId) {
        try {
            Optional<User> user = userService.getUserById(userId);
            return user.map(u -> createResponse(200, u))
                    .orElse(createResponse(404, "{\"message\":\"User not found\"}"));
        } catch (Exception e) {
            return createResponse(500, "{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private APIGatewayProxyResponseEvent handleUpdateUser(String userId, String body) throws Exception {
        try {
            User updatedUser = objectMapper.readValue(body, User.class);
            updatedUser.setId(userId);
            User saved = userService.updateUser(updatedUser);
            return createResponse(200, saved);
        } catch (Exception e) {
            return createResponse(400, "{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private APIGatewayProxyResponseEvent handleDeleteUser(String userId) {
        try {
            userService.deleteUser(userId);
            return createResponse(204, "");
        } catch (Exception e) {
            return createResponse(404, "{\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}