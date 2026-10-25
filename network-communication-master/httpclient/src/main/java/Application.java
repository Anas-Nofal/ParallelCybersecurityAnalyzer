
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class Application {

    private static final String WORKER_ADDRESS_1 = "http://localhost:8080/task";
    private static final String WORKER_ADDRESS_2 = "http://localhost:8081/task";

    public static void main(String[] args) {

        Aggregator aggregator = new Aggregator();

        String task1 = "10,200";
        String task2 = "123456789,100000000000000,700000002342343";

        String headers = "X-Debug: true";

        // Test 1: Send tasks without custom headers
        System.out.println("=== Test 1: Without custom headers ===");

        List<String> results = aggregator.sendTasksToWorkers(
                Arrays.asList(WORKER_ADDRESS_1, WORKER_ADDRESS_2),
                Arrays.asList(task1, task2)
        );

        for (String result : results) {
            System.out.println(result);
        }

        // Test 2: Send tasks with the X-Debug header
        System.out.println("=== Test 2: With custom headers ===");

        List<String> resultsWithHeaders = aggregator.sendTasksToWorkers(
                Arrays.asList(WORKER_ADDRESS_1, WORKER_ADDRESS_2),
                Arrays.asList(task1, task2),
                headers
        );

        for (String result : resultsWithHeaders) {
            System.out.println(result);
        }

        // Test 3: Check the response header returned by the server
        System.out.println("=== Test 3: Check X-Debug-Info response header ===");

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(WORKER_ADDRESS_1))
                    .header("X-Debug", "true")
                    .POST(HttpRequest.BodyPublishers.ofString(task1))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("Response body: " + response.body());

            System.out.println(
                    "X-Debug-Info: " +
                            response.headers().firstValue("X-Debug-Info")
                                    .orElse("Header not found")
            );

        } catch (Exception e) {
            System.out.println("Header test failed: " + e.getMessage());
        }

        // Test 4: Check mismatched numbers of workers and tasks
        System.out.println("=== Test 4: Mismatched workers and tasks ===");

        try {
            aggregator.sendTasksToWorkers(
                    Arrays.asList(WORKER_ADDRESS_1, WORKER_ADDRESS_2),
                    Arrays.asList(task1)
            );

            System.out.println("ERROR: Mismatched input was not rejected.");

        } catch (IllegalArgumentException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
    }
}
