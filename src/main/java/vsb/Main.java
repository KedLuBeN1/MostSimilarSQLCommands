package vsb;

import vsb.database.DBConnector;
import vsb.grammar.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.File;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import vsb.restAPI.service.SqlCommandService;
import vsb.xml.XmlReader;
import vsb.xml.model.SqlStatements;
import vsb.xml.model.Statement;

import javax.xml.bind.JAXBException;

@SpringBootApplication
//@ComponentScan(basePackages = {"vsb", "vsb.restAPI"})
public class Main {
    public static void main(String[] args){

        int choice = 2;

        if(choice == 0)
        {
            System.out.println("Write 'quit' to stop");
            System.out.println("Enter sql commands: ");

            Scanner scanner = new Scanner(System.in);
            System.out.println("choice vytvari dbconnector");
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

                parser.removeErrorListeners();
                //parser.addErrorListener(new LexerDispatchingErrorListener(lexer));
                parser.addErrorListener(new ParserDispatchingErrorListener(parser));

                // begin parsing at root rule
                try{
                    ParseTree tree = parser.root();
                    // insert returned tree into database
                    MyCustomVisitor visitor = new MyCustomVisitor(dbConnector.insertSQLStatement(sqlString));
                    visitor.visit(tree);
                } catch (RecognitionException e) {
                    System.out.println("Invalid SQL command.");
                } catch (Exception e) {
                    System.out.println("Failed to insert SQL command.");
                }

                // scans input sql command from user
                sqlString = scanner.nextLine();
            }
        }
        else if(choice == 1)
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("choice vytvari dbconnector");
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
            SpringApplication.run(Main.class, args);
        }
        else if (choice == 3) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter path of xml file to scan from:");

            var path = scanner.nextLine();

            File xmlFile = new File(path);

            if(!xmlFile.exists())
            {
                System.out.println("File does not exist");
                return;
            }

            try {
                SqlStatements sqlStatements = XmlReader.readXml(xmlFile);
                SqlCommandService sqlCommandService = new SqlCommandService(new DBConnector());

                for(Statement sql : sqlStatements.getStatementList())
                {
                    try {
                        sqlCommandService.insertSQLStatement(sql.getValue(), sql.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else if (choice == 4) {
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

                //parser.removeErrorListeners();
                //parser.addErrorListener(new LexerDispatchingErrorListener(lexer));
                //parser.addErrorListener(new ParserDispatchingErrorListener(parser));

                // begin parsing at root rule
                try {
                    ParseTree tree = parser.root();
                    // insert returned tree into database
                    MyCustomVisitorNDB visitor = new MyCustomVisitorNDB();
                    var list = visitor.collectPaths(tree);

                    List<Integer> allSqlIds = dbConnector.getAllSqlIds();
                    int i = 0;

                    for (int sqlId : allSqlIds) {
                        List<String> dbPaths = dbConnector.getPathsById(sqlId);
                        i++;
                        System.out.println(i + " " + sqlId + " Jaccard similarity: " + JaccardSimilarity.calculateJaccardSimilarity(list, dbPaths));
                    }
                }catch (RecognitionException e) {
                    System.out.println("controller: Invalid SQL command.");
                    return; // Přerušení zpracování v případě chyby
                } catch (Exception e) {
                    System.out.println("Failed to insert SQL command.");
                }
                // scans input sql command from user
                sqlString = scanner.nextLine();
            }
        }
        else {
            System.out.println("Wrong choice");
        }

    }
}