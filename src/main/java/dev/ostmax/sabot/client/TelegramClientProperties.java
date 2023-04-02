package dev.ostmax.sabot.client;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class TelegramClientProperties {
    @Value("${bot.name}") String botName;
    @Value("${bot.token}") String token;
}
