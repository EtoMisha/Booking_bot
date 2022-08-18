package booking_bot.configs;

import booking_bot.Bot;
import booking_bot.commands.*;
import booking_bot.models.User;
import booking_bot.repositories.ConcreteRepository;
import booking_bot.repositories.Repository;
import booking_bot.models.Booking;
import booking_bot.repositories.RepositoryImpl;
import booking_bot.repositories.UserRepository;
import booking_bot.services.SendMessageService;
import booking_bot.services.SendMessageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:bot.properties")
public class BotConfig {
//    @Autowired
//    CommandContainer commandContainer;

    @Value("${botSt.username}")
    private String username;

    @Value("${botSt.token}")
    private String token;

    @Bean
    public SendMessageService sendMessageService (Bot bot) {
        return new SendMessageServiceImpl(bot);
    }

    @Bean
    Bot bot(CommandContainer commandContainer) {
       Bot bot = new Bot(username, token);
       bot.setCommandContainer(commandContainer);
       return bot;
    }

//    @Bean
//    TelegramBotsApi telegramBotsApi() throws TelegramApiException {
//
//        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
//        telegramBotsApi.registerBot(bot);
//        return telegramBotsApi;
//    }

    @Bean
    public CommandContainer commandContainer () {
        return new CommandContainer();
    }

    @Bean
    public Command testCommand(SendMessageService sendMessageService, Repository repository, CommandContainer commandContainer) {
        return new TestCommand(sendMessageService, repository, commandContainer);
    }

    @Bean
    public Command userRedact(SendMessageService sendMessageService, Repository repository, CommandContainer commandContainer) {
        return new UserRedact(sendMessageService, repository, commandContainer);
    }

    @Bean
    public Command startCommand(SendMessageService sendMessageService, Repository repository, CommandContainer commandContainer) {
        return new StartCommand(sendMessageService, repository, commandContainer);
    }

    @Bean
    public Command addObject(SendMessageService sendMessageService, Repository repository, CommandContainer commandContainer) {
        return new AddObject(sendMessageService, repository, commandContainer);
    }

    @Bean
    public Command redactObject(SendMessageService sendMessageService, Repository repository, CommandContainer commandContainer) {
        return new RedactObject(sendMessageService, repository, commandContainer);
    }

    @Bean
    public Command newBooking(SendMessageService sendMessageService, Repository repository, CommandContainer commandContainer) {
        return new NewBooking(sendMessageService, repository, commandContainer);
    }

}
