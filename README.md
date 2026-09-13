# 🏥 SmartHospital Management System (Spring Boot)

> A high-performance, containerized, production-ready Monolithic Hospital Management System built with enterprise-grade Java backend technologies, load balancing, and automated SSL security.
---

## 🏗️ System Architecture Diagram

The application is deployed on an **AWS EC2** instance, orchestrated via **Docker Compose**, and fronted by an **Nginx** reverse proxy handling SSL termination and load balancing across dual Spring Boot instances.

```text
 [ User / Browser ] (HTTPS via smart-hms-yash.duckdns.org)
         │
         │  (Port 443 / SSL Encrypted Traffic)
         ▼
 ┌────────────────────────────────────────────────────────┐
 │                      AWS EC2 Server                    │
 │                                                        │
 │   ┌────────────────────────────────────────────────┐   │
 │   │               Docker Network                   │   │
 │   │                                                │   │
 │   │   [ Nginx Reverse Proxy / Load Balancer ]      │   │
 │   │         (Container: hospital-nginx-lb)         │   │
 │   │         - Port 80 (HTTP ──> Redirect to 443)   │   │
 │   │         - Port 443 (HTTPS + SSL Certificates)  │   │
 │   │                       │                        │   │
 │   │         ┌─────────────┴─────────────┐          │   │
 │   │         ▼                           ▼          │   │
 │   │   [ App Instance 1 ]      [ App Instance 2 ]       │   │
 │   │    (Spring Boot:8080)      (Spring Boot:8080)      │   │
 │   │         │                           │          │   │
 │   │         └─────────────┬─────────────┘          │   │
 │   │                       │                        │   │
 │   │       ┌───────────────┼───────────────┐        │   │
 │   │       ▼               ▼               ▼        │   │
 │   │  [ PostgreSQL ]    [ Redis ]    [ RabbitMQ ]   │   │
 │   │   (Database)      (Caching)     (Async Msg)    │   │
 │   │                                                │   │
 │   └────────────────────────────────────────────────┘   │
 └────────────────────────────────────────────────────────┘


🔄 Appointment Booking Workflow (Sequence Flow)
How a patient appointment flows through the high-performance backend architecture:


[ Patient / UI ] 
       │
       ▼ (1. POST /api/appointments/book)
 [ Nginx Load Balancer ]
       │
       ▼ (2. Routes traffic round-robin)
 [ Spring Boot App Instance ]
       │
       ├─► (3. Validates Slot Availability via Redis Cache)
       ├─► (4. Persists Appointment & Transaction in PostgreSQL)
       ├─► (5. Publishes Booking Event to RabbitMQ Queue) ──► [ Async Email Worker ]
       │
       ▼ (6. Returns 201 Created & Booking Confirmation)
 [ Patient / UI ]


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

        ## 🛠️ Tech Stack & Core Features

* **Language:** Java 17+
* **Framework:** Spring Boot 3.x
* **Security:** Spring Security, JWT, OAuth2
* **Database & Caching: PostgreSQL (Relational Data), Redis (Token storage & slot caching)
* **Load Balancing & Security: Nginx, Let's Encrypt SSL/TLS (HTTPS enabled)
* **Build Tool:** Maven
* **Monitoring & Metrics: Prometheus & Grafana dashboards
* **Messaging & Async Processing: RabbitMQ (Asynchronous confirmation and cancellation emails)
* **Deployment Infrastructure: AWS EC2 (Ubuntu), Docker & Docker Compose

---

        ## Project Directory Structure

```
     SmartHospitalManagementSystem/
     │
     ├── HospittalManagement/
     │   ├── src/                  # Spring Boot Source Code (Controllers, Services, Repositories)
     │   ├── Dockerfile            # Multi-stage build for Spring Boot application container
     │   ├── docker-compose.yml    # Multi-container orchestration (App, DB, Cache, Broker, Monitoring)
     │   ├── nginx.conf            # Reverse proxy, load balancing & SSL configuration
     │   └── pom.xml               # Maven dependencies (Spring Security, Data JPA, etc.)
     │
     └── README.md                 # Project Documentation
        ```

   ---

      

### 1️⃣ Clone the repository

```
git clone https://github.com/yashjadhav8903-git/SmartHospitalManagementSystem.git <br>
cd SmartHospitalManagementSystem/HospittalManagement
```

   ## Access the Application:
  - API Documentation (Swagger UI): https://smart-hms-yash.duckdns.org/doc

---

        ## 📈 Future Improvements
        
 ## 🔮 Future Improvements & Roadmap

* **Dedicated Frontend Application:** 
  * Build a modern, responsive Single Page Application (SPA) using **React.js / Next.js** and **Tailwind CSS** to replace the default Swagger/Thymeleaf UI.
  * Implement role-based dashboards for **Patients**, **Doctors**, and **Admins** with secure JWT and OAuth2 session management.
* **Real-Time Notifications:** 
  * Integrate **WebSockets (Spring WebSocket)** to push real-time appointment status updates and emergency notifications to clients.
* **Container Orchestration:** 
  * Deploy the multi-container stack onto **AWS EKS (Elastic Kubernetes Service)** with auto-scaling policies to handle traffic spikes beyond 50k+ RPS.

---

 ## 👨‍💻 Author
**Yash Jadhav**

        ---

        ⭐ If you like this project, give it a star!

        ---
