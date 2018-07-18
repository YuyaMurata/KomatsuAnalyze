/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

/**
 *
 * @author kaeru_yuya
 */
public class YLOPTest {

    private static Client client = ClientBuilder.newClient();

    public static void main(String[] args) {
        /*RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(
                "http://geo.search.olp.yahooapis.jp/OpenLocalPlatform/V1/geoCoder?appid={appId}&query={query}&output={output}",
                JsonNode.class,
                "[YahooのAPIキー]",
                "[検索する住所]",
                "json");
        */
        
        WebTarget target = client.target("https://api.example.com")
                .path("/oauth/access_token")
                .queryParam("token", "shortAccessToken");

        try {
            String result = target.request().get(String.class);
        } catch (BadRequestException e) {
            System.out.println("response=" + e.getResponse().readEntity(String.class) + e);
            throw e;
        }

        //String coordinates = response.getBody().get("Feature").get(0).get("Geometry").get("Coordinates").asText();
    }
}
