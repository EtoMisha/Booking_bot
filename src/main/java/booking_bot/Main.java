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

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BotConfig.class, DataBaseConfig.class);


        Bot bot = context.getBean("bot", Bot.class);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

}
