package org.lrospocher.commissioncalculator.service;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.lrospocher.commissioncalculator.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommissionRulesService {

    private final KieContainer kieContainer;

    @Autowired
    public CommissionRulesService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    public Transaction setCommission(Transaction transaction, List<Transaction> lastClientTransactions) {
        KieSession kieSession = kieContainer.newKieSession();
        kieSession.insert(lastClientTransactions);
        kieSession.setGlobal("transaction", transaction);
        kieSession.fireAllRules();
        kieSession.dispose();
        return transaction;
    }
}
