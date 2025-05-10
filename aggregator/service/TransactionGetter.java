package aggregator.service;

import aggregator.response.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionGetter {
    private final int MAX_ATTEMPTS = 100;

    @Cacheable(cacheNames = "myCache", key = "#a0 + '_' + #a1")
    /*
        Note -
            key = "#a0 + '_' + #a1"
                denotes that {argument1_argument2} is used as key

            The following did not work
                key = "#port + '_' + #account"
                why ? because it was coming null_null
                    why ? because #part and #account are not available at runtime
                        why ? because Java doesn't retain method parameter names unless you
                            explicitly tell it to.
                            You have to have "-parameters" flag while running the application.
                            This can be done by adding a configuration to pox.xml

    */
    public List<Transaction> get(int port, String account) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("port", String.valueOf(port));
        params.put("account", account);

        HttpStatusCode lastResponseStatus = HttpStatus.OK;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            System.err.printf("Attempt %d, for port %d\n", attempt, port);

            try {
                ResponseEntity<List<Transaction>> response = restTemplate.exchange(
                        "http://localhost:{port}/transactions?account={account}",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Transaction>>() {
                        },
                        params
                );

                return response.getBody();

            } catch (HttpServerErrorException httpServerErrorException) {
                lastResponseStatus = httpServerErrorException.getStatusCode();

            }
        }

        throw new ResponseStatusException(lastResponseStatus);
    }

    @Autowired
    private RestTemplate restTemplate;
}
