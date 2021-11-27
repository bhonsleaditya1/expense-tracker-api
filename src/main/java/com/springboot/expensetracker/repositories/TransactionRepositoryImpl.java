package com.springboot.expensetracker.repositories;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import com.springboot.expensetracker.domain.Transaction;
import com.springboot.expensetracker.exceptions.EtBadRequestException;
import com.springboot.expensetracker.exceptions.EtResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository{

    public static final String SQL_CREATE = "INSERT INTO ET_TRANSACTIONS (TRANSACTION_ID, USER_ID, CATEGORY_ID, AMOUNT, NOTE, TRANSACTION_DATE) VALUES(NEXTVAL('ET_TRANSACTIONS_SEQ'),?,?,?,?,?);";
    public static final String SQL_FIND_BY_ID = "SELECT TRANSACTION_ID, USER_ID, CATEGORY_ID, AMOUNT, NOTE, TRANSACTION_DATE FROM ET_TRANSACTIONS WHERE USER_ID = ? AND CATEGORY_ID = ? AND TRANSACTION_ID=?;";
    public static final String SQL_FIND_ALL = "SELECT TRANSACTION_ID, USER_ID, CATEGORY_ID, AMOUNT, NOTE, TRANSACTION_DATE FROM ET_TRANSACTIONS WHERE USER_ID = ? AND CATEGORY_ID = ?;";;
    public static final String SQL_UPDATE = "UPDATE ET_TRANSACTIONS SET NOTE = ?, AMOUNT = ?, TRANSACTION_DATE = ? WHERE USER_ID = ? AND CATEGORY_ID = ? AND TRANSACTION_ID = ?;";
    public static final String SQL_REMOVE = "DELETE FROM ET_TRANSACTIONS WHERE USER_ID=? AND CATEGORY_ID=? AND TRANSACTION_ID=? ;";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Transaction> findAll(Integer userId, Integer categoryId) {
        try {
            return jdbcTemplate.query(SQL_FIND_ALL, transactionRowMapper, new Object[]{userId,categoryId});
        } catch (Exception e) {
            throw new EtResourceNotFoundException("Transaction was not found");
        }
    }

    @Override
    public Transaction findById(Integer userId, Integer categoryId, Integer transactionId)
            throws EtResourceNotFoundException {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_BY_ID,transactionRowMapper,new Object[]{userId,categoryId,transactionId});
            
        } catch (Exception e) {
            throw new EtResourceNotFoundException("Transaction not found");
        }
    }

    @Override
    public Integer create(Integer userId, Integer categoryId, Double amount, String note, Long transactionDate)
            throws EtBadRequestException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1,userId);
                ps.setInt(2,categoryId);
                ps.setDouble(3, amount);
                ps.setString(4,note);
                ps.setLong(5, transactionDate);
                return ps;
            },keyHolder);
            return (Integer) keyHolder.getKeys().get("TRANSACTION_ID");
        } catch (Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }

    @Override
    public void update(Integer userId, Integer categoryId, Integer transactionId, Transaction transaction)
            throws EtBadRequestException {
        try {
            jdbcTemplate.update(SQL_UPDATE,new Object[]{transaction.getNote(),transaction.getAmount(),transaction.getTransactionDate(),userId,categoryId,transactionId});
        } catch (Exception e) {
            System.out.println(e);
            throw new EtResourceNotFoundException("Invalid Request");
        }
        
    }

    @Override
    public void removeById(Integer userId, Integer categoryId, Integer transactionId)
            throws EtResourceNotFoundException {
        int count = jdbcTemplate.update(SQL_REMOVE, new Object[]{userId,categoryId,transactionId});
        if(count ==0){
            throw new EtResourceNotFoundException("Transaction not found");
        }
    }
    
    private RowMapper<Transaction> transactionRowMapper = ((rs,rowNum) -> {
        return new Transaction(rs.getInt("TRANSACTION_ID"),
            rs.getInt("CATEGORY_ID"),
            rs.getInt("USER_ID"),
            rs.getDouble("AMOUNT"),
            rs.getString("NOTE"),
            rs.getLong("TRANSACTION_DATE")
        );
    });

}
