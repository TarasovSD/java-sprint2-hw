package manager.HTMLController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class KVTaskClient {
    String token;
    HttpClient client;
    URI uri;
    String url;

    public KVTaskClient(String url) {
        this.url = url;
        client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                .GET()    // указываем HTTP-метод запроса
                .uri(URI.create(url + "register")) // указываем адрес ресурса
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        try {
            HttpResponse<String> response = client.send(request, handler);
            this.token = response.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    String load(String key) throws IOException, InterruptedException {
        if (key == null) {
            return "null";
        }
        uri = URI.create(url + "load/" + key + "?API_TOKEN=" + token);
        HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                .GET()    // указываем HTTP-метод запроса
                .uri(uri) // указываем адрес ресурса
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String json = jsonObject.getAsString();

        System.out.println("Код состояния: " + response.statusCode());
        System.out.println("Тело ответа: " + response.body());
        return json;
    }

    void put(String key, String json) throws IOException, InterruptedException {
        uri = URI.create(url + "save/" + key + "?API_TOKEN=" + token);
        HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                .POST(HttpRequest.BodyPublishers.ofString(json))    // указываем HTTP-метод запроса
                .uri(uri) // указываем адрес ресурса
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        System.out.println("Код состояния: " + response.statusCode());
        System.out.println("Тело ответа: " + response.body());
    }

}

