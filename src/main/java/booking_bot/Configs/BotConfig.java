package booking_bot.Configs;

import booking_bot.Bot;
import booking_bot.Handler;
import booking_bot.Repositories.ClientsRepository;
import booking_bot.Repositories.Repository;
import booking_bot.Repositories.SlotsRepository;
import booking_bot.models.Client;
import booking_bot.models.Slot;
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
    public Repository<Client> clientsRepository (DataSource hikariDataSource) {
        return new ClientsRepository(hikariDataSource);
    }

    @Bean
    public Handler handler(Repository<Slot> slotsRepository) {
        return new Handler(slotsRepository);
    }

    @Bean
    Bot bot(Handler handler) {
        return new Bot(username, token, handler);
    }

}
