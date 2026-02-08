package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRepo;

    public TransactionService(UserRepository userRepository, TransactionRecordRepository transactionRepo) {
        this.userRepository = userRepository;
        this.transactionRepo = transactionRepo;
    }

    @Transactional
    public void  process(Transaction tx) {

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

        // update balance
        sender.setBalance(sender.getBalance() - tx.getAmount());
        recipient.setBalance(recipient.getBalance() + tx.getAmount());

        // persist transaction
        TransactionRecord record =
                new TransactionRecord(sender, recipient, tx.getAmount());

        transactionRepo.save(record);
        userRepository.save(sender);
        userRepository.save(recipient);

        System.out.println("Processing transaction:" + tx.getAmount());

        if (sender.getName().equals("waldorf")) {
            System.out.println("WALDORF BALANCE = " + sender.getBalance());
        }

    }
}
