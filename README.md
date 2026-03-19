# 🏥 SmartHospital Management System (Spring Boot)

A backend-based Hospital Management System built using **Spring Boot**, focusing on secure authentication, scalable architecture, and real-world production practices.

---

        ## 🚀 Features

* 🔐 JWT Authentication (Login / Signup)
* 🔄 Refresh Token Implementation
* 🔑 Google OAuth2 Login
* ⚡ Redis Caching for performance
* 🚪 Secure Logout (Token invalidation using Redis)
* 🧠 Role-Based Authorization (Admin, Doctor, Patient)
* 📦 RESTful APIs
* 🛡️ Global Exception Handling
* 📊 Structured Logging

---

        ## 🛠️ Tech Stack

* **Language:** Java 17+
* **Framework:** Spring Boot 3.x
* **Security:** Spring Security, JWT, OAuth2
* **Database:** MySQL
* **Caching:** Redis
* **Build Tool:** Maven
* **API Testing:** Postman
* **Messaging:** Gmail SMTP

---

        ## 📂 Project Structure

```
src/
        ├── controller/
        ├── service/
        ├── repository/
        ├── entity/
        ├── security/
        ├── config/
        └── exception/
        ```

        ---

        ## ⚙️ Setup & Run

### 1️⃣ Clone the repository

```
git clone https://github.com/yashjadhav8903-git/SmartHospitalManagementSystem.git <br>
cd your-repo-name
```

        ### 2️⃣ Configure application.yml
spring:
datasource:
url: jdbc:postgresql://localhost:5432/hms_db
username: your_username
password: your_password

Update your database and Redis config:

        ```
spring.datasource.url=your-db-url
spring.datasource.username=your-username
spring.datasource.password=your-password

spring.redis.host=localhost
spring.redis.port=6379
        ```


  ### 3️⃣ Run the application
mvn spring-boot:run
```
mvn spring-boot:run


        
## 4️⃣ Environment Variables:
OAuth aur Email ke liye ye variables set karein:

1. GOOGLE_CLIENT_ID
2. GOOGLE_CLIENT_SECRET
3. EMAIL_PASSWORD (App Password)


## 🔐 Authentication Flow

1. User logs in → receives JWT + Refresh Token
2. JWT used for API access
3. Refresh Token stored in Redis
4. Logout → Refresh Token invalidated from Redis

---

        ## 📌 API Endpoints (Sample)

        | Method | Endpoint          | Description |
        | ------ | ----------------- | ----------- |
        | POST   | /auth/v5/login    | User login  |
        | POST   | /auth/v5/register | User signup |
        | POST   | /auth/v5/refresh  | Refresh JWT |
        | POST   | /auth/v5/logout   | Logout user |

        ---

        ## 💡 Highlights


* Implemented **Redis-based session management**
* Designed **secure authentication system**
* Followed **clean architecture principles**
* Built with **scalability in mind**

        ---

        ## 📈 Future Improvements

- 🌐 Deploy the application on cloud platforms (AWS / Render / Railway)
- 🚀 Make APIs live and accessible over the web
- 📄 Integrate Swagger for API documentation

---

 ## 👨‍💻 Author
**Yash Jadhav**

        ---

        ⭐ If you like this project, give it a star!

        ---
