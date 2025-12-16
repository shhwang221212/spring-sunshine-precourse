package study;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.time.LocalDate;
import java.util.function.Function;

@Configuration
public class Functions {

    @Tool(description = "Calculate a date after adding days from today")
    public DateResponse addDaysFromToday(AddDaysRequest request) {
         var result = LocalDate.now().plusDays(request.days());
         return new DateResponse(result.toString());

    }
}