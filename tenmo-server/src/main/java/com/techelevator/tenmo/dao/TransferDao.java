package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    Transfer create(Transfer transfer);

    List<Transfer> findAllByUserId(int userId);

    List<Transfer> findAllPendingByUserId(int userId);

    Transfer findByTransferId(int transferId);

    Transfer update(Transfer transfer);



}
