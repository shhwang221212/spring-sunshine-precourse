package sunshine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import study.Functions;

import java.util.Map;

@RestController
public class WeatherQueryController {
    private final ChatClient client;
    private final RestClient httpClient;
    private static final Logger log = LoggerFactory.getLogger(WeatherQueryController.class);

    public WeatherQueryController(ChatClient.Builder builder, RestClient httpClient) {
        this.client = builder.build();
        this.httpClient = httpClient;
    }

    @GetMapping("/getLatLngDto")
    public String getLatLngDto(@RequestParam(defaultValue = "서울시 강서구") String city) {

        var beanOutputConverter = new BeanOutputConverter<>(LatLngDto.class);
        var format = beanOutputConverter.getFormat();

        var user = """
        {city}의 위도와 경도 알려줘. 답변 양식은 {format}으로 알려줘야해.;
        """;
        var prompt = new PromptTemplate(user).create(Map.of("city", city, "format", format));

        try {
            // ✅ ChatResponse를 명시적으로 받기 (메타데이터/토큰/ID 로깅용)
            var chatResponse = client.prompt(prompt)
                    .call()
                    .chatResponse();

            var metadata = chatResponse.getMetadata(); // ChatResponseMetadata :contentReference[oaicite:1]{index=1}
            var usage = metadata.getUsage();           // Usage :contentReference[oaicite:2]{index=2}

            // ✅ 호출 ID + 토큰 사용량 로그
            log.info("llm.call id={} model={} promptTokens={} totalTokens={}",
                    metadata.getId(),
                    metadata.getModel(),
                    usage.getPromptTokens(),
                    usage.getTotalTokens()
            );

            // 기존 로직: content를 꺼내서 변환
            String content = chatResponse.getResult().getOutput().getText(); // 또는 너가 쓰던 result.content() 방식
            LatLngDto convert = beanOutputConverter.convert(content);

            WeatherApiResponseDto weatherByCoordinates = getWeatherByCoordinates(convert);
            log.info("weather={}", weatherByCoordinates);

            return recommendOutfit(weatherByCoordinates);

        } catch (Exception e) {
            log.info("llm.call failureType={}", e.getClass().getSimpleName(), e);
            return null;
        }
    }

    public WeatherApiResponseDto getWeatherByCoordinates(LatLngDto latLngDto) {
        RestClient.RequestHeadersSpec<?> uri = httpClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.open-meteo.com")
                        .path("/v1/forecast")
                        .queryParam("latitude", latLngDto.lat())
                        .queryParam("longitude", latLngDto.lng())
                        .queryParam("current",
                                "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code")
                        .build());

        log.info(uri.retrieve().body(WeatherApiResponseDto.class).toString());
        return uri.retrieve().body(WeatherApiResponseDto.class);
    }

    public String recommendOutfit(WeatherApiResponseDto weatherinfo) {
        var template = """
            기온은 {temperature} 이고,
            습도는 {relativeHumidity} 이고,
            체감온도는 {apparentTemperature} 이고,
            날씨코드는 {weatherCode} 야.5
            이 데이터에 맞는 옷차림을 추천해줘.
        """;

        var prompt = new PromptTemplate(template).create(Map.of("temperature", weatherinfo.current().temperature2m(),
                                                                    "relativeHumidity", weatherinfo.current().relativeHumidity2m(),
                                                                    "apparentTemperature", weatherinfo.current().apparentTemperature(),
                                                                    "weatherCode", weatherinfo.current().weatherCode()));

        ChatClient.CallResponseSpec call = client.prompt(prompt).call();
        log.info(call.toString());

        return call.content();
    }
}
