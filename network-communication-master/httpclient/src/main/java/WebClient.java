
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WebClient {

    private HttpClient client;

    /* instantiate web client */
    /* Read more about Builder pattern https://en.wikipedia.org/wiki/Builder_pattern */
    public WebClient() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2).build();
    }

    /* send task (post http request) asynchronously */
    public CompletableFuture<String> sendTask(String url, byte[] requestPayload) {
        /*
        Add your code here
        */
        return sendTask(url, requestPayload, null);
    }

    /* send task (post http request) asynchronously with custom headers*/
    public CompletableFuture<String> sendTask(String url, byte[] requestPayload, String headers) {
        /*
        Add your code here
        */
        java.net.http.HttpRequest.Builder requestBuilder =
                java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(url))
                        .POST(java.net.http.HttpRequest.BodyPublishers
                                .ofByteArray(requestPayload));

        if (headers != null && !headers.isBlank()) {
            int separator = headers.indexOf(':');

            if (separator <= 0) {
                throw new IllegalArgumentException(
                        "Invalid header format. Expected Name: Value");
            }

            String headerName = headers.substring(0, separator).trim();
            String headerValue = headers.substring(separator + 1).trim();

            requestBuilder.header(headerName, headerValue);
        }

        java.net.http.HttpRequest request = requestBuilder.build();

        return client.sendAsync(
                        request,
                        java.net.http.HttpResponse.BodyHandlers.ofString())
                .thenApply(java.net.http.HttpResponse::body);
    }
}
