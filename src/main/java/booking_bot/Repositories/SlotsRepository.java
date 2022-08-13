package booking_bot.Repositories;

import booking_bot.models.Slot;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;

public class SlotsRepository implements Repository<Slot> {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Slot> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        return new Slot(resultSet.getDate("time"));
    };

    public SlotsRepository(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public List<Slot> findAll() {
        return jdbcTemplate.query("SELECT * FROM slots;", ROW_MAPPER);
    }

    @Override
    public void save(Slot entity) throws DataAccessException {
        System.out.println("-- repo before: " + entity.getDate());
        String query = String.format("INSERT into slots (time, client) VALUES ('%s', '%d');",
                ((Slot)entity).getDate(), ((Slot)entity).getClient().getId());
        System.out.println("-- repo: query " + query);
        jdbcTemplate.update(query);
        System.out.println("-- repo after: " + entity.getDate());
    }

    @Override
    public void update(Slot entity) {
        String query = String.format("UPDATE slots SET client = '%d' WHERE time = '%s';",
                ((Slot) entity).getClient().getId(), ((Slot) entity).getDate());
        jdbcTemplate.update(query);
    }

    @Override
    public void delete(Object date) {
        String query = String.format("DELETE FROM slots WHERE time = '%s';", date);
        jdbcTemplate.update(query);
    }
}
