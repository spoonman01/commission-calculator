package org.lrospocher.commissioncalculator.repository;

import org.lrospocher.commissioncalculator.model.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    @Query("select t from Transaction t where client_id = :clientId AND date >= :fromDate")
    List<Transaction> findByClientIdWithDateAfter(@Param("clientId") Long clientId, @Param("fromDate") LocalDate fromDate);
}
