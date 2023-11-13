package vsb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.List;

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
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(String treePath)
    {
        try {
            var sql = "INSERT INTO paths (path) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, treePath);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPath(List<String> treePath)
    {
        try {
            for(var node : treePath) {
                var sql = "INSERT INTO paths (path) VALUES (?) ON CONFLICT (path) DO NOTHING";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, node);
                preparedStatement.executeUpdate();
                System.out.println(node);
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
