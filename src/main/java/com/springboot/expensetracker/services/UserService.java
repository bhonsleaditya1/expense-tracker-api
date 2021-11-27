package com.springboot.expensetracker.services;

import com.springboot.expensetracker.domain.User;
import com.springboot.expensetracker.exceptions.EtAuthException;

public interface UserService {
    
    User validateUser(String email, String password) throws EtAuthException;

    User registerUser(String firstName, String lastName, String email, String password) throws EtAuthException;
}
