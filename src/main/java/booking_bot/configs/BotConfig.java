package booking_bot.configs;

import booking_bot.Bot;
import booking_bot.commands.Command;
import booking_bot.commands.CommandContainer;
import booking_bot.commands.StartCommand;
import booking_bot.commands.TestCommand;
import booking_bot.repositories.Repository;
import booking_bot.repositories.SlotsRepository;
import booking_bot.models.User;
import booking_bot.models.Slot;
import booking_bot.services.SendMessageService;
import booking_bot.services.SendMessageServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:bot.properties")
public class BotConfig {

    @Value("${admin.username}")
    private String username;

    @Value("${admin.token}")
    private String token;

    @Bean
    public Repository<Slot> slotsRepository (DataSource hikariDataSource) {
        return new SlotsRepository(hikariDataSource);
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
                                              Repository<Slot> slotRepository) {
        return new CommandContainer(sendMessageService, slotRepository);
    }

    @Bean
    public Command testCommand(SendMessageService sendMessageService, Repository slotsRepository, CommandContainer commandContainer) {
        return new TestCommand(sendMessageService, slotsRepository, commandContainer);
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
