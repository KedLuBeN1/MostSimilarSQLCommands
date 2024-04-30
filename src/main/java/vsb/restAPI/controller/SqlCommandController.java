package vsb.restAPI.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.antlr.v4.runtime.RecognitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vsb.model.SqlStatement;
import vsb.restAPI.service.SqlCommandService;
import vsb.xml.model.Statement;

import java.util.List;
import java.util.Map;

@RestController
public class SqlCommandController {

    private final SqlCommandService sqlCommandService;

    public SqlCommandController(SqlCommandService sqlCommandService){
        this.sqlCommandService = sqlCommandService;
    }

    // endpoint for inserting SQL commands to the database
    @PostMapping("/insertSql")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> insertSqlCommand(@RequestBody Map<String, String> request) {

        String sqlQuery = request.get("sqlQuery");
        String questionId = request.get("id");
        boolean useIdentifiers = request.get("useIdentifiers").equals("true");;

        int intValue = 0;

        try {
            intValue = Integer.parseInt(questionId);
        } catch (NumberFormatException e) {
            System.out.println("Error: The string is not a valid integer.");
            return ResponseEntity.badRequest().body("Invalid question ID.");
        }

        if(sqlQuery == null || sqlQuery.isEmpty()) {
            return ResponseEntity.badRequest().body("SQL command cannot be empty.");
        }
        try {
            long startTime = System.currentTimeMillis();
            System.out.println("Sending SQL command: " + sqlQuery);
            sqlCommandService.insertSQLStatementDB(sqlQuery, intValue, useIdentifiers);
            long endTime = System.currentTimeMillis();
            System.out.println("Time elapsed: " + (endTime - startTime) + "ms");
        } catch (StackOverflowError e) {
            return ResponseEntity.badRequest().body("Invalid SQL command or processing error.");
        } catch (RecognitionException e) {
            System.out.println("controller: Invalid SQL command.");
            return ResponseEntity.badRequest().body("Invalid SQL command.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to insert SQL command.");
        }

        return ResponseEntity.ok("SQL command successfully inserted to database.");
    }

    // endpoint for processing SQL commands, then finding similar SQL commands in the database
    @PostMapping("/processSql")
    public ResponseEntity<String> processSql(@RequestBody Map<String, String> sqlRequest) {
        String sqlQuery = sqlRequest.get("sqlQuery");
        boolean useIdentifiers = sqlRequest.get("useIdentifiers").equals("true");;

        if(sqlQuery == null || sqlQuery.isEmpty()) {
            return ResponseEntity.badRequest().body("SQL command cannot be empty.");
        }

        List<Map.Entry<SqlStatement, Double>> similarStatements;

        try {
            similarStatements = sqlCommandService.findSimilarSQLStatementsInDB(sqlQuery, useIdentifiers);
        }
        catch (Exception e) {
            System.out.println("controller: Invalid SQL command.");
            return ResponseEntity.badRequest().body("Invalid SQL command.");
        }

        if(similarStatements.isEmpty()) {
            return ResponseEntity.ok("No similar SQL commands found.");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode jsonResult = objectMapper.createArrayNode();

        for (Map.Entry<SqlStatement, Double> sqlStatement : similarStatements) {
            ObjectNode statementNode = objectMapper.createObjectNode();
            statementNode.put("questionId", sqlStatement.getKey().getQuestionId());
            statementNode.put("sqlStatement", sqlStatement.getKey().getSqlStatement());
            statementNode.put("similarity", sqlStatement.getValue());
            jsonResult.add(statementNode);
        }

        try {
            String jsonString = objectMapper.writeValueAsString(jsonResult);
            return ResponseEntity.ok(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the result.");
        }
    }
}
