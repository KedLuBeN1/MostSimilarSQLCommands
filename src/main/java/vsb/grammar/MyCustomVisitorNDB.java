package vsb.grammar;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.*;
import vsb.database.DBConnector;

public class MyCustomVisitorNDB extends PostgreSQLParserBaseVisitor<Void> {

    private final DBConnector dbConnector = new DBConnector();
    private int sqlId;

    private List<String> paths;

    public List<String> collectPaths(ParseTree tree, boolean identifier) {

        paths = new ArrayList<>();

        if(!identifier)
            collectPathNI(tree, new ArrayList<>());
        else
            collectPathNDB(tree, new ArrayList<>());

        return paths;
    }

    private void collectPath(ParseTree tree, List<String> path) {

        if (tree instanceof ParserRuleContext ctx) {
            String ruleName = PostgreSQLParser.ruleNames[ctx.getRuleIndex()];
            int ruleId = dbConnector.insertTermIfNotExists(ruleName.toLowerCase());
            if(ruleId != -1)
                path.add(Integer.toString(ruleId));
            //System.out.print(ruleName + "/");
        } else if (tree instanceof TerminalNode) {
            Token token = ((TerminalNode) tree).getSymbol();
            if (token.getType() != Token.EOF){
                int tokenId = dbConnector.insertTermIfNotExists(token.getText().toLowerCase());
                if(tokenId != -1)
                    path.add(Integer.toString(tokenId));
                //System.out.print(token.getText() + "/");
            }
        }

        if (tree.getChildCount() == 0 && path.size() > 1) {
            String pathStr = String.join("/", path);
            paths.add(pathStr);
            //System.out.println();
        } else {
            for (int i = 0; i < tree.getChildCount(); i++) {
                collectPath(tree.getChild(i), new ArrayList<>(path));
            }
        }

    }

    private void collectPathNDB(ParseTree tree, List<String> path) {

        if (tree instanceof ParserRuleContext ctx) {
            String ruleName = PostgreSQLParser.ruleNames[ctx.getRuleIndex()];
            //int ruleId = dbConnector.insertTermIfNotExists(ruleName.toLowerCase());
            //if(ruleId != -1)
            //path.add(Integer.toString(ruleId));
            path.add(ruleName.toLowerCase());
            //System.out.print(ruleName + "/");
        } else if (tree instanceof TerminalNode) {
            Token token = ((TerminalNode) tree).getSymbol();
            if (token.getType() != Token.EOF){
                //int tokenId = dbConnector.insertTermIfNotExists(token.getText().toLowerCase());
                //if(tokenId != -1)
                //path.add(Integer.toString(tokenId));
                path.add(token.getText().toLowerCase());
                //System.out.print(token.getText() + "/");
            }
        }

        if (tree.getChildCount() == 0 && path.size() > 1) {
            String pathStr = String.join("/", path);
            paths.add(pathStr);
            //System.out.println();
        } else {
            for (int i = 0; i < tree.getChildCount(); i++) {
                collectPathNDB(tree.getChild(i), new ArrayList<>(path));
            }
        }

    }

    private void collectPathNI(ParseTree tree, List<String> path) {
        if (tree instanceof ParserRuleContext ctx) {
            String ruleName = PostgreSQLParser.ruleNames[ctx.getRuleIndex()];
            //int ruleId = dbConnector.insertTermIfNotExists(ruleName.toLowerCase());
            //if(ruleId != -1)
               // path.add(Integer.toString(ruleId));
            path.add(ruleName.toLowerCase());
            //System.out.print(ruleName + "/");
        } else if (tree instanceof TerminalNode) {
            Token token = ((TerminalNode) tree).getSymbol();
            if (token.getType() != Token.EOF && token.getType() != PostgreSQLLexer.Identifier){
                //int tokenId = dbConnector.insertTermIfNotExists(token.getText().toLowerCase());
                //if(tokenId != -1)
                    //path.add(Integer.toString(tokenId));
                path.add(token.getText().toLowerCase());
                //System.out.print(token.getText() + "/");
            }
        }

        if (tree.getChildCount() == 0 && path.size() > 1) {
            String pathStr = String.join("/", path);
            paths.add(pathStr);
            //System.out.println();
        } else {
            for (int i = 0; i < tree.getChildCount(); i++) {
                collectPathNI(tree.getChild(i), new ArrayList<>(path));
            }
        }
    }
}