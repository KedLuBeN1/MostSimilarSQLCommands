package vsb.grammar;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.*;
import vsb.DBConnector;

public class MyCustomVisitor extends PostgreSQLParserBaseVisitor<Void> {
    private final DBConnector dbConnector = new DBConnector();

    @Override
    public Void visit(ParseTree tree) {
        collectPath(tree, new ArrayList<>(), "");
        return super.visit(tree);
    }

    private void collectPath(ParseTree tree, List<String> path, String parentText) {
        String currentText = tree.getText();

        if (!currentText.equals(parentText) ) {
            path.add(currentText);
        }

        if (tree.getChildCount() == 0 && path.size() > 1) {
            dbConnector.insertPath(new ArrayList<>(path));
        }

        for (int i = 0; i < tree.getChildCount(); i++) {
            collectPath(tree.getChild(i), new ArrayList<>(path), currentText);
        }
    }

}