package vsb.restAPI.service;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;

import org.springframework.stereotype.Service;
import vsb.database.DBConnector;
import vsb.JaccardSimilarity;
import vsb.grammar.*;
import vsb.model.SqlStatement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SqlCommandService {

    private final DBConnector dbConnector;

    public SqlCommandService(DBConnector dbConnector){
        this.dbConnector = dbConnector;
    }

    public void insertSQLStatement(String sqlString, int questionId)
    {
        try {
            dbConnector.setAutoCommit(false);
            // create a char stream from sql command
            CharStream input = CharStreams.fromString(sqlString);
            // create a lexer that feeds off of input CharStream
            PostgreSQLLexer lexer = new PostgreSQLLexer(input);
            // create a buffer of tokens pulled from the lexer
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            // create a parser that feeds off the tokens buffer
            PostgreSQLParser parser = new PostgreSQLParser(tokens);

            parser.removeErrorListeners();
            parser.addErrorListener(new LexerDispatchingErrorListener(lexer));
            parser.addErrorListener(new ParserDispatchingErrorListener(parser));

            // begin parsing at root rule
            ParseTree tree = parser.root();

            // insert sql command into database
            int sqlId = dbConnector.insertSQLStatement(sqlString, questionId);
            // insert returned tree into database
            MyCustomVisitorNDB visitor = new MyCustomVisitorNDB();

            var paths = visitor.collectPaths(tree);

            dbConnector.insertPaths(paths, sqlId);

            dbConnector.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                dbConnector.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        } finally {
            try {
                dbConnector.setAutoCommit(true);
            } catch (SQLException autoCommitException) {
                autoCommitException.printStackTrace();
            }
        }
    }

    public List<SqlStatement> findSimilarSQLStatements(String sqlString) {
        List<SqlStatement> similarStatements = new ArrayList<>();

        // create a char stream from sql command
        CharStream  input = CharStreams.fromString(sqlString);
        // create a lexer that feeds off of input CharStream
        PostgreSQLLexer lexer = new PostgreSQLLexer(input);
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // create a parser that feeds off the tokens buffer
        PostgreSQLParser parser = new PostgreSQLParser(tokens);

        parser.removeErrorListeners();
        //parser.addErrorListener(new LexerDispatchingErrorListener(lexer));
        parser.addErrorListener(new ParserDispatchingErrorListener(parser));

        try {
            // begin parsing at root rule
            ParseTree tree = parser.root();
            // insert returned tree into database
            MyCustomVisitorNDB visitor = new MyCustomVisitorNDB();
            var sqlPaths = visitor.collectPaths(tree);

            var allSqlStatements = dbConnector.getSqlStatements();

            for (SqlStatement sqlStatement : allSqlStatements) {
                List<String> dbPaths = dbConnector.getPathsById(sqlStatement.getId());

                var jaccardSimilarity = JaccardSimilarity.calculateJaccardSimilarity(sqlPaths, dbPaths);

                if (jaccardSimilarity > 0.5) {
                    similarStatements.add(sqlStatement);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return similarStatements;
    }
}
