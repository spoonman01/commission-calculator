package org.lrospocher.commissioncalculator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

public class CurrencyRateResponse {

    @JsonProperty("rates")
    private Map<String, BigDecimal> rates;

    public BigDecimal getCurrencyRate(String currency) {
        return rates.get(currency);
    }
}
