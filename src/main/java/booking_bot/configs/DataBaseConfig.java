package booking_bot.configs;

import booking_bot.models.*;
import booking_bot.repositories.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:db.properties")
public class DataBaseConfig {

    @Value("${db.url}")
    private String url;

    @Value("${db.user}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Value("${db.driver.name}")
    private String driverName;

    @Bean
    public HikariDataSource hikariDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName(driverName);
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource hikariDataSource) {
        return new JdbcTemplate(hikariDataSource);
    }

    @Bean
    public Repository repository(JdbcTemplate jdbcTemplate) {
        Repository repository = new RepositoryImpl();
        repository.addRepository(BookObject.class, new ObjectRepository(jdbcTemplate));
        repository.addRepository(Booking.class, new BookingRepository(jdbcTemplate));
        repository.addRepository(Campus.class, new CampusRepository(jdbcTemplate));
        repository.addRepository(Role.class, new RolesRepository(jdbcTemplate));
        repository.addRepository(Status.class, new StatusRepository(jdbcTemplate));
        repository.addRepository(Type.class, new TypeRepository(jdbcTemplate));
        repository.addRepository(User.class, new UserRepository(jdbcTemplate));

        return repository;
    }



}