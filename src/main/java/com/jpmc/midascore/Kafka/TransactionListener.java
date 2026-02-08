package com.jpmc.midascore.Kafka;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "midas-core-group",
            properties = {
                    "auto.offset.reset=earliest"
            }
    )
    public void listen(Transaction transaction) {
        System.out.println(">>> TRANSACTION RECEIVED <<<");
        System.out.println("AMOUNT = " + transaction.getAmount());
    }


}
