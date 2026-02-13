package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.model.Incentive;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRepo;
    private final IncentiveClient incentiveClient;

    public TransactionService(UserRepository userRepository, TransactionRecordRepository transactionRepo, IncentiveClient incentiveClient) {
        this.userRepository = userRepository;
        this.transactionRepo = transactionRepo;
        this.incentiveClient = incentiveClient;
    }

    @Transactional
    public void process(Transaction tx) {

        Optional<UserRecord> senderOpt =
                userRepository.findById(tx.getSenderId());
        Optional<UserRecord> recipientOpt =
                userRepository.findById(tx.getRecipientId());

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            return;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        if (sender.getBalance() < tx.getAmount()) {
            return;
        }

        Incentive incentive = incentiveClient.fetchIncentive(tx);
        double incentiveAmount = incentive.getAmount();

        tx.setIncentive(incentiveAmount);

        // update balances
        sender.setBalance(sender.getBalance() - tx.getAmount());
        recipient.setBalance((float) (recipient.getBalance() + tx.getAmount() + incentiveAmount));

        // persist transaction
        TransactionRecord record =
                new TransactionRecord(sender, recipient, tx.getAmount());

        transactionRepo.save(record);
        userRepository.save(sender);
        userRepository.save(recipient);
    }

}
