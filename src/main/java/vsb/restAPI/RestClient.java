package vsb.restAPI;

import vsb.xml.XmlReader;
import vsb.xml.model.SqlStatements;
import vsb.xml.model.Statement;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

// This class was used to test the REST API endpoints. It doesn t have any use in the final project.
public class RestClient {
    private static final String REST_API_URL = "http://localhost:8080/insertSql";

    public static void main(String[] args) {

        File xmlFile = new File("final_correct.xml");
        try {
            SqlStatements sqlStatements = XmlReader.readXml(xmlFile);

            for (Statement sqlCommand : sqlStatements.getStatementList()) {
                long startTime = System.currentTimeMillis();
                System.out.println("Sending SQL command: " + sqlCommand.getValue());
                sendPostRequest(sqlCommand, "user", "password");
                long endTime = System.currentTimeMillis();
                System.out.println("Time elapsed: " + (endTime - startTime) + "ms");
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
