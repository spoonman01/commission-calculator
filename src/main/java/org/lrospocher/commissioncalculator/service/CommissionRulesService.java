package org.lrospocher.commissioncalculator.service;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.lrospocher.commissioncalculator.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.lrospocher.commissioncalculator.service.TransactionService.EUR_CURRENCY;


/**
 * Given a Transaction, calculates the commission in EUR.
 *
 * <p>Uses Drools rule-engine to determine the commission and a Rest API to adjust values to EUR</p>
 */
@Service
public class CommissionRulesService {

    public static final BigDecimal PERCENTAGE_CURRENCY_THRESHOLD = BigDecimal.valueOf(0.05);

    private final KieContainer kieContainer;
    private final CurrencyRateService currencyRateService;

    // Injected in Drools to fetch the result
    public static class Result {

        public BigDecimal commission;

        public void setMinCommission(BigDecimal commission) {
            if (this.commission == null) {
                this.commission = commission;
            } else {
                this.commission = this.commission.min(commission);
            }
        }

    }

    // Injected in Drools as param
    public static class ClientTransactionAmount {

        private BigDecimal amount;

        public ClientTransactionAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getAmount() {
            return amount;
        }

    }

    @Autowired
    public CommissionRulesService(KieContainer kieContainer, CurrencyRateService currencyRateService) {
        this.kieContainer = kieContainer;
        this.currencyRateService = currencyRateService;
    }

    public Transaction setCommission(Transaction transaction, List<Transaction> lastClientTransactions) {
        // Calculate the sum in EUR of the recent transactions of a single client
        ClientTransactionAmount clientTransactionAmount =
            new ClientTransactionAmount(lastClientTransactions.stream()
                                                              .map(t -> {
                                                                  if (!t.getCurrency().equals(EUR_CURRENCY)) {
                                                                      final BigDecimal currencyRateToEur = currencyRateService.getCurrencyRateToEur(t.getCurrency());
                                                                      return t.getAmount().divide(currencyRateToEur, RoundingMode.HALF_UP);
                                                                  } else {
                                                                      return t.getAmount();
                                                                  }
                                                              })
                                                              .reduce(BigDecimal.ZERO, BigDecimal::add));
        // Set parameters and launch rule-engine
        KieSession kieSession = kieContainer.newKieSession();
        kieSession.insert(transaction);
        kieSession.insert(clientTransactionAmount);
        kieSession.setGlobal("result", new Result());

        kieSession.fireAllRules();
        Result result = (Result) kieSession.getGlobal("result");
        kieSession.dispose();

        transaction.setCommissionAmount(result.commission);
        transaction.setCommissionCurrency(EUR_CURRENCY);

        // Adjust commission value and currency, when calculated commission isn't in EUR
        if (!transaction.getCurrency().equals(EUR_CURRENCY) &&
            transaction.getCommissionAmount().compareTo(PERCENTAGE_CURRENCY_THRESHOLD) > 0) {
            final BigDecimal currencyRateToEur = currencyRateService.getCurrencyRateToEur(transaction.getCurrency());
            final BigDecimal adjustedCommission = transaction.getCommissionAmount().divide(currencyRateToEur,
                                                                                           RoundingMode.HALF_UP);
            transaction.setCommissionAmount(adjustedCommission);
        }

        return transaction;
    }
}
