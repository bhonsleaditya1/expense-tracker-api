package com.springboot.expensetracker.repositories;

import com.springboot.expensetracker.domain.User;
import com.springboot.expensetracker.exceptions.EtAuthException;

public interface UserRepository {

    Integer create(String firstName, String lastName, String email, String password) throws EtAuthException;

    User findByEmailAndPassword(String email, String password) throws EtAuthException;

    Integer getCountByEmail(String email);

    User findById(Integer userId);
}
