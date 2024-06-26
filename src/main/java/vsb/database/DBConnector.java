package vsb.database;

import java.sql.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import vsb.model.SqlStatement;

@Component
public class DBConnector {
    private Connection connection;

    public DBConnector() {
        String url = "jdbc:postgresql://dbsys.cs.vsb.cz:5432/JUR0396";
        String user = "JUR0396";
        String password = "Z63Vuxpb6GjW442o";
        connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
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

    public int insertPath(String path) throws SQLException {
        String selectSql = "SELECT id FROM path_index WHERE path = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
            selectStmt.setString(1, path);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
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
            throw e;
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

    public int insertSQLStatement(String sqlStatement, int questionId) throws SQLException {
        String selectSql = "SELECT id FROM sql_statement WHERE sql_text = ? AND question_id = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
            selectStmt.setString(1, sqlStatement);
            selectStmt.setInt(2, questionId);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        String insertSql = "INSERT INTO sql_statement (sql_text, question_id) VALUES (?, ?) RETURNING id";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            insertStmt.setString(1, sqlStatement);
            insertStmt.setInt(2, questionId);
            ResultSet rs = insertStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
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

    public int getTerm(String term) {
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

        return -1;
    }

    public void linkPathToSQL(int pathId, int sqlId) throws SQLException {
        String insertSql = "INSERT INTO path_sql (p_id, s_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
            stmt.setInt(1, pathId);
            stmt.setInt(2, sqlId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void linkPathNIToSQL(int pathId, int sqlId) throws SQLException {
        String insertSql = "INSERT INTO path_sql_ni (p_id, s_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
            stmt.setInt(1, pathId);
            stmt.setInt(2, sqlId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
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

    public List<Integer> getAllSqlIds() {
        List<Integer> sqlIds = new ArrayList<>();
        String selectSql = "SELECT id FROM sql_statement";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
            ResultSet rs = selectStmt.executeQuery();

            while (rs.next()) {
                sqlIds.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sqlIds;
    }

    public List<SqlStatement> getSqlStatements() {
        List<SqlStatement> sqlStatements = new ArrayList<>();
        String selectSql = "SELECT id, sql_text" +
                ", question_id FROM sql_statement";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
            ResultSet rs = selectStmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String sqlStatement = rs.getString("sql_text");
                int questionId = rs.getInt("question_id");

                SqlStatement info = new SqlStatement(id, sqlStatement, questionId);
                sqlStatements.add(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sqlStatements;
    }

    public List<Map.Entry<SqlStatement, Double>> findSimilarSQLStatements(List<String> inputPaths, boolean useIdentifiers) throws SQLException {
        List<Map.Entry<SqlStatement, Double>> similarStatements = new ArrayList<>();
        String findFuncSql;

        if (useIdentifiers)
            findFuncSql = "SELECT * FROM find_similar_sql_statements(?)";
        else
            findFuncSql = "SELECT * FROM find_similar_sql_statements_ni(?)";

        try (PreparedStatement stmt = connection.prepareStatement(findFuncSql)) {
            Array inputPathsArray = connection.createArrayOf("text", inputPaths.toArray());
            stmt.setArray(1, inputPathsArray);

            var startTime = System.currentTimeMillis();
            System.out.println("Executing find_similar_sql_statements");
            ResultSet rs = stmt.executeQuery();

            var endTime = System.currentTimeMillis();
            System.out.println("Time elapsed(Executing find_similar_sql_statements): " + (endTime - startTime) + "ms");

            startTime = System.currentTimeMillis();
            System.out.println("Processing results");
            while (rs.next()) {
                int sqlId = rs.getInt("sql_id");
                String sqlText = rs.getString("sql_text");
                int questionId = rs.getInt("question_id");
                double similarity = rs.getDouble("similarity");

                SqlStatement sqlStatement = new SqlStatement(sqlId, sqlText, questionId);
                similarStatements.add(new AbstractMap.SimpleEntry<>(sqlStatement, similarity));
            }
            endTime = System.currentTimeMillis();
            System.out.println("Time elapsed(Processing results): " + (endTime - startTime) + "ms");
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        return similarStatements;
    }

    public void insertPaths(List<String> paths, int sqlId) throws SQLException {
        for (String path : paths) {
            int pathId = insertPath(path);
            linkPathToSQL(pathId, sqlId);
        }
    }

    public void insertPathsNI(List<String> paths, int sqlId) throws SQLException {
        for (String path : paths) {
            int pathId = insertPath(path);
            linkPathNIToSQL(pathId, sqlId);
        }
    }

    public void insertData(String sqlCommand, List<String> paths, int questionId, boolean useIdentifiers) throws SQLException {

        String sqlCall;

        if (useIdentifiers)
            sqlCall = "{call insert_data(?, ?, ?)}";
        else
            sqlCall = "{call insert_data_ni(?, ?, ?)}";

        try {
            connection.setAutoCommit(false);
            try (CallableStatement callableStatement = connection.prepareCall(sqlCall)) {
                callableStatement.setString(1, sqlCommand);
                callableStatement.setArray(2, connection.createArrayOf("text", paths.toArray()));
                callableStatement.setInt(3, questionId);

                callableStatement.execute();
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public List<String> convertNewPathsToIds(List<String> paths) throws SQLException {

        String selectSql = "SELECT convert_new_paths_to_ids(?) AS result";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {

            selectStmt.setArray(1, connection.createArrayOf("text", paths.toArray()));

            ResultSet rs = selectStmt.executeQuery();

            try (ResultSet resultSet = selectStmt.executeQuery()) {
                if (resultSet.next()) {
                    String[] resultArray = (String[]) resultSet.getArray("result").getArray();
                    return List.of(resultArray);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<String> convertPathsToIds(List<String> paths) throws SQLException {

        String selectSql = "SELECT convert_paths_to_ids(?) AS result";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {

            selectStmt.setArray(1, connection.createArrayOf("text", paths.toArray()));

            ResultSet rs = selectStmt.executeQuery();

            try (ResultSet resultSet = selectStmt.executeQuery()) {
                if (resultSet.next()) {
                    String[] resultArray = (String[]) resultSet.getArray("result").getArray();
                    return List.of(resultArray);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
