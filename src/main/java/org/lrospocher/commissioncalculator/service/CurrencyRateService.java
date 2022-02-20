package org.lrospocher.commissioncalculator.service;

import org.lrospocher.commissioncalculator.model.CurrencyRateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class CurrencyRateService {

    private final RestTemplate restTemplate;
    private static final Logger LOG = LoggerFactory.getLogger(CurrencyRateService.class);

    @Autowired
    public CurrencyRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = "currencyRates")
    public BigDecimal getCurrencyRateToEur(String currency) {
        LOG.info("Fetching currency rates");
        final String strUrl = String.format("https://api.exchangerate.host/%s", LocalDate.now().toString());
        try {
            CurrencyRateResponse currencyRateResponse = restTemplate.getForObject(strUrl, CurrencyRateResponse.class);
            final BigDecimal currencyRate = currencyRateResponse.getCurrencyRate(currency);
            if (currencyRate != null) {
                return currencyRate;
            } else {
                throw new IllegalArgumentException("Invalid currency specified");
            }
        } catch (HttpClientErrorException httpError) {
            LOG.error("Error from currency API with status {}", httpError.getStatusText(), httpError);
            throw httpError;
        }
    }
}
