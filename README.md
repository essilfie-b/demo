# Serverless User Management System

This repository contains a serverless implementation of a user management system using AWS Lambda, DynamoDB, and other AWS services.

## Architecture

The system uses the following AWS services:
- AWS Lambda for serverless compute
- Amazon DynamoDB for user data storage
- Amazon SES for email notifications
- AWS KMS for encryption
- Amazon SQS for dead letter queues
- AWS IAM for security
- Amazon API Gateway for REST API endpoints

## Components

### User Model
- User entity with DynamoDB persistence
- Role-based access control (RBAC)
- Permission-based authorization
- Email verification system
- Password reset functionality

### Lambda Functions
1. UserAuthHandler
   - User registration
   - User login with JWT token generation

2. UserManagementHandler
   - CRUD operations for users
   - Role and permission management

3. EmailHandler
   - Email verification
   - Password reset emails

4. AccessKeyHandler
   - API access key management

## Deployment

### Prerequisites
1. AWS CLI installed and configured
2. SAM CLI installed
3. Java 11 or later
4. Maven

### Configuration
Create a `samconfig.toml` file with the following parameters:
```toml
version = 0.1
[default]
[default.deploy]
[default.deploy.parameters]
stack_name = "user-management-system"
s3_bucket = "your-deployment-bucket"
region = "your-region"
confirm_changeset = true
capabilities = "CAPABILITY_IAM"
parameter_overrides = [
  "SenderEmailParameter=your-verified-email@domain.com",
  "JwtSecret=your-secure-jwt-secret"
]
```

### Build and Deploy
1. Build the application:
```bash
mvn clean package
```

2. Deploy using SAM:
```bash
sam deploy --guided
```

## API Endpoints

### Authentication
- POST /auth/register - Register new user
- POST /auth/login - User login

### User Management
- GET /users/{id} - Get user details
- PUT /users/{id} - Update user
- DELETE /users/{id} - Delete user

### Access Keys
- POST /access-keys - Create new access key
- GET /access-keys - List access keys
- DELETE /access-keys/{id} - Delete access key

### Email Operations
- POST /email - Send email (verification, reset password)

## Security

The system implements several security measures:
1. JWT-based authentication
2. Role-based access control
3. KMS encryption for sensitive data
4. Secure password hashing
5. Email verification
6. API access key management

## Data Model

### User
```json
{
  "id": "string",
  "username": "string",
  "email": "string",
  "password": "string (hashed)",
  "role": "enum (USER/ADMIN)",
  "accountVerified": "boolean",
  "verificationCode": "string",
  "verificationCodeExpiration": "datetime",
  "resetToken": "string"
}
```

### Role and Permissions
- USER role: user:create, user:read
- ADMIN role: admin:create, admin:read, admin:update, admin:delete

## Development

### Local Testing
1. Start local DynamoDB:
```bash
docker-compose up dynamodb-local
```

2. Run tests:
```bash
mvn test
```

3. Invoke functions locally:
```bash
sam local invoke UserAuthFunction --event events/register.json
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.