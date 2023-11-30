package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AccountDao accountDao;

    @Override
    public Transfer create(Transfer transfer) {
        Transfer createdTransfer = null;

        String sql = "INSERT INTO transfer(status, sender_id, receiver_id, amount)\n" +
                "VALUES(?, ?, ?, ?) RETURNING transfer_id;";

        Integer newTransferId;

        try {
            if (transfer.getSenderId() != transfer.getReceiverId() && accountDao.withdraw(transfer.getSenderId(), transfer.getAmount()) != -1) {
                accountDao.deposit(transfer.getReceiverId(), transfer.getAmount());
                newTransferId = jdbcTemplate.queryForObject(sql, Integer.class, transfer.getStatus(), transfer.getSenderId(), transfer.getReceiverId(), transfer.getAmount());
                if (newTransferId != null) {
                    createdTransfer = transfer;
                    transfer.setTransferId(newTransferId);
                }

            }
        } catch (DataAccessException e) {
            throw new DaoException("Something went wrong");
        }
        return createdTransfer;
    }

    @Override
    public List<Transfer> findAllByUserId(int userId) {
        List<Transfer> transferList = new ArrayList<>();

        String sql = "SELECT *\n" +
                "FROM transfer\n" +
                "WHERE sender_id = ? OR receiver_id = ?;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);

        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            transferList.add(transfer);
        }


        return transferList;
    }

    @Override
    public Transfer findByTransferId(int transferId) {
        String sql = "SELECT *\n" +
                "FROM transfer\n" +
                "WHERE transfer_id = ?;";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferId);

            if (result.next()) {
                return mapRowToTransfer(result);
            }

        } catch (DataAccessException exception) {
            throw new DaoException("Something went wrong");
        }

        throw new DaoException("Transfer not found: " + transferId);
    }

    private Transfer mapRowToTransfer(SqlRowSet result) {
        Transfer transfer = new Transfer();
        transfer.setStatus(result.getString("status"));
        transfer.setReceiverId(result.getInt("receiver_id"));
        transfer.setSenderId(result.getInt("sender_id"));
        transfer.setAmount(result.getDouble("amount"));
        transfer.setTransferId(result.getInt("transfer_id"));
        return transfer;
    }


}
