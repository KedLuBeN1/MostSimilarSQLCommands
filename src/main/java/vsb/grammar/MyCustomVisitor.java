package vsb.grammar;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.*;
import vsb.DBConnector;

public class MyCustomVisitor extends PostgreSQLParserBaseVisitor<Void> {

    private final DBConnector dbConnector = new DBConnector();
    private int sqlId;

    public MyCustomVisitor(int sqlId)
    {
        this.sqlId = sqlId;
    }

    @Override
    public Void visit(ParseTree tree) {
        collectPath(tree, new ArrayList<>(), "");
        return super.visit(tree);
    }

    private void collectPath(ParseTree tree, List<String> path, String parentText) {
        String currentText = tree.getText();

        if (!currentText.isEmpty() && !currentText.equals(parentText) ) {
            int termId = dbConnector.insertTermIfNotExists(currentText);
            if (termId != -1) {
                path.add(Integer.toString(termId));
            }
        }

        if (tree.getChildCount() == 0 && path.size() > 1) {
            String pathStr = String.join("/", path);
            int pathId = dbConnector.insertPath(pathStr);
            if (pathId != -1 && sqlId != -1) {
                dbConnector.linkPathToSQL(pathId, sqlId);
            }
        }

        for (int i = 0; i < tree.getChildCount(); i++) {
            collectPath(tree.getChild(i), new ArrayList<>(path), currentText);
        }
    }

}