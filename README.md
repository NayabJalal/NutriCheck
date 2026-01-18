# NutriCheck v2.3.1

NutriCheck is a robust Spring Boot backend application designed to analyze product ingredient labels through AI-powered evaluations and OCR (Optical Character Recognition). By integrating Google Gemini 2.0 Flash via the Spring AI framework, the system extracts ingredients from product images, assesses their safety levels (Safe, Harmful, or Needs Caution), and provides detailed health impact assessments.

---

## 🚀 Key Features

### **Multimodal AI Scanning**

Leverages Google Gemini 2.0 Flash to process images and text simultaneously for high-accuracy ingredient extraction.

### **Safety Analysis**

Automatically categorizes ingredients into **LOW, MEDIUM, or HIGH** risk levels with detailed health explanations.

### **Comprehensive Data Model**

Tracks users, scan history, and a master database of unique ingredients to prevent redundant processing.

### **RESTful API Architecture**

Cleanly separated controllers for image scanning, ingredient-only analysis, and user management.

### **Local LLM Support**

Integrated with Spring AI Ollama for optional local testing with models like **Llama 3**.

---

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot 3.2.5
* **AI Integration:** Spring AI 1.1.2 (Milestone)
* **Models:** Google Gemini 2.0 Flash (via `spring-ai-starter-model-google-genai`)
* **Database:** MySQL with Hibernate/JPA
* **Mapping:** ModelMapper 3.2.6
* **Build Tool:** Gradle 8.6

---

## 🏗️ Project Structure

```
NutriCheck/
 ├── src/main/java/com/nutricheck/
 │   ├── controller/    # REST Endpoints (OCR, User, Scan)
 │   ├── service/       # Business logic (Gemini AI, Image processing)
 │   ├── repository/    # JPA Repositories (MySQL interaction)
 │   ├── entity/        # Persistence models (Scan, Ingredient, User)
 │   ├── dto/           # Data Transfer Objects (Requests/Responses)
 │   └── mapper/        # Object mapping configurations
 └── src/main/resources/
     └── application.properties  # System configurations
```

---

## 🔧 Setup & Installation

### **1. Prerequisites**

* JDK 17 or higher
* A Google AI (Gemini) API Key
* MySQL Database instance

### **2. Configure Environment Variables**

Set the following environment variables:

* `JDBC_DATABASE_URL` — MySQL connection string
* `JDBC_DATABASE_USERNAME` — Database username
* `JDBC_DATABASE_PASSWORD` — Database password
* `GEMINI_KEY` — Google Gemini API Key
* `MODEL` — *(Optional)* Defaults to `gemini-2.0-flash`

### **3. Run the Application**

```
./gradlew bootRun
```

Server starts at: **[http://localhost:8080](http://localhost:8080)**

---

## 📡 API Documentation

### **1. Scan Product Image**

`POST /api/scan/image`

Extracts ingredients from an uploaded image and performs safety analysis.

**Parameters:**

* `image`: MultipartFile — Product label photo
* `userId`: Long — ID of user
* `category`: String — FOOD (default), COSMETICS, BEVERAGES

---

### **2. Analyze Ingredients Text**

`POST /api/scan/ingredients`

```json
{
  "ingredients": "Sodium Benzoate, Citric Acid, Sugar",
  "productCategory": "FOOD"
}
```

---

### **3. User History**

* `GET /api/scan/user/{userId}` — Retrieve all previous scans for a user
* `GET /api/scan/{scanId}` — Get detailed results for a specific scan

---

## 🧪 Database Schema Overview

* **Users:** Stores basic profile information
* **Scans:** Metadata such as product name and timestamp
* **Ingredients:** Master table of analyzed ingredients with risk level
* **ScanResults:** Links scans to ingredients with explanations and scores

---

## 📜 License

This project is managed and maintained by **Devbrat Pradhan** and **Md Nayab**. Contributions are welcome!
