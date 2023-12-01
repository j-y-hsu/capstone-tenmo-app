package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/transfers")
@PreAuthorize("isAuthenticated()")
public class TransferController {
    @Autowired
    private TransferDao transferDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UserDao userDao;

    /*
        Make a new record of a transfer but do not allow the exchange of money
        Make sure the receiverId is not equal to the current userId

         */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "")
    public Transfer sendTransfer(@RequestBody @Valid Transfer transfer, Principal principal) {
        String username = principal.getName();
        int userId = userDao.findIdByUsername(username);
        Transfer createdTransfer = null;

        try {
            if (transfer.getSenderId() == 0 && userId != transfer.getReceiverId()) {
                transfer.setSenderId(userId);
                if (exchangeMoney(transfer.getSenderId(), transfer.getReceiverId(), transfer.getAmount())) {
                    transfer.setStatus("Approved");
                    createdTransfer = transferDao.create(transfer);
                }
            } else if (transfer.getReceiverId() == 0 && userId != transfer.getSenderId()) {
                transfer.setReceiverId(userId);
                transfer.setStatus("Pending");
                createdTransfer = transferDao.create(transfer);
            }

            if (createdTransfer == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } catch (DaoException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        return createdTransfer;
    }



    @PutMapping(path = "/{transferId}")
    public void completeTransfer(@PathVariable int transferId, @RequestParam String status, Principal principal) {
        String username = principal.getName();
        int userId = userDao.findIdByUsername(username);

        Transfer transfer = transferDao.findByTransferId(transferId);

        if (transfer.getStatus().equals("Pending") && userId == transfer.getSenderId()) {
            transfer.setStatus(status);
            try {
                if (status.equals("Approved") && exchangeMoney(transfer.getSenderId(), transfer.getReceiverId(), transfer.getAmount())) {
                    transferDao.update(transfer);
                } else if (status.equals("Rejected")) {
                    transferDao.update(transfer);
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            } catch (DaoException exception) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
            }

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping
    public List<Transfer> getTransfers(Principal principal) {
        String username = principal.getName();
        int userId = userDao.findIdByUsername(username);
        List<Transfer> transferList;
        if (userId != -1) {
            try {
                transferList = transferDao.findAllByUserId(userId);
            } catch (DaoException exception) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        return transferList;
    }

    @GetMapping(path = "/{transferId}")
    public Transfer getTransferByTransferId(@PathVariable int transferId) {
        Transfer transfer;
        try {
            transfer = transferDao.findByTransferId(transferId);
        } catch (DaoException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }


        return transfer;
    }

    private boolean exchangeMoney(int senderId, int receiverId, double amount) {
        if (senderId != 0 && receiverId != 0 && accountDao.withdraw(senderId, amount) > -1) {
            accountDao.deposit(receiverId, amount);
            return true;
        }
        return false;
    }

}
