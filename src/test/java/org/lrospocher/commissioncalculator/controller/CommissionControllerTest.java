package org.lrospocher.commissioncalculator.controller;

import org.junit.jupiter.api.Test;
import org.lrospocher.commissioncalculator.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests, warning: makes an actual public Rest API call.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CommissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void givenUsdTransaction_returnPercentageCommission() throws Exception {
        final String requestBody = "{\n" +
                                   "    \"date\": \"2022-02-20\",\n" +
                                   "    \"amount\": \"100\",\n" +
                                   "    \"currency\": \"USD\",\n" +
                                   "    \"client_id\": 1\n" +
                                   "}";
        final String expectedResponseBody = "{\n" +
                                            "    \"amount\": \"0.44\",\n" +
                                            "    \"currency\": \"EUR\"\n" +
                                            "}";
        mockMvc.perform(post("/transaction/commission")
                            .contentType("application/json")
                            .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().json(expectedResponseBody));

        assertTrue(transactionRepository.findById(1L).isPresent());
    }
}