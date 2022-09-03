package booking_bot.configs;

import booking_bot.Bot;
import booking_bot.commands.*;
import booking_bot.commands.impls.*;
import booking_bot.repositories.Controller;
import booking_bot.services.BotService;
import booking_bot.services.BotServiceImpl;
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

    @Value("${admin.password}")
    private String adminPassword;

    @Bean
    public BotService sendMessageService (Bot bot) {
        return new BotServiceImpl(bot);
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
    public Command editUser(BotService botService, Controller controller, CommandContainer commandContainer) {
        return new EditUser(botService, controller, commandContainer);
    }

    @Bean
    public Command start(BotService botService, Controller controller, CommandContainer commandContainer) {
        return new Start(botService, controller, commandContainer);
    }

    @Bean
    public Command admin(BotService botService, Controller controller, CommandContainer commandContainer) {
        Admin admin = new Admin(botService, controller, commandContainer);
        admin.setAdminPassword(adminPassword);
        return admin;
    }

    @Bean
    public Command addObject(BotService botService, Controller controller, CommandContainer commandContainer) {
        return new AddObject(botService, controller, commandContainer);
    }

    @Bean
    public Command editObject(BotService botService, Controller controller, CommandContainer commandContainer) {
        return new EditObject(botService, controller, commandContainer);
    }

    @Bean
    public Command newBooking(BotService botService, Controller controller, CommandContainer commandContainer) {
        return new NewBooking(botService, controller, commandContainer);
    }

    @Bean
    public Command MyBookings(BotService botService, Controller controller, CommandContainer commandContainer) {
        return new MyBookings(botService, controller, commandContainer);
    }

}
