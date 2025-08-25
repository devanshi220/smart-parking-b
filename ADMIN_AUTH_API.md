# Admin Authentication API

This API provides endpoints for admin-specific authentication in the Smart Parking System. The admin routes handle registration and login functionality specifically for administrator users.

## Endpoints

### 1. Admin Registration
**POST** `/auth/admin/register`

Registers a new admin user in the system. Only existing admin users can register new admins.

**Headers:**
- `Authorization: Bearer <admin-jwt-token>`

**Request Body:**
```json
{
  "firstName": "Admin",
  "lastName": "User",
  "email": "admin@example.com",
  "mobileNo": "1234567890",
  "password": "securepassword"
}
```

**Response (Success - 200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "ADMIN"
}
```

**Response (Conflict - 409):**
```json
"Email already registered"
```

**Response (Unauthorized - 401):**
```json
"Invalid or expired token"
```

**Response (Forbidden - 403):**
```json
"Only existing admins can create new admin accounts"
```

### 2. Admin Login
**POST** `/auth/admin/login`

Authenticates an existing admin user. This endpoint verifies that the user has the ADMIN role.

**Request Body:**
```json
{
  "email": "admin@example.com",
  "password": "securepassword"
}
```

**Response (Success - 200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "ADMIN"
}
```

**Response (Unauthorized - 401):**
```json
"Invalid email or password"
```

**Response (Forbidden - 403):**
```json
"Access denied: Admin privileges required"
```

## Security Considerations

1. **JWT Authentication**: The admin registration endpoint requires a valid JWT token from an existing admin user
2. **Role Verification**: Both endpoints verify that the user has the ADMIN role
3. **JWT Authorization**: Upon successful authentication, the system returns a JWT token for subsequent requests
4. **Input Validation**: All input fields are validated for required values and correct formats

## Business Rules

1. **ADMIN Role**: Users registered through this API automatically receive the "ADMIN" role
2. **Unique Email**: Admin registration will fail if the email is already registered
3. **Role Check**: Only users with the ADMIN role can log in through the admin login endpoint

## JWT Token Usage

The JWT token received upon successful authentication should be included in the Authorization header of subsequent requests:

```
Authorization: Bearer <token>
```

This token will contain the admin's email and can be verified by the backend to identify the authenticated user and confirm they have admin privileges.
