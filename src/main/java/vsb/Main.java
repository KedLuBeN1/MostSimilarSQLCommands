package vsb;

import vsb.grammar.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;


public class Main {
    public static void main(String[] args) {

        CharStream  input = CharStreams.fromString("select * from books;");
        // create a lexer that feeds off of input CharStream
        PostgreSQLLexer lexer = new PostgreSQLLexer(input);
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // create a parser that feeds off the tokens buffer
        PostgreSQLParser parser = new PostgreSQLParser(tokens);

        ParseTree tree = parser.root(); // begin parsing at init rule
        System.out.println(tree.toStringTree(parser)); // print LISP-style tree

        DBConnector dbConnector = new DBConnector();

        //dbConnector.insert("test/test3");
    }
}