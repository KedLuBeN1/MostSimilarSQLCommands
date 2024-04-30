package vsb.grammar;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.*;
import vsb.database.DBConnector;

// Outdated version of class MyCustomVisitorNDB, that is trying to find ids of paths in the database during the collection process
public class MyCustomVisitor extends PostgreSQLParserBaseVisitor<Void> {

    private final DBConnector dbConnector = new DBConnector();
    private int sqlId;

    public MyCustomVisitor(int sqlId) {
        this.sqlId = sqlId;
    }

    @Override
    public Void visit(ParseTree tree) {
        try {
            collectPath(tree, new ArrayList<>());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return super.visit(tree);
    }

    private void collectPath(ParseTree tree, List<String> path) throws SQLException {
        if (tree instanceof ParserRuleContext ctx) { // Check if the node is a parser rule context
            String ruleName = PostgreSQLParser.ruleNames[ctx.getRuleIndex()]; // Get name of the rule

            int ruleId = dbConnector.insertTermIfNotExists(ruleName);
            if (ruleId != -1) {
                path.add(Integer.toString(ruleId));
            }
        } else if (tree instanceof TerminalNode) { // Check if the node is a terminal node
            Token token = ((TerminalNode) tree).getSymbol();
            if (token.getType() != Token.EOF) {
                int tokenId = dbConnector.insertTermIfNotExists(token.getText());
                if (tokenId != -1) {
                    path.add(Integer.toString(tokenId));
                }
            }
        }

        if (tree.getChildCount() == 0 && path.size() > 1) {
            String pathStr = String.join("/", path);
            int pathId = dbConnector.insertPath(pathStr);
            if (pathId != -1 && sqlId != -1) {
                dbConnector.linkPathToSQL(pathId, sqlId);
            }
        } else {
            for (int i = 0; i < tree.getChildCount(); i++) {
                collectPath(tree.getChild(i), new ArrayList<>(path));
            }
        }
    }


}