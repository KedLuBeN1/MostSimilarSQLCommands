package restAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SqlCommandService {

    private final SqlCommandRepository sqlCommandRepository;

    @Autowired
    public SqlCommandService(SqlCommandRepository sqlCommandRepository) {
        this.sqlCommandRepository = sqlCommandRepository;
    }
}
