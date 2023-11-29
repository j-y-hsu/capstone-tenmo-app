package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

public interface AccountDao {

     boolean create(int userId, double balance);

     double findBalanceByUserIdAndAccountId(int userId, int accountId);





}
