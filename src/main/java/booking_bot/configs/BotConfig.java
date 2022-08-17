package booking_bot.configs;

import booking_bot.Bot;
import booking_bot.commands.*;
import booking_bot.repositories.Repository;
import booking_bot.models.Booking;
import booking_bot.services.SendMessageService;
import booking_bot.services.SendMessageServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:bot.properties")
public class BotConfig {

    @Value("${bot.username}")
    private String username;

    @Value("${bot.token}")
    private String token;

    @Bean
    public Repository<Booking> slotsRepository () {
//        return new SlotsRepository(hikariDataSource);
        return null;
    }

    @Bean
    Bot bot() {
        return new Bot(username, token);
    }

    @Bean
    public SendMessageService sendMessageService (Bot bot) {
        return new SendMessageServiceImpl(bot);
    }

    @Bean
    public CommandContainer commandContainer (SendMessageService sendMessageService,
                                              Repository<Booking> slotRepository) {
        return new CommandContainer(sendMessageService, slotRepository);
    }

    @Bean
    public Command testCommand(SendMessageService sendMessageService, Repository slotsRepository, CommandContainer commandContainer) {
        return new TestCommand(sendMessageService, slotsRepository, commandContainer);
    }

    @Bean
    public Command userRedact(SendMessageService sendMessageService, Repository slotsRepository, CommandContainer commandContainer) {
        return new UserRedact(sendMessageService, slotsRepository, commandContainer);
    }

    @Bean
    public Command startCommand(SendMessageService sendMessageService, Repository slotsRepository, CommandContainer commandContainer) {
        return new StartCommand(sendMessageService, slotsRepository, commandContainer);
    }

//    @Bean
//    public Handler handler(Repository<Slot> slotsRepository) {
//        return new Handler(slotsRepository);
//    }



}
