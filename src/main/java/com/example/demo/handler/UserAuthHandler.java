package com.example.demo.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.demo.model.User;
import com.example.demo.request.LoginUserRequest;
import com.example.demo.request.RegisterUserRequest;
import com.example.demo.response.LoginResponse;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class UserAuthHandler extends LambdaHandler {
    private final UserService userService;

    @Autowired
    public UserAuthHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected APIGatewayProxyResponseEvent processRequest(APIGatewayProxyRequestEvent input, Context context) throws Exception {
        String path = input.getPath();
        String method = input.getHttpMethod();

        if ("/auth/login".equals(path) && "POST".equals(method)) {
            return handleLogin(input);
        } else if ("/auth/register".equals(path) && "POST".equals(method)) {
            return handleRegister(input);
        }

        return createResponse(404, "{\"message\":\"Not Found\"}");
    }

    private APIGatewayProxyResponseEvent handleLogin(APIGatewayProxyRequestEvent input) throws Exception {
        LoginUserRequest loginRequest = objectMapper.readValue(input.getBody(), LoginUserRequest.class);
        try {
            LoginResponse response = userService.login(loginRequest);
            return createResponse(200, response);
        } catch (Exception e) {
            return createResponse(401, "{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    private APIGatewayProxyResponseEvent handleRegister(APIGatewayProxyRequestEvent input) throws Exception {
        RegisterUserRequest registerRequest = objectMapper.readValue(input.getBody(), RegisterUserRequest.class);
        try {
            User savedUser = userService.register(registerRequest);
            return createResponse(201, savedUser);
        } catch (Exception e) {
            return createResponse(400, "{\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}

