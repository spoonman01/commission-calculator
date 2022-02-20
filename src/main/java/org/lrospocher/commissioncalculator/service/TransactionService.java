package org.lrospocher.commissioncalculator.service;

import org.lrospocher.commissioncalculator.model.Transaction;
import org.lrospocher.commissioncalculator.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);
    public static final String EUR_CURRENCY = "EUR";

    private final CurrencyRateService currencyRateService;
    private final CommissionRulesService commissionRulesService;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(CurrencyRateService currencyRateService, CommissionRulesService commissionRulesService,
                              TransactionRepository transactionRepository) {
        this.currencyRateService = currencyRateService;
        this.commissionRulesService = commissionRulesService;
        this.transactionRepository = transactionRepository;
    }

    public Transaction handleTransaction(Transaction transaction) {
        // GET last month saved transactions for client
        final List<Transaction> lastClientTransaction =
            transactionRepository.findByClientIdWithDateAfter(transaction.getClientId(), LocalDate.now().minusMonths(1));

        LOG.info(lastClientTransaction.toString());

        // Call rule-engine to ger correct commission
        transaction = commissionRulesService.setCommission(transaction, lastClientTransaction);

        BigDecimal commission = transaction.getAmount().multiply(BigDecimal.valueOf(0.05));

        // Change commission currency
        if (!transaction.getCurrency().equals(EUR_CURRENCY)) {
            final BigDecimal currencyRateToEur = currencyRateService.getCurrencyRateToEur(transaction.getCurrency());
            final BigDecimal adjustedCommission = commission.multiply(currencyRateToEur);
            transaction.setCommissionAmount(adjustedCommission);
        } else {
            transaction.setCommissionAmount(commission);
        }
        transaction.setCommissionCurrency(EUR_CURRENCY);

        // Save finalized transaction on DB
        transactionRepository.save(transaction);
        return transaction;
    }
}
