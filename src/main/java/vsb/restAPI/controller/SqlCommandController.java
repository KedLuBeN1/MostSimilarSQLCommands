package vsb.restAPI.controller;

import org.antlr.v4.runtime.RecognitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vsb.restAPI.service.SqlCommandService;

import java.util.List;
import java.util.Map;

@RestController
public class SqlCommandController {

    private final SqlCommandService sqlCommandService;

    public SqlCommandController(SqlCommandService sqlCommandService){
        this.sqlCommandService = sqlCommandService;
    }

    @PostMapping("/insertSql")
    public ResponseEntity<String> insertSqlCommand(@RequestBody Map<String, String> sqlRequest) {
        String sqlQuery = sqlRequest.get("sqlQuery");
        if(sqlQuery == null || sqlQuery.isEmpty()) {
            return ResponseEntity.badRequest().body("SQL command cannot be empty.");
        }
        try {
            sqlCommandService.insertSQLStatement(sqlQuery);
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

    @PostMapping("/processSql")
    public ResponseEntity<String> processSql(@RequestBody Map<String, String> sqlRequest) {
        String sqlQuery = sqlRequest.get("sqlQuery");
        System.out.println("V poradku prisel prikaz: " + sqlQuery);
        return ResponseEntity.ok("V poradku prisel prikaz: " + sqlQuery);
    }
}
