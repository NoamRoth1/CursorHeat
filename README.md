# CursorHeat - Mouse Interaction Analytics

CursorHeat is a powerful analytics tool that tracks and visualizes mouse interactions on websites, providing insights into user behavior through heatmaps and detailed event tracking.

## Features

- Real-time mouse event tracking
  - Precise click tracking with coordinates
  - Mouse movement heatmaps
  - Scroll depth analysis
  - Session duration tracking
- Interactive heatmap visualization
  - Customizable heat intensity
  - Time-based filtering
  - Project-specific views
- Session-based analytics
  - Detailed session timelines
  - User journey analysis
  - Event sequence tracking
- Project-based organization
  - Multiple project support
  - Project-specific API keys
  - Cross-project analytics
- Secure API key authentication
  - JWT-based user authentication
  - API key management
  - Role-based access control
- Rate limiting and security features
  - Request throttling
  - CORS protection
  - Data encryption
- Beautiful dashboard interface
  - Real-time data updates
  - Interactive charts
  - Customizable date ranges
  - Responsive design

## Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher
- Node.js 14 or higher (for development)

## Setup Instructions

1. **Database Setup**
   ```bash
   # Create PostgreSQL database
   createdb cursorheat
   ```

2. **Configuration**
   - Update `src/main/resources/application.properties` with your database credentials
   - Set a secure JWT secret in the environment variable `JWT_SECRET`

3. **Build and Run**
   ```bash
   # Build the application
   mvn clean install

   # Run the application
   mvn spring-boot:run
   ```

4. **Initial Access**
   - Default test user:
     - Username: testuser
     - Password: test123
   - API Key: test-api-key-123

## Integration

Add the tracking snippet to your website:

```html
<script src="http://localhost:8080/api/v1/tracker.js"></script>
<script>
    CursorHeat.init({
        apiKey: 'your-api-key',
        projectId: 'your-project-id',
        options: {
            trackClicks: true,      // Track mouse clicks
            trackMoves: true,       // Track mouse movements
            trackScrolls: true,     // Track scroll events
            sampleRate: 100,        // Sample rate for move events (ms)
            heatmapRadius: 20,      // Heatmap point radius
            sessionTimeout: 1800    // Session timeout in seconds
        }
    });
</script>
```

## API Documentation

### Authentication Endpoints
- POST `/api/v1/auth/register` - Register new user
- POST `/api/v1/auth/login` - Login user

### Event Endpoints
- POST `/api/v1/events` - Track mouse events
- GET `/api/v1/events/sessions` - Get session data
- GET `/api/v1/events/heatmap` - Get heatmap data

### Analytics Endpoints
- GET `/api/v1/analytics/sessions` - Get session analytics
- GET `/api/v1/analytics/events` - Get event analytics
- GET `/api/v1/analytics/heatmap` - Get heatmap data

## Security

- JWT-based authentication
- API key validation
- Rate limiting (100 requests per minute)
- CORS configuration
- Role-based access control

## Development

### Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── org/cursorheat/
│   │       ├── config/      # Configuration classes
│   │       ├── controller/  # REST controllers
│   │       ├── model/       # Entity classes
│   │       ├── repository/  # Data repositories
│   │       ├── security/    # Security configuration
│   │       └── service/     # Business logic
│   └── resources/
│       ├── static/         # Static assets
│       │   ├── js/        # JavaScript files
│       │   └── css/       # Stylesheets
│       └── templates/      # HTML templates
```

### Testing Mouse Click Tracking

To verify that mouse click tracking is working correctly:

1. Start the application:
```bash
mvn spring-boot:run
```

2. Open the test page:
```bash
curl http://localhost:8080/test.html
```

3. Monitor the events in the dashboard:
```bash
curl http://localhost:8080/api/v1/events?projectId=test
```

The response should include click events with coordinates and timestamps.

### Running Tests
```bash
mvn test
```

## License

MIT License 