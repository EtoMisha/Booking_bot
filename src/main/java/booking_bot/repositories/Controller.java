package booking_bot.repositories;


import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class Controller {

    private RolesRepository roles;
    private TypeRepository type;
    private ObjectRepository bookingObject;
    private BookingRepository booking;
    private StatusRepository status;
    private UserRepository user;
    private CampusRepository campus;

    public Controller(JdbcTemplate template) {
        this.roles = new RolesRepository(template);
        this.type = new TypeRepository(template);
        this.bookingObject = new ObjectRepository(template);
        this.booking = new BookingRepository(template);
        this.status = new StatusRepository(template);
        this.user = new UserRepository(template);
        this.campus = new CampusRepository(template);
    }

    public RolesRepository getRole() {
        return roles;
    }

    public void setRole(RolesRepository roles) {
        this.roles = roles;
    }

    public TypeRepository getType() {
        return type;
    }

    public void setType(TypeRepository type) {
        this.type = type;
    }

    public ObjectRepository getBookingObject() {
        return bookingObject;
    }

    public void setBookingObject(ObjectRepository bookingObject) {
        this.bookingObject = bookingObject;
    }

    public BookingRepository getBooking() {
        return booking;
    }

    public void setBooking(BookingRepository booking) {
        this.booking = booking;
    }

    public StatusRepository getStatus() {
        return status;
    }

    public void setStatus(StatusRepository status) {
        this.status = status;
    }

    public UserRepository getUser() {
        return user;
    }

    public void setUser(UserRepository user) {
        this.user = user;
    }

    public RolesRepository getRoles() {
        return roles;
    }

    public void setRoles(RolesRepository roles) {
        this.roles = roles;
    }

    public CampusRepository getCampus() {
        return campus;
    }

    public void setCampus(CampusRepository campus) {
        this.campus = campus;
    }

    //    @Override
//    public Connection getConnection() throws SQLException {
//        return null;
//    }
//
//    @Override
//    public Connection getConnection(String username, String password) throws SQLException {
//        return DriverManager.getConnection("jdbc:mysql://31.28.27.72/bocking_bot", "booker", "BBot21@shool");
//    }
//
//    @Override
//    public PrintWriter getLogWriter() throws SQLException {
//        return null;
//    }
//
//    @Override
//    public void setLogWriter(PrintWriter out) throws SQLException {
//
//    }
//
//    @Override
//    public void setLoginTimeout(int seconds) throws SQLException {
//
//    }
//
//    @Override
//    public int getLoginTimeout() throws SQLException {
//        return 0;
//    }
//
//    @Override
//    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
//        return null;
//    }
//
//    @Override
//    public <T> T unwrap(Class<T> iface) throws SQLException {
//        return null;
//    }
//
//    @Override
//    public boolean isWrapperFor(Class<?> iface) throws SQLException {
//        return false;
//    }
}
