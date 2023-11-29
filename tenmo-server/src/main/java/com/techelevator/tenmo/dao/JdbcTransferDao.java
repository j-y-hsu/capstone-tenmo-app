package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcTransferDao implements TransferDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean create(Transfer transfer) {


        String sql = "INSERT INTO transfer(status, sender_id, receiver_id)\n" +
                "VALUES(?, ?, ?);";

        try {
            jdbcTemplate.update(sql, transfer.getStatus(), transfer.getSenderId(), transfer.getReceiverId());
                return true;
        } catch (DataAccessException e) {
            throw new DaoException("Something went wrong");
        }

    }


}
