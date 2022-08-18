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
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@PropertySource("classpath:bot.properties")
public class BotConfig {
//    @Autowired
//    CommandContainer commandContainer;

    @Value("${test.username}")
    private String username;

    @Value("${test.token}")
    private String token;

    @Bean
    public Repository<Booking> slotsRepository () {
//        return new SlotsRepository(hikariDataSource);
        return null;
    }

    @Bean
    public SendMessageService sendMessageService (Bot bot) {
        return new SendMessageServiceImpl(bot);
    }

    @Bean
    Bot bot() {
       Bot bot = new Bot(username, token);
       bot.setCommandContainer(commandContainer(sendMessageService(bot), slotsRepository()));
       return bot;
    }

    @Bean
    TelegramBotsApi telegramBotsApi() throws TelegramApiException {

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot());
        return telegramBotsApi;
    }

    @Bean
    public CommandContainer commandContainer (SendMessageService sendMessageService,
                                              Repository<Booking> slotRepository) {
        CommandContainer commandContainer = new CommandContainer(sendMessageService, slotRepository);
        return commandContainer;
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

    @Bean
    public Command addObject(SendMessageService sendMessageService, Repository slotsRepository, CommandContainer commandContainer) {
        return new AddObject(sendMessageService, slotsRepository, commandContainer);
    }

    @Bean
    public Command redactObject(SendMessageService sendMessageService, Repository slotsRepository, CommandContainer commandContainer) {
        return new RedactObject(sendMessageService, slotsRepository, commandContainer);
    }

    @Bean
    public Command newBooking(SendMessageService sendMessageService, Repository slotsRepository, CommandContainer commandContainer) {
        return new NewBooking(sendMessageService, slotsRepository, commandContainer);
    }

}
