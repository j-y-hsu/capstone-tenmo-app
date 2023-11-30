package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequestMapping(path = "/accounts")
@PreAuthorize("isAuthenticated()")
public class AccountController {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private UserDao userDao;

    @GetMapping(path = "/{accountId}")
    public double getBalance(@PathVariable int accountId, Principal principal) {
        String username = principal.getName();
        int userId = userDao.findIdByUsername(username);
        if (userId != -1) {
            try {

                double balance = accountDao.findBalanceByUserIdAndAccountId(userId, accountId);
                if (balance != -1){
                    return balance;
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found!");
                }
            } catch (DaoException exception) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
            }

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }


    }


}
