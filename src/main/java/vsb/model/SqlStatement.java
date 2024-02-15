package vsb.model;

public class SqlStatement {
    private int id;
    private String sqlStatement;
    private int questionId;

    public SqlStatement(int id, String sqlStatement) {
        this.id = id;
        this.sqlStatement = sqlStatement;
    }

    public SqlStatement(int id, String sqlStatement, int questionId) {
        this.id = id;
        this.sqlStatement = sqlStatement;
        this.questionId = questionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSqlStatement() {
        return sqlStatement;
    }

    public void setSqlStatement(String sqlStatement) {
        this.sqlStatement = sqlStatement;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
}
