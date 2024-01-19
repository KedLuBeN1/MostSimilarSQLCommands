package vsb;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import vsb.grammar.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ComponentScan(basePackages = {"vsb", "restAPI"})
public class Main {
    public static void main(String[] args) {

        int choice = 1;

        if(choice == 0)
        {
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
                MyCustomVisitor visitor = new MyCustomVisitor(dbConnector.insertSQLStatement(sqlString));
                visitor.visit(tree);
                // scans input sql command from user
                sqlString = scanner.nextLine();
            }
        }
        else if(choice == 1)
        {
            Scanner scanner = new Scanner(System.in);
            DBConnector dbConnector = new DBConnector();

            System.out.println("Enter sql id 1: ");
            var sqlId1 = scanner.nextInt();
            System.out.println("Enter sql id 2: ");
            var sqlId2 = scanner.nextInt();

            var paths1 = dbConnector.getPathsById(sqlId1);
            var paths2 = dbConnector.getPathsById(sqlId2);

            System.out.println("Jaccard similarity: " + JaccardSimilarity.calculateJaccardSimilarity(paths1, paths2));
        }
        else if (choice == 2) {
            var dbConnector = new DBConnector();
            for(var sqlCommand : dbConnector.getAllSqlCommands()){
                System.out.println(sqlCommand);
            }
            SpringApplication.run(Main.class, args);
        }
        else {
            System.out.println("Wrong choice");
        }

    }
}