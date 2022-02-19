package org.lrospocher.commissioncalculator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long clientId;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false, length = 3)
    private String currency;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal commissionAmount;
    @Column(nullable = false, length = 3)
    private String commissionCurrency;

    public Transaction() {
    }

    public Transaction(TransactionRequest request) {
        this.clientId = request.getClientId();
        this.date = request.getDate();
        this.amount = request.getAmount();
        this.currency = request.getCurrency();
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Long getClientId() {
        return clientId;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public String getCommissionCurrency() {
        return commissionCurrency;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public void setCommissionCurrency(String commissionCurrency) {
        this.commissionCurrency = commissionCurrency;
    }

    @Override
    public String toString() {
        return "Transaction{" +
               "clientId=" + clientId +
               ", date=" + date +
               ", amount=" + amount +
               ", currency='" + currency + '\'' +
               ", commissionAmount='" + commissionAmount + '\'' +
               ", commissionCurrency='" + commissionCurrency + '\'' +
               '}';
    }
}
