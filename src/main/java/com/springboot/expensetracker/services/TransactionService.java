package com.springboot.expensetracker.services;

import java.util.List;

import com.springboot.expensetracker.domain.Transaction;
import com.springboot.expensetracker.exceptions.EtBadRequestException;
import com.springboot.expensetracker.exceptions.EtResourceNotFoundException;

public interface TransactionService {

    List<Transaction> fetchAllTransaction(Integer userId, Integer categoryId);

    Transaction fetchTransactionById(Integer userId, Integer categoryId, Integer transactionId) throws EtResourceNotFoundException;
    
    Transaction addTransaction(Integer userId, Integer categoryId, Double amount, String note, Long transactionDate) throws EtBadRequestException;

    void updateTransaction(Integer userId, Integer categoryId, Integer transactionId, Transaction transaction) throws EtBadRequestException;

    void removeTransaction(Integer userId, Integer categoryId, Integer trnsactionId) throws EtResourceNotFoundException;
}
