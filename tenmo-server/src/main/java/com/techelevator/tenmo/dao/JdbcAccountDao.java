package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcAccountDao implements AccountDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean create(int userId, double balance) {

        String sql = "INSERT INTO account(user_id, balance)\n" +
                "VALUES(?, ?) RETURNING account_id;";

        try {

            jdbcTemplate.queryForObject(sql, int.class, userId, balance);


        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public double findBalanceByUserIdAndAccountId(int userId, int accountId) {
        //declare what to return
        Double balance;

        //write sql string
        String sql = "SELECT balance\n" +
                "FROM account\n" +
                "WHERE user_id = ? AND account_id = ?;";

        //send to the database
        try {

            balance = jdbcTemplate.queryForObject(sql, Double.class, userId, accountId);
            if (balance != null) {
                return balance;
            } else {
                return -1;
            }


        } catch (DataAccessException e) {
            throw new DaoException("Something went wrong");
        }


    }

    @Override
    public double findBalanceByUserId(int userId) {
        //declare what to return
        Double balance = 0.00;

        //write sql string
        String sql = "SELECT balance\n" +
                "FROM account\n" +
                "WHERE user_id = ?;";

        //send to the database
        try {
            balance = jdbcTemplate.queryForObject(sql, Double.class, userId);
            if (balance != null) {
                return balance;
            } else {
                return -1;
            }


        } catch (DataAccessException e) {
            throw new DaoException("Something went wrong");
        }


    }

    @Override
    public double withdraw(int userId, double amount) {
        double balance = findBalanceByUserId(userId);

        if (balance >= amount) {
            balance -= amount;
            String sql = "UPDATE account\n" +
                    "SET balance = ?\n" +
                    "WHERE user_id = ?;";
            try {
                jdbcTemplate.update(sql, balance, userId);
                return balance;
            } catch (DataAccessException e) {
                throw new DaoException("Something went wrong!");
            }
        } else {
            return -1;
        }


    }

    @Override
    public double deposit(int userId, double amount) {
        double balance = findBalanceByUserId(userId);

        balance += amount;
        String sql = "UPDATE account\n" +
                "SET balance = ?\n" +
                "WHERE user_id = ?;";
        try {
            jdbcTemplate.update(sql, balance, userId);
            return balance;
        } catch (DataAccessException e) {
            throw new DaoException("Something went wrong!");
        }

    }




}
