package vsb.restAPI.service;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import vsb.database.DBConnector;
import vsb.JaccardSimilarity;
import vsb.grammar.*;
import vsb.model.SqlStatement;

import java.sql.SQLException;
import java.util.*;

@Service
public class SqlCommandService {

    private final DBConnector dbConnector;

    public SqlCommandService(DBConnector dbConnector){
        this.dbConnector = dbConnector;
    }

    public void insertSQLStatement(String sqlString, int questionId) throws SQLException {
        try {
            dbConnector.setAutoCommit(false);
            long startTime = System.currentTimeMillis();
            System.out.println("Creating parsing tree: ");
            // create a char stream from sql command
            CharStream input = CharStreams.fromString(sqlString);
            // create a lexer that feeds off of input CharStream
            PostgreSQLLexer lexer = new PostgreSQLLexer(input);
            // create a buffer of tokens pulled from the lexer
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            // create a parser that feeds off the tokens buffer
            PostgreSQLParser parser = new PostgreSQLParser(tokens);

            //parser.removeErrorListeners();
            //parser.addErrorListener(new LexerDispatchingErrorListener(lexer));
            //parser.addErrorListener(new ParserDispatchingErrorListener(parser));

            // begin parsing at root rule
            ParseTree tree = parser.root();

            long endTime = System.currentTimeMillis();
            System.out.println("Time elapsed(Creating parsing tree): " + (endTime - startTime) + "ms");

            // insert sql command into database
            System.out.println("   Inserting SQL command into database: " + sqlString);
            startTime = System.currentTimeMillis();
            int sqlId = dbConnector.insertSQLStatement(sqlString, questionId);
            endTime = System.currentTimeMillis();
            System.out.println("Time elapsed(Inserting SQL command into database): " + (endTime - startTime) + "ms");
            // insert returned tree into database

            System.out.println("getting paths");
            startTime = System.currentTimeMillis();
            MyCustomVisitorNDB visitor = new MyCustomVisitorNDB();

            var paths = visitor.collectPaths(tree, true);
            endTime = System.currentTimeMillis();
            System.out.println("Time elapsed(getting paths): " + (endTime - startTime) + "ms");


            System.out.println("Inserting paths into database");
            startTime = System.currentTimeMillis();
            dbConnector.insertPaths(paths, sqlId);
            endTime = System.currentTimeMillis();
            System.out.println("Time elapsed(Inserting paths into database): " + (endTime - startTime) + "ms");

            dbConnector.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                dbConnector.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            throw e;
        } finally {
            try {
                dbConnector.setAutoCommit(true);
            } catch (SQLException autoCommitException) {
                autoCommitException.printStackTrace();
            }
        }
    }

    public void insertSQLStatementDB(String sqlString, int questionId) throws SQLException {
        try {

            long startTime = System.currentTimeMillis();
            System.out.println("Creating parsing tree: ");
            // create a char stream from sql command
            CharStream input = CharStreams.fromString(sqlString);
            // create a lexer that feeds off of input CharStream
            PostgreSQLLexer lexer = new PostgreSQLLexer(input);
            // create a buffer of tokens pulled from the lexer
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            // create a parser that feeds off the tokens buffer
            PostgreSQLParser parser = new PostgreSQLParser(tokens);

            //parser.removeErrorListeners();
            //parser.addErrorListener(new LexerDispatchingErrorListener(lexer));
            //parser.addErrorListener(new ParserDispatchingErrorListener(parser));

            // begin parsing at root rule
            ParseTree tree = parser.root();

            long endTime = System.currentTimeMillis();
            System.out.println("Time elapsed(Creating parsing tree): " + (endTime - startTime) + "ms");

            System.out.println("getting paths");
            startTime = System.currentTimeMillis();
            MyCustomVisitorNDB visitor = new MyCustomVisitorNDB();

            var paths = visitor.collectPaths(tree, true);
            endTime = System.currentTimeMillis();
            System.out.println("Time elapsed(getting paths): " + (endTime - startTime) + "ms");

            System.out.println("Convering paths to ids");
            startTime = System.currentTimeMillis();
            var list = dbConnector.convertPathsToIds(paths);
            endTime = System.currentTimeMillis();
            System.out.println("Time elapsed(Convering paths to ids): " + (endTime - startTime) + "ms");

            System.out.println("Inserting paths into database");
            startTime = System.currentTimeMillis();
            dbConnector.insertData(sqlString, list, questionId);
            endTime = System.currentTimeMillis();
            System.out.println("Time elapsed(Inserting paths into database): " + (endTime - startTime) + "ms");

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Map.Entry<SqlStatement, Double>> findSimilarSQLStatements(String sqlString) {
        List<Map.Entry<SqlStatement, Double>> similarStatements = new ArrayList<>();

        // create a char stream from sql command
        CharStream  input = CharStreams.fromString(sqlString);
        // create a lexer that feeds off of input CharStream
        PostgreSQLLexer lexer = new PostgreSQLLexer(input);
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // create a parser that feeds off the tokens buffer
        PostgreSQLParser parser = new PostgreSQLParser(tokens);

        //parser.removeErrorListeners();
        //parser.addErrorListener(new LexerDispatchingErrorListener(lexer));
        //parser.addErrorListener(new ParserDispatchingErrorListener(parser));

        try {
            // begin parsing at root rule
            ParseTree tree = parser.root();
            // insert returned tree into database
            MyCustomVisitorNDB visitor = new MyCustomVisitorNDB();
            var sqlPaths = visitor.collectPaths(tree, true);

            var allSqlStatements = dbConnector.getSqlStatements();

            for (SqlStatement sqlStatement : allSqlStatements) {
                List<String> dbPaths = dbConnector.getPathsById(sqlStatement.getId());

                var jaccardSimilarity = JaccardSimilarity.calculateJaccardSimilarity(sqlPaths, dbPaths);

                if (jaccardSimilarity > 0.5) {
                    similarStatements.add(new AbstractMap.SimpleEntry<>(sqlStatement, jaccardSimilarity));
                }
            }

            similarStatements.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        int numberOfElementsToRetrieve = Math.min(20, similarStatements.size());

        return similarStatements.subList(0, numberOfElementsToRetrieve);
    }

    public List<Map.Entry<SqlStatement, Double>> findSimilarSQLStatementsInDB(String sqlString, boolean useIdentifiers) {

        // create a char stream from sql command
        CharStream  input = CharStreams.fromString(sqlString);
        // create a lexer that feeds off of input CharStream
        PostgreSQLLexer lexer = new PostgreSQLLexer(input);
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // create a parser that feeds off the tokens buffer
        PostgreSQLParser parser = new PostgreSQLParser(tokens);

        //parser.removeErrorListeners();
        //parser.addErrorListener(new LexerDispatchingErrorListener(lexer));
        //parser.addErrorListener(new ParserDispatchingErrorListener(parser));

        try {
            // begin parsing at root rule
            ParseTree tree = parser.root();
            // insert returned tree into database
            MyCustomVisitorNDB visitor = new MyCustomVisitorNDB();
            var sqlPaths = visitor.collectPaths(tree, useIdentifiers);

            return dbConnector.findSimilarSQLStatements(sqlPaths, useIdentifiers);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
}
