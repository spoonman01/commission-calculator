package org.lrospocher.commissioncalculator.controller;

import org.lrospocher.commissioncalculator.model.CommissionResponse;
import org.lrospocher.commissioncalculator.model.Transaction;
import org.lrospocher.commissioncalculator.model.TransactionRequest;
import org.lrospocher.commissioncalculator.repository.TransactionRepository;
import org.lrospocher.commissioncalculator.service.CurrencyRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
public class CommissionController {

    private static final Logger LOG = LoggerFactory.getLogger(CommissionController.class);

    public static final String EUR_CURRENCY = "EUR";
    private final CurrencyRateService currencyRateService;
    private final TransactionRepository transactionRepository;

    @Autowired
    public CommissionController(CurrencyRateService currencyRateService, TransactionRepository transactionRepository) {
        this.currencyRateService = currencyRateService;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping("/transaction/commission")
    public ResponseEntity<CommissionResponse> calculateCommission(@RequestBody @Valid TransactionRequest request) {

        LOG.info("New transaction {}", request);
        Transaction transaction = new Transaction(request);

        // GET saved transaction user last month

        // Call Rule engine
        // Assert.isTrue(transaction.getCurrency().equals(EUR_CURRENCY), "Could not perform currency change correctly");

        BigDecimal commission = transaction.getAmount().multiply(BigDecimal.valueOf(0.05));

        // Change commission currency
        if (!transaction.getCurrency().equals(EUR_CURRENCY)) {
            transaction.setCommissionAmount(currencyRateService.adjustAmountToEur(commission, transaction.getCurrency()));
        } else {
            transaction.setCommissionAmount(commission);
        }
        transaction.setCommissionCurrency(EUR_CURRENCY);

        // Save on DB
        transactionRepository.save(transaction);
        return new ResponseEntity<>(new CommissionResponse(request.getAmount(), "EUR"), HttpStatus.OK);
    }
}
