package vsb;

import vsb.grammar.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {

        System.out.println("Write 'quit' to stop");
        System.out.println("Enter sql commands: ");

        Scanner scanner = new Scanner(System.in);
        DBConnector dbConnector = new DBConnector();
        String sqlString = scanner.nextLine(); // scans input sql command from user

        while(!sqlString.equals("quit"))
        {
            // create a char stream from sql command
            CharStream  input = CharStreams.fromString(sqlString);
            // create a lexer that feeds off of input CharStream
            PostgreSQLLexer lexer = new PostgreSQLLexer(input);
            // create a buffer of tokens pulled from the lexer
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            // create a parser that feeds off the tokens buffer
            PostgreSQLParser parser = new PostgreSQLParser(tokens);
            // begin parsing at root rule
            ParseTree tree = parser.root();
            // insert returned tree into database
            dbConnector.insert(tree.toStringTree(parser));
            // scans input sql command from user
            sqlString = scanner.nextLine();
        }
    }
}