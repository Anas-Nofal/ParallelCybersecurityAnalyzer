
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Aggregator {

    private WebClient webClient;

    public Aggregator() {
        this.webClient = new WebClient();
    }

    /*send task to list of workers*/
    public List<String> sendTasksToWorkers(List<String> workersAddresses, List<String> tasks) {
       /*
       Add your code here
        */
        return sendTasksToWorkers(workersAddresses, tasks, null);
    }

    /*send task to list of workers*/
    public List<String> sendTasksToWorkers(List<String> workersAddresses, List<String> tasks, String headers) {
       /*
       Add your code here
        */
        if (workersAddresses == null || tasks == null) {
            throw new IllegalArgumentException(
                    "Worker addresses and tasks must not be null");
        }

        if (workersAddresses.size() != tasks.size()) {
            throw new IllegalArgumentException(
                    "The number of workers must match the number of tasks");
        }

        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (int i = 0; i < tasks.size(); i++) {
            String address = workersAddresses.get(i);
            byte[] payload = tasks.get(i).getBytes(java.nio.charset.StandardCharsets.UTF_8);

            CompletableFuture<String> future;

            if (headers == null || headers.isBlank()) {
                future = webClient.sendTask(address, payload);
            } else {
                future = webClient.sendTask(address, payload, headers);
            }

            futures.add(future);
        }

        CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        ).join();

        List<String> results = new ArrayList<>();

        for (CompletableFuture<String> future : futures) {
            results.add(future.join());
        }

        return results;
    }
}
