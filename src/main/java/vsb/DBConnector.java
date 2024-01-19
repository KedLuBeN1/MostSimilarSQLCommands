package vsb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class DBConnector {
    private  final String url;
    private  final String user;
    private  final String password;
    private Connection connection;

    public DBConnector()
    {
        url = "jdbc:postgresql://dbsys.cs.vsb.cz:5432/JUR0396";
        user = "JUR0396";
        password = "Z63Vuxpb6GjW442o";
        connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertUniquePath(String path) {
        String sql = "INSERT INTO path_index (path) VALUES (?) ON CONFLICT (path) DO NOTHING";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, path);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insertPath(String path) {
        String selectSql = "SELECT id FROM path_index WHERE path = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
            selectStmt.setString(1, path);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String insertSql = "INSERT INTO path_index (path) VALUES (?) RETURNING id";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            insertStmt.setString(1, path);
            ResultSet rs = insertStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int insertSQLStatement(String sqlStatement) {
        String selectSql = "SELECT id FROM sql_statement WHERE sql_text = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
            selectStmt.setString(1, sqlStatement);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String insertSql = "INSERT INTO sql_statement (sql_text) VALUES (?) RETURNING id";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            insertStmt.setString(1, sqlStatement);
            ResultSet rs = insertStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }


    public int getPathId(String path) {
        String sql = "SELECT id FROM path_index WHERE path = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, path);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getSQLId(String sqlText) {
        String sql = "SELECT id FROM sql_statement WHERE sql_text = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, sqlText);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int insertTermIfNotExists(String term) {
        String selectSql = "SELECT id FROM term_index WHERE term = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
            selectStmt.setString(1, term);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String insertSql = "INSERT INTO term_index (term) VALUES (?) RETURNING id";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            insertStmt.setString(1, term);
            ResultSet rs = insertStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void linkPathToSQL(int pathId, int sqlId) {
        String insertSql = "INSERT INTO path_sql (p_id, s_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
            stmt.setInt(1, pathId);
            stmt.setInt(2, sqlId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getPathsById(int sqlId) {
        List<String> paths = new ArrayList<>();

        String sql = "SELECT path_index.path FROM path_index " +
                "INNER JOIN path_sql ON path_index.id = path_sql.p_id " +
                "WHERE path_sql.s_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, sqlId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    paths.add(resultSet.getString("path"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return paths;
    }

    public List<String> getAllSqlCommands() {
        List<String> sqlCommands = new ArrayList<>();

        String sql = "SELECT sql_text FROM sql_statement";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    sqlCommands.add(resultSet.getString("sql_text"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sqlCommands;
    }

}
