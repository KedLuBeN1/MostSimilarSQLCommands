package vsb;

import vsb.database.DBConnector;

import java.io.File;
import java.sql.SQLException;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import vsb.restAPI.service.SqlCommandService;
import vsb.xml.XmlReader;
import vsb.xml.model.SqlStatements;
import vsb.xml.model.Statement;

@SpringBootApplication
public class Main {
    public static void main(String[] args){
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java -jar MostSimSQLComm-1.0-SNAPSHOT.jar <path_to_xml_file> [-ni]");
            return;
        }

        String path = args[0];
        boolean useIdentifiers = true;

        if (args.length == 2 && args[1].equals("-ni")) {
            useIdentifiers = false;
        }

        File xmlFile = new File(path);

        if(!xmlFile.exists())
        {
            System.out.println("File does not exist");
            return;
        }

        DBConnector dbConnector = new DBConnector();

        try {
            SqlStatements sqlStatements = XmlReader.readXml(xmlFile);

            SqlCommandService sqlCommandService = new SqlCommandService(dbConnector);

            for(Statement sql : sqlStatements.getStatementList())
            {
                int cnt = sql.getValue().replaceAll("\\s", "").length();

                if(cnt > 600){
                    continue;
                }
                try {
                    sqlCommandService.insertSQLStatementDB(sql.getValue(), sql.getId(), useIdentifiers);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                dbConnector.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}