package sunshine;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public record WeatherApiResponseDto(
        double latitude,
        double longitude,
        @JsonProperty("current")
        Current current
) {}
