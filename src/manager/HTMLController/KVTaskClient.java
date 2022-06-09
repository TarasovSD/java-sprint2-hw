package manager.HTMLController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    String token;
    HttpClient client;
    URI uri;
    String url;

    public KVTaskClient(String url) {
        client = HttpClient.newHttpClient();
    }

    void load(String key) throws IOException, InterruptedException {
        uri = URI.create(url + "load/" + key + "?API_TOKEN=" + token);
        HttpRequest request = HttpRequest.newBuilder() // получаем экземпляр билдера
                .GET()    // указываем HTTP-метод запроса
                .uri(uri) // указываем адрес ресурса
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
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

