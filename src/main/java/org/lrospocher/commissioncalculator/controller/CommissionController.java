package org.lrospocher.commissioncalculator.controller;

import org.lrospocher.commissioncalculator.model.CommissionResponse;
import org.lrospocher.commissioncalculator.model.Transaction;
import org.lrospocher.commissioncalculator.model.TransactionRequest;
import org.lrospocher.commissioncalculator.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class CommissionController {

    private static final Logger LOG = LoggerFactory.getLogger(CommissionController.class);

    private final TransactionService transactionService;

    @Autowired
    public CommissionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction/commission")
    public ResponseEntity<CommissionResponse> calculateCommission(@RequestBody @Valid TransactionRequest request) {

        LOG.info("New transaction {}", request);
        Transaction transaction = new Transaction(request);

        transaction = transactionService.handleTransaction(transaction);

        LOG.info("Transaction handled correctly {}", transaction);
        return new ResponseEntity<>(new CommissionResponse(transaction.getCommissionAmount(),
                                                           transaction.getCommissionCurrency()),
                                    HttpStatus.OK);
    }
}
