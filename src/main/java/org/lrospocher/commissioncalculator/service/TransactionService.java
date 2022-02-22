package org.lrospocher.commissioncalculator.service;

import org.lrospocher.commissioncalculator.model.Transaction;
import org.lrospocher.commissioncalculator.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);
    public static final String EUR_CURRENCY = "EUR";

    private final CommissionRulesService commissionRulesService;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(CommissionRulesService commissionRulesService,
                              TransactionRepository transactionRepository) {
        this.commissionRulesService = commissionRulesService;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Gets the commission on a transaction and saves it.
     * @param transaction without commission
     * @return the transaction with commission specified
     */
    public Transaction handleTransaction(Transaction transaction) {
        // GET last month saved transactions for client
        final List<Transaction> lastClientTransaction =
            transactionRepository.findByClientIdWithDateAfter(transaction.getClientId(), LocalDate.now().minusMonths(1));

        // Call rule-engine to get commission
        transaction = commissionRulesService.setCommission(transaction, lastClientTransaction);

        LOG.info("Transaction with commission {}", transaction.toString());

        // Save finalized transaction on DB
        transactionRepository.save(transaction);
        return transaction;
    }
}
