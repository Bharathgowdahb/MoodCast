package com.bharath.moodcast.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MoodController {

    private final String API_KEY = "9eaeef6c3edb3d8913e46ac936dd3d2d";
    private static final Logger logger = LoggerFactory.getLogger(MoodController.class);

    @PostMapping("/moodcast")
    public Map<String, String> getMood(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String city = request.get("city");

        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric", city, API_KEY);
        
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        String weather = ((Map<String, String>) ((java.util.List<Object>) response.get("weather")).get(0)).get("main");
        Double temp = (Double) ((Map<String, Object>) response.get("main")).get("temp");

        String mood = switch (weather.toLowerCase()) {
        case "clear" -> "ಎಲ್ಲವೂ ಪ್ರಕಾಶಮಾನ, ಹೃದಯವನ್ನೋರುವ ದಿನವಿದು!";
        case "clouds" -> "ಮುಗಿಯದ ಯೋಚನೆಗಳಲ್ಲಿ ನೆನೆಸುವ ಮೋಡದ ಹೊತ್ತು.";
        case "rain" -> "ಹನಿ ಹನಿಯಲ್ಲಿ ನೆನಪುಗಳು... ಹೃದಯ ಸ್ಪರ್ಶಿಸುವ ಕ್ಷಣಗಳು.";
        case "snow" -> "ಬಿಳಿಯ ಹಿಮದ ತಂಪು, ಮನಸ್ಸಿಗೆ ಮಾಯಾಜಾಲದ ಸವಿ.";
        default -> "ದಿನವೆಂಬುದು ಖಾಲಿ ಹಾಳೆ, ಅದು ನಿನ್ನಿಂದಲೇ ರೂಪುಗೊಳ್ಳುವುದು.";
    };

        Map<String, String> result = new HashMap<>();
        result.put("name", name);
        result.put("city", city);
        result.put("weather", weather);
        result.put("temp", String.valueOf(temp));
        result.put("mood", mood);
        
        String quoteUrl = "https://zenquotes.io/api/random";
        // 🧠 Quote of the Day
        try {
        List quoteList = restTemplate.getForObject("https://zenquotes.io/api/today", List.class);
        Map quote = (Map) quoteList.get(0);
        String quoteText = quote.get("q") + " — " + quote.get("a");
        result.put("quote", quoteText);
        logger.info("Fetched quote successfully");
    } catch (Exception e) {
        logger.warn("Failed to fetch quote", e);
        result.put("quote", "Believe in yourself. Every day is a new beginning.");
    }

        // 😂 Joke of the Day
        try {
            Map joke = restTemplate.getForObject("https://official-joke-api.appspot.com/random_joke", Map.class);
            String jokeText = joke.get("setup") + " " + joke.get("punchline");
            result.put("joke", jokeText);
            logger.info("Fetched joke successfully");
        } catch (Exception e) {
            logger.warn("Failed to fetch joke", e);
            result.put("joke", "Why did the Java developer quit his job? Because he didn’t get arrays!");
        }

        return result;
    }
}
