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

    public List<String> collectPaths(ParseTree tree) {
        List<String> paths = new ArrayList<>();

        collectPath(tree, new ArrayList<>(), paths);

        return paths;
    }

    private void collectPath(ParseTree tree, List<String> path, List<String> paths) {
        if (tree instanceof ParserRuleContext ctx) {
            String ruleName = PostgreSQLParser.ruleNames[ctx.getRuleIndex()]; // Získání názvu pravidla
            int ruleId = dbConnector.insertTermIfNotExists(ruleName.toLowerCase());
            if(ruleId != -1)
                path.add(Integer.toString(ruleId));
        } else if (tree instanceof TerminalNode) { // Kontrola, zda je uzel terminál
            Token token = ((TerminalNode) tree).getSymbol();
            if (token.getType() != Token.EOF) {
                int tokenId = dbConnector.insertTermIfNotExists(token.getText().toLowerCase());
                if(tokenId != -1)
                    path.add(Integer.toString(tokenId));
            }
        }

        if (tree.getChildCount() == 0 && path.size() > 1) {
            String pathStr = String.join("/", path);
            paths.add(pathStr);
        } else {
            for (int i = 0; i < tree.getChildCount(); i++) {
                collectPath(tree.getChild(i), new ArrayList<>(path), paths);
            }
        }
    }
}