package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

public interface AccountDao {

     boolean create(int userId, double balance);

     double findBalanceByUserIdAndAccountId(int userId, int accountId);

     double withdraw(int userId, double amount);

     double findBalanceByUserId(int userId);

     double deposit(int userId, double amount);





}
