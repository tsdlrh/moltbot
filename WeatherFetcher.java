import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * WeatherFetcher - A simple Java class to fetch current weather data from Open-Meteo API
 * This implementation uses the free Open-Meteo API which doesn't require an API key
 * 
 * Features:
 * - Fetches current weather for given coordinates
 * - Supports multiple locations
 * - Handles HTTP errors gracefully
 * - Returns structured weather data
 * 
 * Usage:
 * WeatherFetcher fetcher = new WeatherFetcher();
 * WeatherData weather = fetcher.getCurrentWeather(39.9042, 116.4074); // Beijing coordinates
 * System.out.println("Temperature: " + weather.temperature + "°C");
 * 
 * @author Moltbot Assistant
 */
public class WeatherFetcher {
    
    private static final String API_BASE_URL = "https://api.open-meteo.com/v1/forecast";
    
    /**
     * Fetches current weather data for given latitude and longitude
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate  
     * @return WeatherData object containing current weather information
     * @throws Exception if HTTP request fails or response is invalid
     */
    public WeatherData getCurrentWeather(double latitude, double longitude) throws Exception {
        String urlString = String.format(
            "%s?latitude=%.4f&longitude=%.4f&current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,rain,showers,snowfall,weather_code,cloud_cover,pressure_msl,surface_pressure,wind_speed_10m,wind_direction_10m,wind_gusts_10m",
            API_BASE_URL, latitude, longitude
        );
        
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);
        
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HTTP request failed with code: " + responseCode);
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String jsonResponse = reader.lines().collect(Collectors.joining("\n"));
            return parseWeatherResponse(jsonResponse);
        } finally {
            connection.disconnect();
        }
    }
    
    /**
     * Parses the JSON response from Open-Meteo API
     * @param jsonResponse Raw JSON response string
     * @return WeatherData object with parsed weather information
     * @throws Exception if JSON parsing fails
     */
    private WeatherData parseWeatherResponse(String jsonResponse) throws Exception {
        // Simple JSON parsing without external dependencies
        WeatherData data = new WeatherData();
        
        // Extract latitude
        int latStart = jsonResponse.indexOf("\"latitude\":");
        if (latStart != -1) {
            int latEnd = jsonResponse.indexOf(",", latStart);
            if (latEnd != -1) {
                data.latitude = Double.parseDouble(
                    jsonResponse.substring(latStart + 12, latEnd).trim()
                );
            }
        }
        
        // Extract longitude  
        int lonStart = jsonResponse.indexOf("\"longitude\":");
        if (lonStart != -1) {
            int lonEnd = jsonResponse.indexOf(",", lonStart);
            if (lonEnd != -1) {
                data.longitude = Double.parseDouble(
                    jsonResponse.substring(lonStart + 13, lonEnd).trim()
                );
            }
        }
        
        // Extract current weather data
        int currentStart = jsonResponse.indexOf("\"current\":{");
        if (currentStart != -1) {
            int currentEnd = jsonResponse.indexOf("}", currentStart);
            if (currentEnd != -1) {
                String currentData = jsonResponse.substring(currentStart + 10, currentEnd + 1);
                parseCurrentWeather(data, currentData);
            }
        }
        
        return data;
    }
    
    /**
     * Parses current weather section of the JSON response
     */
    private void parseCurrentWeather(WeatherData data, String currentData) {
        // Temperature
        extractDoubleValue(currentData, "\"temperature_2m\":", value -> data.temperature = value);
        
        // Relative humidity
        extractDoubleValue(currentData, "\"relative_humidity_2m\":", value -> data.humidity = value);
        
        // Apparent temperature
        extractDoubleValue(currentData, "\"apparent_temperature\":", value -> data.apparentTemperature = value);
        
        // Wind speed
        extractDoubleValue(currentData, "\"wind_speed_10m\":", value -> data.windSpeed = value);
        
        // Wind direction
        extractDoubleValue(currentData, "\"wind_direction_10m\":", value -> data.windDirection = value);
        
        // Pressure
        extractDoubleValue(currentData, "\"pressure_msl\":", value -> data.pressure = value);
        
        // Cloud cover
        extractDoubleValue(currentData, "\"cloud_cover\":", value -> data.cloudCover = value);
    }
    
    /**
     * Helper method to extract double values from JSON string
     */
    private void extractDoubleValue(String json, String key, java.util.function.DoubleConsumer setter) {
        int start = json.indexOf(key);
        if (start != -1) {
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            if (end != -1) {
                try {
                    String valueStr = json.substring(start + key.length(), end).trim();
                    if (!valueStr.equals("null")) {
                        setter.accept(Double.parseDouble(valueStr));
                    }
                } catch (NumberFormatException e) {
                    // Ignore parsing errors
                }
            }
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        WeatherFetcher fetcher = new WeatherFetcher();
        
        try {
            // Test with Beijing coordinates
            WeatherData beijingWeather = fetcher.getCurrentWeather(39.9042, 116.4074);
            System.out.println("Beijing Weather:");
            System.out.println("Temperature: " + beijingWeather.temperature + "°C");
            System.out.println("Humidity: " + beijingWeather.humidity + "%");
            System.out.println("Wind Speed: " + beijingWeather.windSpeed + " m/s");
            System.out.println("Pressure: " + beijingWeather.pressure + " hPa");
            
            // Test with New York coordinates
            WeatherData nyWeather = fetcher.getCurrentWeather(40.7128, -74.0060);
            System.out.println("\nNew York Weather:");
            System.out.println("Temperature: " + nyWeather.temperature + "°C");
            System.out.println("Humidity: " + nyWeather.humidity + "%");
            
        } catch (Exception e) {
            System.err.println("Error fetching weather: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/**
 * Simple data class to hold weather information
 */
class WeatherData {
    public double latitude;
    public double longitude;
    public double temperature = Double.NaN; // Celsius
    public double humidity = Double.NaN; // Percentage
    public double apparentTemperature = Double.NaN; // Celsius
    public double windSpeed = Double.NaN; // m/s
    public double windDirection = Double.NaN; // degrees
    public double pressure = Double.NaN; // hPa
    public double cloudCover = Double.NaN; // percentage
    
    @Override
    public String toString() {
        return String.format(
            "WeatherData{lat=%.4f, lon=%.4f, temp=%.1f°C, humidity=%.1f%%, wind=%.1fm/s}",
            latitude, longitude, temperature, humidity, windSpeed
        );
    }
}