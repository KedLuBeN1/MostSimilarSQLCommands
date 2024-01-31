package vsb.restAPI.service;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.NoViableAltException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vsb.DBConnector;
import vsb.grammar.*;


@Service
public class SqlCommandService {

    private final DBConnector dbConnector;

    public SqlCommandService(DBConnector dbConnector){
        this.dbConnector = dbConnector;
    }

    public void insertSQLStatement(String sqlString)
    {
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

        System.out.println("Jsem pred parseer.");
        ParseTree tree = parser.root();
        System.out.println("Jsem za parseer.");
        // insert sql command into database
        int sqlId = dbConnector.insertSQLStatement(sqlString);
        // insert returned tree into database
        MyCustomVisitor visitor = new MyCustomVisitor(sqlId);
        visitor.visit(tree);

    }
}
