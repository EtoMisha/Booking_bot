package booking_bot;

import booking_bot.commands.CommandContainer;
import booking_bot.commands.StartCommand;
import booking_bot.commands.TestCommand;
import booking_bot.configs.BotConfig;
import booking_bot.configs.DataBaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

//@SpringBootApplication
//@Import(BotConfig.class)
public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DataBaseConfig.class, BotConfig.class);

//        CommandContainer commandContainer = context.getBean("commandContainer", CommandContainer.class);
//        TestCommand testCommand = context.getBean("testCommand", TestCommand.class);
//        StartCommand start = context.getBean("startCommand", StartCommand.class);

//        Bot bot = context.getBean("bot", Bot.class);
//        bot.setCommandContainer(commandContainer);


//        try {
//            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//            botsApi.registerBot(bot);
//
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }

//        context.close();

//        HikariConfig hikariConfig = new HikariConfig();
//        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5433/postgres");
//        hikariConfig.setUsername("postgres");
//        hikariConfig.setPassword("");
//        hikariConfig.setDriverClassName("org.postgresql.Driver");
//
//        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(hikariDataSource);
//        String query = "INSERT into slots (time) VALUES ('2022-05-15');";
//        jdbcTemplate.update(query);
    }

}
