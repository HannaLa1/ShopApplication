import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBShop {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/shop?" +
            "allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASSWORD = "anN1adutko111";
    private Connection connection;

    public DBShop() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
