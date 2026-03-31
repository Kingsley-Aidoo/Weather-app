package com.weather.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.app.model.WeatherData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Cacheable(value = "weatherCache", key = "#city.toLowerCase()")
    public WeatherData getWeatherByCity(String city) throws Exception {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/weather")
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();

        String response = restTemplate.getForObject(url, String.class);
        return parseWeatherData(response);
    }

    @Cacheable(value = "weatherCache", key = "#lat + ',' + #lon")
    public WeatherData getWeatherByCoords(double lat, double lon) throws Exception {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/weather")
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();

        String response = restTemplate.getForObject(url, String.class);
        return parseWeatherData(response);
    }

    private WeatherData parseWeatherData(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        WeatherData data = new WeatherData();

        data.setCity(root.path("name").asText());
        data.setCountry(root.path("sys").path("country").asText());
        data.setLat(root.path("coord").path("lat").asDouble());
        data.setLon(root.path("coord").path("lon").asDouble());

        JsonNode main = root.path("main");
        data.setTemperature(main.path("temp").asDouble());
        data.setFeelsLike(main.path("feels_like").asDouble());
        data.setTempMin(main.path("temp_min").asDouble());
        data.setTempMax(main.path("temp_max").asDouble());
        data.setHumidity(main.path("humidity").asInt());
        data.setPressure(main.path("pressure").asInt());

        JsonNode wind = root.path("wind");
        data.setWindSpeed(wind.path("speed").asDouble());
        data.setWindDeg(wind.path("deg").asInt());

        data.setVisibility(root.path("visibility").asInt(10000));
        data.setClouds(root.path("clouds").path("all").asInt());

        JsonNode weather = root.path("weather").get(0);
        if (weather != null) {
            data.setDescription(weather.path("description").asText());
            data.setIcon(weather.path("icon").asText());
            data.setMain(weather.path("main").asText());
        }

        JsonNode sys = root.path("sys");
        data.setSunrise(sys.path("sunrise").asLong());
        data.setSunset(sys.path("sunset").asLong());

        data.setAnalysis(analyseConditions(data));
        return data;
    }

    private String analyseConditions(WeatherData data) {
        StringBuilder sb = new StringBuilder();
        double temp = data.getTemperature();
        int humidity = data.getHumidity();
        double windSpeed = data.getWindSpeed();
        String main = data.getMain() != null ? data.getMain().toLowerCase() : "";

        // Temperature analysis
        if (temp <= 0) sb.append("❄️ Freezing conditions — dress in heavy layers. ");
        else if (temp <= 10) sb.append("🧥 Very cold — a warm coat is essential. ");
        else if (temp <= 18) sb.append("🌤 Cool weather — a light jacket recommended. ");
        else if (temp <= 26) sb.append("😊 Pleasant temperature — comfortable outdoor conditions. ");
        else if (temp <= 33) sb.append("☀️ Warm and sunny — stay hydrated. ");
        else sb.append("🔥 Very hot — limit sun exposure and drink plenty of water. ");

        // Humidity analysis
        if (humidity >= 85) sb.append("💧 Very high humidity — expect muggy, oppressive air. ");
        else if (humidity >= 65) sb.append("🌫 Elevated humidity — air feels heavy. ");
        else if (humidity <= 30) sb.append("🏜️ Dry air — consider moisturising and drinking extra water. ");

        // Wind analysis
        if (windSpeed >= 17) sb.append("🌬️ Strong winds — secure loose objects outdoors. ");
        else if (windSpeed >= 10) sb.append("💨 Moderate breeze — wind chill may affect perceived temperature. ");

        // Condition-specific
        if (main.contains("rain")) sb.append("🌧️ Rain detected — carry an umbrella. ");
        else if (main.contains("drizzle")) sb.append("🌦 Light drizzle — a rain jacket is handy. ");
        else if (main.contains("thunderstorm")) sb.append("⛈️ Thunderstorm warning — stay indoors if possible. ");
        else if (main.contains("snow")) sb.append("🌨️ Snowfall — roads may be slippery, drive carefully. ");
        else if (main.contains("fog") || main.contains("mist")) sb.append("🌫️ Low visibility — drive slowly and use fog lights. ");
        else if (main.contains("clear")) sb.append("🌞 Clear skies — great day for outdoor activities. ");
        else if (main.contains("cloud")) sb.append("⛅ Cloudy skies — UV exposure still possible through clouds. ");

        // Feels like note
        double diff = data.getFeelsLike() - temp;
        if (Math.abs(diff) >= 3) {
            if (diff < 0) sb.append(String.format("🌡 Feels %.1f°C cooler than actual temperature.", Math.abs(diff)));
            else sb.append(String.format("🌡 Feels %.1f°C warmer than actual temperature.", diff));
        }

        return sb.toString().trim();
    }

    public String getWindDirection(int deg) {
        String[] dirs = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                         "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        return dirs[(int) Math.round(deg / 22.5) % 16];
    }
}
