package vsb.restAPI;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vsb.xml.XmlReader;
import vsb.xml.model.SqlStatements;
import vsb.xml.model.Statement;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class RestClient {
    private static final String REST_API_URL = "http://localhost:8080/insertSql";

    public static void main(String[] args) {

        File xmlFile = new File("final_correct.xml");
        try {
            SqlStatements sqlStatements = XmlReader.readXml(xmlFile);

            int i = 0;
            for (Statement sqlCommand : sqlStatements.getStatementList()) {
                if(i++ > 10) break;
                sendPostRequest(sqlCommand, "user", "password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendPostRequest(Statement sqlCommand, String username, String password) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String authHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

            String requestBody = "{\"id\": \"" + sqlCommand.getId() + "\", \"sqlQuery\": \"" + sqlCommand.getValue() + "\"}";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(REST_API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", authHeader)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("HTTP Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
