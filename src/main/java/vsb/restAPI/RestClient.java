package vsb.restAPI;

import vsb.xml.XmlReader;
import vsb.xml.model.SqlStatements;
import vsb.xml.model.Statement;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RestClient {
    private static final String REST_API_URL = "http://localhost:8080/insertSql";

    public static void main(String[] args) {
        // Načtěte XML a získávejte SQL příkazy (předpokládáme, že máte implementováno načítání SQL z XML)
        File xmlFile = new File("final_correct.xml");
        try {
            SqlStatements sqlStatements = XmlReader.readXml(xmlFile);

            // Pro každý SQL příkaz provede HTTP POST na REST API
            for (Statement sqlCommand : sqlStatements.getStatementList()) {
                sendPostRequest(sqlCommand);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendPostRequest(Statement sqlCommand) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String requestBody = "{\"id\": \"" + sqlCommand.getId() + "\", \"sqlQuery\": \"" + sqlCommand.getValue() + "\"}";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(REST_API_URL))
                    .header("Content-Type", "application/json")
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
