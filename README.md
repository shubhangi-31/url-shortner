# URL Shortener

A URL shortening service built with **Java, Spring Boot, MySQL, Redis, and Docker**.

The application converts long URLs into short URLs using **Base62 encoding** and uses **Redis caching** to improve the performance of URL redirection.

---

## 🚀 Features

- Create short URLs from long URLs
- Generate short codes using Base62 encoding
- Store URL mappings in MySQL
- Redis cache-aside pattern for fast URL lookups
- 1-hour Redis cache TTL
- Redirect short URLs using HTTP 302
- Request validation for URLs
- Global exception handling
- REST API documentation with Swagger/OpenAPI
- Unit and integration tests
- Docker Compose setup for MySQL and Redis
- Persistent MySQL storage using Docker volumes

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.1.1 | Backend framework |
| Spring Data JPA | Database access |
| MySQL 8.4 | Persistent storage |
| Redis | Caching |
| Maven | Build and dependency management |
| Docker | Containerization |
| Docker Compose | Local infrastructure |
| Swagger / OpenAPI | API documentation |
| JUnit 5 | Testing |
| Mockito | Unit testing |

---

## 🏗️ Architecture

```text
                    ┌──────────────┐
                    │    Client    │
                    └──────┬───────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │   Spring Boot    │
                  │    REST API      │
                  └────────┬─────────┘
                           │
                  ┌────────┴────────┐
                  │                 │
                  ▼                 ▼
           ┌─────────────┐   ┌─────────────┐
           │    Redis    │   │    MySQL    │
           │    Cache    │   │  Database   │
           └─────────────┘   └─────────────┘
````

---

## 🔗 URL Creation Flow

```text
Client
  │
  │ POST /api/urls
  ▼
Spring Boot
  │
  ├── Check if URL already exists
  │
  ├── Save URL to MySQL
  │
  ├── Get generated database ID
  │
  ├── Convert ID to Base62
  │
  └── Save short code
```

---

## ⚡ URL Redirection & Caching Flow

The application follows the **Cache-Aside Pattern**.

```text
Client
  │
  │ GET /api/urls/{shortCode}
  ▼
Redis
  │
  ├── Cache HIT ──────────► 302 Redirect
  │
  └── Cache MISS
          │
          ▼
        MySQL
          │
          ▼
      Store in Redis
          │
          ▼
      302 Redirect
```

Redis entries have a **1-hour TTL**.

Example:

```text
Key   : url:1
Value : https://www.youtube.com
TTL   : 3600 seconds
```

MySQL remains the **source of truth**, while Redis acts as the caching layer.

---

## 🔑 Base62 Encoding

The short code is generated from the database-generated numeric ID using Base62 encoding.

Example:

```text
Database ID
     │
     ▼
   1000
     │
     ▼
 Base62 Encoder
     │
     ▼
    g8
```

The Base62 character set contains:

```text
0-9
a-z
A-Z
```

This allows numeric IDs to be represented using shorter strings.

---

## 📡 API Endpoints

### Create Short URL

**POST**

```text
/api/urls
```

Request:

```json
{
  "originalUrl": "https://www.youtube.com"
}
```

Response:

```json
{
  "shortCode": "1",
  "shortUrl": "http://localhost:8080/api/urls/1",
  "originalUrl": "https://www.youtube.com",
  "createdAt": "2026-09-11T00:00:00"
}
```

---

### Redirect to Original URL

**GET**

```text
/api/urls/{shortCode}
```

Example:

```text
GET /api/urls/1
```

Response:

```text
HTTP 302 Found
Location: https://www.youtube.com
```

The endpoint returns the original URL through the HTTP `Location` header.

---

## 📖 Swagger / OpenAPI

Swagger UI is available when the application is running:

```text
http://localhost:8080/swagger-ui.html
```

API documentation:

```text
http://localhost:8080/v3/api-docs
```

> **Note:** Swagger UI may show "Failed to fetch" for the redirect endpoint because the browser follows the 302 redirect to an external website. The redirect itself works correctly and can be verified using Postman.

---

## 🗄️ Database

MySQL stores URL mappings in the `urls` table.

### Table Structure

```text
urls
├── id
├── short_code
├── original_url
└── created_at
```

Example:

| id | short_code | original_url                                       | created_at |
| -: | ---------- | -------------------------------------------------- | ---------- |
|  1 | 1          | [https://www.youtube.com](https://www.youtube.com) | ...        |

---

## 🐳 Docker Setup

The project uses Docker Compose to run:

* MySQL 8.4
* Redis

Start the services:

```bash
docker compose up -d
```

Check running containers:

```bash
docker ps
```

Expected containers:

```text
url-shortner-mysql
url-shortner-redis
```

Stop the services:

```bash
docker compose down
```

### Persistent MySQL Storage

MySQL uses a Docker named volume:

```text
mysql_data
```

This keeps database data persistent when the MySQL container is recreated.

> **Warning:** Do not use `docker compose down -v` unless you intentionally want to delete the MySQL volume and its data.

---

## 🔐 Environment Variables

Database credentials are not stored directly in the source code.

Create a `.env` file in the project root:

```properties
DB_PASSWORD=your_mysql_password
MYSQL_ROOT_PASSWORD=your_mysql_password
```

The `.env` file is excluded from Git using `.gitignore`.

For IntelliJ, configure the following environment variable in the Spring Boot Run Configuration:

```text
DB_PASSWORD=your_mysql_password
```

---

## 🧪 Testing

The project contains unit and integration tests covering:

* Base62 encoding
* URL service logic
* Redis service
* REST controller
* MySQL repository integration
* Redis integration

Run the complete test suite using the Maven wrapper.

### Windows

```powershell
.\mvnw.cmd clean test
```

### Linux / macOS

```bash
./mvnw clean test
```

---

## 📁 Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.example.urlshortner
│   │       ├── config
│   │       │   └── RedisConfig.java
│   │       ├── controller
│   │       │   └── UrlController.java
│   │       ├── dto
│   │       │   ├── CreateUrlRequest.java
│   │       │   └── UrlResponse.java
│   │       ├── entity
│   │       │   └── Url.java
│   │       ├── exception
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   └── ShortUrlNotFoundException.java
│   │       ├── repository
│   │       │   └── UrlRepository.java
│   │       ├── service
│   │       │   ├── RedisService.java
│   │       │   └── UrlService.java
│   │       ├── util
│   │       │   ├── Base62Encoder.java
│   │       │   └── RedisKeyUtil.java
│   │       └── UrlShortnerApplication.java
│   │
│   └── resources
│       └── application.properties
│
└── test
    └── java
        └── com.example.urlshortner
            ├── controller
            ├── service
            └── util
```

---

## 🎯 System Design Concepts

This project demonstrates practical implementation of:

* REST API design
* Layered architecture
* Cache-Aside pattern
* Redis TTL
* Database persistence
* Base62 encoding
* Database-generated IDs
* HTTP 302 redirects
* Docker containerization
* Persistent Docker volumes
* Unit testing
* Integration testing
* Request validation
* Exception handling
* API documentation

---

## 🔮 Future Improvements

Potential enhancements:

* Custom aliases
* URL expiration
* Click analytics
* Rate limiting
* Distributed ID generation
* Custom domain support
* Authentication and authorization
* Horizontal scaling
* Load balancing
* Advanced monitoring and observability

---

Backend and system-design learning project built using Java and Spring Boot.

