package booking_bot;

import booking_bot.configs.BotConfig;
import booking_bot.configs.DataBaseConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

//@SpringBootApplication
//@Import(BotConfig.class)
public class Main {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BotConfig.class, DataBaseConfig.class);
        System.out.println("start");
        Bot bot = context.getBean("bot", Bot.class);
        System.out.println("bot bean created");

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            System.out.println("bot registered");

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

}
