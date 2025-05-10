package aggregator.service;

import aggregator.config.CacheInspector;
import aggregator.response.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class TransactionsService {

    @Autowired
    private TransactionGetter transactionGetter;

    @Autowired
    private CacheInspector cacheInspector;

    public List<Transaction> getTransactions(String account) {
        List<TransactionTask> tasks = getTasks(account);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Transaction> result = new ArrayList<>();

        try {
            List<Future<List<Transaction>>> futures = executor.invokeAll(tasks);

            for (Future<List<Transaction>> future: futures) {
                result.addAll(future.get());
            }

        } catch (ResponseStatusException e) {
            throw e;

        } catch (InterruptedException | ExecutionException e) {
            System.err.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);

        } finally {
            executor.shutdown();
        }

        result.sort(
                (t1, t2) -> {
                    return LocalDateTime.parse(t2.getTimestamp())
                            .compareTo(LocalDateTime.parse(t1.getTimestamp()));
                }
        );

        cacheInspector.printCacheContents("myCache");

        return result;
    }

    private List<TransactionTask> getTasks(String account) {
        TransactionTask task1 = new TransactionTask(transactionGetter, 8889, account);
        TransactionTask task2 = new TransactionTask(transactionGetter, 8888, account);

        return List.of(task1, task2);
    }

}
