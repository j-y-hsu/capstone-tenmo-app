package com.techelevator.tenmo.controller;

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
    private UserDao userDao;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{receiverId}")
    public Transfer makeTransfer(@PathVariable int receiverId, @RequestParam double amount, Principal principal) {
        String username = principal.getName();
        int userId = userDao.findIdByUsername(username);

        Transfer createdTransfer;
        if (userId != -1) {
            try {

                Transfer transfer = new Transfer();
                transfer.setAmount(amount);
                transfer.setSenderId(userId);
                transfer.setReceiverId(receiverId);
                transfer.setStatus("Approved");

                createdTransfer = transferDao.create(transfer);

                if (createdTransfer == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            } catch (DaoException exception) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
            }

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        return createdTransfer;
    }

    @PostMapping(path = "")
    public Transfer makeTransfer(@RequestBody @Valid Transfer transfer, Principal principal) {
        String username = principal.getName();
        int userId = userDao.findIdByUsername(username);

        Transfer createdTransfer;
        if (userId != -1) {
            try {
                transfer.setSenderId(userId);

                createdTransfer = transferDao.create(transfer);

                if (createdTransfer == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                }
            } catch (DaoException exception) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
            }

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        return createdTransfer;
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

}
