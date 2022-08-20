package booking_bot.configs;

import booking_bot.Bot;
import booking_bot.commands.*;
import booking_bot.repositories.Controller;
import booking_bot.services.SendMessageService;
import booking_bot.services.SendMessageServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:bot.properties")
public class BotConfig {
//    @Autowired
//    CommandContainer commandContainer;

    @Value("${botOb.username}")
    private String username;
    @Value("${botOb.token}")
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

    @Bean
    public CommandContainer commandContainer () {
        return new CommandContainer();
    }

    @Bean
    public Command testCommand(SendMessageService sendMessageService, Controller controller, CommandContainer commandContainer) {
        return new TestCommand(sendMessageService, controller, commandContainer);
    }

    @Bean
    public Command userRedact(SendMessageService sendMessageService, Controller controller, CommandContainer commandContainer) {
        return new UserRedact(sendMessageService, controller, commandContainer);
    }

    @Bean
    public Command startCommand(SendMessageService sendMessageService, Controller controller, CommandContainer commandContainer) {
        return new StartCommand(sendMessageService, controller, commandContainer);
    }

    @Bean
    public Command addObject(SendMessageService sendMessageService, Controller controller, CommandContainer commandContainer, Bot bot) {
        AddObject addObject = new AddObject(sendMessageService, controller, commandContainer);
        addObject.setBot(bot);
        return addObject;
    }

    @Bean
    public Command adminStartCommand(SendMessageService sendMessageService, Controller controller, CommandContainer commandContainer) {
        return new AdminStartCommand(sendMessageService, controller, commandContainer);
    }

    @Bean
    public Command redactObject(SendMessageService sendMessageService, Controller controller, CommandContainer commandContainer) {
        return new RedactObject(sendMessageService, controller, commandContainer);
    }

    @Bean
    public Command newBooking(SendMessageService sendMessageService, Controller controller, CommandContainer commandContainer) {
        return new NewBooking(sendMessageService, controller, commandContainer);
    }

    @Bean
    public Command MyBooking(SendMessageService sendMessageService, Controller controller, CommandContainer commandContainer) {
        return new MyBooking(sendMessageService, controller, commandContainer);
    }

}
