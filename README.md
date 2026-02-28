# PlanMyStudy
# AI Study Planner

An AI-powered study planner application that helps students create personalized study plans with subtopics, time allocation, detailed content, and YouTube video recommendations.

## Features

- 📚 **AI-Powered Planning**: Generate study plans using OpenAI API
- 📋 **Subtopics Generation**: Automatically break down topics into manageable subtopics
- ⏱️ **Time Management**: Calculate time allocation based on daily time limit and deadline
- 📖 **Detailed Content**: Get comprehensive study content for each subtopic
- 🎥 **YouTube Links**: Receive relevant YouTube video recommendations
- 💾 **MongoDB Storage**: Persistent storage of study plans and subtopics

## Tech Stack

### Backend
- **Spring Boot 3.3.2** - Java framework
- **MongoDB** - Database
- **OpenAI API** - AI content generation

### Frontend
- **HTML5** - Structure
- **CSS3** - Styling
- **JavaScript** - Interactivity

## Prerequisites

1. **Java 17+** installed
2. **Maven** installed
3. **MongoDB** running on `localhost:27017`
4. **OpenAI API Key** (set as environment variable)

## Setup Instructions

### 1. MongoDB Setup

Make sure MongoDB is installed and running:

```bash
# Windows (if MongoDB is installed as a service, it should start automatically)
# Or start manually:
mongod

# Verify MongoDB is running
# Default port: 27017
```

### 2. Backend Setup

1. Navigate to the backend directory:
```bash
cd study-planner/backend
```

2. Set your OpenAI API key as an environment variable:

**PowerShell:**
```powershell
$env:OPENAI_API_KEY="sk-your-api-key-here"
```

**Command Prompt:**
```cmd
set OPENAI_API_KEY=sk-your-api-key-here
```

**Linux/Mac:**
```bash
export OPENAI_API_KEY="sk-your-api-key-here"
```

3. Run the Spring Boot application:
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 3. Frontend Setup

1. Navigate to the frontend directory:
```bash
cd study-planner/frontend
```

2. Open `index.html` in a web browser, or use a local server:

**Using Python:**
```bash
python -m http.server 8000
```

**Using Node.js (http-server):**
```bash
npx http-server -p 8000
```

3. Open `http://localhost:8000` in your browser

## Usage

1. **Create Study Plan** (`index.html`):
   - Enter the subject (optional)
   - Enter the topic you want to study
   - Set your daily time limit (hours per day)
   - Select a deadline date
   - Click "Generate Study Plan"

2. **View Subtopics** (`subtopics.html`):
   - Review all generated subtopics
   - See time allocation for each subtopic
   - Click on any subtopic to view detailed content

3. **Study Content** (`content.html`):
   - Read comprehensive study content
   - Access recommended YouTube videos
   - Navigate back to subtopics list

## API Endpoints

- `POST /api/plan` - Create a new study plan
- `GET /api/plan/{id}` - Get study plan details
- `GET /api/plan/{id}/subtopics` - Get all subtopics for a study plan
- `GET /api/plan/{studyPlanId}/subtopic/{subtopicId}` - Get subtopic with content and videos

## Project Structure

```
study-planner/
├── backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/studyplanner/
│   │       │   ├── config/          # Configuration classes
│   │       │   ├── controller/      # REST controllers
│   │       │   ├── model/           # Data models
│   │       │   ├── repository/      # MongoDB repositories
│   │       │   └── service/         # Business logic
│   │       └── resources/
│   │           └── application.properties
│   └── pom.xml
└── frontend/
    ├── index.html           # Input form
    ├── subtopics.html      # Subtopics list
    ├── content.html         # Content view
    ├── styles.css           # Stylesheet
    ├── app.js               # Main JavaScript
    ├── subtopics.js         # Subtopics page logic
    └── content.js           # Content page logic
```

## Configuration

### MongoDB Connection

Edit `backend/src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/studyplanner
spring.data.mongodb.database=studyplanner
```

### OpenAI Model

Default model is `gpt-4o-mini`. You can change it in `application.properties`:

```properties
openai.model=gpt-4o-mini
```

## Troubleshooting

1. **MongoDB Connection Error**: Ensure MongoDB is running on port 27017
2. **OpenAI API Error**: Verify your API key is set correctly as an environment variable
3. **CORS Error**: The backend is configured to allow all origins. If issues persist, check browser console
4. **Port Already in Use**: Change `server.port` in `application.properties` if 8080 is occupied

## Notes

- The application uses sessionStorage to maintain state between pages
- Content and video links are generated on-demand when you click a subtopic
- Study plans are saved in MongoDB and can be retrieved later

## License

This project is for educational purposes.
