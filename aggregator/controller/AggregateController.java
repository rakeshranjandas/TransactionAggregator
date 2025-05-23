package aggregator.controller;

import aggregator.response.Transaction;
import aggregator.service.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/aggregate")
public class AggregateController {

    @Autowired
    TransactionsService transactionsService;

    @GetMapping
    public List<Transaction> getTransactions(@RequestParam("account") String account) {
        return transactionsService.getTransactions(account);
    }

}
