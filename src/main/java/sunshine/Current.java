package sunshine;


import com.fasterxml.jackson.annotation.JsonProperty;

public record Current(
        @JsonProperty("temperature_2m") double temperature2m,
        @JsonProperty("relative_humidity_2m") double relativeHumidity2m,
        @JsonProperty("apparent_temperature") double apparentTemperature,
        @JsonProperty("weather_code") int weatherCode
) {}