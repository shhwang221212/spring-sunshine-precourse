package study;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.template.TemplateRenderer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class JokeController {
    private final ChatClient client;

    public JokeController(ChatClient.Builder builder) {
        this.client = builder.build();
    }




    @GetMapping("/addDays")
    public String addDays(
        @RequestParam(defaultValue = "0") int days
    ) {
        var template = new PromptTemplate("오늘 기준으로 {days}일 뒤 날짜를 알려줘.");
        var prompt = template.render(Map.of("days", days));
        return client.prompt(prompt)
                .tools(new Functions())
                .call()
                .content();
    }



    @GetMapping("/joke")
    public ChatResponse joke(
        @RequestParam(defaultValue = "HWANG SEONGHYUN") String name,
        @RequestParam(defaultValue = "pirate") String voice
    ) {
        var user = new UserMessage("""
            Tell me about three famous pirates from the Golden Age of Piracy and what they did.
            Write at least one sentence for each pirate.
            """
        );
        var template = new SystemPromptTemplate("""
            You are a helpful AI assistant.
            You are an AI assistant that helps people find information.
            Your name is {name}.
            You should reply to the user's request using your name and in the style of a {voice}.
        """
        );
        var system = template.createMessage(Map.of("name", name, "voice", voice));
        var prompt = new Prompt(user, system);
        return client.prompt(prompt).call().chatResponse();
    }
}
