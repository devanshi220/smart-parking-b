# Parking Lot Management API

This API provides endpoints to manage parking lots in the smart parking system. All endpoints require JWT authentication via Bearer token in the Authorization header.

## Authentication

All requests must include the Authorization header with a valid JWT token:
```
Authorization: Bearer <your-jwt-token>
```

The JWT token must contain the user's email, and the user must be authenticated.

## User Roles

- **USER**: Can only view parking lots (GET requests)
- **ADMIN**: Can view, create, and update parking lots (GET, POST, PUT requests)

## Endpoints

### 1. Get All Parking Lots
**GET** `/api/parking-lots`

Returns a list of all available parking lots.

**Headers:**
- `Authorization: Bearer <token>`

**Response:**
```json
[
  {
    "id": 1,
    "name": "Downtown Parking",
    "address": "123 Main St, City",
    "totalSlots": 100,
    "bookedSlots": 45,
    "availableSlots": 55,
    "isOpen": true
  }
]
```

### 2. Create Parking Lot
**POST** `/api/parking-lots`

Creates a new parking lot. **Requires ADMIN role.**

**Headers:**
- `Authorization: Bearer <token>`
- `Content-Type: application/json`

**Request Body:**
```json
{
  "name": "New Parking Lot",
  "address": "456 New St, City",
  "totalSlots": 150,
  "isOpen": true
}
```

**Response:**
```json
{
  "id": 2,
  "name": "New Parking Lot",
  "address": "456 New St, City",
  "totalSlots": 150,
  "bookedSlots": 0,
  "availableSlots": 150,
  "isOpen": true
}
```

### 3. Update Parking Lot
**PUT** `/api/parking-lots/{id}`

Updates an existing parking lot. **Requires ADMIN role.**

**Headers:**
- `Authorization: Bearer <token>`
- `Content-Type: application/json`

**Path Parameters:**
- `id`: The ID of the parking lot to update

**Request Body:** (All fields are optional)
```json
{
  "name": "Updated Parking Lot Name",
  "address": "Updated Address",
  "totalSlots": 200,
  "isOpen": false
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Updated Parking Lot Name",
  "address": "Updated Address",
  "totalSlots": 200,
  "bookedSlots": 45,
  "availableSlots": 155,
  "isOpen": false
}
```

### 4. Get Parking Lot Details by IDs
**POST** `/api/parking-lots/batch-details`

Returns an object with parking lot IDs as keys and name/address as values.

**Headers:**
- `Authorization: Bearer <token>`
- `Content-Type: application/json`

**Request Body:**
```json
{
  "parkingLotIds": [1, 2, 3]
}
```

**Response:**
```json
{
  "1": {
    "id": 1,
    "name": "Downtown Parking",
    "address": "123 Main St, City"
  },
  "2": {
    "id": 2,
    "name": "Airport Parking",
    "address": "456 Airport Rd, City"
  },
  "3": {
    "id": 3,
    "name": "Mall Parking",
    "address": "789 Shopping Ave, City"
  }
}
```

## Error Responses

### 401 Unauthorized
```json
"Invalid or expired token"
```

### 403 Forbidden
```json
"Only admin users can create parking lots"
```
or
```json
"Only admin users can update parking lots"
```

### 400 Bad Request
```json
"Parking lot with name 'Downtown Parking' already exists"
```
or
```json
"Total slots cannot be less than currently booked slots (45)"
```

### 404 Not Found
```json
"Parking lot with id 999 not found"
```

## Business Rules

1. **Authentication**: All endpoints require a valid JWT token
2. **Authorization**: Only ADMIN users can create or update parking lots
3. **Unique Names**: Parking lot names must be unique
4. **Slot Validation**: Total slots cannot be less than currently booked slots when updating
5. **Initial State**: New parking lots start with 0 booked slots

## Testing

To test the API:

1. First, register a user and get a JWT token from `/auth/register` or `/auth/login`
2. Set the user's role to "ADMIN" in the database if you want to test create/update operations
3. Use the token in the Authorization header for all parking lot API calls

Example curl commands:

```bash
# Get all parking lots
curl -H "Authorization: Bearer YOUR_TOKEN" \
     http://localhost:8080/api/parking-lots

# Create a parking lot (admin only)
curl -X POST \
     -H "Authorization: Bearer YOUR_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"name":"Test Lot","address":"123 Test St","totalSlots":50,"isOpen":true}' \
     http://localhost:8080/api/parking-lots

# Update a parking lot (admin only)
curl -X PUT \
     -H "Authorization: Bearer YOUR_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"name":"Updated Test Lot","totalSlots":75}' \
     http://localhost:8080/api/parking-lots/1
```
