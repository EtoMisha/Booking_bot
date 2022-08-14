package booking_bot;

import booking_bot.commands.CommandContainer;
import booking_bot.configs.BotConfig;
import booking_bot.configs.DataBaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DataBaseConfig.class, BotConfig.class);

        CommandContainer commandContainer = context.getBean("commandContainer", CommandContainer.class);

        Bot bot = context.getBean("bot", Bot.class);
        bot.setCommandContainer(commandContainer);


        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

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
