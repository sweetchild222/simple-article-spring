# 🚀 TaskMaster API

A robust, production-ready REST API for managing team tasks, subtasks, and project workflows. Built to demonstrate RESTful best practices, secure authentication, and clean architecture.

## 🛠️ Tech Stack
* **Backend:** Node.js, Express.js
* **Database:** PostgreSQL (with Prisma ORM)
* **Authentication:** JWT (JSON Web Tokens)
* **Testing:** Jest & Supertest
* **Documentation:** OpenAPI / Swagger

---

## ⚡ Quick Start

### 1. Prerequisites
Ensure you have [Node.js (v18+)](https://nodejs.org) and [PostgreSQL](https://postgresql.org) installed.

### 2. Installation & Setup
Clone the repository and install the dependencies:
\`\`\`bash
git clone https://github.com
cd taskmaster-api
npm install
\`\`\`

### 3. Environment Variables
Create a `.env` file in the root directory and populate it with your credentials:
\`\`\`env
PORT=5000
DATABASE_URL="postgresql://user:password@localhost:5432/taskmaster_db"
JWT_SECRET="your_super_secret_jwt_key"
\`\`\`

### 4. Database Migration & Seed
\`\`\`bash
npx prisma migrate dev --name init
npm run seed
\`\`\`

### 5. Run the Server
\`\`\`bash
# Run in development mode (with hot reloading)
npm run dev
\`\`\`
The server will start spinning at `http://localhost:5000`.

---

## 🔐 Authentication

All requests to protected routes must include a Bearer token in the `Authorization` header.

\`\`\`http
Authorization: Bearer <your_jwt_token>
\`\`\`

---

## 📡 API Endpoints Reference

### Base URL
\`\`\`http
http://localhost:5000/api/v1
\`\`\`

### Tasks Resource

| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| **GET** | `/tasks` | Public | Retrieve a paginated list of all tasks. |
| **POST** | `/tasks` | Private | Create a new task. |
| **GET** | `/tasks/:id` | Public | Fetch details of a single task. |
| **PUT** | `/tasks/:id` | Private | Update an existing task completely. |
| **DELETE** | `/tasks/:id` | Private (Admin) | Permanently remove a task. |

---

## 📝 Request & Response Examples

### 1. Create a Task
* **Endpoint:** `POST /tasks`
* **Headers:** `Content-Type: application/json`, `Authorization: Bearer <token>`

#### Request Body
\`\`\`json
{
  "title": "Implement JWT Auth",
  "description": "Secure the API routes using JSON Web Tokens.",
  "dueDate": "2026-10-15"
}
\`\`\`

#### Success Response (`201 Created`)
\`\`\`json
{
  "success": true,
  "data": {
    "id": "tsk_8f3d92",
    "title": "Implement JWT Auth",
    "description": "Secure the API routes using JSON Web Tokens.",
    "status": "PENDING",
    "dueDate": "2026-10-15T00:00:00.000Z",
    "createdAt": "2026-09-14T12:00:00.000Z"
  }
}
\`\`\`

### 2. Error Response Handling
The API returns standard JSON error objects for all failed requests.

#### Example Response (`400 Bad Request`)
\`\`\`json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "The 'title' field is required and cannot be empty.",
    "details": [
      {
        "field": "title",
        "issue": "is_missing"
      }
    ]
  }
}
\`\`\`

---

## 🧪 Running Tests

This project features a comprehensive test suite covering integration and unit tests.

\`\`\`bash
# Run all tests
npm run test

# Run tests with coverage report
npm run test:coverage
\`\`\`

---

## 📖 Extended Documentation
For a complete, interactive sandbox experience featuring every query parameter, filtering constraint, and response model, run the application and visit the local host route:
* **Interactive Swagger UI:** `http://localhost:5000/api-docs`

---

## 📄 License
Distributed under the MIT License. See `LICENSE` for more information.
