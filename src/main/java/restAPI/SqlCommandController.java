package restAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vsb.DBConnector;

import java.util.List;

@RestController
@RequestMapping("/api/sql-commands")
public class SqlCommandController {

    private final DBConnector dbConnector;

    @Autowired
    public SqlCommandController(DBConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    @GetMapping
    public List<String> getAllSqlCommands() {
        // Zde získáš seznam všech SQL příkazů ze svého DBConnectoru
        return dbConnector.getAllSqlCommands();
    }

    // Další metody pro manipulaci s SQL příkazy
}
