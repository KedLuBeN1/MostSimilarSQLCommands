package vsb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class DBConnector {
    private  final String url;
    private  final String user;
    private  final String password;
    private Connection connection;

    public DBConnector()
    {
        url = "jdbc:postgresql://localhost:5432/postgres";
        user = "postgres";
        password = "user";
        connection = null;
    }

    public void insert(String treePath)
    {
        try {
            connection = DriverManager.getConnection(url, user, password);
            var sql = "INSERT INTO paths (path) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, treePath);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
