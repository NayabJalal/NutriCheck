# NutriCheck

NutriCheck is a Spring Boot based backend system designed to analyze product ingredient labels using OCR and AI-driven evaluation. The system scans ingredients from product labels (via images) and determines whether each ingredient is safe, harmful, or conditionally safe for consumption. It also provides detailed explanations and health impacts for every ingredient.

---

## 🚀 Features

### **1. OCR-Powered Ingredient Extraction**

* Integrates with Google Cloud Vision API (or other OCR providers)
* Extracts text from product label images
* Parses and normalizes ingredient lists

### **2. AI Ingredient Safety Analysis**

* Uses an AI model (configured in Spring Boot) to analyze extracted ingredients
* Evaluates each ingredient based on:

  * Safety level (Safe / Harmful / Needs Caution)
  * Known health impacts
  * Benefits (if any)
  * Typical use cases in food products

### **3. Well-Structured Backend Architecture**

* Follows clean controller–service–repository pattern
* Supports modular AI integrations
* Ready for extension into mobile or web applications

### **4. Easy Integration (REST APIs)**

Sample API endpoints:

* `POST /api/ocr/extract` → Extract ingredients from image
* `POST /api/ai/analyze` → Analyze ingredient safety
* `POST /api/scan` → Full workflow: Scan + Analyze

---

## 🏗️ Project Structure

```
NutriCheck/
 ├── src/
 │   ├── main/java/com/nutricheck/
 │   │   ├── controller/        # API Controllers (OCR, AI, Scan)
 │   │   ├── service/           # OCR & AI Logic
 │   │   ├── model/             # Ingredient models and DTOs
 │   │   └── config/            # Configurations (API keys, beans)
 │   └── main/resources/        # Application properties
 ├── pom.xml                    # Dependencies
 └── README.md
```

---

## 🧠 Tech Stack

* **Backend:** Spring Boot (Java)
* **OCR:** Google Cloud Vision API / Tesseract (optional)
* **AI Model:** External LLM API (OpenAI, Gemini, or Spring AI)
* **Build Tool:** Maven

---

## 🔧 Setup & Installation

### **1. Clone the Repository**

```bash
git clone https://github.com/your-username/nutricheck.git
cd nutricheck
```

### **2. Configure API Keys**

Add your keys inside `application.properties`:

```
google.cloud.vision.key=YOUR_KEY
spring.ai.api-key=YOUR_OPENAI_OR_GEMINI_KEY
```

### **3. Run the Application**

```bash
mvn spring-boot:run
```

---

## 📡 API Usage Example

### **Request:**

```json
POST /api/scan
{
  "imageBase64": "<BASE64_ENCODED_IMAGE>"
}
```

### **Response:**

```json
{
  "ingredients": [
    {
      "name": "Sodium Benzoate",
      "safety": "Harmful",
      "impact": "Preservative that may cause allergies and hyperactivity in children."
    }
  ]
}
```

---

## 🗂️ Database Schema (Simplified)

```
ingredients
-----------
id (PK)
name
category
safety_level
health_impact
benefits
```

---

## 📌 Roadmap

* [ ] Add user accounts & preference-based ingredient alerts
* [ ] Mobile app integration (Flutter/React Native)
* [ ] Ingredient history & analytics dashboard
* [ ] FDA/EU food ingredient dataset integration

---

## 🤝 Contributing

Contributions are welcome! Feel free to open issues or submit pull requests.

---

## 📜 License

MIT License – free to use and modify.

---

## ✉️ Contact

For questions or suggestions, reach out to **Devbrat Pradhan**.
