package vsb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class DBConnector {
    private  String url;
    private  String user;
    private  String password;
    private Connection connection;

    public DBConnector()
    {
        url = "jdbc:postgresql://localhost:5432/postgres";
        user = "postgres";
        password = "user";
        connection = null;
    }

    public void insert(String s)
    {
        try {
            connection = DriverManager.getConnection(url, user, password);
            String sql = "INSERT INTO paths (path) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, s);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
