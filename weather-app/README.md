# ◈ Stratos — Real-Time Weather Analysis App

A full-stack Spring Boot + HTML/CSS/JS web application that analyses real-time weather conditions using the OpenWeatherMap API.

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- Free API key from [OpenWeatherMap](https://openweathermap.org/api)

---

## ⚙️ Configuration

Open `src/main/resources/application.properties` and replace the placeholder:

```properties
weather.api.key=YOUR_OPENWEATHERMAP_API_KEY
```

Sign up at https://openweathermap.org/api → subscribe to the **Current Weather Data** (free tier).

---

## ▶️ Running the App

```bash
# From the project root
mvn spring-boot:run
```

Then open your browser at: **http://localhost:8080**

---

## 📦 Building a JAR

```bash
mvn clean package
java -jar target/weather-app-1.0.0.jar
```

---

## 🏗️ Project Structure

```
weather-app/
├── pom.xml
└── src/main/
    ├── java/com/weather/app/
    │   ├── WeatherApplication.java       # Entry point
    │   ├── controller/
    │   │   └── WeatherController.java    # REST + MVC routes
    │   ├── service/
    │   │   └── WeatherService.java       # API calls + condition analysis
    │   ├── model/
    │   │   └── WeatherData.java          # Data model
    │   └── config/
    │       └── CacheConfig.java          # Caffeine cache (10-min TTL)
    └── resources/
        ├── application.properties
        ├── templates/
        │   └── index.html                # Thymeleaf template
        └── static/
            ├── css/styles.css            # Full UI styles
            └── js/app.js                 # Frontend logic
```

---

## 🌐 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Main dashboard UI |
| GET | `/api/weather/city?city=London` | Weather by city name |
| GET | `/api/weather/coords?lat=5.6&lon=-0.2` | Weather by GPS coordinates |

---

## ✨ Features

- 🔍 Search by city name or use GPS location
- 🌡 Temperature, feels-like, min/max display
- 💨 Wind speed and direction
- 💧 Humidity with visual bar
- ☁️ Cloud cover, pressure, visibility
- 🌅 Sunrise/sunset with animated sun progress
- 🧠 Smart atmospheric analysis with contextual tips
- ⚡ Response caching (10 minutes via Caffeine)
- 📱 Responsive design

---

## 🔑 Free API Tier Limits

OpenWeatherMap free tier allows **60 calls/minute** and **1,000,000 calls/month** — more than enough for personal use. Responses are also cached for 10 minutes to minimise API usage.
