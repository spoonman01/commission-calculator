package org.lrospocher.commissioncalculator.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.lrospocher.commissioncalculator.model.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.lrospocher.commissioncalculator.config.ConfigurationWebAndRules.RULES_CUSTOMER_RULES_DRL;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommissionRulesServiceTest {

    private CommissionRulesService commissionRulesService;
    private static KieContainer kieContainer;
    private CurrencyRateService currencyRateService;

    @BeforeEach
    public void setUpClass() {
        KieFileSystem kieFileSystem = KieServices.Factory.get().newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_CUSTOMER_RULES_DRL));
        KieBuilder kb = KieServices.Factory.get().newKieBuilder(kieFileSystem);
        kb.buildAll();
        KieModule kieModule = kb.getKieModule();
        kieContainer = KieServices.Factory.get().newKieContainer(kieModule.getReleaseId());

        currencyRateService = mock(CurrencyRateService.class);
        commissionRulesService = new CommissionRulesService(kieContainer, currencyRateService);
    }

    @Test
    void givenEurTransaction_returnPercentageCommission() {
        Transaction transaction = new Transaction(1L, LocalDate.now(), BigDecimal.valueOf(100.00), "EUR");

        transaction = commissionRulesService.setCommission(transaction, Collections.emptyList());

        assertTrue(transaction.getCommissionAmount().compareTo(BigDecimal.valueOf(0.5)) == 0);
    }

    @Test
    void givenSmallEurTransaction_returnMinCommission() {
        Transaction transaction = new Transaction(1L, LocalDate.now(), BigDecimal.valueOf(0.1), "EUR");

        transaction = commissionRulesService.setCommission(transaction, Collections.emptyList());

        assertTrue(transaction.getCommissionAmount().compareTo(BigDecimal.valueOf(0.05)) == 0);
    }

    @Test
    void givenUsdTransaction_returnPercentageCommission() {
        when(currencyRateService.getCurrencyRateToEur(eq("USD"))).thenReturn(BigDecimal.valueOf(1.131503));
        Transaction transaction = new Transaction(1L, LocalDate.now(), BigDecimal.valueOf(100.00), "USD");

        transaction = commissionRulesService.setCommission(transaction, Collections.emptyList());

        final BigDecimal calculatedCommission =
            BigDecimal.valueOf(100.00).multiply(BigDecimal.valueOf(0.005)).divide(BigDecimal.valueOf(1.131503),
                                                                                  RoundingMode.HALF_UP);
        assertTrue(transaction.getCommissionAmount().compareTo(calculatedCommission) == 0);
        assertEquals("EUR", transaction.getCommissionCurrency());
    }

    @Test
    void givenClient42Transaction_returnRule2() {
        Transaction transaction = new Transaction(42L, LocalDate.now(), BigDecimal.valueOf(100.00), "EUR");

        transaction = commissionRulesService.setCommission(transaction, Collections.emptyList());

        assertTrue(transaction.getCommissionAmount().compareTo(BigDecimal.valueOf(0.05)) == 0);
    }

    @Test
    void givenUnknownCurrency_throwException() {
        when(currencyRateService.getCurrencyRateToEur(eq("ZZZ"))).thenThrow(IllegalArgumentException.class);
        Transaction transaction = new Transaction(1L, LocalDate.now(), BigDecimal.valueOf(100.00), "ZZZ");

        assertThrows(IllegalArgumentException.class,
                     () -> {
                         commissionRulesService.setCommission(transaction, Collections.emptyList());
                     });
    }

    @Test
    void givenHighTurnover_returnRule3() {
        Transaction transaction = new Transaction(1L, LocalDate.now(), BigDecimal.valueOf(100.00), "EUR");

        final List<Transaction> pastTransactions =
            List.of(new Transaction(1L, LocalDate.now(), BigDecimal.valueOf(500.00), "EUR"),
                    new Transaction(1L, LocalDate.now(), BigDecimal.valueOf(499.00), "EUR"),
                    new Transaction(1L, LocalDate.now(), BigDecimal.valueOf(2.00), "EUR"));
        transaction = commissionRulesService.setCommission(transaction, pastTransactions);

        assertTrue(transaction.getCommissionAmount().compareTo(BigDecimal.valueOf(0.03)) == 0);
    }
}