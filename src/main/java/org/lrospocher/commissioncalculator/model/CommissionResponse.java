package org.lrospocher.commissioncalculator.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class CommissionResponse {
    private final BigDecimal amount;
    private final String currency;

    public CommissionResponse(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    @JsonProperty("amount")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public BigDecimal getAmount() {
        return amount;
    }

    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "CommissionResponse{" +
               "amount=" + amount +
               ", currency='" + currency + '\'' +
               '}';
    }
}
