# Smart Mock Interview

## AI-Powered Full-Stack Interview Simulation Platform

Smart Mock Interview is a full-stack web application that simulates real technical interviews using AI. It provides users with a realistic interview experience through dynamic question generation, real-time interaction, and session tracking. The platform includes secure JWT-based authentication and supports both authenticated and anonymous interview sessions.

---

## 🚀 Features

### 🤖 AI Integration
- Dynamic interview question generation using **Spring AI**
- Context-aware follow-up questions
- Prompt-driven interview flow using LLMs
- Real-time conversational interaction
- AI-powered feedback and scoring system

### 🔐 Authentication & Security
- JWT-based authentication (Access + Refresh tokens)
- Secure login and registration system
- Refresh token stored in HttpOnly cookies
- Protected endpoints for user-specific data

### 🧠 Interview System
- Create and manage interview sessions
- Unique session IDs for tracking interviews
- Supports both authenticated and anonymous users
- Persistent session state and history

### 📊 User Features
- Interview history tracking
- AI-generated feedback and performance insights
- Personalized experience for authenticated users

---

## 🛠️ Tech Stack

### Backend
- Java + Spring Boot
- Spring Security
- Spring AI (LLM integration)
- JWT (JSON Web Tokens)
- RESTful APIs
- In-memory persistence (extendable to database)

### Frontend
- Nuxt (Vue.js)
- Pinia (state management)
- Fetch API
- Modular and scalable architecture

---

## 📂 Project Structure

### Backend (Spring Boot)
```text
com.smartmock.interview
├── api
├── application
├── auth
├── config
├── domain
├── infrastructure
```

### Frontend (Nuxt)
```text
/components
/composables
/layouts
/middleware
/pages
/plugins
/public
/stores
```

---

## 🔄 Authentication Flow

1. User logs in via `/auth/login`
2. Server returns:
   - Access Token (short-lived)
   - Refresh Token (HttpOnly cookie)
3. Access token is used in requests:
   Authorization: Bearer <token>
4. When the access token expires:
   - Frontend calls `/auth/refresh`
   - A new access token is issued
5. Logout invalidates the refresh token

---

## 🧪 Interview Flow

1. User starts an interview session
2. A unique `sessionId` is generated
3. AI generates the first question using Spring AI
4. User responds in real time
5. AI evaluates responses and generates follow-up questions
6. Feedback and scoring are provided
7. Session is saved and can be retrieved later

---

## ⚙️ Setup & Installation

### Backend
cd backend  
./mvnw spring-boot:run  

### Frontend
cd frontend  
npm install  
npm run dev  

---

## 🔌 API Endpoints (Overview)

### Authentication
- POST /auth/register
- POST /auth/login
- POST /auth/refresh
- POST /auth/logout

### Interview
- POST /interview/start
- POST /interview/answer
- GET /interview/{sessionId}

### Analytics
- GET /api/interview/analytics

### History
- GET /api/interview/history
- GET /api/interview/history/{sessionId}

---

## 📈 Future Improvements
- Database integration (PostgreSQL / MongoDB)
- Voice-based interview simulations
- Advanced analytics dashboard
- Role-based access control

---

## 💡 Key Highlights
- Clean architecture with strong separation of concerns
- Secure JWT authentication with refresh token flow
- AI-driven interview experience using Spring AI
- Built-in AI feedback and scoring system
- Supports both anonymous and authenticated users
- Scalable and extensible system design

---
<div align="center">
  <img src="https://github.com/musseGkel/Github--demo/blob/main/SmartMockInterview.png"  />
</div>
---

## 📄 License
This project is open-source and available under the MIT License.
