# Booking Management API

This API provides endpoints to manage parking lot bookings in the smart parking system. All endpoints require JWT authentication via Bearer token in the Authorization header.

## Authentication

All requests must include the Authorization header with a valid JWT token:
```
Authorization: Bearer <your-jwt-token>
```

The JWT token must contain the user's email, and the user must be authenticated.

## Endpoints

### 1. Get User Bookings
**GET** `/api/bookings`

Returns all bookings for the currently logged-in user.

**Headers:**
- `Authorization: Bearer <token>`

**Response:**
```json
[
  {
    "id": 1,
    "userId": 123,
    "parkingLotId": 1,
    "parkingLotName": "Downtown Parking",
    "parkingLotAddress": "123 Main St, City",
    "ownerName": "John Doe",
    "mobileNo": "1234567890",
    "vehicalNo": "ABC123",
    "vehicalType": "Car",
    "timingSlot": "09:00-17:00",
    "createdAt": "2025-08-19T10:30:00",
    "status": "PENDING"
  }
]
```

### 2. Create Booking
**POST** `/api/bookings`

Creates a new parking booking for the logged-in user.

**Headers:**
- `Authorization: Bearer <token>`
- `Content-Type: application/json`

**Request Body:**
```json
{
  "parkingLotId": 1,
  "ownerName": "John Doe",
  "mobileNo": "1234567890",
  "vehicalNo": "ABC123",
  "vehicalType": "Car",
  "timingSlot": "09:00-17:00"
}
```

**Response:**
```json
{
  "id": 1,
  "userId": 123,
  "parkingLotId": 1,
  "parkingLotName": "Downtown Parking",
  "parkingLotAddress": "123 Main St, City",
  "ownerName": "John Doe",
  "mobileNo": "1234567890",
  "vehicalNo": "ABC123",
  "vehicalType": "Car",
  "timingSlot": "09:00-17:00",
  "createdAt": "2025-08-19T10:30:00",
  "status": "PENDING"
}
```

### 3. Update Booking Status
**PUT** `/api/bookings/{id}/status`

Updates the status of an existing booking for the logged-in user.

**Headers:**
- `Authorization: Bearer <token>`
- `Content-Type: application/json`

**Path Parameters:**
- `id`: The ID of the booking to update

**Request Body:**
```json
{
  "status": "CONFIRMED"
}
```

**Response:**
```json
{
  "id": 1,
  "userId": 123,
  "parkingLotId": 1,
  "parkingLotName": "Downtown Parking",
  "parkingLotAddress": "123 Main St, City",
  "ownerName": "John Doe",
  "mobileNo": "1234567890",
  "vehicalNo": "ABC123",
  "vehicalType": "Car",
  "timingSlot": "09:00-17:00",
  "createdAt": "2025-08-19T10:30:00",
  "status": "CONFIRMED"
}
```

## Booking Status Values

The booking system supports the following status values:

- **PENDING**: Initial status when booking is created (default)
- **CONFIRMED**: Booking has been confirmed
- **COMPLETED**: Parking session has been completed
- **CANCELLED**: Booking has been cancelled

## Status Transition Rules

Valid status transitions are enforced by the system:

- **PENDING** → **CONFIRMED** or **CANCELLED**
- **CONFIRMED** → **COMPLETED** or **CANCELLED**
- **COMPLETED** → No further transitions allowed (final state)
- **CANCELLED** → No further transitions allowed (final state)

## Request Field Validation

### Create Booking Request
- `parkingLotId`: **Required** - Must be a valid parking lot ID
- `ownerName`: **Required** - Cannot be blank
- `mobileNo`: **Required** - Cannot be blank
- `vehicalNo`: **Required** - Cannot be blank, vehicle number
- `vehicalType`: **Required** - Cannot be blank (e.g., "Car", "Motorcycle", "SUV")
- `timingSlot`: **Required** - Cannot be blank, time slot for parking

### Update Booking Status Request
- `status`: **Required** - Must be a valid status value (PENDING, CONFIRMED, COMPLETED, CANCELLED)

## Error Responses

### 401 Unauthorized
```json
"Invalid or expired token"
```

### 400 Bad Request

**User not found:**
```json
"User not found"
```

**Parking lot not found:**
```json
"Parking lot not found"
```

**Parking lot closed:**
```json
"Parking lot is currently closed"
```

**No available slots:**
```json
"No available slots in this parking lot"
```

**Booking not found:**
```json
"Booking not found"
```

**Unauthorized booking access:**
```json
"You can only update your own bookings"
```

**Invalid status transition:**
```json
"Invalid status transition from COMPLETED to PENDING"
```

**Validation errors:**
```json
{
  "parkingLotId": "Parking lot ID is required",
  "ownerName": "Owner name is required",
  "mobileNo": "Mobile number is required",
  "vehicalNo": "Vehicle number is required",
  "vehicalType": "Vehicle type is required",
  "timingSlot": "Timing slot is required"
}
```

**Status update validation:**
```json
{
  "status": "Status is required"
}
```

## Business Rules

1. **Authentication**: All endpoints require a valid JWT token
2. **User-specific data**: Users can only see their own bookings
3. **Slot availability**: Bookings are only allowed if slots are available
4. **Parking lot status**: Bookings are only allowed for open parking lots
5. **Automatic slot management**: Booked slots count is automatically updated when creating bookings
6. **Booking details**: Each booking includes complete parking lot information for easy reference
7. **Status management**: Bookings start with "PENDING" status and follow defined transition rules
8. **User ownership**: Users can only update the status of their own bookings
9. **Slot release**: Cancelling a booking automatically releases the parking slot

## Integration with Parking Lots

The booking system automatically integrates with the parking lot management:
- Validates parking lot existence and availability
- Updates booked slots count when creating bookings
- Includes parking lot name and address in booking responses
- Checks if parking lot is open before allowing bookings

## Example Usage

### Get User Bookings
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
     http://localhost:8080/api/bookings
```

### Create a New Booking
```bash
curl -X POST \
     -H "Authorization: Bearer YOUR_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{
       "parkingLotId": 1,
       "ownerName": "John Doe",
       "mobileNo": "1234567890",
       "vehicalNo": "ABC123",
       "vehicalType": "Car",
       "timingSlot": "09:00-17:00"
     }' \
     http://localhost:8080/api/bookings
```

### Update Booking Status
```bash
curl -X PUT \
     -H "Authorization: Bearer YOUR_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"status": "CONFIRMED"}' \
     http://localhost:8080/api/bookings/1/status
```

## Database Schema Updates

The booking system includes the following enhancements:
- Added `parkingLotId` field to the Booking entity to link bookings with parking lots
- Added `status` field with default value "PENDING" to track booking lifecycle
- Automatic tracking of booked slots in parking lots
- Real-time slot availability checking
- Slot release mechanism when bookings are cancelled

## Workflow

1. **User Authentication**: User logs in and gets JWT token
2. **View Available Lots**: User calls GET `/api/parking-lots` to see available parking lots
3. **Create Booking**: User calls POST `/api/bookings` with desired parking lot and details
4. **View Bookings**: User calls GET `/api/bookings` to see their current bookings
5. **Update Status**: User calls PUT `/api/bookings/{id}/status` to update booking status (confirm, complete, cancel)
6. **System Updates**: Booking creation automatically updates parking lot's booked slots count, cancellation releases slots
