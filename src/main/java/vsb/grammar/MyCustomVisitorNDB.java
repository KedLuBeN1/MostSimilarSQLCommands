package vsb.grammar;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.*;

public class MyCustomVisitorNDB extends PostgreSQLParserBaseVisitor<Void> {

    private List<String> paths;

    public List<String> collectPaths(ParseTree tree, boolean identifier) {
        paths = new ArrayList<>();

        if (!identifier)
            collectPathNI(tree, new ArrayList<>());
        else
            collectPathNDB(tree, new ArrayList<>());

        return paths;
    }

    // Collect paths with identifiers
    private void collectPathNDB(ParseTree tree, List<String> path) {
        if (tree instanceof ParserRuleContext ctx) {
            String ruleName = PostgreSQLParser.ruleNames[ctx.getRuleIndex()];
            path.add(ruleName.toLowerCase());
        } else if (tree instanceof TerminalNode) {
            Token token = ((TerminalNode) tree).getSymbol();
            if (token.getType() != Token.EOF) {
                path.add(token.getText().toLowerCase());
            }
        }

        if (tree.getChildCount() == 0 && path.size() > 1) {
            String pathStr = String.join("/", path);
            paths.add(pathStr);
        } else {
            for (int i = 0; i < tree.getChildCount(); i++) {
                collectPathNDB(tree.getChild(i), new ArrayList<>(path));
            }
        }

    }

    // Collect paths without identifiers
    private void collectPathNI(ParseTree tree, List<String> path) {
        if (tree instanceof ParserRuleContext ctx) {
            String ruleName = PostgreSQLParser.ruleNames[ctx.getRuleIndex()];
            path.add(ruleName.toLowerCase());
        } else if (tree instanceof TerminalNode) {
            Token token = ((TerminalNode) tree).getSymbol();
            if (token.getType() != Token.EOF && token.getType() != PostgreSQLLexer.Identifier) {
                path.add(token.getText().toLowerCase());
            }
        }

        if (tree.getChildCount() == 0 && path.size() > 1) {
            String pathStr = String.join("/", path);
            paths.add(pathStr);
        } else {
            for (int i = 0; i < tree.getChildCount(); i++) {
                collectPathNI(tree.getChild(i), new ArrayList<>(path));
            }
        }
    }
}