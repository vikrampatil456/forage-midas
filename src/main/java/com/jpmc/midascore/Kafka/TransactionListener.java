package com.jpmc.midascore.Kafka;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private final TransactionService transactionService;

    public TransactionListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "midas-core-group",
            properties = {
                    "auto.offset.reset=earliest"
            }
    )
    public void listen(Transaction transaction) {
        transactionService.process(transaction);
    }
}
