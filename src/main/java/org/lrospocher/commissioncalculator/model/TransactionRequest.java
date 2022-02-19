package org.lrospocher.commissioncalculator.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionRequest {

    @NotNull
    private LocalDate date;
    @Min(0L)
    private BigDecimal amount;
    @NotBlank
    private String currency;
    @Min(1L)
    private Long clientId;

    @JsonCreator
    public TransactionRequest(@JsonProperty("date") LocalDate date, @JsonProperty("amount") String amount,
                              @JsonProperty("currency") String currency, @JsonProperty("client_id") Long clientId) {
        this.date = date;
        this.amount = BigDecimal.valueOf(Double.parseDouble(amount));
        this.currency = currency;
        this.clientId = clientId;
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

    @Override
    public String toString() {
        return "TransactionRequest{" +
               "date=" + date +
               ", amount=" + amount +
               ", currency='" + currency + '\'' +
               ", clientId=" + clientId +
               '}';
    }
}
