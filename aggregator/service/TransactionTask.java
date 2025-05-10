package aggregator.service;

import aggregator.response.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class TransactionTask implements Callable<List<Transaction>> {

    private int port;
    private String account;

    private TransactionGetter transactionGetter;

    public TransactionTask(TransactionGetter transactionGetter, int port, String account) {
        this.transactionGetter = transactionGetter;
        this.port = port;
        this.account = account;
    }

    @Override
    public List<Transaction> call() throws Exception {
        return transactionGetter.get(port, account);
    }
}
