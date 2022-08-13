package booking_bot.Repositories;

import booking_bot.Repositories.Repository;
import booking_bot.models.Client;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;

public class ClientsRepository implements Repository<Client> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Client> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        return new Client(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("email"));
    };

    public ClientsRepository(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    public Client findById(int id) {
        Client client = null;
        try {
            client = jdbcTemplate.queryForObject("SELECT * FROM clients WHERE id = ?;", ROW_MAPPER, id);
        } catch (DataAccessException dataAccessException) {
            System.out.println("Couldn't find User with id " + id);
        }
        return client;
    }

    @Override
    public List<Client> findAll() {
        return jdbcTemplate.query("SELECT * FROM clients;", ROW_MAPPER);
    }

    @Override
    public void save(Client entity) {
        String query = String.format("INSERT into clients (name, email) VALUES ('%s', '%s');",
                entity.getName(), entity.getEmail());
        jdbcTemplate.update(query);
    }

    @Override
    public void update(Client entity) {
        String query = String.format("UPDATE clients SET name = '%s', email = '%s' WHERE id = %d;",
                entity.getName(), entity.getEmail(), entity.getId());
        jdbcTemplate.update(query);
    }

    @Override
    public void delete(Object id) {
        String query = String.format("DELETE FROM clients WHERE id = %d;", (Integer) id);
        jdbcTemplate.update(query);
    }

    public Client findByName(String name) {
        Client client = null;
        try {
            client = jdbcTemplate.queryForObject("SELECT * FROM clients WHERE name = ?;", ROW_MAPPER, name);
        } catch (DataAccessException dataAccessException) {
            System.out.println("Couldn't find User with name " + name);
        }
        return client;
    }

}
